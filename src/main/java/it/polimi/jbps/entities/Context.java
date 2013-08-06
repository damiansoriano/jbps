package it.polimi.jbps.entities;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import lombok.Getter;

public class Context {
	
	@Getter
	private Map<String, String> variables; 
	
	public Context() {
		variables = newHashMap();
	}
	
	public void restart() {
		variables = newHashMap();
	}

}
