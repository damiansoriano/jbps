package it.polimi.jbps.web.controller;

import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.ActionType;
import it.polimi.jbps.actions.PropertyAssignment;
import it.polimi.jbps.bpmn.simulation.OntologySimulator;
import it.polimi.jbps.bpmn.simulation.Simulator;
import it.polimi.jbps.engine.Engine;
import it.polimi.jbps.engine.SimpleEngine;
import it.polimi.jbps.entities.SimulationState;
import it.polimi.jbps.entities.SimulationTransition;
import it.polimi.jbps.exception.BPMNInvalidTransition;
import it.polimi.jbps.form.Form;
import it.polimi.jbps.form.FormsConfiguration;
import it.polimi.jbps.model.ModelFacade;
import it.polimi.jbps.model.ModelManipulator;
import it.polimi.jbps.model.OntologyModelFacade;
import it.polimi.jbps.model.OntologyModelManipulator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mindswap.pellet.utils.Pair;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

import com.hp.hpl.jena.ontology.OntModel;

public class EngineControllerTest {
	
	private final String bpmnOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestBPMN.owl";
	private final String baseURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl";

	private final String modelOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestModel.owl";
	private final String inputDataExampleWithVariables = "./src/test/resources/it/polimi/bpmn/simulation/inputDataExampleWithVariables.json";
	
	private final String sfRequestAuthorizationURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfRequestAuthorization";
	private final String sfRejectPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfRejectPurchaseOrder";
	private final String sfRequestAuthorization2URI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfRequestAuthorization2";
	private final String sfAuthorizePurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfAuthorizePurchaseOrder";
	
	private final String damianURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#damian";
	private final String employeeURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#employee";
	
	private final String purchaseRequestClientURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestClient";
	private final String purchaseRequestResponsibleURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestResponsible";
	
	private final String notExprectedPropertyAssignment = "%s does not match expected properties.";
	private final String notExprectedTransition = "%s does not match expected transition.";
	
	protected Pair<Engine, ModelFacade> getControllers() throws IOException {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		Simulator simulator = new OntologySimulator(bpmnOntology);
		
		OntModel ontologyModel = getOntologyFromFile(modelOntologyPath);
		Form form = new Form(FormsConfiguration.createFromFile(inputDataExampleWithVariables, ontologyModel));
		ModelManipulator manipulator = new OntologyModelManipulator(ontologyModel, form, baseURI);
		
		ModelFacade modelFacade = new OntologyModelFacade(ontologyModel);
		
		Engine engine = new SimpleEngine(simulator, manipulator, modelFacade);
		
		return new Pair<Engine, ModelFacade>(engine, modelFacade);
	}
	
