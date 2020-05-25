package com.bromleyoil.cog.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.yaml.snakeyaml.Yaml;

public class YamlPersistor {

	private static Logger log = new Logger();

	/**
	 * Constructs a Java object from a handle.
	 * 
	 * @param handle
	 * @param clazz
	 * @return
	 */
	public <T> T construct(Handle handle, Class<T> clazz) {
		ConstructionContext context = new ConstructionContext();

		@SuppressWarnings("unchecked")
		T bean = (T) context.constructBean(compose(handle), TypeModel.of(clazz));

		context.applyReferences();

		return bean;
	}

	private class ConstructionContext {

		private Map<Class<?>, List<Object>> beanRepository = new HashMap<>();
		private List<Reference> references = new ArrayList<>();

		protected Object constructBean(Map<Object, Object> data, TypeModel type) {
			log.info("Constructing bean %s", type);

			// Construct the bean
			Object bean;
			try {
				bean = type.getMainClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				throw new LoadException("Cannot instantiate " + type);
			}

			// Cache the bean for looking up references later
			if (!beanRepository.containsKey(bean.getClass())) {
				beanRepository.put(bean.getClass(), new ArrayList<>());
			}
			beanRepository.get(bean.getClass()).add(bean);

			// Set the properties
			for (PropertyModel property : type.getProperties()) {
				Object propertyData = data.get(property.getName());
				if (propertyData == null) {
					// Do nothing
				} else if (property.isReferencedBy()) {
					// Save the reference for assignment later
					references.add(new Reference(bean, property.getName(), property.getType().getMainClass(),
							property.getReferencedBy(), propertyData));
				} else {
					setValue(bean, property.getName(), construct(propertyData, property.getType()));
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
				return constructMap((Map<Object, Object>) data, type);
			} else {
				// Property is a bean
				return constructBean((Map<Object, Object>) data, type);
			}
		}

		protected Object constructScalar(Object data, Class<?> clazz) {
			log.info("Constructing scalar %s from %s", clazz.getSimpleName(), data.getClass().getSimpleName());
			if (String.class.isAssignableFrom(clazz)) {
				return "null".equals(data) ? null : data;
			} else if (int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
				return data;
			} else {
				throw new LoadException("Unable to construct type: " + clazz);
			}
		}

		protected List<Object> constructList(List<Object> data, TypeModel type) {
			log.info("Constructing %s", type);
			List<Object> value = new ArrayList<>();

			for (Object datum : data) {
				value.add(construct(datum, type.getValueType()));
			}

			return value;
		}

		protected Map<Object, Object> constructMap(Map<Object, Object> data, TypeModel type) {
			log.info("Constructing %s", type);
			Map<Object, Object> value = new HashMap<>();

			for (Entry<Object, Object> datum : data.entrySet()) {
				value.put(construct(datum.getKey(), type.getKeyType()),
						construct(datum.getValue(), type.getValueType()));
			}

			return value;
		}

		protected void applyReferences() {
			for (Reference reference : references) {
				log.info("Applying %s", reference.toString());

				// Find the reference value
				Object referenceValue = null;

				if (!beanRepository.containsKey(reference.getPropertyClass())) {
					throw new LoadException(String.format("No %s references have been cached",
							reference.getPropertyClass()));
				}

				for (Object bean : beanRepository.get(reference.getPropertyClass())) {
					Object value = getValue(bean, reference.getReferenceName());

					if (value != null && value.equals(reference.getReferenceValue())) {
						referenceValue = bean;
						break;
					}
				}

				if (referenceValue == null) {
					throw new LoadException("Could not find reference " + reference.toString());
				}

				// Set the reference value
				setValue(reference.getBean(), reference.getPropertyName(), referenceValue);
			}
		}

		protected void setValue(Object bean, String propertyName, Object propertyValue) {
			try {
				PropertyUtils.setProperty(bean, propertyName, propertyValue);
			} catch (Exception e) {
				throw new LoadException(String.format("Cannot set property %s.%s with value %s",
						bean.getClass().getSimpleName(), propertyName, propertyName), e);
			}
		}
	}

	/**
	 * Searches for the YAML resource identified by handle, and returns the equivalent data composition.
	 * 
	 * @param handle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Object, Object> compose(Handle handle) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(handle.getFilename());

		try (InputStreamReader reader = new InputStreamReader(inputStream)) {
			Yaml yaml = new Yaml();
			for (Object document : yaml.loadAll(reader)) {
				Map<Object, Object> map = (Map<Object, Object>) document;
				if (handle.getId().equals(map.get("id"))) {
					return map;
				}
			}
		} catch (IOException e) {
			throw new LoadException("Unable to open " + handle.getFilename(), e);
		}

		throw new LoadException("Unable to find " + handle);
	}

	/**
	 * Presents a Java object as YAML text.
	 * 
	 * @param object
	 * @return
	 */
	public static String present(Object object) {
		Yaml yaml = new Yaml();
		return yaml.dump(represent(object));
	}

	/**
	 * Represents a Java object as a data composition.
	 * 
	 * @param object
	 * @return
	 */
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
			// ReferencedBy properties are represented by a sub-property (such as a State's name for nextState)
			Object value = property.isReferencedBy()
					? getValue(getValue(bean, property.getName()), property.getReferencedBy())
					: represent(getValue(bean, property.getName()));

			if (!property.isDefault(value)) {
				map.put(property.getName(), value);
			}
		}

		return map;
	}

	protected static Object getValue(Object object, String propertyName) {
		try {
			return object == null ? null : PropertyUtils.getProperty(object, propertyName);
		} catch (Exception e) {
			throw new CannotGetValueException(object, propertyName, e);
		}
	}

	protected static List<Object> representList(List<Object> list) {
		return list.stream().map(YamlPersistor::represent).collect(Collectors.toList());
	}

	protected static Map<Object, Object> representMap(Map<Object, Object> map) {
		return map.entrySet().stream()
				.filter(e -> e.getValue() != null)
				.collect(Collectors.toMap(
						Entry<Object, Object>::getKey,
						entry -> represent(entry.getValue())));
	}
}
