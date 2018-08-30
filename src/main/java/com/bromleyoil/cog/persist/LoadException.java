package com.bromleyoil.cog.persist;

public class LoadException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LoadException(String message) {
		super(message);
	}

	public LoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
