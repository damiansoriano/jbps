package it.polimi.jbps.engine;

import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.ObjectUtils.isNotNull;
import static it.polimi.jbps.utils.ObjectUtils.not;
import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.PropertyAssignment;
import it.polimi.jbps.bpmn.simulation.OntologySimulator;
import it.polimi.jbps.bpmn.simulation.Simulator;
import it.polimi.jbps.entities.Context;
import it.polimi.jbps.entities.JBPSIndividual;
import it.polimi.jbps.entities.JBPSProperty;
import it.polimi.jbps.entities.SimulationState;
import it.polimi.jbps.exception.BPMNInvalidTransition;
import it.polimi.jbps.exception.InvalidPropertyAssignment;
import it.polimi.jbps.form.Form;
import it.polimi.jbps.form.FormsConfiguration;
import it.polimi.jbps.model.ModelFacade;
import it.polimi.jbps.model.ModelManipulator;
import it.polimi.jbps.model.OntologyModelFacade;
import it.polimi.jbps.model.OntologyModelManipulator;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.mindswap.pellet.utils.Pair;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class SimpleEngineTest {

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
	private final String otherPersonURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#otherPerson";
	
	private final String purchaseRequestClientURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestClient";
	private final String purchaseRequestResponsibleURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestResponsible";
	
	private final String contextVariableName = "purchaseOrder";
	
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

	@Test
	public void correctlyUpdateVariablesInModel() throws InvalidPropertyAssignment, BPMNInvalidTransition, IOException {
		Context context = new Context();
		Pair<Engine, ModelFacade> controllers = getControllers();
		Engine engine = controllers.first;
		ModelFacade modelFacade = controllers.second;
		
		
		SimulationState state = engine.startSimulation();
		
		Action action = engine.getActionsWithPossibleAssignments(state, context).get(0);
		for (PropertyAssignment propAss : action.getPropertyAssignments()) {
			assertNull(propAss.getPropertyValue());
		}
		
		
		Map<String, String> map = newHashMap();
		map.put(purchaseRequestClientURI, damianURI);
		map.put(purchaseRequestResponsibleURI, otherPersonURI);
		state = engine.makeTransition(state, map, sfRequestAuthorizationURI, context);
		
		checkPropertiesValuesInModel(engine, modelFacade, damianURI, otherPersonURI, context);
		
		map = newHashMap();
		state = engine.makeTransition(state, map, sfRejectPurchaseOrderURI, context);
		
		checkPropertiesValuesInModel(engine, modelFacade, damianURI, otherPersonURI, context);
		checkPropertiesValuesOnPossibleAssignments(engine, state, damianURI, otherPersonURI, context);
		
		map = newHashMap();
		map.put(purchaseRequestResponsibleURI, employeeURI);
		state = engine.makeTransition(state, map, sfRequestAuthorization2URI, context);
		
		checkPropertiesValuesInModel(engine, modelFacade, null, employeeURI, context);
		
		map = newHashMap();
		state = engine.makeTransition(state, map, sfRejectPurchaseOrderURI, context);
		
		checkPropertiesValuesInModel(engine, modelFacade, null, employeeURI, context);
		checkPropertiesValuesOnPossibleAssignments(engine, state, null, employeeURI, context);
		
		map = newHashMap();
		map.put(purchaseRequestClientURI, damianURI);
		map.put(purchaseRequestResponsibleURI, otherPersonURI);
		state = engine.makeTransition(state, map, sfRequestAuthorization2URI, context);
		
		checkPropertiesValuesInModel(engine, modelFacade, damianURI, otherPersonURI, context);
		
		
		
		map = newHashMap();
		state = engine.makeTransition(state, map, sfRejectPurchaseOrderURI, context);
		
		checkPropertiesValuesInModel(engine, modelFacade, damianURI, otherPersonURI, context);
		checkPropertiesValuesOnPossibleAssignments(engine, state, damianURI, otherPersonURI, context);
		
		map = newHashMap();
		map.put(purchaseRequestClientURI, otherPersonURI);
		map.put(purchaseRequestResponsibleURI, damianURI);
		state = engine.makeTransition(state, map, sfRequestAuthorization2URI, context);
		
		checkPropertiesValuesInModel(engine, modelFacade, otherPersonURI, damianURI, context);
		
		
		
		
		map = newHashMap();
		state = engine.makeTransition(state, map, sfAuthorizePurchaseOrderURI, context);
		assertTrue(engine.isEndState(state));
	}
	
	public void checkPropertiesValuesInModel(Engine engine, ModelFacade modelFacade,
			String expectedPurchaseRequestClient, String expectedPurchaseRequestResponsible,
			Context context) {
		
		JBPSIndividual jspsPurchaseOrderIndividual = modelFacade.getIndividual(context.getVariables().get(contextVariableName));
		Individual purchaseOrderIndividual = jspsPurchaseOrderIndividual.getIndividual();
		
		JBPSIndividual purchaseOrder = new JBPSIndividual(purchaseOrderIndividual);
		Map<Property, RDFNode> properties = modelFacade.getProperties(purchaseOrder);
		
		JBPSProperty purchaseRequestClient = modelFacade.getProperty(purchaseRequestClientURI);
		if (isNotNull(expectedPurchaseRequestClient)) {
			assertTrue(properties.containsKey(purchaseRequestClient.getOntProperty()));
			assertEquals(expectedPurchaseRequestClient, properties.get(purchaseRequestClient.getOntProperty()).asResource().getURI());
		} else {
			assertTrue(not(properties.containsKey(purchaseRequestClient.getOntProperty())));
		}
		
		JBPSProperty purchaseRequestResponsible = modelFacade.getProperty(purchaseRequestResponsibleURI);
		if (isNotNull(purchaseRequestResponsible)) {
			assertTrue(properties.containsKey(purchaseRequestResponsible.getOntProperty()));
			assertEquals(expectedPurchaseRequestResponsible, properties.get(purchaseRequestResponsible.getOntProperty()).asResource().getURI());
		} else {
			assertTrue(not(properties.containsKey(purchaseRequestResponsible.getOntProperty())));
		}
	}
	
	public void checkPropertiesValuesOnPossibleAssignments(Engine engine, SimulationState state,
			String expectedPurchaseRequestClient, String expectedPurchaseRequestResponsible,
			Context context) {
		
		Action action = engine.getActionsWithPossibleAssignments(state, context).get(0);
		for (PropertyAssignment propAss : action.getPropertyAssignments()) {
			if (propAss.getJbpsProperty().getOntProperty().getURI().endsWith(purchaseRequestClientURI)) {
				assertEquals(expectedPurchaseRequestClient, propAss.getPropertyValue());
			} else if (propAss.getJbpsProperty().getOntProperty().getURI().endsWith(purchaseRequestResponsibleURI)) {
				assertEquals(expectedPurchaseRequestResponsible, propAss.getPropertyValue());
			}
		}
	}
}
