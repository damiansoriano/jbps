package it.polimi.io;

import static it.polimi.PropertyType.OBJECT_PROPERTY;
import static org.junit.Assert.*;
import it.polimi.actions.Action;
import it.polimi.actions.PropertyAssignment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Json2ModelActionTest {

	@Test
	public void test() throws IOException {
		File inputFile = new File("./src/test/resources/it/polimi/bpmn/simulation/inputDataExample.json");
		String inputJson = Files.toString(inputFile, Charsets.UTF_8);
		
		Json2ModelAction json2ModelAction = new Json2ModelAction();
		List<Action> actions = json2ModelAction.parseJson(inputJson);
		assertEquals(2, actions.size());
		
		Action action01 = actions.get(0);
		
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#PurchaseRequest", action01.getClassURI());
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequest01", action01.getIndividualURI());
		
		List<PropertyAssignment> propertyAssignments01 = action01.getActions();
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
		assertNull(action02.getIndividualURI());
		
		List<PropertyAssignment> propertyAssignments02 = action02.getActions();
		assertEquals(1, propertyAssignments02.size());
		
		PropertyAssignment propertyAssignment03 = propertyAssignments02.get(0);
		assertEquals(OBJECT_PROPERTY, propertyAssignment03.getPropertyType());
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestClient", propertyAssignment03.getPropertyURI());
		assertEquals("http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#damian", propertyAssignment03.getPropertyValue());
		
	}

}
