package it.polimi.bpmn.simulation;

import static it.polimi.utils.OntologyUtils.getIndividuales;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import it.polimi.actions.Action;
import it.polimi.io.Json2ModelAction;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import com.hp.hpl.jena.util.FileManager;

public class SimulatorTest {
	
	private final static String bpmnOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequest.owl";
	private final static String modelOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestModel.owl";
	
	private final static String purchaseRequestURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#PurchaseRequest";
	
	private final static String purchaseRequestClientURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestClient";
	private final static String purchaseRequestResponsibleURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestResponsible";
	
	private final static String purchaseRequest01URI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequest01";
	private final static String individualPersonURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#damian";
	private final static String employeeURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#employee";
	
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
		
		List<Individual> prePurchaseRequestIndividuales = getIndividuales(modelOntology, purchaseRequestURI);
		assertTrue(prePurchaseRequestIndividuales.isEmpty());
		
		Simulator simulator = new Simulator(bpmnOntology, modelOntology);
		simulator.execute(actions);
		
		List<Individual> postPurchaseRequestIndividuales = getIndividuales(modelOntology, purchaseRequestURI);
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
	}

}
