package it.polimi.jbps.model;

import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.polimi.jbps.entities.JBPSClass;
import it.polimi.jbps.entities.JBPSIndividual;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;

public abstract class ModelFacadeTest {
	
	protected abstract ModelFacade getModeFacade(OntModel ontologyModel);
	
	private final static String modelOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestModel.owl";
	
	private final static String damianURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#damian";
	private final static String employeeURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#employee";
	
	private final static String personURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#Person";
	private final static String namedIndividualURI = "http://www.w3.org/2002/07/owl#NamedIndividual";
	
	@Test
	public void getAllIndividuals() throws IOException {
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		ModelFacade model = getModeFacade(modelOntology);
		List<JBPSIndividual> allIndividuals = model.getAllIndividuals();
		
		assertEquals(2, allIndividuals.size());
		
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

}
