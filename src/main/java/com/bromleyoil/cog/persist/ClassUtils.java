package com.bromleyoil.cog.persist;

import java.util.Arrays;
import java.util.List;

public class ClassUtils {

	public static final List<Class<?>> SCALAR_CLASSES = Arrays.asList(boolean.class, Boolean.class, int.class,
			Integer.class, long.class, Long.class, float.class, Float.class, double.class, Double.class, String.class,
			Enum.class);
}
