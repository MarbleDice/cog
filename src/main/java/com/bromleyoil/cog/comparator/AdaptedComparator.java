package com.bromleyoil.cog.comparator;

import java.util.Comparator;
import java.util.function.Function;

public class AdaptedComparator<T, U> implements Comparator<T> {

	private Function<T, U> adaptor;
	private Comparator<U> comparator;

	public AdaptedComparator(Function<T, U> adaptor, Comparator<U> comparator) {
		this.adaptor = adaptor;
		this.comparator = comparator;
	}

	@Override
	public int compare(T a, T b) {
		return comparator.compare(adaptor.apply(a), adaptor.apply(b));
	}
}
