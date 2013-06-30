package it.polimi.jbps.bpmn.simulation;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.OntologyUtils.getIndividuals;
import static it.polimi.jbps.utils.OntologyUtils.getIndividualsInDomain;
import static it.polimi.jbps.utils.OntologyUtils.getIndividualsInRange;
import it.polimi.jbps.constants.BPMNConstants;
import it.polimi.jbps.exception.BPMNInvalidTransition;

import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;

public class SimulatorImp implements Simulator {
	
	private final OntModel bpmnOntologyModel;
	
	public SimulatorImp(OntModel bpmnOntologyModel) {
		this.bpmnOntologyModel = bpmnOntologyModel;
	}

	@Override
	public SimulationState startSimulation() {
		List<SimulationState> startStates = getStartStates();
		if (startStates.isEmpty()) { return null; }
		
		SimulationState startState = startStates.get(0);
		Map<SimulationTransition, SimulationState> nextStates = getNextStates(startState);
		return nextStates.get(nextStates.keySet().iterator().next());
	}
	
	private List<SimulationState> getStartStates() {
		List<SimulationState> startEvents = newLinkedList();
		for(Individual individual : getIndividuals(bpmnOntologyModel, BPMNConstants.EVENT_START_URI)) {
			startEvents.add(new SimulationState(individual.getURI()));
		}
		return startEvents;
	}

	@Override
	public Map<SimulationTransition, SimulationState> getNextStates(SimulationState state) {
		Map<SimulationTransition, SimulationState> nextStates = newHashMap();
		
		Individual stateIndividual = bpmnOntologyModel.getIndividual(state.getStateURI());
		Property sequenceFlowSource = bpmnOntologyModel.getProperty(BPMNConstants.SEQUENCE_FLOW_SOURCE_URI);
		Property sequenceFlowTarget = bpmnOntologyModel.getProperty(BPMNConstants.SEQUENCE_FLOW_TARGET_URI);
		
		for (Individual seqenceFlow: getIndividualsInDomain(bpmnOntologyModel, sequenceFlowSource, stateIndividual)){
			for(Individual nextStateIndividual : getIndividualsInRange(bpmnOntologyModel, seqenceFlow, sequenceFlowTarget)) {
				nextStates.put(new SimulationTransition(seqenceFlow.getURI()), new SimulationState(nextStateIndividual.getURI()));
			}
		}
		
		return nextStates;
	}

	@Override
	public SimulationState move(SimulationState state, SimulationTransition transition) throws BPMNInvalidTransition {
		Map<SimulationTransition, SimulationState> nextStates = getNextStates(state);
		if (nextStates.containsKey(transition)) {
			return nextStates.get(transition);
		} else {
			throw new BPMNInvalidTransition(String.format("Is not possible to take transition %s from state %s", state, transition));
		}
	}

	@Override
	public boolean isEndState(SimulationState state) {
		List<Individual> endStates = getIndividuals(bpmnOntologyModel, BPMNConstants.EVENT_END_URI);
		Individual endState = bpmnOntologyModel.getIndividual(state.getStateURI());
		return endStates.contains(endState);
	}

	@Override
	public SimulationTransition getTransitionFromURI(String transitionURI) {
		return new SimulationTransition(transitionURI);
	}

	@Override
	public SimulationState getStateFromURI(String stateURI) {
		return new SimulationState(stateURI);
	}
}