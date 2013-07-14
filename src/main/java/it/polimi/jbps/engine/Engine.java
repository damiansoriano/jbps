package it.polimi.jbps.engine;

import it.polimi.jbps.actions.Action;
import it.polimi.jbps.entities.SimulationState;
import it.polimi.jbps.entities.SimulationTransition;
import it.polimi.jbps.exception.BPMNInvalidTransition;
import it.polimi.jbps.exception.InvalidPropertyAssignment;

import java.util.List;
import java.util.Map;

public interface Engine {

	SimulationState startSimulation();
	
	List<Action> getActionsWithPossibleAssignments(SimulationState state);
	
	Map<SimulationTransition, SimulationState> getPossibleTransitions(SimulationState state);
	
	SimulationState makeTransition(SimulationState state, Map<String, String> assignments, String transitionURI)
			throws InvalidPropertyAssignment, BPMNInvalidTransition;
	
	boolean isEndState(SimulationState state);
}
