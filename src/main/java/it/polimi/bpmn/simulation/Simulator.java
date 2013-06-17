package it.polimi.bpmn.simulation;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.utils.ObjectUtils.isNull;
import static it.polimi.utils.OntologyUtils.getIndividuals;
import static it.polimi.utils.OntologyUtils.getIndividualsInDomain;
import static it.polimi.utils.OntologyUtils.getIndividualsInRange;
import it.polimi.actions.Action;
import it.polimi.actions.PropertyAssignment;
import it.polimi.constants.BPMNConstants;
import it.polimi.jbps.exception.BPMNInvalidTransition;

import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class Simulator {
	
	protected final OntModel bpmnOntologyModel;
	protected final OntModel modelOntologyModel;
	
	public Simulator(OntModel bpmnOntologyModel, OntModel modelOntologyModel) {
		this.bpmnOntologyModel = bpmnOntologyModel;
		this.modelOntologyModel = modelOntologyModel;
	}
	
	public void execute(List<Action> actions) {
		for (Action action : actions) { execute(action); }
	}
	
	public void execute(Action action) {
		OntClass ontClass = modelOntologyModel.createClass(action.getClassURI());
		
		Individual individual;
		if(isNull(action.getIndividualURI())) { individual = ontClass.createIndividual(); }
		else { individual = ontClass.createIndividual(action.getIndividualURI()); }
		
		for(PropertyAssignment propertyAssignment : action.getActions()) {
			makePropertyAssignment(individual, propertyAssignment);
		}
	}
	
	protected void makePropertyAssignment(Individual individual, PropertyAssignment propertyAssignment) {
		Property property = modelOntologyModel.getProperty(propertyAssignment.getPropertyURI());
		
		RDFNode value;
		String propertyValue = propertyAssignment.getPropertyValue();
		
		if (propertyAssignment.isObjectProperty()) { value =  modelOntologyModel.getIndividual(propertyValue); }
		else { value = modelOntologyModel.createLiteral(propertyValue); }
		
		individual.setPropertyValue(property, value);
	}
	
	public SimulationState startSimulation() {
		return getStartStates().get(0);
	}
	
	public List<SimulationState> getStartStates() {
		List<SimulationState> startEvents = newLinkedList();
		
		for(Individual individual : getIndividuals(bpmnOntologyModel, BPMNConstants.EVENT_START_URI)) {
			startEvents.add(new SimulationState(individual.getURI()));
		}
		
		return startEvents;
	}
	
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
	
	public SimulationState move(SimulationState state, SimulationTransition transition) throws BPMNInvalidTransition {
		Map<SimulationTransition, SimulationState> nextStates = getNextStates(state);
		if (nextStates.containsKey(transition)) {
			return nextStates.get(transition);
		} else {
			throw new BPMNInvalidTransition(String.format("Is not possible to take transition %s from state %s", state, transition));
		}
	}

	public boolean isEndState(SimulationState state) {
		List<Individual> endStates = getIndividuals(bpmnOntologyModel, BPMNConstants.EVENT_END_URI);
		Individual endState = bpmnOntologyModel.getIndividual(state.getStateURI());
		return endStates.contains(endState);
	}
	
	
}
