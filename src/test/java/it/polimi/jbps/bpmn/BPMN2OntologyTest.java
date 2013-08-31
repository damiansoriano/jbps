package it.polimi.jbps.bpmn;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.polimi.jbps.constants.BPMNConstants;
import it.polimi.jbps.utils.PurchaseRequestConstants;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public abstract class BPMN2OntologyTest {
	
	private final String bpmnFilePath = "./src/test/resources/it/polimi/bpmn/simulation/purchaseOrderDiagarm.bpmn";
	
	protected abstract BPMN2Ontology bpmn2Ontology();
	
	@Test
	public void loadStartEvent() throws IOException {
		BPMN2Ontology bpmn2Ontology = bpmn2Ontology();
		OntModel ontology = bpmn2Ontology.createOntology(FileUtils.readFileToString(new File(bpmnFilePath)));
		
		OntClass startEventClass = ontology.getOntClass(BPMNConstants.EVENT_START_URI);
		ExtendedIterator<? extends OntResource> listInstances = startEventClass.listInstances();
		
		Map<String, Individual> uri2individual = newHashMap();
		while(listInstances.hasNext()) {
			Individual individual = listInstances.next().asIndividual();
			uri2individual.put(individual.getURI(), individual);
		}
		
		assertEquals(1, uri2individual.keySet().size());
		assertTrue(uri2individual.containsKey(PurchaseRequestConstants.START_PURCHASE_ORDER_URI));
	}
	
	@Test
	public void loadEndEvent() throws IOException {
		BPMN2Ontology bpmn2Ontology = bpmn2Ontology();
		OntModel ontology = bpmn2Ontology.createOntology(FileUtils.readFileToString(new File(bpmnFilePath)));
		
		OntClass startEventClass = ontology.getOntClass(BPMNConstants.EVENT_END_URI);
		ExtendedIterator<? extends OntResource> listInstances = startEventClass.listInstances();
		
		Map<String, Individual> uri2individual = newHashMap();
		while(listInstances.hasNext()) {
			Individual individual = listInstances.next().asIndividual();
			uri2individual.put(individual.getURI(), individual);
		}
		
		assertEquals(1, uri2individual.keySet().size());
		assertTrue(uri2individual.containsKey(PurchaseRequestConstants.END_PURCHASE_ORDER_URI));
	}
	
	@Test
	public void loadTasks() throws IOException {
		BPMN2Ontology bpmn2Ontology = bpmn2Ontology();
		OntModel ontology = bpmn2Ontology.createOntology(FileUtils.readFileToString(new File(bpmnFilePath)));
		
		OntClass taskClass = ontology.getOntClass(BPMNConstants.USER_TASK_URI);
		ExtendedIterator<? extends OntResource> listInstances = taskClass.listInstances();
		
		Map<String, Individual> uri2individual = newHashMap();
		while(listInstances.hasNext()) {
			Individual individual = listInstances.next().asIndividual();
			uri2individual.put(individual.getURI(), individual);
		}
		
		assertEquals(3, uri2individual.keySet().size());
		assertTrue(uri2individual.containsKey(PurchaseRequestConstants.CREATE_PURCHASE_ORDER_URI));
		assertTrue(uri2individual.containsKey(PurchaseRequestConstants.AUTHORIZE_PURCHASE_ORDER_URI));
		assertTrue(uri2individual.containsKey(PurchaseRequestConstants.CHANGE_PURCHASE_ORDER_URI));
	}
	
	@Test
	public void loadFlow() throws IOException {
		BPMN2Ontology bpmn2Ontology = bpmn2Ontology();
		OntModel ontology = bpmn2Ontology.createOntology(FileUtils.readFileToString(new File(bpmnFilePath)));
		
		OntClass taskClass = ontology.getOntClass(BPMNConstants.SEQUENCE_FLOW_URI);
		ExtendedIterator<? extends OntResource> listInstances = taskClass.listInstances();
		
		Map<String, Individual> uri2individual = newHashMap();
		while(listInstances.hasNext()) {
			Individual individual = listInstances.next().asIndividual();
			uri2individual.put(individual.getURI(), individual);
		}
		
		assertEquals(5, uri2individual.keySet().size());
		assertTrue(uri2individual.containsKey(PurchaseRequestConstants.SF_START_PURCHASE_ORDER_URI));
		assertTrue(uri2individual.containsKey(PurchaseRequestConstants.SF_REQUEST_AUTHORIZATION_URI));
		assertTrue(uri2individual.containsKey(PurchaseRequestConstants.SF_REJECT_PURCHASE_ORDER_URI));
		assertTrue(uri2individual.containsKey(PurchaseRequestConstants.SF_REQUEST_AUTHORIZATION2_URI));
		assertTrue(uri2individual.containsKey(PurchaseRequestConstants.SF_AUTHORIZE_PURCHASE_ORDER_URI));
	}
	
	@Test
	public void loadFlowProperty() throws IOException {
		BPMN2Ontology bpmn2Ontology = bpmn2Ontology();
		OntModel ontology = bpmn2Ontology.createOntology(FileUtils.readFileToString(new File(bpmnFilePath)));
		
		OntClass taskClass = ontology.getOntClass(BPMNConstants.SEQUENCE_FLOW_URI);
		ExtendedIterator<? extends OntResource> listInstances = taskClass.listInstances();
		
		Map<String, Individual> uri2individual = newHashMap();
		while(listInstances.hasNext()) {
			Individual individual = listInstances.next().asIndividual();
			uri2individual.put(individual.getURI(), individual);
		}
		
		Individual sfStartPurchaseOrder = uri2individual.get(PurchaseRequestConstants.SF_START_PURCHASE_ORDER_URI);
		OntProperty sequenceFlowSource = ontology.getOntProperty(BPMNConstants.SEQUENCE_FLOW_SOURCE_URI);
		OntProperty sequenceFlowTarget = ontology.getOntProperty(BPMNConstants.SEQUENCE_FLOW_TARGET_URI);
		
		Resource startPurchaseOrder = sfStartPurchaseOrder.getPropertyResourceValue(sequenceFlowSource);
		assertNotNull(startPurchaseOrder);
		assertEquals(PurchaseRequestConstants.START_PURCHASE_ORDER_URI, startPurchaseOrder.getURI());
		
		Resource createPurchaseOrder = sfStartPurchaseOrder.getPropertyResourceValue(sequenceFlowTarget);
		assertNotNull(createPurchaseOrder);
		assertEquals(PurchaseRequestConstants.CREATE_PURCHASE_ORDER_URI, createPurchaseOrder.getURI());
		
		Individual sfAuthorizePurchaseOrder = uri2individual.get(PurchaseRequestConstants.SF_AUTHORIZE_PURCHASE_ORDER_URI);
		
		Resource authorizePurchaseOrder = sfAuthorizePurchaseOrder.getPropertyResourceValue(sequenceFlowSource);
		assertNotNull(authorizePurchaseOrder);
		assertEquals(PurchaseRequestConstants.AUTHORIZE_PURCHASE_ORDER_URI, authorizePurchaseOrder.getURI());
		
		Resource endPurchaseOrder = sfAuthorizePurchaseOrder.getPropertyResourceValue(sequenceFlowTarget);
		assertNotNull(endPurchaseOrder);
		assertEquals(PurchaseRequestConstants.END_PURCHASE_ORDER_URI, endPurchaseOrder.getURI());
	}

}
