package com.bromleyoil.cog.persist;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.beanutils.PropertyUtils;

import com.bromleyoil.cog.persist.annotation.PropertyOrder;

/**
 * Describes a Java type.
 *
 */
public class TypeModel {

	private static Logger log = new Logger();

	private static final List<Class<?>> SCALAR_CLASSES = Collections
			.unmodifiableList(Arrays.asList(boolean.class, Boolean.class, int.class, Integer.class, long.class,
					Long.class, float.class, Float.class, double.class, Double.class, String.class, Enum.class));

	private static Map<Type, TypeModel> repository = new HashMap<>();

	private Class<?> mainClass;

	private TypeModel keyType = null;

	private TypeModel valueType = null;

	private Set<PropertyModel> properties;

	private TypeModel() {
	}

	public static TypeModel of(Type type) {
		if (!repository.containsKey(type)) {
			TypeModel typeModel = new TypeModel();
			repository.put(type, typeModel);

			if (type instanceof Class) {
				typeModel.init((Class<?>) type);
			} else if (type instanceof ParameterizedType) {
				typeModel.init((ParameterizedType) type);
			} else {
				throw new IllegalArgumentException("Unsupported Type: " + type.getClass());
			}
		}

		return repository.get(type);
	}

	protected void init(Class<?> mainClass) {
		log.debug("Initializing with %s", mainClass);
		this.mainClass = mainClass;
		// Instantiate a comparator for ordering the properties
		Comparator<PropertyModel> comparator = new PropertyOrderComparator(
				mainClass.getAnnotation(PropertyOrder.class));

		// Determine the properties to represent
		properties = Stream.of(PropertyUtils.getPropertyDescriptors(mainClass))
				.filter(pd -> pd.getReadMethod() != null && pd.getWriteMethod() != null)
				.map(PropertyModel::new)
				.collect(Collectors.toCollection(() -> new TreeSet<PropertyModel>(comparator)));
	}

	protected void init(ParameterizedType paramType) {
		log.debug("Initializing with %s", paramType.getTypeName());
		mainClass = (Class<?>) paramType.getRawType();
		if (List.class.isAssignableFrom(mainClass)) {
			valueType = TypeModel.of(paramType.getActualTypeArguments()[0]);
		} else if (Map.class.isAssignableFrom(mainClass)) {
			keyType = TypeModel.of(paramType.getActualTypeArguments()[0]);
			valueType = TypeModel.of(paramType.getActualTypeArguments()[1]);
		} else {
			throw new IllegalArgumentException("Unsupported ParameterizedType: " + paramType.getRawType());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(mainClass.getSimpleName());
		if (keyType != null || valueType != null) {
			sb.append("<");
			if (keyType != null) {
				sb.append(keyType).append(", ");
			}
			sb.append(valueType).append(">");
		}
		return sb.toString();
	}

	public Set<PropertyModel> getProperties() {
		return properties;
	}

	public boolean isScalar() {
		return SCALAR_CLASSES.contains(mainClass);
	}

	public static boolean isScalar(Class<?> clazz) {
		return SCALAR_CLASSES.contains(clazz);
	}

	public boolean isList() {
		return valueType != null && keyType == null;
	}

	public boolean isMap() {
		return valueType != null && keyType != null;
	}

	public Class<?> getMainClass() {
		return mainClass;
	}

	public TypeModel getKeyType() {
		return keyType;
	}

	public TypeModel getValueType() {
		return valueType;
	}
}
