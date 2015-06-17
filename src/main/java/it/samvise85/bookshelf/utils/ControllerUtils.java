package it.samvise85.bookshelf.utils;

public class ControllerUtils {
	
	public static String getMethodName() {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		if(ste.length >= 3)
			return ste[2].getMethodName();
		return null;
	}
}
