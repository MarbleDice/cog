package com.bromleyoil.cog.persist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.bromleyoil.cog.comparator.CompositeComparator;
import com.bromleyoil.cog.persist.annotation.PropertyOrder;

public class PropertyOrderComparator implements Comparator<PropertyModel> {

	private List<String> orderedProperties;
	private Comparator<PropertyModel> comparator;

	public PropertyOrderComparator(PropertyOrder propertyOrder) {
		orderedProperties = propertyOrder == null ? new ArrayList<>() : Arrays.asList(propertyOrder.value());

		comparator = new CompositeComparator<>(
				Comparator.comparingInt(this::getFieldIndex),
				Comparator.comparingInt(this::getFieldTypeOrder),
				Comparator.comparing(PropertyModel::getName));
	}

	public int getFieldIndex(PropertyModel property) {
		int index = orderedProperties.indexOf(property.getName());
		return index > -1 ? index : orderedProperties.size();
	}

	public int getFieldTypeOrder(PropertyModel property) {
		if (property.getType().isScalar()) {
			return 1;
		} else if (property.getType().isList()) {
			return 3;
		} else if (property.getType().isMap()) {
			return 4;
		} else {
			return 2;
		}
	}

	@Override
	public int compare(PropertyModel o1, PropertyModel o2) {
		return comparator.compare(o1, o2);
	}
}
