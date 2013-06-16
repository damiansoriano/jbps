package it.polimi.utils;

public class ObjectUtils {
	
	public static boolean not(boolean bool) {
		return !bool;
	}
	
	public static boolean isNull(Object obj) {
		return (obj == null);
	}
	
	public static boolean isNotNull(Object obj) {
		return (obj != null);
	}
}
