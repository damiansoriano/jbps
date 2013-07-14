package it.polimi.jbps.model;

import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import it.polimi.jbps.PropertyType;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.ActionType;
import it.polimi.jbps.actions.PropertyAssignment;
import it.polimi.jbps.bpmn.simulation.Simulator;
import it.polimi.jbps.entities.JBPSIndividual;
import it.polimi.jbps.entities.SimulationState;
import it.polimi.jbps.exception.InvalidPropertyAssignment;
import it.polimi.jbps.form.Form;
import it.polimi.jbps.form.FormsConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

public abstract class ModelManipulatorTest {
	
	private final static String bpmnOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestBPMN.owl";
	private final static String modelOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestModel.owl";
	private final static String inputDataExample = "./src/test/resources/it/polimi/bpmn/simulation/inputDataExample.json";
	
	private final static String createPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#createPurchaseOrder";
	
	private final static String purchaseRequestClassURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#PurchaseRequest";
	
	private final static String purchaseRequestClientURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestClient";
	private final static String purchaseRequestResponsibleURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestResponsible";
	
	private final static String purchaseRequest01URI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequest01";
		
	private final static String damianURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#damian";
	private final static String employeeURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#employee";
	
	protected abstract ModelManipulator getModelManipulator(OntModel ontologyModel, Form form);
	protected abstract Simulator getSimulator(OntModel bpmnOntologyModel);
	
	@Test
	public void getActionsReturnsOneAction() throws IOException {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		Map<String, String> map = newHashMap();
		map.put(createPurchaseOrderURI, inputDataExample);
		Form form = new Form(FormsConfiguration.createFromFiles(map, modelOntology));
		
		ModelManipulator manipulator = getModelManipulator(modelOntology, form);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState createPurchaseOrder = simulator.getStateFromURI(createPurchaseOrderURI);
		
		List<Action> actions = manipulator.getActions(createPurchaseOrder);
		
		assertEquals(1, actions.size());
		
		Action action = actions.get(0);
		assertEquals(ActionType.INSERT, action.getActionType());
		assertEquals(purchaseRequestClassURI, action.getClassURI());
		assertEquals("PurchaseRequest", action.getJbpsClass().toString());
		assertEquals(purchaseRequest01URI, action.getIndividualURI());
		
		List<PropertyAssignment> propertyAssignments = action.getPropertyAssignments();
		assertEquals(2, propertyAssignments.size());
		
		
		PropertyAssignment purchaseRequestClient = null;
		PropertyAssignment purchaseRequestResponsible = null;
		for (PropertyAssignment propertyAssignment : propertyAssignments) {
			
			assertEquals(PropertyType.OBJECT_PROPERTY, propertyAssignment.getPropertyType());
			
			if (propertyAssignment.getPropertyURI().equals(purchaseRequestClientURI)) {
				purchaseRequestClient = propertyAssignment;
			} else if (propertyAssignment.getPropertyURI().equals(purchaseRequestResponsibleURI)) {
				purchaseRequestResponsible = propertyAssignment;
			}
		}
		assertNotNull(purchaseRequestClient);
		assertNotNull(purchaseRequestResponsible);
	}
	
