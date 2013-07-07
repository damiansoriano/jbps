package it.polimi.jbps.engine;

import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.PropertyAssignment;
import it.polimi.jbps.bpmn.simulation.SimulationState;
import it.polimi.jbps.bpmn.simulation.SimulationTransition;
import it.polimi.jbps.bpmn.simulation.Simulator;
import it.polimi.jbps.exception.BPMNInvalidTransition;
import it.polimi.jbps.exception.InvalidPropertyAssignment;
import it.polimi.jbps.model.ModelManipulator;

import java.util.List;
import java.util.Map;

public class SimpleEngine implements Engine {

	private final Simulator simulator;
	private final ModelManipulator manipulator;

	public SimpleEngine(Simulator simulator, ModelManipulator manipulator) {
		this.simulator = simulator;
		this.manipulator = manipulator;
	}

	@Override
	public SimulationState startSimulation() {
		return simulator.startSimulation();
	}

	@Override
	public List<Action> getActionsWithPossibleAssignments(SimulationState state) {
		List<Action> actions = manipulator.getActions(state);

		for (Action action : actions) {
			for (PropertyAssignment propertyAssignment : action.getPropertyAssignments()) {
				propertyAssignment.setPossibleAssignments(manipulator.getPossibleAssignments(propertyAssignment));
			}
		}

		return actions;
	}

	@Override
	public Map<SimulationTransition, SimulationState> getPossibleTransitions(SimulationState state) {
		return simulator.getNextStates(state);
	}

	@Override
	public SimulationState makeTransition(SimulationState state, Map<String, String> assignments, String transitionURI)
			throws InvalidPropertyAssignment, BPMNInvalidTransition {
		
		List<Action> actions = manipulator.getActions(state);
		for (Action action : actions) {
			for (PropertyAssignment propertyAssignment : action.getPropertyAssignments()) {
				if (assignments.containsKey(propertyAssignment.getPropertyURI())) {
					String assignmentValue = assignments.get(propertyAssignment.getPropertyURI());
					propertyAssignment.setPropertyValue(assignmentValue);
				}
			}
		}
		
		manipulator.execute(actions);
		
		SimulationTransition transition = simulator.getTransitionFromURI(transitionURI);
		SimulationState newState = simulator.move(state, transition);
		
		return newState;
	}

	@Override
	public boolean isEndState(SimulationState state) {
		return simulator.isEndState(state);
	}

}
