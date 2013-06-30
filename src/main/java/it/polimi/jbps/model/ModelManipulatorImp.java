package it.polimi.jbps.model;

import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.PropertyAssignment;
import it.polimi.jbps.bpmn.simulation.SimulationState;
import it.polimi.jbps.exception.InvalidPropertyAssignment;
import it.polimi.jbps.form.Form;

import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

public class ModelManipulatorImp implements ModelManipulator {
	
	private final OntModel ontologyModel;
	private final Form form;
	
	public ModelManipulatorImp(OntModel ontologyModel, Form form) {
		this.ontologyModel = ontologyModel;
		this.form = form;
	}
	
	@Override
	public List<Action> getActions(SimulationState state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(List<Action> actions) throws InvalidPropertyAssignment {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Individual> getPossibleAssignments(PropertyAssignment propertyAssignment) {
		// TODO Auto-generated method stub
		return null;
	}

}
