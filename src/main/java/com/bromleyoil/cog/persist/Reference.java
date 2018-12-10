package com.bromleyoil.cog.persist;

public class Reference {

	private Object bean;
	private String propertyName;
	private Class<?> propertyClass;
	private String referenceName;
	private Object referenceValue;

	public Reference(Object bean, String propertyName, Class<?> propertyClass, String referenceName,
			Object referenceValue) {
		this.bean = bean;
		this.propertyName = propertyName;
		this.propertyClass = propertyClass;
		this.referenceName = referenceName;
		this.referenceValue = referenceValue;
	}

	public String toString() {
		return String.format("%s.%s=%s", propertyClass.getSimpleName(), referenceName, referenceValue);
	}

	public Object getBean() {
		return bean;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Class<?> getPropertyClass() {
		return propertyClass;
	}

	public String getReferenceName() {
		return referenceName;
	}

	public Object getReferenceValue() {
		return referenceValue;
	}
}
