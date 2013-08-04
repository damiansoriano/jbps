package it.polimi.jbps.io;

import static it.polimi.jbps.PropertyType.OBJECT_PROPERTY;
import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.ActionType;
import it.polimi.jbps.actions.PropertyAssignment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.hp.hpl.jena.ontology.OntModel;

public class Json2ModelActionTest {
	
	private final static String modelOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestModel.owl";
	
	private final static String createPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#createPurchaseOrder";
	private final static String changePurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#changePurchaseOrder";
	
	@Test
	public void correctlyGenerateInputDataExample() throws IOException {
		File inputFile = new File("./src/test/resources/it/polimi/bpmn/simulation/inputDataExampleWith2Actions.json");
		String inputJson = Files.toString(inputFile, Charsets.UTF_8);
		
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		Json2ModelAction json2ModelAction = new Json2ModelAction();
		Map<String, List<Action>> formsConfiguration = json2ModelAction.parseFormsConfiguration(inputJson, modelOntology);
		
		assertTrue(formsConfiguration.containsKey(createPurchaseOrderURI));
		
		List<Action> actions = formsConfiguration.get(createPurchaseOrderURI);
		
		assertEquals(2, actions.size());
		
		Action action01 = actions.get(0);
		
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#PurchaseRequest", action01.getClassURI());
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequest01", action01.getIndividualURI());
		assertEquals(ActionType.INSERT, action01.getActionType());
		
		List<PropertyAssignment> propertyAssignments01 = action01.getPropertyAssignments();
		assertEquals(2, propertyAssignments01.size());
		
		PropertyAssignment propertyAssignment01 = propertyAssignments01.get(0);
		assertEquals(OBJECT_PROPERTY, propertyAssignment01.getPropertyType());
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestClient", propertyAssignment01.getPropertyURI());
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#damian", propertyAssignment01.getPropertyValue());
		
		PropertyAssignment propertyAssignment02 = propertyAssignments01.get(1);
		assertEquals(OBJECT_PROPERTY, propertyAssignment02.getPropertyType());
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestResponsible", propertyAssignment02.getPropertyURI());
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#employee", propertyAssignment02.getPropertyValue());
		
		Action action02 = actions.get(1);
		
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#PurchaseRequest", action02.getClassURI());
		assertEquals("", action02.getIndividualURI());
		assertEquals(ActionType.INSERT, action02.getActionType());
		
		List<PropertyAssignment> propertyAssignments02 = action02.getPropertyAssignments();
		assertEquals(1, propertyAssignments02.size());
		
		PropertyAssignment propertyAssignment03 = propertyAssignments02.get(0);
		assertEquals(OBJECT_PROPERTY, propertyAssignment03.getPropertyType());
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestClient", propertyAssignment03.getPropertyURI());
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#damian", propertyAssignment03.getPropertyValue());
	}
	
	@Test
	public void correctlyGenerateStateFormAssociation() throws IOException {
		File inputFile = new File("./src/test/resources/it/polimi/bpmn/simulation/stateFormAssociation.json");
		String inputJson = Files.toString(inputFile, Charsets.UTF_8);
		
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		Json2ModelAction json2ModelAction = new Json2ModelAction();
		Map<String, List<Action>> formsConfiguration = json2ModelAction.parseFormsConfiguration(inputJson, modelOntology);
		
		assertTrue(formsConfiguration.containsKey(createPurchaseOrderURI));
		
		List<Action> actions = formsConfiguration.get(createPurchaseOrderURI);
		
		assertEquals(1, actions.size());
		
		Action action = actions.get(0);
		
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#PurchaseRequest", action.getClassURI());
		assertEquals("", action.getIndividualURI());
		assertEquals(ActionType.INSERT, action.getActionType());
		
		List<PropertyAssignment> propertyAssignments01 = action.getPropertyAssignments();
		assertEquals(2, propertyAssignments01.size());
		
		PropertyAssignment propertyAssignment01 = propertyAssignments01.get(0);
		assertEquals(OBJECT_PROPERTY, propertyAssignment01.getPropertyType());
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestClient", propertyAssignment01.getPropertyURI());
		assertNull(propertyAssignment01.getPropertyValue());
		
		PropertyAssignment propertyAssignment02 = propertyAssignments01.get(1);
		assertEquals(OBJECT_PROPERTY, propertyAssignment02.getPropertyType());
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestResponsible", propertyAssignment02.getPropertyURI());
		assertNull(propertyAssignment01.getPropertyValue());
	}
	
	@Test
	public void correctlyGetVariables() throws IOException {
		File inputFile = new File("./src/test/resources/it/polimi/bpmn/simulation/inputDataExampleWithVariables.json");
		String inputJson = Files.toString(inputFile, Charsets.UTF_8);
		
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		Json2ModelAction json2ModelAction = new Json2ModelAction();
		Map<String, List<Action>> formsConfiguration = json2ModelAction.parseFormsConfiguration(inputJson, modelOntology);
		
		assertTrue(formsConfiguration.containsKey(createPurchaseOrderURI));
		
		List<Action> actions = formsConfiguration.get(createPurchaseOrderURI);
		
		assertEquals(1, actions.size());
		
		Action action = actions.get(0);
		
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#PurchaseRequest", action.getClassURI());
		assertEquals(ActionType.INSERT, action.getActionType());
		assertEquals("purchaseOrder", action.getVariableName());
		
		assertTrue(formsConfiguration.containsKey(changePurchaseOrderURI));
		
		actions = formsConfiguration.get(changePurchaseOrderURI);
		
		assertEquals(1, actions.size());
		
		action = actions.get(0);
		
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#PurchaseRequest", action.getClassURI());
		assertEquals(ActionType.UPDATE, action.getActionType());
		assertEquals("purchaseOrder", action.getVariableName());
	}
}
