package it.polimi.jbps.bpmn.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import it.polimi.jbps.entities.SimulationState;
import it.polimi.jbps.entities.SimulationTransition;
import it.polimi.jbps.exception.BPMNInvalidTransition;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

public abstract class SimulatorTest {
	
	private final static String createPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#createPurchaseOrder";
	private final static String authorizePurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#authorizePurchaseOrder";
	private final static String endPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#endPurchaseOrder";
	private final static String changePurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#changePurchaseOrder";
	
	private final static String sfRequestAuthorizationURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfRequestAuthorization";
	private final static String sfAuthorizePurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfAuthorizePurchaseOrder";
	private final static String sfRejectPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfRejectPurchaseOrder";
	
	protected abstract String getResource(String resourceName);
	protected abstract Simulator getSimulator(String resource) throws IOException;
	
	@Test
	public void getStateFromURIWhenExists() throws IOException {
		Simulator simulator = getSimulator(getResource("SimplePurchaseRequestBPMN"));
		
		SimulationState state = simulator.getStateFromURI(createPurchaseOrderURI);
		assertEquals(createPurchaseOrderURI, state.getStateURI());
	}
	
	@Test
	public void getStateFromURIWhenNotExists() throws IOException {
		Simulator simulator = getSimulator(getResource("SimplePurchaseRequestBPMN"));
		
		SimulationState state = simulator.getStateFromURI("notExistingURIInModel");
		assertNull(state);
	}
	
	@Test
	public void getTransitionFromURIWhenExists() throws IOException {
		Simulator simulator = getSimulator(getResource("SimplePurchaseRequestBPMN"));
		
		SimulationTransition transition = simulator.getTransitionFromURI(sfRequestAuthorizationURI);
		assertEquals(sfRequestAuthorizationURI, transition.getTransitionURI());
	}
	
	@Test
	public void getTransitionFromURIWhenNotExists() throws IOException {
		Simulator simulator = getSimulator(getResource("SimplePurchaseRequestBPMN"));
		
		SimulationTransition transition = simulator.getTransitionFromURI("notExistingURIInModel");
		assertNull(transition);
	}
	
	@Test
	public void correctlyStartSimulation() throws IOException {
		Simulator simulator = getSimulator(getResource("SimplePurchaseRequestBPMN"));
		
		SimulationState createPurchaseOrder = simulator.getStateFromURI(createPurchaseOrderURI);
		SimulationState startSimulation = simulator.startSimulation();
		
		assertEquals(createPurchaseOrder.getStateURI(), startSimulation.getStateURI());
	}
	
	@Test
	public void correctlyStartSimulationWithoutInitialization() throws IOException {
		Simulator simulator = getSimulator(getResource("SimplePurchaseRequestBPMN"));
		
		SimulationState startSimulation = simulator.startSimulation();
		assertNotNull(startSimulation);
	}
	
	@Test
	public void getNextStatesWithOneTransition() throws IOException {
		Simulator simulator = getSimulator(getResource("SimplePurchaseRequestBPMN"));
		
		SimulationState state = simulator.getStateFromURI(createPurchaseOrderURI);
		Map<SimulationTransition, SimulationState> nextStates = simulator.getNextStates(state);
		
		SimulationTransition sfRequestAuthorization = simulator.getTransitionFromURI(sfRequestAuthorizationURI);
		SimulationState authorizePurchaseOrder = simulator.getStateFromURI(authorizePurchaseOrderURI);
		
		assertTrue(nextStates.containsKey(sfRequestAuthorization));
		assertEquals(authorizePurchaseOrder.getStateURI(), nextStates.get(sfRequestAuthorization).getStateURI());
	}
	
	@Test
	public void getNextStatesWithTwoTransitions() throws IOException {
		Simulator simulator = getSimulator(getResource("SimplePurchaseRequestBPMN"));
		
		SimulationState state = simulator.getStateFromURI(authorizePurchaseOrderURI);
		Map<SimulationTransition, SimulationState> nextStates = simulator.getNextStates(state);
		
		SimulationTransition sfAuthorizePurchaseOrder = simulator.getTransitionFromURI(sfAuthorizePurchaseOrderURI);
		SimulationTransition sfRejectPurchaseOrder = simulator.getTransitionFromURI(sfRejectPurchaseOrderURI);
		
		assertTrue(nextStates.containsKey(sfAuthorizePurchaseOrder));
		assertTrue(nextStates.containsKey(sfRejectPurchaseOrder));
		
		SimulationState endPurchaseOrder = simulator.getStateFromURI(endPurchaseOrderURI);
		SimulationState changePurchaseOrder = simulator.getStateFromURI(changePurchaseOrderURI);
		
		assertEquals(endPurchaseOrder.getStateURI(), nextStates.get(sfAuthorizePurchaseOrder).getStateURI());
		assertEquals(changePurchaseOrder.getStateURI(), nextStates.get(sfRejectPurchaseOrder).getStateURI());
	}
	
	@Test
	public void isEndStateOnFinalState() throws IOException {
		Simulator simulator = getSimulator(getResource("SimplePurchaseRequestBPMN"));
		
		SimulationState state = simulator.getStateFromURI(endPurchaseOrderURI);
		
		assertTrue(simulator.isEndState(state));
	}
	
	@Test
	public void isEndStateOnNotFinalState() throws IOException {
		Simulator simulator = getSimulator(getResource("SimplePurchaseRequestBPMN"));
		
		SimulationState state = simulator.getStateFromURI(authorizePurchaseOrderURI);
		
		assertFalse(simulator.isEndState(state));
	}
	
	@Test
	public void moveToNextStateMoves() throws BPMNInvalidTransition, IOException {
		Simulator simulator = getSimulator(getResource("SimplePurchaseRequestBPMN"));
		
		SimulationState createPurchaseOrder = simulator.getStateFromURI(createPurchaseOrderURI);
		SimulationTransition sfRequestAuthorization = simulator.getTransitionFromURI(sfRequestAuthorizationURI);
		
		SimulationState state = simulator.move(createPurchaseOrder, sfRequestAuthorization);
		
		SimulationState authorizePurchaseOrder = simulator.getStateFromURI(authorizePurchaseOrderURI);
		assertEquals(authorizePurchaseOrder.getStateURI(), state.getStateURI());
	}
	
	@Test(expected = BPMNInvalidTransition.class)
	public void moveToNextStateThrowException() throws BPMNInvalidTransition, IOException {
		Simulator simulator = getSimulator(getResource("SimplePurchaseRequestBPMN"));
		
		SimulationState createPurchaseOrder = simulator.getStateFromURI(createPurchaseOrderURI);
		SimulationTransition sfAuthorizePurchaseOrder = simulator.getTransitionFromURI(sfAuthorizePurchaseOrderURI);
		
		simulator.move(createPurchaseOrder, sfAuthorizePurchaseOrder);
	}

}
