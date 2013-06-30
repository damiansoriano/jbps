package it.polimi.jbps.bpmn.simulation;

import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import it.polimi.jbps.exception.BPMNInvalidTransition;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public abstract class SimulatorTest {
	
	private final static String bpmnOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestBPMN.owl";
	
	private final static String createPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#createPurchaseOrder";
	private final static String authorizePurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#authorizePurchaseOrder";
	private final static String endPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#endPurchaseOrder";
	private final static String changePurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#changePurchaseOrder";
	
	private final static String sfRequestAuthorizationURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfRequestAuthorization";
	private final static String sfAuthorizePurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfAuthorizePurchaseOrder";
	private final static String sfRejectPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfRejectPurchaseOrder";
	
	protected abstract Simulator getSimulator(OntModel bpmnOntologyModel);
	
	@Test
	public void getStateFromURIWhenExists() {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState state = simulator.getStateFromURI(createPurchaseOrderURI);
		assertEquals(createPurchaseOrderURI, state.getStateURI());
	}
	
	@Test
	@Ignore
	public void getStateFromURIWhenNotExists() {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState state = simulator.getStateFromURI("notExistingURIInModel");
		assertNull(state);
	}
	
	@Test
	public void getTransitionFromURIWhenExists() {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationTransition transition = simulator.getTransitionFromURI(sfRequestAuthorizationURI);
		assertEquals(sfRequestAuthorizationURI, transition.getTransitionURI());
	}
	
	@Test
	@Ignore
	public void getTransitionFromURIWhenNotExists() {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationTransition transition = simulator.getTransitionFromURI("notExistingURIInModel");
		assertNull(transition);
	}
	
	@Test
	public void correctlyStartSimulation() {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState createPurchaseOrder = simulator.getStateFromURI(createPurchaseOrderURI);
		SimulationState startSimulation = simulator.startSimulation();
		
		assertEquals(createPurchaseOrder, startSimulation);
	}
	
	@Test
	public void correctlyStartSimulationWithoutInitialization() {
		OntModel bpmnOntology = ModelFactory.createOntologyModel();
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState startSimulation = simulator.startSimulation();
		assertNull(startSimulation);
	}
	
	@Test
	public void getNextStatesWithOneTransition() {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState state = simulator.getStateFromURI(createPurchaseOrderURI);
		Map<SimulationTransition, SimulationState> nextStates = simulator.getNextStates(state);
		
		SimulationTransition sfRequestAuthorization = simulator.getTransitionFromURI(sfRequestAuthorizationURI);
		SimulationState authorizePurchaseOrder = simulator.getStateFromURI(authorizePurchaseOrderURI);
		
		assertTrue(nextStates.containsKey(sfRequestAuthorization));
		assertEquals(authorizePurchaseOrder, nextStates.get(sfRequestAuthorization));
	}
	
	@Test
	public void getNextStatesWithTwoTransitions() {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState state = simulator.getStateFromURI(authorizePurchaseOrderURI);
		Map<SimulationTransition, SimulationState> nextStates = simulator.getNextStates(state);
		
		SimulationTransition sfAuthorizePurchaseOrder = simulator.getTransitionFromURI(sfAuthorizePurchaseOrderURI);
		SimulationTransition sfRejectPurchaseOrder = simulator.getTransitionFromURI(sfRejectPurchaseOrderURI);
		
		assertTrue(nextStates.containsKey(sfAuthorizePurchaseOrder));
		assertTrue(nextStates.containsKey(sfRejectPurchaseOrder));
		
		SimulationState endPurchaseOrder = simulator.getStateFromURI(endPurchaseOrderURI);
		SimulationState changePurchaseOrder = simulator.getStateFromURI(changePurchaseOrderURI);
		
		assertEquals(endPurchaseOrder, nextStates.get(sfAuthorizePurchaseOrder));
		assertEquals(changePurchaseOrder, nextStates.get(sfRejectPurchaseOrder));
	}
	
	@Test
	public void isEndStateOnFinalState() {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState state = simulator.getStateFromURI(endPurchaseOrderURI);
		
		assertTrue(simulator.isEndState(state));
	}
	
	@Test
	public void isEndStateOnNotFinalState() {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState state = simulator.getStateFromURI(authorizePurchaseOrderURI);
		
		assertFalse(simulator.isEndState(state));
	}
	
	@Test
	public void moveToNextStateMoves() throws BPMNInvalidTransition {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState createPurchaseOrder = simulator.getStateFromURI(createPurchaseOrderURI);
		SimulationTransition sfRequestAuthorization = simulator.getTransitionFromURI(sfRequestAuthorizationURI);
		
		SimulationState state = simulator.move(createPurchaseOrder, sfRequestAuthorization);
		
		SimulationState authorizePurchaseOrder = simulator.getStateFromURI(authorizePurchaseOrderURI);
		assertEquals(authorizePurchaseOrder, state);
	}
	
	@Test(expected = BPMNInvalidTransition.class)
	public void moveToNextStateThrowException() throws BPMNInvalidTransition {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState createPurchaseOrder = simulator.getStateFromURI(createPurchaseOrderURI);
		SimulationTransition sfAuthorizePurchaseOrder = simulator.getTransitionFromURI(sfAuthorizePurchaseOrderURI);
		
		simulator.move(createPurchaseOrder, sfAuthorizePurchaseOrder);
	}

}
