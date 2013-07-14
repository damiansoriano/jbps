package it.polimi.jbps.engine;

import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.bpmn.simulation.OntologySimulator;
import it.polimi.jbps.bpmn.simulation.Simulator;
import it.polimi.jbps.exception.BPMNInvalidTransition;
import it.polimi.jbps.form.Form;
import it.polimi.jbps.form.FormsConfiguration;
import it.polimi.jbps.io.Json2ModelAction;
import it.polimi.jbps.model.ModelManipulator;
import it.polimi.jbps.model.OntologyModelManipulator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.hp.hpl.jena.ontology.OntModel;

public class LunchConsoleEngine {
	
	private final static String bpmnOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestBPMN.owl";
	private final static String modelOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestModel.owl";
	private final static String formAssociationPath = "./src/test/resources/it/polimi/bpmn/simulation/stateFormAssociation.json";
	
	private final static String createPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#createPurchaseOrder";
	
	@Test
	@Ignore
	public void test() throws IOException, BPMNInvalidTransition {
		Json2ModelAction json2ModelAction = new Json2ModelAction();
		
		OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		
		File formAssociationFile = new File(formAssociationPath);
		String formAssociationJson = Files.toString(formAssociationFile, Charsets.UTF_8);
		
		List<Action> actions = json2ModelAction.parseJson(formAssociationJson, modelOntology);
		
		Map<String, List<Action>> configurationMap = newHashMap();
		configurationMap.put(createPurchaseOrderURI, actions);
		FormsConfiguration formConfiguration = new FormsConfiguration();
		formConfiguration.setConfiguration(configurationMap);
		Form form = new Form(formConfiguration);
		Simulator simulator = new OntologySimulator(bpmnOntology);
		ModelManipulator manipulator = new OntologyModelManipulator(modelOntology, form);
		
		ConsoleEngine engine = new ConsoleEngine(simulator, manipulator);
		engine.run();
	}

}
