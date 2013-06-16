package it.polimi.bpmn.simulation;

import static it.polimi.utils.ObjectUtils.isNull;
import it.polimi.actions.Action;
import it.polimi.actions.PropertyAssignment;

import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class Simulator {
	
	protected final OntModel bpmnOntologyModel;
	protected final OntModel modelOntologyModel;
	
	public Simulator(OntModel bpmnOntologyModel, OntModel modelOntologyModel) {
		this.bpmnOntologyModel = bpmnOntologyModel;
		this.modelOntologyModel = modelOntologyModel;
	}
	
	public void execute(List<Action> actions) {
		for (Action action : actions) { execute(action); }
	}
	
	public void execute(Action action) {
		OntClass ontClass = modelOntologyModel.createClass(action.getClassURI());
		
		Individual individual;
		if(isNull(action.getIndividualURI())) { individual = ontClass.createIndividual(); }
		else { individual = ontClass.createIndividual(action.getIndividualURI()); }
		
		for(PropertyAssignment propertyAssignment : action.getActions()) {
			makePropertyAssignment(individual, propertyAssignment);
		}
	}
	
	protected void makePropertyAssignment(Individual individual, PropertyAssignment propertyAssignment) {
		Property property = modelOntologyModel.getProperty(propertyAssignment.getPropertyURI());
		
		RDFNode value;
		String propertyValue = propertyAssignment.getPropertyValue();
		
		if (propertyAssignment.isObjectProperty()) { value =  modelOntologyModel.getIndividual(propertyValue); }
		else { value = modelOntologyModel.createLiteral(propertyValue); }
		
		individual.setPropertyValue(property, value);
	}	
}
