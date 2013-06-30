package it.polimi.jbps.bpmn.simulation;

import com.hp.hpl.jena.ontology.OntModel;

public class SimulatorImpTest extends SimulatorTest {

	@Override
	protected Simulator getSimulator(OntModel bpmnOntologyModel) {
		return new SimulatorImp(bpmnOntologyModel);
	}

}