	@Test
	public void getPossibleAssignments() throws IOException, InvalidPropertyAssignment {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		Individual prePurchaseRequest01 = modelOntology.getIndividual(purchaseRequest01URI);
		assertNull(prePurchaseRequest01);
		
		
		Map<String, String> map = newHashMap();
		map.put(createPurchaseOrderURI, inputDataExample);
		Form form = new Form(FormsConfiguration.createFromFiles(map, modelOntology));
		
		ModelManipulator manipulator = getModelManipulator(modelOntology, form);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState createPurchaseOrder = simulator.getStateFromURI(createPurchaseOrderURI);
		
		List<Action> actions = manipulator.getActions(createPurchaseOrder);
		Action action = actions.get(0);
		List<PropertyAssignment> propertyAssignments = action.getPropertyAssignments();
		
		
		for (PropertyAssignment propertyAssignment : propertyAssignments) {
			if (propertyAssignment.getPropertyURI().equals(purchaseRequestClientURI)) {
				List<Individual> possibleAssignments = manipulator.getPossibleAssignments(propertyAssignment);
				assertEquals(2, possibleAssignments.size());
				
				Individual damianIndividual = null;
				Individual employeeIndividual = null;
				
				for (Individual individual : possibleAssignments) {
					if (damianURI.equals(individual.getURI())) {
						damianIndividual = individual;
					} else if (employeeURI.equals(individual.getURI())) {
						employeeIndividual = individual;
					}
				}
				
				assertNotNull(damianIndividual);
				assertNotNull(employeeIndividual);
				
				propertyAssignment.setPropertyValue(damianIndividual.getURI());
				
			} else if (propertyAssignment.getPropertyURI().equals(purchaseRequestResponsibleURI)) {
				List<Individual> possibleAssignments = manipulator.getPossibleAssignments(propertyAssignment);
				assertEquals(2, possibleAssignments.size());
				
				Individual damianIndividual = null;
				Individual employeeIndividual = null;
				
				for (Individual individual : possibleAssignments) {
					if (damianURI.equals(individual.getURI())) {
						damianIndividual = individual;
					} else if (employeeURI.equals(individual.getURI())) {
						employeeIndividual = individual;
					}
				}
				
				assertNotNull(damianIndividual);
				assertNotNull(employeeIndividual);
				
				propertyAssignment.setPropertyValue(employeeIndividual.getURI());
			}
		}
		
		List<Action> toExecuteActions = newLinkedList();
		toExecuteActions.add(action);
		manipulator.execute(toExecuteActions);
		
		Individual purchaseRequest01 = modelOntology.getIndividual(purchaseRequest01URI);
		Property purchaseRequestClientProperty = modelOntology.getProperty(purchaseRequestClientURI);
		RDFNode damianRDFNode = purchaseRequest01.getPropertyValue(purchaseRequestClientProperty);
		
		assertNotNull(damianRDFNode);
		assertTrue(damianRDFNode.isResource());
		assertEquals(damianRDFNode.asResource().getURI(), damianURI);
		
		Property purchaseRequestResponsibleProperty = modelOntology.getProperty(purchaseRequestResponsibleURI);
		RDFNode employeeRDFNode = purchaseRequest01.getPropertyValue(purchaseRequestResponsibleProperty);
		
		assertNotNull(employeeRDFNode);
		assertTrue(employeeRDFNode.isResource());
		assertEquals(employeeRDFNode.asResource().getURI(), employeeURI);
	}
	
	@Test
	public void executeAction() throws IOException, InvalidPropertyAssignment {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		Map<String, String> map = newHashMap();
		map.put(createPurchaseOrderURI, inputDataExample);
		Form form = new Form(FormsConfiguration.createFromFiles(map, modelOntology));
		
		ModelManipulator manipulator = getModelManipulator(modelOntology, form);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState createPurchaseOrder = simulator.getStateFromURI(createPurchaseOrderURI);
		
		List<Action> actions = manipulator.getActions(createPurchaseOrder);
		Action action = actions.get(0);
		List<PropertyAssignment> propertyAssignments = action.getPropertyAssignments();
		
		PropertyAssignment purchaseRequestClient = null;
		PropertyAssignment purchaseRequestResponsible = null;
		
		for (PropertyAssignment propertyAssignment : propertyAssignments) {
			if (propertyAssignment.getPropertyURI().equals(purchaseRequestClientURI)) {
				purchaseRequestClient = propertyAssignment;
			} else if (propertyAssignment.getPropertyURI().equals(purchaseRequestResponsibleURI)) {
				purchaseRequestResponsible = propertyAssignment;
			}
		}
		
		assertNotNull(purchaseRequestClient);
		assertNotNull(purchaseRequestResponsible);
		
		purchaseRequestClient.setPropertyValue(damianURI);
		purchaseRequestResponsible.setPropertyValue(employeeURI);
		
		List<Action> actionsToExecute = newLinkedList();
		actionsToExecute.add(action);
		manipulator.execute(actionsToExecute);
	}
	
