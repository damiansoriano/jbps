package it.polimi.jbps.bpmn;

import java.io.IOException;

import com.hp.hpl.jena.ontology.OntModel;

public interface BPMN2Ontology {
	
	OntModel createOntology(String string) throws IOException;
	
}
