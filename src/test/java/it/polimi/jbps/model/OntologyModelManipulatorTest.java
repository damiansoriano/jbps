package it.polimi.jbps.model;

import it.polimi.jbps.bpmn.simulation.Simulator;
import it.polimi.jbps.bpmn.simulation.OntologySimulator;
import it.polimi.jbps.form.Form;

import com.hp.hpl.jena.ontology.OntModel;

public class OntologyModelManipulatorTest extends ModelManipulatorTest {
	
	protected final String baseURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequestModel.owl";
	
	@Override
	protected ModelManipulator getModelManipulator(OntModel ontologyModel, Form form) {
		return new OntologyModelManipulator(ontologyModel, form, baseURI);
	}

	@Override
	protected Simulator getSimulator(OntModel bpmnOntologyModel) {
		return new OntologySimulator(bpmnOntologyModel);
	}

}
