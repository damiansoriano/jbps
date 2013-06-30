package it.polimi.jbps.model;

import it.polimi.jbps.bpmn.simulation.Simulator;
import it.polimi.jbps.bpmn.simulation.SimulatorImp;
import it.polimi.jbps.form.Form;

import com.hp.hpl.jena.ontology.OntModel;

public class ModelManipulatorImpTest extends ModelManipulatorTest {

	@Override
	protected ModelManipulator getModelManipulator(OntModel ontologyModel, Form form) {
		return new ModelManipulatorImp(ontologyModel, form);
	}

	@Override
	protected Simulator getSimulator(OntModel bpmnOntologyModel) {
		return new SimulatorImp(bpmnOntologyModel);
	}

}
