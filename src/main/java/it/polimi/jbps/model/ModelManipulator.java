package it.polimi.jbps.model;

import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.PropertyAssignment;
import it.polimi.jbps.entities.Context;
import it.polimi.jbps.entities.SimulationState;
import it.polimi.jbps.exception.InvalidPropertyAssignment;

import java.util.List;

import com.hp.hpl.jena.ontology.Individual;

public interface ModelManipulator {
	
	List<Action> getActions(SimulationState state);
	
	Context execute(List<Action> actions, Context context) throws InvalidPropertyAssignment;
	
	List<Individual> getPossibleAssignments(PropertyAssignment propertyAssignment);
}
