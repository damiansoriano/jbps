package it.polimi.jbps.engine;

import static com.google.common.collect.Lists.newLinkedList;
import static it.polimi.jbps.utils.ObjectUtils.not;
import it.polimi.actions.Action;
import it.polimi.actions.ActionType;
import it.polimi.actions.PropertyAssignment;
import it.polimi.bpmn.simulation.SimulationState;
import it.polimi.bpmn.simulation.SimulationTransition;
import it.polimi.bpmn.simulation.Simulator;
import it.polimi.jbps.exception.BPMNInvalidTransition;
import it.polimi.jbps.exception.InvalidPropertyAssignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.Individual;

public class ConsoleEngine {
	
	private final Simulator simulator;
	private SimulationState currentState;
	
	public ConsoleEngine(Simulator simulator) {
		this.simulator = simulator;
	}
	
	public void run() throws IOException, BPMNInvalidTransition {
		currentState = simulator.startSimulation();
		
		while(not(simulator.isEndState(currentState))) {
			System.out.println(String.format("In state %s", currentState));
			System.out.println();
			List<Action> actions = simulator.getActions(currentState);
			
			for (Action action : actions) {
				ActionType actionType = action.getActionType();
				System.out.println(String.format("Action: %s", actionType));
				
				List<PropertyAssignment> propertiesAssignment = action.getActions();
				
				List<PropertyAssignment> propertiesAssignmentToApply = newLinkedList();
				for (PropertyAssignment propertyAssignment : propertiesAssignment) {
					System.out.println();
					System.out.println(String.format("Setting property %s",propertyAssignment.getPropertyURI()));
					
					List<Individual> possibleAssignments = simulator.getPossibleAssignments(propertyAssignment);
					
					System.out.println("Select one from possible assignments:");
					for(Individual possibleAssignment : possibleAssignments) {
						System.out.println(String.format("\t %s",possibleAssignment.getURI()));
					}
					
					System.out.println();
					System.out.print("> ");
					BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
					String input = bufferRead.readLine();
					System.out.println();
					
					PropertyAssignment PropertyAssignmentToApply = (PropertyAssignment) propertyAssignment.clone();
					PropertyAssignmentToApply.setPropertyValue(input);
					propertiesAssignmentToApply.add(PropertyAssignmentToApply);
				}
				
				
				Action actionToApply = (Action) action.clone();
				actionToApply.setActions(propertiesAssignmentToApply);
				try {
					simulator.execute(actionToApply);
				} catch (InvalidPropertyAssignment e) {
					System.out.println("Error occure while setting properties");
					e.printStackTrace();
					return;
				}
			}
			
			Map<SimulationTransition, SimulationState> nextStates = simulator.getNextStates(currentState);
			
			if (nextStates.keySet().isEmpty()) {
				System.out.println("No transition was found in this state and this is not a terminal state. The BPMN Ontology may be badly defined.");
				System.out.println("Exiting simulation");
				return;
			}
			
			System.out.println("Select the transition:");
			for (SimulationTransition transition : nextStates.keySet()) {
				System.out.println(String.format("\t %s",transition.getTransitionURI()));
			}
			System.out.println();
			System.out.print("> ");
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			String inputTransition = bufferRead.readLine();
			System.out.println();
			
			SimulationTransition transition = new SimulationTransition(inputTransition);
			currentState = simulator.move(currentState, transition);
		}
		
		System.out.println("End State reached, stopping engine.");
		System.out.println(String.format("End State: %s", currentState));
	}
	
	
	
}
