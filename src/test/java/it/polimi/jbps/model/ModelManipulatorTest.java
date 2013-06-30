package it.polimi.jbps.model;

import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import static org.junit.Assert.*;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.ActionType;
import it.polimi.jbps.actions.PropertyAssignment;
import it.polimi.jbps.bpmn.simulation.SimulationState;
import it.polimi.jbps.bpmn.simulation.Simulator;
import it.polimi.jbps.form.Form;
import it.polimi.jbps.form.FormsConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;

public abstract class ModelManipulatorTest {
	
	private final static String bpmnOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestBPMN.owl";
	private final static String modelOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestModel.owl";
	private final static String inputDataExample = "./src/test/resources/it/polimi/bpmn/simulation/inputDataExample.json";
	
	private final static String createPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#createPurchaseOrder";
	private final static String authorizePurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#authorizePurchaseOrder";
	
	protected abstract ModelManipulator getModelManipulator(OntModel ontologyModel, Form form);
	protected abstract Simulator getSimulator(OntModel bpmnOntologyModel);
	
	@Test
	public void getActionsReturnsOneAction() throws IOException {
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		Map<String, String> map = newHashMap();
		map.put(createPurchaseOrderURI, inputDataExample);
		Form form = new Form(FormsConfiguration.createFromFiles(map));
		
		ModelManipulator manipulator = getModelManipulator(modelOntology, form);
		Simulator simulator = getSimulator(bpmnOntology);
		
		SimulationState createPurchaseOrder = simulator.getStateFromURI(createPurchaseOrderURI);
		
		List<Action> actions = manipulator.getActions(createPurchaseOrder);
		
		assertEquals(1, actions.size());
		
		Action action = actions.get(0);
		assertEquals(ActionType.INSERT, action.getActionType());
		assertEquals("", action.getClassURI());
		assertEquals("", action.getIndividualURI());
		
		List<PropertyAssignment> propertyAssignments = action.getPropertyAssignments();
		assertEquals(2, propertyAssignments.size());
	}

}
