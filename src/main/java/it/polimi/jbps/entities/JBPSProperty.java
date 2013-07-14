package it.polimi.jbps.entities;

import static it.polimi.jbps.utils.ObjectUtils.isNullOrEmpty;
import lombok.Getter;

import com.hp.hpl.jena.ontology.OntProperty;

public class JBPSProperty {
	
	@Getter 
	private final OntProperty ontProperty;
	
	public JBPSProperty(OntProperty ontProperty) {
		this.ontProperty = ontProperty;
	}
	
	public String getURI() {
		return ontProperty.getURI();
	}
	
	@Override
	public String toString() {
		String label = ontProperty.getLabel(null);
		if (isNullOrEmpty(label)) {
			return ontProperty.getURI();
		}
		return label;
	}
	
}
