package it.polimi.jbps.entities;

import static it.polimi.jbps.utils.ObjectUtils.isNullOrEmpty;
import lombok.Getter;

import com.hp.hpl.jena.ontology.OntClass;

public class JBPSClass {
	
	@Getter 
	private final OntClass ontClass;
	
	public JBPSClass(OntClass ontClass) {
		this.ontClass = ontClass;
	}
	
	public String getURI() {
		return ontClass.getURI();
	}
	
	@Override
	public String toString() {
		String label = ontClass.getLabel(null);
		if (isNullOrEmpty(label)) {
			return ontClass.getURI();
		}
		return label;
	}
	
}
