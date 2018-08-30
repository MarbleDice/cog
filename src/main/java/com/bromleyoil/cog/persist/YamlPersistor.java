package com.bromleyoil.cog.persist;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.beanutils.PropertyUtils;
import org.yaml.snakeyaml.Yaml;

import com.bromleyoil.cog.persist.annotation.PropertyOrder;
import com.bromleyoil.cog.persist.annotation.ReferencedBy;

public class YamlPersistor {

	public <T> T construct(Handle handle, Class<T> clazz) {
		return construct(compose(handle), clazz);
	}

	protected <T> T construct(Map<String, Object> map, Class<T> clazz) {
		for (PropertyDescriptor property : getProperties(clazz)) {
			System.out.println("Constructin " + property.getName());
		}
		return null;
	}

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
		if (object == null || ClassUtils.SCALAR_CLASSES.contains(object.getClass())) {
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

		// Represent the properties
		for (PropertyDescriptor property : getProperties(bean.getClass())) {
			if (property.getReadMethod() == null || property.getWriteMethod() == null) {
				continue;
			}

			ReferencedBy rb = property.getReadMethod().getAnnotation(ReferencedBy.class);
			if (rb != null) {
				// Value is represented by a certain property
				map.put(property.getName(), getValue(getValue(bean, property.getName()), rb.value()));
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
		return map.entrySet().stream().collect(Collectors.toMap(
				entry -> entry.getKey(),
				entry -> represent(entry.getValue())));
	}

	protected static Set<PropertyDescriptor> getProperties(Class<?> clazz) {
		// Instantiate a comparator for ordering the represented properties
		Comparator<PropertyDescriptor> comparator = new PropertyOrderComparator(
				clazz.getAnnotation(PropertyOrder.class));

		// Determine the properties to represent
		return Stream.of(PropertyUtils.getPropertyDescriptors(clazz))
				.filter(pd -> pd.getReadMethod() != null && pd.getWriteMethod() != null)
				.collect(Collectors.toCollection(() -> new TreeSet<PropertyDescriptor>(comparator)));
	}
}
