package it.polimi.jbps.engine;

import it.polimi.actions.Action;
import it.polimi.actions.ActionType;
import it.polimi.actions.PropertyAssignment;
import it.polimi.bpmn.simulation.SimulationState;
import it.polimi.bpmn.simulation.Simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ConsoleEngine {
	
	private final Simulator simulator;
	private SimulationState currentState;
	
	public ConsoleEngine(Simulator simulator) {
		this.simulator = simulator;
	}
	
	public void run() throws IOException {
		currentState = simulator.startSimulation();
		
		while(simulator.isEndState(currentState)) {
			System.out.println(String.format("In state %s", currentState));
			List<Action> actions = simulator.getActions(currentState);
			
			Action action = actions.get(0);
			ActionType actionType = action.getActionType();
			System.out.println(String.format("Action: %s", actionType));
			
			List<PropertyAssignment> propertiesAssignment = action.getActions();
			for (PropertyAssignment propertyAssignment : propertiesAssignment) {
				
				BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
				String input = bufferRead.readLine();
				
			}
		}
		
		System.out.println("End State reached, stopping engine.");
		System.out.println(String.format("End State: %s", currentState));
	}
	
	
	
}
