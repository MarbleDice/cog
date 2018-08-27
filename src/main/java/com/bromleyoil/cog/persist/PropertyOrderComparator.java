package com.bromleyoil.cog.persist;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.bromleyoil.cog.comparator.CompositeComparator;
import com.bromleyoil.cog.persist.annotation.PropertyOrder;

public class PropertyOrderComparator implements Comparator<PropertyDescriptor> {

	private List<String> orderedProperties;
	private Comparator<PropertyDescriptor> comparator;

	public PropertyOrderComparator(PropertyOrder propertyOrder) {
		orderedProperties = propertyOrder == null ? new ArrayList<>() : Arrays.asList(propertyOrder.value());

		comparator = new CompositeComparator<>(
				Comparator.comparingInt(this::getFieldIndex),
				Comparator.comparingInt(this::getFieldTypeOrder),
				Comparator.comparing(PropertyDescriptor::getName));
	}

	public int getFieldIndex(PropertyDescriptor property) {
		int index = orderedProperties.indexOf(property.getName());
		return index > -1 ? index : orderedProperties.size();
	}

	public int getFieldTypeOrder(PropertyDescriptor property) {
		if (ClassUtils.SCALAR_CLASSES.contains(property.getPropertyType())) {
			return 1;
		} else if (List.class.isAssignableFrom(property.getPropertyType())) {
			return 3;
		} else if (Map.class.isAssignableFrom(property.getPropertyType())) {
			return 4;
		} else {
			return 2;
		}
	}

	@Override
	public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
		return comparator.compare(o1, o2);
	}
}
