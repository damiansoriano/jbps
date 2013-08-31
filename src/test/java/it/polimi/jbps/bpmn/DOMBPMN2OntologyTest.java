package it.polimi.jbps.bpmn;

public class DOMBPMN2OntologyTest extends BPMN2OntologyTest {

	private final String bpmnBaseOntologyPath = "./src/test/resources/it/polimi/bpmn/simulation/OntoBPMN.owl";
	private final String baseURIForNameing = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl";
	
	@Override
	protected BPMN2Ontology bpmn2Ontology() {
		return new DOMBPMN2Ontology(bpmnBaseOntologyPath, baseURIForNameing);
	}
}
