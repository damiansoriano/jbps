package it.polimi.bpmn.simulation;

import static it.polimi.utils.OntologyUtils.getIndividuals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import it.polimi.PropertyType;
import it.polimi.actions.Action;
import it.polimi.actions.PropertyAssignment;
import it.polimi.io.Json2ModelAction;
import it.polimi.jbps.exception.BPMNInvalidTransition;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.util.FileManager;

public class SimulatorTest {
	
	private final static String bpmnOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestBPMN.owl";
	private final static String modelOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestModel.owl";
	
	private final static String purchaseRequestURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#PurchaseRequest";
	
	private final static String purchaseRequestClientURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestClient";
	private final static String purchaseRequestResponsibleURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestResponsible";
	
	private final static String purchaseRequest01URI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequest01";
	private final static String individualPersonURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#damian";
	private final static String employeeURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#employee";
	
	private final static String startPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#startPurchaseOrder";
	private final static String createPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#createPurchaseOrder";
	private final static String authorizePurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#authorizePurchaseOrder";
	private final static String endPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#endPurchaseOrder";
	
	private final static String sfStartPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfStartPurchaseOrder";
	private final static String sfRequestAuthorizationURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfRequestAuthorization";
	private final static String sfAuthorizePurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#sfAuthorizePurchaseOrder";
	
	private OntModel getOntologyFromFile(String filePath) {
		Model model = FileManager.get().loadModel(filePath);
		
		OntModel ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
		ontologyModel.add(model);
		return ontologyModel;
	}
	
	private List<Action> getActionsFromFile(String filePath) throws IOException {
		Json2ModelAction json2Model = new Json2ModelAction();
		File file = new File(filePath);
		String json = Files.toString(file, Charsets.UTF_8);
		return json2Model.parseJson(json);
	}
	
	@Test
	public void correctlyInsertIndividual() throws IOException {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		List<Action> actions = getActionsFromFile("./src/test/resources/it/polimi/bpmn/simulation/inputDataExample.json");
		
		List<Individual> prePurchaseRequestIndividuales = getIndividuals(modelOntology, purchaseRequestURI);
		assertTrue(prePurchaseRequestIndividuales.isEmpty());
		
		Simulator simulator = new Simulator(bpmnOntology, modelOntology, null);
		simulator.execute(actions);
		
		List<Individual> postPurchaseRequestIndividuales = getIndividuals(modelOntology, purchaseRequestURI);
		assertEquals(2, postPurchaseRequestIndividuales.size());
		
		Individual namedPurchaseRequest = null;
		Individual anonymousPurchaseRequest = null;
		
		for (Individual individual : postPurchaseRequestIndividuales) {
			if (purchaseRequest01URI.equals(individual.getURI())) { namedPurchaseRequest = individual; }
			else { anonymousPurchaseRequest = individual; }
		}
		
		assertNotNull(namedPurchaseRequest);
		assertNotNull(anonymousPurchaseRequest);
		
		Property purchaseRequestClient = modelOntology.getProperty(purchaseRequestClientURI);
		Property purchaseRequestResponsible = modelOntology.getProperty(purchaseRequestResponsibleURI);
		
		Resource person01 = namedPurchaseRequest.getPropertyResourceValue(purchaseRequestClient);
		assertEquals(individualPersonURI, person01.getURI());
		
		Resource employee = namedPurchaseRequest.getPropertyResourceValue(purchaseRequestResponsible);
		assertEquals(employeeURI, employee.getURI());
		
		Resource person02 = anonymousPurchaseRequest.getPropertyResourceValue(purchaseRequestClient);
		assertEquals(individualPersonURI, person02.getURI());
		
		Resource nullEmployee = anonymousPurchaseRequest.getPropertyResourceValue(purchaseRequestResponsible);
		assertNull(nullEmployee);
		
		ValidityReport validityReport = modelOntology.validate();
		assertTrue(validityReport.isValid());
		assertTrue(validityReport.isClean());
	}
	
	@Test
	public void correctlyNavigateStates() throws IOException, BPMNInvalidTransition {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		Simulator simulator = new Simulator(bpmnOntology, modelOntology, null);
		
		List<SimulationState> startEvents = simulator.getStartStates();
		
		assertEquals(1, startEvents.size());
		
		SimulationState state = startEvents.get(0);
		assertEquals(startPurchaseOrderURI, state.getStateURI());
		
		SimulationTransition sfStartPurchaseOrder = new SimulationTransition(sfStartPurchaseOrderURI);
		
		Map<SimulationTransition, SimulationState> nextStates = simulator.getNextStates(state);
		
		assertEquals(1, nextStates.size());
		assertTrue(nextStates.containsKey(sfStartPurchaseOrder));
		
		state = simulator.move(state, sfStartPurchaseOrder);
		assertEquals(createPurchaseOrderURI, state.getStateURI());
		
		SimulationTransition sfRequestAuthorization = new SimulationTransition(sfRequestAuthorizationURI);
		
		state = simulator.move(state, sfRequestAuthorization);
		assertEquals(authorizePurchaseOrderURI, state.getStateURI());
		
		SimulationTransition sfAuthorizePurchaseOrder = new SimulationTransition(sfAuthorizePurchaseOrderURI);
		
		state = simulator.move(state, sfAuthorizePurchaseOrder);
		assertEquals(endPurchaseOrderURI, state.getStateURI());
		
		assertTrue(simulator.isEndState(state));
	}
	
	@Test
	public void correctlyGetPossiblePropertyAssignments() throws IOException, BPMNInvalidTransition {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		Simulator simulator = new Simulator(bpmnOntology, modelOntology, null);
		
		PropertyAssignment propertyAssignment = new PropertyAssignment();
		propertyAssignment.setPropertyType(PropertyType.OBJECT_PROPERTY);
		propertyAssignment.setPropertyURI(purchaseRequestClientURI);
		List<Individual> possibleAssignments = simulator.getPossibleAssignments(propertyAssignment);
	}

}
