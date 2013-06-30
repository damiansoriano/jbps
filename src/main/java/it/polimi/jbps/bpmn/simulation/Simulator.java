package it.polimi.jbps.bpmn.simulation;

import it.polimi.jbps.exception.BPMNInvalidTransition;

import java.util.Map;

public interface Simulator {
	
	SimulationState startSimulation();
	
	Map<SimulationTransition, SimulationState> getNextStates(SimulationState state);
	
	SimulationState move(SimulationState state, SimulationTransition transition) throws BPMNInvalidTransition;
	
	boolean isEndState(SimulationState state);
	
	SimulationState getStateFromURI(String stateURI);
	
	SimulationTransition getTransitionFromURI(String transitionURI);
}
