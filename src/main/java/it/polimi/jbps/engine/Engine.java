package it.polimi.jbps.engine;

import java.util.List;
import java.util.Map;

import it.polimi.jbps.actions.Action;
import it.polimi.jbps.bpmn.simulation.SimulationState;
import it.polimi.jbps.bpmn.simulation.SimulationTransition;

public interface Engine {

	SimulationState startSimulation();
	
	List<Action> getActionsWithPossibleAssignments(SimulationState state);
	
	Map<SimulationTransition, SimulationState> getPossibleTransitions(SimulationState state);
	
	SimulationState makeTransition(SimulationState state, Map<String, String> assignments, String transitionURI);
	
	boolean isEndState(SimulationState state);
}
