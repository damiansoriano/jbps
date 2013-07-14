package it.polimi.jbps.model;

import static com.google.common.collect.Lists.newLinkedList;
import static it.polimi.jbps.utils.ObjectUtils.isNullOrEmpty;
import static it.polimi.jbps.utils.ObjectUtils.not;
import static it.polimi.jbps.utils.OntologyUtils.getIndividuals;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.PropertyAssignment;
import it.polimi.jbps.entities.SimulationState;
import it.polimi.jbps.exception.InvalidPropertyAssignment;
import it.polimi.jbps.form.Form;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.ValidityReport.Report;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyModelManipulator implements ModelManipulator {
	
	protected final OntModel ontologyModel;
	protected final Form form;
	protected final String baseURIForNameing;
	
	public OntologyModelManipulator(OntModel ontologyModel, Form form, String baseURIForNameing) {
		this.ontologyModel = ontologyModel;
		this.form = form;
		this.baseURIForNameing = baseURIForNameing;
	}
	
	@Override
	public List<Action> getActions(SimulationState state) {
		return form.getActions(state.getStateURI());
	}

	@Override
	public void execute(List<Action> actions) throws InvalidPropertyAssignment {
		OntModel freshModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		freshModel.add(ontologyModel);
		
		for (Action action : actions) { execute(action, freshModel); }
		
		ontologyModel.add(freshModel);
	}
	
	protected void execute(Action action, OntModel freshModel) throws InvalidPropertyAssignment {
		OntClass ontClass = freshModel.getOntClass(action.getClassURI());
		
		Individual individual;
		String uriName;
		if(isNullOrEmpty(action.getIndividualURI())) {
			uriName = String.format("%s/%s", baseURIForNameing, UUID.randomUUID().toString());
		} else {
			uriName = action.getIndividualURI();
		}
		individual = ontClass.createIndividual(uriName);
		
		for(PropertyAssignment propertyAssignment : action.getPropertyAssignments()) {
			makePropertyAssignment(individual, propertyAssignment);
		}
		freshModel.prepare();
		ValidityReport validityReport = freshModel.validate();
		if (not(validityReport.isValid())) {
			String errorMessage = "";
			Iterator<Report> reports = validityReport.getReports();
			while(reports.hasNext()) {
				Report report = reports.next();
				errorMessage += report.toString() + "\n";
			}
			throw new InvalidPropertyAssignment(errorMessage);
		}
		if (not(validityReport.isClean())) {
			String errorMessage = "";
			Iterator<Report> reports = validityReport.getReports();
			while(reports.hasNext()) {
				Report report = reports.next();
				errorMessage += report.toString() + "\n";
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
