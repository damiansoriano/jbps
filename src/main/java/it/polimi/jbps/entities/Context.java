package it.polimi.jbps.entities;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import lombok.Getter;

import com.hp.hpl.jena.ontology.Individual;

public class Context {
	
	@Getter
	private Map<String, Individual> variables; 
	
	public Context() {
		variables = newHashMap();
	}
	
	public void restart() {
		variables = newHashMap();
	}

}
