package it.polimi.jbps.model;

import java.util.List;

import com.hp.hpl.jena.ontology.Individual;

import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.PropertyAssignment;
import it.polimi.jbps.bpmn.simulation.SimulationState;
import it.polimi.jbps.exception.InvalidPropertyAssignment;

public interface ModelManipulator {
	
	List<Action> getActions(SimulationState state);
	
	void execute(Action action) throws InvalidPropertyAssignment;
	void execute(List<Action> actions) throws InvalidPropertyAssignment;
	
	List<Individual> getPossibleAssignments(PropertyAssignment propertyAssignment);
}
