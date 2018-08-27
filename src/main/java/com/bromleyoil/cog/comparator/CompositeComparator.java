package com.bromleyoil.cog.comparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CompositeComparator<T> implements Comparator<T> {

	private List<Comparator<T>> comparators = new ArrayList<>();

	@SafeVarargs
	public CompositeComparator(Comparator<T>... comparators) {
		for (Comparator<T> comp : comparators) {
			this.comparators.add(comp);
		}
	}

	@Override
	public int compare(T a, T b) {
		int rv;
		for (Comparator<T> comp : comparators) {
			rv = comp.compare(a, b);
			if (rv != 0) {
				return rv;
			}
		}
		return 0;
	}
}