	public EngineController getEngineController() throws IOException {
		Pair<Engine, ModelFacade> controllers = getControllers();
		
		Map<String, Engine> engines = newHashMap();
		engines.put("lane", controllers.first);
		Map<String, String> lanesDescriptions = newHashMap();
		lanesDescriptions.put("lane", "Lane Description");
		
		return new EngineController(engines, lanesDescriptions);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void startSequenceUpdateAndRestart() throws IOException, BPMNInvalidTransition {
		EngineController engineController = getEngineController();
		
		/*
		 * Start Simulation
		 */
		MockHttpServletRequest request = new MockHttpServletRequest();
		ModelMap model = new ModelMap();
		String viewName = engineController.startSimulation("lane", request, model);
		
		assertEquals("redirect:/lane/currentState", viewName);
		
		/*
		 * Simulation State
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.simulationState("lane", null, request, model);
		
		checkInitialState(viewName, model);
		
		/*
		 * Make Trasition, Request Authorization
		 */
		request = new MockHttpServletRequest();
		request.addParameter("transition", sfRequestAuthorizationURI);
		request.addParameter(purchaseRequestClientURI, damianURI);
		request.addParameter(purchaseRequestResponsibleURI, employeeURI);
		model = new ModelMap();
		viewName = engineController.makeTransition("lane", request, model);
		
		assertEquals("redirect:/lane/currentState", viewName);
		
		/*
		 * Simulation State
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.simulationState("lane", null, request, model);
		
		SimulationState currentState = (SimulationState) model.get("currentState");
		assertEquals("Authorize Purchase Order", currentState.toString());
		List<Action> actions = (List<Action>) model.get("actions");
		assertEquals(0, actions.size());
		
		Map<SimulationTransition, SimulationState> nextStates =
				(Map<SimulationTransition, SimulationState>) model.get("transitions");
		assertEquals(2, nextStates.size());
		for (SimulationTransition key : nextStates.keySet()) {
			if (key.getTransition().toString().equals("Reject Purchase Order")) {
				SimulationState simulationState = nextStates.get(key);
				assertEquals("Change Purchase Order", simulationState.getState().toString());
			} else if (key.getTransition().toString().equals("Authorize Purchase Order")) {
				SimulationState simulationState = nextStates.get(key);
				assertEquals("End Purchase Order", simulationState.getState().toString());
			} else {
				assertTrue(String.format(notExprectedTransition, key.getTransition()), false);
			}
		}
		
		/*
		 * Make Trasition, Reject Purchase Order
		 */
		request = new MockHttpServletRequest();
		request.addParameter("transition", sfRejectPurchaseOrderURI);
		model = new ModelMap();
		viewName = engineController.makeTransition("lane", request, model);
		
		assertEquals("redirect:/lane/currentState", viewName);
		
		/*
		 * Simulation State
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.simulationState("lane", null, request, model);
				
		assertEquals("state", viewName);
		currentState = (SimulationState) model.get("currentState");
		assertEquals("Change Purchase Order", currentState.toString());
		actions = (List<Action>) model.get("actions");
		assertEquals(1, actions.size());
		
		Action action = actions.get(0);
		assertEquals(ActionType.UPDATE, action.getActionType());
		assertEquals("", action.getIndividualURI());
		assertEquals("purchaseOrder", action.getVariableName());
		assertEquals(2, action.getPropertyAssignments().size());
		for (PropertyAssignment proprAss : action.getPropertyAssignments()) {
			if (proprAss.getJbpsProperty().toString().equals("Purchase Request Client")) {
				assertEquals(3, proprAss.getPossibleAssignments().size());
				assertEquals(damianURI, proprAss.getPropertyValue());
			} else if (proprAss.getJbpsProperty().toString().equals("Purchase Request Responsible")) {
				assertEquals(3, proprAss.getPossibleAssignments().size());
				assertEquals(employeeURI, proprAss.getPropertyValue());
			} else {
				assertTrue(String.format(notExprectedPropertyAssignment, proprAss.getJbpsProperty()), false);
			}
		}
		
		nextStates = (Map<SimulationTransition, SimulationState>) model.get("transitions");
		assertEquals(1, nextStates.size());
		for (SimulationTransition key : nextStates.keySet()) {
			if (key.getTransition().toString().equals("Request Authorization")) {
				SimulationState simulationState = nextStates.get(key);
				assertEquals("Authorize Purchase Order", simulationState.getState().toString());
			} else {
				assertTrue(String.format(notExprectedTransition, key.getTransition()), false);
			}
		}
		
		/*
		 * Start Simulation
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.startSimulation("lane", request, model);
		
		assertEquals("redirect:/lane/currentState", viewName);
		
		/*
		 * Simulation State
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.simulationState("lane", null, request, model);
		
		checkInitialState(viewName, model);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void startSequenceUpdateAndSetPropertyAssigmentsToNull() throws IOException, BPMNInvalidTransition {
		EngineController engineController = getEngineController();
		
		/*
		 * Start Simulation
		 */
		MockHttpServletRequest request = new MockHttpServletRequest();
		ModelMap model = new ModelMap();
		String viewName = engineController.startSimulation("lane", request, model);
		
		assertEquals("redirect:/lane/currentState", viewName);
		
		/*
		 * Simulation State
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.simulationState("lane", null, request, model);
		
		checkInitialState(viewName, model);
		
		/*
		 * Make Trasition, Request Authorization
		 */
		request = new MockHttpServletRequest();
		request.addParameter("transition", sfRequestAuthorizationURI);
		request.addParameter(purchaseRequestClientURI, damianURI);
		request.addParameter(purchaseRequestResponsibleURI, employeeURI);
		model = new ModelMap();
		viewName = engineController.makeTransition("lane", request, model);
		
		assertEquals("redirect:/lane/currentState", viewName);
		
		/*
		 * Simulation State
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.simulationState("lane", null, request, model);
		
		SimulationState currentState = (SimulationState) model.get("currentState");
		assertEquals("Authorize Purchase Order", currentState.toString());
		
		/*
		 * Make Trasition, Reject Purchase Order
		 */
		request = new MockHttpServletRequest();
		request.addParameter("transition", sfRejectPurchaseOrderURI);
		model = new ModelMap();
		viewName = engineController.makeTransition("lane", request, model);
		
		assertEquals("redirect:/lane/currentState", viewName);
		
		/*
		 * Simulation State
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.simulationState("lane", null, request, model);
		
		List<Action> actions = (List<Action>) model.get("actions");
		assertEquals(1, actions.size());
		
		Action action = actions.get(0);
		assertEquals(ActionType.UPDATE, action.getActionType());
		assertEquals(2, action.getPropertyAssignments().size());
		for (PropertyAssignment proprAss : action.getPropertyAssignments()) {
			if (proprAss.getJbpsProperty().toString().equals("Purchase Request Client")) {
				assertEquals(3, proprAss.getPossibleAssignments().size());
				assertEquals(damianURI, proprAss.getPropertyValue());
			} else if (proprAss.getJbpsProperty().toString().equals("Purchase Request Responsible")) {
				assertEquals(3, proprAss.getPossibleAssignments().size());
				assertEquals(employeeURI, proprAss.getPropertyValue());
			} else {
				assertTrue(String.format(notExprectedPropertyAssignment, proprAss.getJbpsProperty()), false);
			}
		}
		
		/*
		 * Make Trasition, Request Authorization 2
		 */
		request = new MockHttpServletRequest();
		request.addParameter("transition", sfRequestAuthorization2URI);
		request.addParameter(purchaseRequestClientURI, damianURI);
		model = new ModelMap();
		viewName = engineController.makeTransition("lane", request, model);
		
		assertEquals("redirect:/lane/currentState", viewName);
		
		/*
		 * Simulation State
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.simulationState("lane", null, request, model);
		
		currentState = (SimulationState) model.get("currentState");
		assertEquals("Authorize Purchase Order", currentState.toString());
		
		/*
		 * Make Trasition, Reject Purchase Order
		 */
		request = new MockHttpServletRequest();
		request.addParameter("transition", sfRejectPurchaseOrderURI);
		model = new ModelMap();
		viewName = engineController.makeTransition("lane", request, model);
		
		assertEquals("redirect:/lane/currentState", viewName);
		
		/*
		 * Simulation State
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.simulationState("lane", null, request, model);
		
		actions = (List<Action>) model.get("actions");
		assertEquals(1, actions.size());
		
		action = actions.get(0);
		assertEquals(ActionType.UPDATE, action.getActionType());
		assertEquals(2, action.getPropertyAssignments().size());
		for (PropertyAssignment proprAss : action.getPropertyAssignments()) {
			if (proprAss.getJbpsProperty().toString().equals("Purchase Request Client")) {
				assertEquals(3, proprAss.getPossibleAssignments().size());
				assertEquals(damianURI, proprAss.getPropertyValue());
			} else if (proprAss.getJbpsProperty().toString().equals("Purchase Request Responsible")) {
				assertEquals(3, proprAss.getPossibleAssignments().size());
				assertNull(proprAss.getPropertyValue());
			} else {
				assertTrue(String.format(notExprectedPropertyAssignment, proprAss.getJbpsProperty()), false);
			}
		}
	}
	
	@Test
	public void startAndFinish() throws IOException, BPMNInvalidTransition {
		EngineController engineController = getEngineController();
		
		/*
		 * Start Simulation
		 */
		MockHttpServletRequest request = new MockHttpServletRequest();
		ModelMap model = new ModelMap();
		String viewName = engineController.startSimulation("lane", request, model);
		
		assertEquals("redirect:/lane/currentState", viewName);
		
		/*
		 * Simulation State
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.simulationState("lane", null, request, model);
		
		checkInitialState(viewName, model);
		
		/*
		 * Make Trasition, Request Authorization
		 */
		request = new MockHttpServletRequest();
		request.addParameter("transition", sfRequestAuthorizationURI);
		request.addParameter(purchaseRequestClientURI, damianURI);
		request.addParameter(purchaseRequestResponsibleURI, employeeURI);
		model = new ModelMap();
		viewName = engineController.makeTransition("lane", request, model);
		
		assertEquals("redirect:/lane/currentState", viewName);
		
		/*
		 * Simulation State
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.simulationState("lane", null, request, model);
		
		SimulationState currentState = (SimulationState) model.get("currentState");
		assertEquals("Authorize Purchase Order", currentState.toString());
		
		/*
		 * Make Trasition, Reject Purchase Order
		 */
		request = new MockHttpServletRequest();
		request.addParameter("transition", sfAuthorizePurchaseOrderURI);
		model = new ModelMap();
		viewName = engineController.makeTransition("lane", request, model);
		
		assertEquals("redirect:/lane/currentState", viewName);
		
		/*
		 * Simulation State
		 */
		request = new MockHttpServletRequest();
		model = new ModelMap();
		viewName = engineController.simulationState("lane", null, request, model);
				
		assertEquals("successfullyFinished", viewName);
		currentState = (SimulationState) model.get("currentState");
		assertNull(currentState);
	}
	
	@SuppressWarnings("unchecked")
	public void checkInitialState(String viewName, ModelMap model) {
		assertEquals("state", viewName);
		SimulationState currentState = (SimulationState) model.get("currentState");
		assertEquals("Create Purchase Order", currentState.toString());
		List<Action> actions = (List<Action>) model.get("actions");
		assertEquals(1, actions.size());
		
		Action action = actions.get(0);
		assertEquals(ActionType.INSERT, action.getActionType());
		assertEquals("", action.getIndividualURI());
		assertEquals("purchaseOrder", action.getVariableName());
		assertEquals(2, action.getPropertyAssignments().size());
		for (PropertyAssignment proprAss : action.getPropertyAssignments()) {
			if (proprAss.getJbpsProperty().toString().equals("Purchase Request Client")) {
				assertEquals(3, proprAss.getPossibleAssignments().size());
				assertNull(proprAss.getPropertyValue());
			} else if (proprAss.getJbpsProperty().toString().equals("Purchase Request Responsible")) {
				assertEquals(3, proprAss.getPossibleAssignments().size());
				assertNull(proprAss.getPropertyValue());
			} else {
				assertTrue(String.format(notExprectedPropertyAssignment, proprAss.getJbpsProperty()), false);
			}
		}
		
		Map<SimulationTransition, SimulationState> nextStates =
				(Map<SimulationTransition, SimulationState>) model.get("transitions");
		assertEquals(1, nextStates.size());
		for (SimulationTransition key : nextStates.keySet()) {
			if (key.getTransition().toString().equals("Request Authorization")) {
				SimulationState simulationState = nextStates.get(key);
				assertEquals("Authorize Purchase Order", simulationState.getState().toString());
			} else {
				assertTrue(String.format(notExprectedTransition, key.getTransition()), false);
			}
		}
	}

}
