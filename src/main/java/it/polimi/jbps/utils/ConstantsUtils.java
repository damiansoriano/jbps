package it.polimi.jbps.utils;

import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.PropertyType.DATA_PROPERTY;
import static it.polimi.jbps.PropertyType.OBJECT_PROPERTY;
import it.polimi.jbps.PropertyType;

import java.util.Map;

public class ConstantsUtils {
	
	private final static Map<String, PropertyType> propertyMapping;
	
	static {
		 propertyMapping = newHashMap();
		 propertyMapping.put("objectProperty", OBJECT_PROPERTY);
		 propertyMapping.put("dataProperty", DATA_PROPERTY);
	}
	
	public static PropertyType parsePropertyType(String propertyType) {
		return propertyMapping.get(propertyType);
	}
	
}
