package com.bromleyoil.cog.persist;

import java.beans.PropertyDescriptor;

import com.bromleyoil.cog.persist.annotation.ReferencedBy;

/**
 * Describes a Java property.
 *
 */
public class PropertyModel {

	private PropertyDescriptor propertyDescriptor;
	private ReferencedBy referencedBy;
	private TypeModel typeModel;

	public PropertyModel(PropertyDescriptor propertyDescriptor) {
		this.propertyDescriptor = propertyDescriptor;
		this.referencedBy = propertyDescriptor.getReadMethod().getAnnotation(ReferencedBy.class);
		this.typeModel = TypeModel.of(propertyDescriptor.getReadMethod().getGenericReturnType());
	}

	public boolean isDefault(Object value) {
		return value == null;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {
		return propertyDescriptor.getName();
	}

	public boolean isReferencedBy() {
		return referencedBy != null;
	}

	public String getReferencedBy() {
		return referencedBy.value();
	}

	/**
	 * Gets the TypeModel of this Property.
	 */
	public TypeModel getType() {
		return typeModel;
	}
}
