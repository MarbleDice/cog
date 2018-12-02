package com.bromleyoil.cog.persist;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.yaml.snakeyaml.Yaml;

public class YamlPersistor {

	private static Logger log = new Logger();

	public <T> T construct(Handle handle, Class<T> clazz) {
		return constructBean(compose(handle), TypeModel.of(clazz), clazz);
	}

	protected <T> T constructBean(Map<String, Object> data, TypeModel type, Class<T> clazz) {
		log.info("Constructing %s", clazz.getSimpleName());

		T bean;
		try {
			bean = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			throw new LoadException("Cannot instantiate " + clazz.getSimpleName());
		}

		for (PropertyModel property : type.getProperties()) {
			if (property.isReferencedBy()) {

			} else {
				try {
					PropertyUtils.setProperty(bean, property.getName(),
							construct(data.get(property.getName()), property.getType()));
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					throw new LoadException(String.format("Cannot set property %s.%s with value %s", type,
							property.getName(), data.get(property.getName())));
				}
			}
		}

		return bean;
	}

	@SuppressWarnings("unchecked")
	protected Object construct(Object data, TypeModel type) {
		if (type.isScalar()) {
			// Property is a scalar
			return constructScalar(data, type.getMainClass());
		} else if (type.isList()) {
			// Property is a list
			return constructList((List<Object>) data, type);
		} else if (type.isMap()) {
			// Property is a map
			return constructMap((Map<String, Object>) data, type);
		} else {
			// Property is a bean
			// TODO referenced by
			return constructBean((Map<String, Object>) data, type, type.getMainClass());
		}
	}

	protected Object constructScalar(Object data, Class<?> clazz) {
		log.info("Constructing %s from %s", clazz.getSimpleName(), data.getClass().getSimpleName());
		if (String.class.isAssignableFrom(clazz)) {
			return data;
		} else if (int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
			return data;
		} else {
			throw new LoadException("Unable to construct type: " + clazz);
		}
	}

	protected <T> T mapScalarWithNull(Object scalar, Function<Object, T> mapper) {
		return scalar == null || "null".equals(scalar) ? null : mapper.apply(scalar);
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> constructList(List<Object> data, TypeModel type) {
		log.info("Constructing %s", type);
		List<T> value = new ArrayList<>();

		for (Object datum : data) {
			value.add((T) construct(datum, type.getValueType()));
		}

		return value;
	}

	protected <K, V> Map<K, V> constructMap(Map<String, Object> data, TypeModel type) {
		log.info("Constructing %s", type);
		Map<K, V> value = new HashMap<>();

		for (Entry<String, Object> datum : data.entrySet()) {

		}

		return value;
	}

	/**
	 * Searches for the YAML resource identified by handle, and returns the equivalent data composition.
	 * 
	 * @param handle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> compose(Handle handle) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(handle.getFilename());

		try (InputStreamReader reader = new InputStreamReader(inputStream)) {
			Yaml yaml = new Yaml();
			for (Object document : yaml.loadAll(reader)) {
				Map<String, Object> map = (Map<String, Object>) document;
				if (handle.getId().equals(map.get("id"))) {
					return map;
				}
			}
		} catch (IOException e) {
			throw new LoadException("Unable to open " + handle.getFilename(), e);
		}

		throw new LoadException("Unable to find " + handle);
	}

	public static String present(Object object) {
		Yaml yaml = new Yaml();
		return yaml.dump(represent(object));
	}

	@SuppressWarnings("unchecked")
	public static Object represent(Object object) {
		if (object == null || TypeModel.isScalar(object.getClass())) {
			return object;
		} else if (List.class.isAssignableFrom(object.getClass())) {
			return representList((List<Object>) object);
		} else if (Map.class.isAssignableFrom(object.getClass())) {
			return representMap((Map<Object, Object>) object);
		} else {
			return representBean(object);
		}
	}

	protected static Map<String, Object> representBean(Object bean) {
		// Use a linked hash map to preserve insertion order
		Map<String, Object> map = new LinkedHashMap<>();

		TypeModel type = TypeModel.of(bean.getClass());
		// Represent the properties
		for (PropertyModel property : type.getProperties()) {
			if (property.isReferencedBy()) {
				// Value is represented by a certain property
				map.put(property.getName(), getValue(getValue(bean, property.getName()), property.getReferencedBy()));
			} else {
				// Value should be represented as normal
				map.put(property.getName(), represent(getValue(bean, property.getName())));
			}
		}

		return map;
	}

	protected static Object getValue(Object object, String propertyName) {
		try {
			return object == null ? null : PropertyUtils.getProperty(object, propertyName);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new CannotGetValueException(object, propertyName);
		}
	}

	protected static List<Object> representList(List<Object> list) {
		return list.stream().map(YamlPersistor::represent).collect(Collectors.toList());
	}

	protected static Map<Object, Object> representMap(Map<Object, Object> map) {
		return map.entrySet().stream()
				.filter(e -> e.getValue() != null)
				.collect(Collectors.toMap(
						entry -> entry.getKey(),
						entry -> represent(entry.getValue())));
	}
}