	@Test(expected=InvalidPropertyAssignment.class)
	public void executeActionThatViolatesRestrictionThrowException() throws IOException, InvalidPropertyAssignment {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		Map<String, String> map = newHashMap();
		map.put(createPurchaseOrderURI, inputDataExample);
		Form form = new Form(FormsConfiguration.createFromFiles(map, modelOntology));
		
		ModelManipulator manipulator = getModelManipulator(modelOntology, form);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState createPurchaseOrder = simulator.getStateFromURI(createPurchaseOrderURI);
		
		List<Action> actions = manipulator.getActions(createPurchaseOrder);
		Action action = actions.get(0);
		List<PropertyAssignment> propertyAssignments = action.getPropertyAssignments();
		
		PropertyAssignment purchaseRequestClient = null;
		PropertyAssignment purchaseRequestResponsible = null;
		
		for (PropertyAssignment propertyAssignment : propertyAssignments) {
			if (propertyAssignment.getPropertyURI().equals(purchaseRequestClientURI)) {
				purchaseRequestClient = propertyAssignment;
			} else if (propertyAssignment.getPropertyURI().equals(purchaseRequestResponsibleURI)) {
				purchaseRequestResponsible = propertyAssignment;
			}
		}
		
		assertNotNull(purchaseRequestClient);
		assertNotNull(purchaseRequestResponsible);
		
		purchaseRequestClient.setPropertyValue(damianURI);
		purchaseRequestResponsible.setPropertyValue(damianURI);
		
		List<Action> actionsToExecute = newLinkedList();
		actionsToExecute.add(action);
		manipulator.execute(actionsToExecute);
	}
	
	@Test
	public void executeActionThatViolatesRestrictionRollBack() throws IOException {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		Map<String, String> map = newHashMap();
		map.put(createPurchaseOrderURI, inputDataExample);
		Form form = new Form(FormsConfiguration.createFromFiles(map, modelOntology));
		
		ModelManipulator manipulator = getModelManipulator(modelOntology, form);
		Simulator simulator = getSimulator(bpmnOntology);
		ModelFacade modelFacade = new OntologyModelFacade(modelOntology);
		
		List<JBPSIndividual> allIndividuals = modelFacade.getAllIndividuals();
		assertEquals(2, allIndividuals.size());
		
		SimulationState createPurchaseOrder = simulator.getStateFromURI(createPurchaseOrderURI);
		
		List<Action> actions = manipulator.getActions(createPurchaseOrder);
		Action action = actions.get(0);
		List<PropertyAssignment> propertyAssignments = action.getPropertyAssignments();
		
		PropertyAssignment purchaseRequestClient = null;
		PropertyAssignment purchaseRequestResponsible = null;
		
		for (PropertyAssignment propertyAssignment : propertyAssignments) {
			if (propertyAssignment.getPropertyURI().equals(purchaseRequestClientURI)) {
				purchaseRequestClient = propertyAssignment;
			} else if (propertyAssignment.getPropertyURI().equals(purchaseRequestResponsibleURI)) {
				purchaseRequestResponsible = propertyAssignment;
			}
		}
		
		assertNotNull(purchaseRequestClient);
		assertNotNull(purchaseRequestResponsible);
		
		purchaseRequestClient.setPropertyValue(damianURI);
		purchaseRequestResponsible.setPropertyValue(damianURI);
		
		List<Action> actionsToExecute = newLinkedList();
		actionsToExecute.add(action);
		try {
			manipulator.execute(actionsToExecute);
			assertTrue(false);
		} catch (InvalidPropertyAssignment ex) { }
		
		allIndividuals = modelFacade.getAllIndividuals();
		assertEquals(2, allIndividuals.size());
	}
	
	
}
