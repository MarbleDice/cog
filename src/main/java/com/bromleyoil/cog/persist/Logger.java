package com.bromleyoil.cog.persist;

public class Logger {

	public void error(String format, Object... args) {
		System.err.println(String.format(format, args));
	}

	public void info(String format, Object... args) {
		System.out.println(String.format(format, args));
	}

	public void debug(String format, Object... args) {
	}
}
