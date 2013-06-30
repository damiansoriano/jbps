package it.polimi.bpmn.simulation;

import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import static org.junit.Assert.*;
import it.polimi.jbps.exception.BPMNInvalidTransition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.vocabulary.OWL2;

public class ToDeleteSimulators {
	
	private final static String modelOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestModel.owl";
	
	private final static String purchaseRequestURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#PurchaseRequest";
	
	private final static String purchaseRequestClientURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestClient";
	private final static String purchaseRequestResponsibleURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequestResponsible";
	
	private final static String purchaseRequest01URI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#purchaseRequest01";
	private final static String individualPersonURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl#damian";
	
	@Test
	@Ignore
	public void test2() throws IOException, BPMNInvalidTransition {
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		Resource purchaseOrderClass = modelOntology.getResource(purchaseRequestURI);
		Individual purchaseOrder = modelOntology.createIndividual(purchaseRequest01URI, purchaseOrderClass);
		OntProperty requestClient = modelOntology.getProperty(purchaseRequestClientURI).as(OntProperty.class);
		OntProperty requestResponsible = modelOntology.getProperty(purchaseRequestResponsibleURI).as(OntProperty.class);
		
		modelOntology.createStatement(requestClient, OWL2.propertyDisjointWith, requestResponsible);
		
		purchaseOrder.addProperty(requestClient, individualPersonURI);
		purchaseOrder.addProperty(requestResponsible, individualPersonURI);
		
		File file = new File("/home/damian/Desktop/ontology.owl");
		if (!file.exists()) { file.createNewFile(); }
		FileOutputStream fop = new FileOutputStream(file);
		
		modelOntology.write(fop);
		
		ValidityReport validate = modelOntology.validate();
		System.out.println(validate.isValid());
		System.out.println(validate.isClean());
	}
	
	@Test	
	public void test() {
		OntModel modelOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
		
		Resource newDomainClass = modelOntology.getResource("newDomainClass");
		Individual x = modelOntology.createIndividual(newDomainClass);
		Resource newRangeClass = modelOntology.getResource("newRangeClass");
		Individual y = modelOntology.createIndividual(newRangeClass);
		Property p = modelOntology.getProperty("p");
		Property q = modelOntology.getProperty("q");
		
		modelOntology.createStatement(p, OWL2.propertyDisjointWith, q);
		
		x.addProperty(p, y);
		x.addProperty(q, y);
		
		ValidityReport validate = modelOntology.validate();
		assertEquals(false, validate.isValid());
	}

}
