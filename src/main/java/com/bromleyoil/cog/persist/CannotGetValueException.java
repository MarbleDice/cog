package com.bromleyoil.cog.persist;

public class CannotGetValueException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = "Cannot get property %2$s of %1$s";

	public CannotGetValueException(Object bean, String propertyName) {
		super(String.format(MESSAGE, bean, propertyName));
	}

	public CannotGetValueException(Object bean, String propertyName, Throwable cause) {
		super(String.format(MESSAGE, bean, propertyName), cause);
	}
}
