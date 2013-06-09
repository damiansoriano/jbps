package it.polimi;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class IndividualManipulation {
	
	protected final OntModel model;
	
	public IndividualManipulation(OntModel model) {
		this.model = model;
	}
	
	public ExtendedIterator<Individual> getAllIndividuals() {
		return model.listIndividuals();
	}
	
	public ExtendedIterator<Individual> getAllIndividuals(OntClass ontClass) {
		return model.listIndividuals(ontClass);
	}
	
}
