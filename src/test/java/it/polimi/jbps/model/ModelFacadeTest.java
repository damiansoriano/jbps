package it.polimi.jbps.model;

import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.polimi.jbps.entities.JBPSClass;
import it.polimi.jbps.entities.JBPSIndividual;
import it.polimi.jbps.entities.JBPSProperty;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Optional;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

public abstract class ModelFacadeTest {
	
	protected abstract ModelFacade getModeFacade(OntModel ontologyModel);
	
	private final static String PurchaseRequestBPMNAfterPurchaseOrderCreated = "./src/test/resources/it/polimi/bpmn/simulation/PurchaseRequestBPMNAfterPurchaseOrderCreated.owl";
	
	private final static String modelOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestModel.owl";
	
	private final static String damianURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#damian";
	private final static String employeeURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#employee";
	
	private final static String purchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl/7dd42c33-a7ba-49d4-9ff6-067900528295";
	
	private final static String personURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#Person";
	private final static String namedIndividualURI = "http://www.w3.org/2002/07/owl#NamedIndividual";
	
	private final static String propertyPurchaseRequestResponsibleURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestResponsible"; 
	private final static String propertyPurchaseRequestClientURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestClient";
	
	@Test
	public void getAllIndividuals() throws IOException {
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		ModelFacade model = getModeFacade(modelOntology);
		List<JBPSIndividual> allIndividuals = model.getAllIndividuals();
		
		assertEquals(3, allIndividuals.size());
		
		JBPSIndividual employeeIndividual = null;
		JBPSIndividual userIndividual = null;
		
		for (JBPSIndividual individual : allIndividuals) {
			if (employeeURI.equals(individual.getURI())) {
				employeeIndividual = individual;
			} else if (damianURI.equals(individual.getURI())) {
				userIndividual = individual;
			}
		}
		
		assertNotNull(employeeIndividual);
		assertNotNull(userIndividual);
	}
	
	@Test
	@Ignore
	public void getAllClassesOfIndividuals() throws IOException {
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		ModelFacade model = getModeFacade(modelOntology);
		List<JBPSIndividual> allIndividuals = model.getAllIndividuals();
		
		JBPSIndividual employeeIndividual = null;
		JBPSIndividual userIndividual = null;
		
		for (JBPSIndividual individual : allIndividuals) {
			if (employeeURI.equals(individual.getURI())) {
				employeeIndividual = individual;
			} else if (damianURI.equals(individual.getURI())) {
				userIndividual = individual;
			}
		}
		
		List<JBPSClass> allClassesEmployee = model.getAllClasses(employeeIndividual);
		List<JBPSClass> allDirectClassesEmployee = model.getAllDirectClasses(employeeIndividual);
		
		assertEquals(4, allClassesEmployee.size());
		assertEquals(2, allDirectClassesEmployee.size());
		
		boolean isEmployeeNamedIndividual = false;
		boolean isEmployeePerson = false;
		for(JBPSClass jbpsClass : allDirectClassesEmployee) {
			if (namedIndividualURI.equals(jbpsClass.getURI())) {
				isEmployeeNamedIndividual = true;
			} else if (personURI.equals(jbpsClass.getURI())) {
				isEmployeePerson = true;
			}
		}
		assertTrue(isEmployeeNamedIndividual);
		assertTrue(isEmployeePerson);
		
		List<JBPSClass> allClassesUser = model.getAllClasses(userIndividual);
		List<JBPSClass> allDirectClassesUser = model.getAllDirectClasses(employeeIndividual);
		
		assertEquals(4, allClassesUser.size());
		assertEquals(2, allDirectClassesUser.size());
		
		boolean isUserNamedIndividual = false;
		boolean isUserPerson = false;
		for(JBPSClass jbpsClass : allDirectClassesUser) {
			if (namedIndividualURI.equals(jbpsClass.getURI())) {
				isUserNamedIndividual = true;
			} else if (personURI.equals(jbpsClass.getURI())) {
				isUserPerson = true;
			}
		}
		assertTrue(isUserNamedIndividual);
		assertTrue(isUserPerson);
	}
	
	@Test
	public void getClassFromURI() throws IOException {
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		ModelFacade model = getModeFacade(modelOntology);
		Optional<JBPSClass> classFromURIOptional = model.getClassFromURI(personURI);
		
		assertTrue(classFromURIOptional.isPresent());
		JBPSClass classFromURI = classFromURIOptional.get();
		assertEquals("Person", classFromURI.toString());
		assertEquals(personURI, classFromURI.getURI());
	}
	
	@Test
	public void getClassFromURINull() throws IOException {
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		ModelFacade model = getModeFacade(modelOntology);
		Optional<JBPSClass> classFromURIOptional = model.getClassFromURI("notExistingClass");
		
		assertFalse(classFromURIOptional.isPresent());
	}
	
	@Test
	public void getIndividual() throws IOException {
		OntModel modelOntology = getOntologyFromFile(PurchaseRequestBPMNAfterPurchaseOrderCreated);
		ModelFacade model = getModeFacade(modelOntology);
		JBPSIndividual damian = model.getIndividual(damianURI);
		
		assertNotNull(damian);
		assertNotNull(damian.getIndividual());
		assertEquals(damianURI, damian.getURI());
	}
	
	@Test
	public void getProperties() throws IOException {
		OntModel modelOntology = getOntologyFromFile(PurchaseRequestBPMNAfterPurchaseOrderCreated);
		ModelFacade model = getModeFacade(modelOntology);
		JBPSIndividual purchaseOrder = model.getIndividual(purchaseOrderURI);
		
		assertNotNull(purchaseOrder);
		assertNotNull(purchaseOrder.getIndividual());
		assertEquals(purchaseOrderURI, purchaseOrder.getURI());
		
		Map<Property, RDFNode> properties = model.getProperties(purchaseOrder);
		
		assertEquals(5, properties.keySet().size());
		
		JBPSProperty propertyPurchaseRequestResponsible = model.getProperty(propertyPurchaseRequestResponsibleURI);
		JBPSProperty propertyPurchaseRequestClient = model.getProperty(propertyPurchaseRequestClientURI);
		
		assertTrue(properties.containsKey(propertyPurchaseRequestResponsible.getOntProperty()));
		assertTrue(properties.containsKey(propertyPurchaseRequestClient.getOntProperty()));
		
		RDFNode employeeRDFNode = properties.get(propertyPurchaseRequestResponsible.getOntProperty());
		RDFNode damianRDFNode = properties.get(propertyPurchaseRequestClient.getOntProperty());
		assertEquals(employeeURI, employeeRDFNode.asResource().getURI());
		assertEquals(damianURI, damianRDFNode.asResource().getURI());
	}

}
