package it.polimi.jbps.bpmn.simulation;

import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import static org.junit.Assert.assertEquals;
import it.polimi.jbps.exception.BPMNInvalidTransition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.tdb.TDBFactory;
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
//	@Ignore
	public void testNotValid() {
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		OntClass newDomainClass = modelOntology.createClass("http://www.polimi.it/newDomainClass");
		
		Individual x = newDomainClass.createIndividual("http://www.polimi.it/x");
		Individual y = newDomainClass.createIndividual("http://www.polimi.it/y");
		
		Property p = modelOntology.getProperty("http://www.polimi.it/p");
		Property q = modelOntology.getProperty("http://www.polimi.it/q");
		
		Statement disjointProperties = modelOntology.createStatement(p, OWL2.propertyDisjointWith, q);
		modelOntology.add(disjointProperties);
		
		x.addProperty(p, y);
		x.addProperty(q, y);
		
		ValidityReport validate = modelOntology.validate();
		assertEquals(false, validate.isValid());
	}
	
	@Test
	@Ignore
	public void test() {
		String location = "/home/damian/Desktop/tdb-assembler.ttl";
		
//		Model model = TDBFactory.assembleModel(assemblerFile);
		Model model = TDBFactory.createModel(location);
		
		OntModel modelOntology = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
//		OntModel modelOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
		
		OntClass newDomainClass = modelOntology.createClass("http://www.polimi.it/newDomainClass");
//		OntClass newDomainClass2 = modelOntology.createClass("http://www.polimi.it/newDomainClass2");
		
//		newDomainClass.addDisjointWith(newDomainClass2);
		
		Individual x = newDomainClass.createIndividual("http://www.polimi.it/x");
//		OntClass newRangeClass = modelOntology.createClass("http://www.polimi.it/newRangeClass");
		
		Individual y = newDomainClass.createIndividual("http://www.polimi.it/y");
		
		Property p = modelOntology.getProperty("http://www.polimi.it/p");
		Property q = modelOntology.getProperty("http://www.polimi.it/q");
		
		
		Statement disjointProperties = modelOntology.createStatement(p, OWL2.propertyDisjointWith, q);
		modelOntology.add(disjointProperties);
		
		
//		x.addOntClass(newDomainClass2);
		
		modelOntology.write(System.out);
		
		Model begin = model.begin();
		
		
		
		x.addProperty(p, y);
		x.addProperty(q, y);
		
		model.write(System.out);
		
		begin.commit();
		
		model.write(System.out);
		
		ValidityReport validate = modelOntology.validate();
		assertEquals(false, validate.isValid());
	}

}
