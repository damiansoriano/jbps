package it.polimi.jbps.entities;

import static it.polimi.jbps.utils.ObjectUtils.isNullOrEmpty;
import lombok.Getter;

import com.hp.hpl.jena.ontology.Individual;

public class JBPSIndividual {
	
	@Getter
	private final Individual individual;
	
	public JBPSIndividual(Individual individual) {
		this.individual = individual;
	}
	
	public String getURI() {
		return individual.getURI();
	}
	
	@Override
	public String toString() {
		String label = individual.getLabel(null);
		if (isNullOrEmpty(label)) {
			return individual.getURI();
		}
		return label;
	}
	
}
