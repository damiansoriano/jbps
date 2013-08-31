package it.polimi.jbps.bpmn.simulation;

import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;

import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;

public class OntologySimulatorTest extends SimulatorTest {
	
	private Map<String, String> resources;
	
	public OntologySimulatorTest () {
		resources = newHashMap();
		resources.put("SimplePurchaseRequestBPMN", "./src/test/resources/it/polimi/bpmn/simulation/SimplePurchaseRequestBPMN.owl");
	}
	
	@Override
	protected String getResource(String resourceName) {
		return resources.get(resourceName);
	}

	@Override
	protected Simulator getSimulator(String resource) {
		OntModel bpmnOntology = getOntologyFromFile(resource);
		return new OntologySimulator(bpmnOntology);
	}

}
