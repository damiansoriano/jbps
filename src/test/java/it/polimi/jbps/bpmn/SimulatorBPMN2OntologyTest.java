package it.polimi.jbps.bpmn;

import static com.google.common.collect.Maps.newHashMap;
import it.polimi.jbps.bpmn.simulation.OntologySimulator;
import it.polimi.jbps.bpmn.simulation.Simulator;
import it.polimi.jbps.bpmn.simulation.SimulatorTest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public abstract class SimulatorBPMN2OntologyTest extends SimulatorTest {
	
	private Map<String, String> resources;
	
	public SimulatorBPMN2OntologyTest () {
		resources = newHashMap();
		resources.put("SimplePurchaseRequestBPMN", "./src/test/resources/it/polimi/bpmn/simulation/purchaseOrderDiagarm.bpmn");
	}
	
	protected abstract BPMN2Ontology bpmn2Ontology();
	
	@Override
	protected String getResource(String resourceName) {
		return resources.get(resourceName);
	}

	@Override
	protected Simulator getSimulator(String resource) throws IOException {
		BPMN2Ontology bpmn2Ontology = bpmn2Ontology();
		return new OntologySimulator(bpmn2Ontology.createOntology(FileUtils.readFileToString(new File(resource))));
	}

}
