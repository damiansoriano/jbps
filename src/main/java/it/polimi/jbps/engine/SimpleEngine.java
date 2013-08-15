package it.polimi.jbps.engine;

import static it.polimi.jbps.utils.ObjectUtils.isNotNull;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.PropertyAssignment;
import it.polimi.jbps.bpmn.simulation.Simulator;
import it.polimi.jbps.entities.Context;
import it.polimi.jbps.entities.JBPSIndividual;
import it.polimi.jbps.entities.SimulationState;
import it.polimi.jbps.entities.SimulationTransition;
import it.polimi.jbps.exception.BPMNInvalidTransition;
import it.polimi.jbps.exception.InvalidPropertyAssignment;
import it.polimi.jbps.model.ModelFacade;
import it.polimi.jbps.model.ModelManipulator;
import it.polimi.jbps.utils.ListUtils;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class SimpleEngine implements Engine {

	private final Simulator simulator;
	private final ModelManipulator manipulator;
	private final ModelFacade modelFacade;
	
	private final Function<Individual, JBPSIndividual> func =
		new Function<Individual, JBPSIndividual>() {
			@Override
			public JBPSIndividual apply(Individual individual) {
				return new JBPSIndividual(individual);
			}
		};

	public SimpleEngine(Simulator simulator, ModelManipulator manipulator, ModelFacade modelFacade) {
		this.simulator = simulator;
		this.manipulator = manipulator;
		this.modelFacade = modelFacade;
	}

	@Override
	public SimulationState startSimulation() {
		return simulator.startSimulation();
	}

	@Override
	public List<Action> getActionsWithPossibleAssignments(SimulationState state, Context context) {
		List<Action> actions = manipulator.getActions(state);

		Individual individual = null;
		
		for (Action action : actions) {
			
			if (context.getVariables().containsKey(action.getVariableName())) {
				individual = modelFacade.getIndividual(context.getVariables().get(action.getVariableName())).getIndividual();
			}
			
			for (PropertyAssignment propertyAssignment : action.getPropertyAssignments()) {
				propertyAssignment.setPossibleAssignments(
						ListUtils.map(func, manipulator.getPossibleAssignments(propertyAssignment)));
				
				if (isNotNull(individual)) {
					Property property = modelFacade.getProperty(propertyAssignment.getPropertyURI()).getOntProperty();
					RDFNode propertyValue = individual.getPropertyValue(property);
					if (isNotNull(propertyValue)) {
						propertyAssignment.setPropertyValue(propertyValue.asResource().getURI());
					}
				}
			}
		}

		return actions;
	}

	@Override
	public Map<SimulationTransition, SimulationState> getPossibleTransitions(SimulationState state) {
		return simulator.getNextStates(state);
	}

	@Override
	public SimulationState makeTransition(SimulationState state, Map<String, String> assignments, String transitionURI, Context context)
			throws InvalidPropertyAssignment, BPMNInvalidTransition {
		
		List<Action> actions = manipulator.getActions(state);
		for (Action action : actions) {
			for (PropertyAssignment propertyAssignment : action.getPropertyAssignments()) {
				if (assignments.containsKey(propertyAssignment.getPropertyURI())) {
					String assignmentValue = assignments.get(propertyAssignment.getPropertyURI());
					propertyAssignment.setPropertyValue(assignmentValue);
				} else {
					propertyAssignment.setPropertyValue(null);
				}
			}
		}
		
		context = manipulator.execute(actions, context);
		
		SimulationTransition transition = simulator.getTransitionFromURI(transitionURI);
		SimulationState newState = simulator.move(state, transition);
		
		return newState;
	}

	@Override
	public boolean isEndState(SimulationState state) {
		return simulator.isEndState(state);
	}

}
