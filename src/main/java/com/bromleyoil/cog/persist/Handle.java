package com.bromleyoil.cog.persist;


public class Handle {

	private static final String DELIMITER = "\\|";

	private String filename;
	private String id;

	public Handle(String handle) {
		String[] parts = handle.split(DELIMITER);
		if (parts.length != 2) {
			throw new IllegalArgumentException("Invalid handle: " + handle);
		}
		filename = parts[0];
		id = parts[1];
	}

	public static Handle of(String handle) {
		return new Handle(handle);
	}

	@Override
	public String toString() {
		return filename + DELIMITER + id;
	}
	public String getFilename() {
		return filename;
	}

	public String getId() {
		return id;
	}
}
