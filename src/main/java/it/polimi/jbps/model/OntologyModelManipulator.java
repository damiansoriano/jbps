package it.polimi.jbps.model;

import static com.google.common.collect.Lists.newLinkedList;
import static it.polimi.jbps.utils.ObjectUtils.isNullOrEmpty;
import static it.polimi.jbps.utils.ObjectUtils.not;
import static it.polimi.jbps.utils.OntologyUtils.getIndividuals;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.PropertyAssignment;
import it.polimi.jbps.bpmn.simulation.SimulationState;
import it.polimi.jbps.exception.InvalidPropertyAssignment;
import it.polimi.jbps.form.Form;

import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.ValidityReport.Report;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyModelManipulator implements ModelManipulator {
	
	protected final OntModel ontologyModel;
	protected final Form form;
	
	public OntologyModelManipulator(OntModel ontologyModel, Form form) {
		this.ontologyModel = ontologyModel;
		this.form = form;
	}
	
	@Override
	public List<Action> getActions(SimulationState state) {
		return form.getActions(state.getStateURI());
	}

	@Override
	public void execute(List<Action> actions) throws InvalidPropertyAssignment {
		for (Action action : actions) { execute(action); }
	}
	
	@Override
	public void execute(Action action) throws InvalidPropertyAssignment {
		OntClass ontClass = ontologyModel.getOntClass(action.getClassURI());
		
		Individual individual;
		if(isNullOrEmpty(action.getIndividualURI())) { individual = ontClass.createIndividual(); }
		else { individual = ontClass.createIndividual(action.getIndividualURI()); }
		
		for(PropertyAssignment propertyAssignment : action.getPropertyAssignments()) {
			makePropertyAssignment(individual, propertyAssignment);
		}
		ontologyModel.prepare();
		ValidityReport validityReport = ontologyModel.validate();
		if (not(validityReport.isValid())) {
			String errorMessage = "";
			Iterator<Report> reports = validityReport.getReports();
			while(reports.hasNext()) {
				Report report = reports.next();
				errorMessage += report.getDescription() + "\n";
			}
			throw new InvalidPropertyAssignment(errorMessage);
		}
		if (not(validityReport.isClean())) {
			String errorMessage = "";
			Iterator<Report> reports = validityReport.getReports();
			while(reports.hasNext()) {
				Report report = reports.next();
				errorMessage += report.getDescription() + "\n";
			}
			throw new InvalidPropertyAssignment(errorMessage);
		}	
	}
	
	protected void makePropertyAssignment(Individual individual, PropertyAssignment propertyAssignment) {
		Property property = ontologyModel.getProperty(propertyAssignment.getPropertyURI());
		
		RDFNode value;
		String propertyValue = propertyAssignment.getPropertyValue();
		
		if (isNullOrEmpty(propertyValue)) {
			return;
		}
		
		if (propertyAssignment.isObjectProperty()) { value =  ontologyModel.getIndividual(propertyValue); }
		else { value = ontologyModel.createLiteral(propertyValue); }
		
		individual.setPropertyValue(property, value);
	}

	@Override
	public List<Individual> getPossibleAssignments(PropertyAssignment propertyAssignment) {
		List<Individual> possibleAssignments = newLinkedList();
		
		Property property = ontologyModel.getProperty(propertyAssignment.getPropertyURI());
		OntProperty ontologyProperty = property.as(OntProperty.class);
		
		List<OntResource> ranges = newLinkedList();
		ExtendedIterator<? extends OntResource> listRange = ontologyProperty.listRange();
		while(listRange.hasNext()) { ranges.add(listRange.next()); }
		
		for(Individual individual : getIndividuals(ontologyModel, ontologyProperty.getRange().getURI(), false)) {
			boolean includeIndividual = true;
			for (OntResource resource : ranges) {
				if (resource.isClass()) {
					OntClass asClass = resource.asClass();
					if(not(individual.hasOntClass(asClass, false))) {
						includeIndividual = false;
						break;
					}
					
				}
			}
			if (includeIndividual) { possibleAssignments.add(individual); }
		}
		
		return possibleAssignments;
	}
	
}
