package it.polimi.jbps.bpmn;

import it.polimi.jbps.constants.BPMNConstants;
import it.polimi.jbps.utils.OntologyUtils;

import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

public class DOMBPMN2Ontology implements BPMN2Ontology {
	
	private final String bpmnBaseOntologyPath;
	
	private final String baseURIForNameing;
	
	private final String START_EVENT_XPATH = "//bpmn2:startEvent";
	private final String END_EVENT_XPATH = "//bpmn2:endEvent";
	private final String TASK_XPATH = "//bpmn2:task";
	private final String SEQUECE_FLOW_XPATH = "//bpmn2:sequenceFlow";
	
	private final String INCOMMING_SEQUENCE_FLOW_XPATH = "bpmn2:incoming";
	private final String OUTGOING_SEQUENCE_FLOW_XPATH = "bpmn2:outgoing";
	
	public DOMBPMN2Ontology(String bpmnBaseOntologyPath, String baseURIForNameing) {
		this.bpmnBaseOntologyPath = bpmnBaseOntologyPath;
		this.baseURIForNameing = baseURIForNameing;
	}
	
	private String generateIndividualName(String name) {
		return String.format("%s#%s", baseURIForNameing, name);
	}

	@Override
	public OntModel createOntology(String text) throws IOException {
		OntModel bpmnBaseOntology = OntologyUtils.getOntologyFromFile(bpmnBaseOntologyPath);
		Document document;
		try {
			document = DocumentHelper.parseText(text);
		} catch (DocumentException e) {
			throw new IOException(e);
		}
		
		return createFlowProperties(
					createSequenceFlow(
						createTasks(
							createEvents(bpmnBaseOntology, document), document), document), document);
	}
	
	private OntModel createEvents(OntModel bpmnBaseOntology, Document document) {
		return createIndividuals(
				BPMNConstants.EVENT_END_URI,
				END_EVENT_XPATH,
				createIndividuals(
						BPMNConstants.EVENT_START_URI,
						START_EVENT_XPATH,
						bpmnBaseOntology,
						document),
				document);
	}
	
	private OntModel createTasks(OntModel bpmnBaseOntology, Document document) {
		return createIndividuals(BPMNConstants.USER_TASK_URI, TASK_XPATH, bpmnBaseOntology, document);
	}
	
	private OntModel createSequenceFlow(OntModel bpmnBaseOntology, Document document) {
		return createIndividuals(BPMNConstants.SEQUENCE_FLOW_URI, SEQUECE_FLOW_XPATH, bpmnBaseOntology, document);
	}
	
	@SuppressWarnings("unchecked")
	private OntModel createIndividuals(String classURI, String xpath, OntModel bpmnBaseOntology, Document document) {
		OntClass ontClass = bpmnBaseOntology.getOntClass(classURI);
		List<Node> nodes = document.selectNodes(xpath);
		for (Node node : nodes) {
			Element element = (Element) node;
			bpmnBaseOntology.createIndividual(
					generateIndividualName(element.attributeValue("id")),
					ontClass);
		}
		return bpmnBaseOntology;
	}
	
	private OntModel createFlowProperties(OntModel bpmnBaseOntology, Document document) {
		return createFlowProperties(BPMNConstants.USER_TASK_URI, TASK_XPATH,
					createFlowProperties(BPMNConstants.EVENT_END_URI, END_EVENT_XPATH,
						createFlowProperties(BPMNConstants.EVENT_START_URI, START_EVENT_XPATH, bpmnBaseOntology, document),
					document),
				document);
	}
	
	private OntModel createFlowProperties(String classURI, String xpath, OntModel bpmnBaseOntology, Document document) {
		OntClass ontClass = bpmnBaseOntology.getOntClass(classURI);
		
		OntProperty sequenceFlowSource = bpmnBaseOntology.getOntProperty(BPMNConstants.SEQUENCE_FLOW_SOURCE_URI);
		OntProperty sequenceFlowTarget = bpmnBaseOntology.getOntProperty(BPMNConstants.SEQUENCE_FLOW_TARGET_URI);
		
		List<Node> nodes = document.selectNodes(xpath);
		
		for (Node node : nodes) {
			Element element = (Element) node;
			Individual taskOrEvent = bpmnBaseOntology.getIndividual(generateIndividualName(element.attributeValue("id")));
			
			List<Node> incommingSequenceFlows = node.selectNodes(INCOMMING_SEQUENCE_FLOW_XPATH);
			for (Node incommingSequenceFlowNode : incommingSequenceFlows) {
				Element incommingSequenceFlow = (Element) incommingSequenceFlowNode;
				Individual sequenceFlow = bpmnBaseOntology.getIndividual(generateIndividualName(incommingSequenceFlow.getText()));
				
				sequenceFlow.addProperty(sequenceFlowTarget, taskOrEvent);
			}
		}
		
		for (Node node : nodes) {
			Element element = (Element) node;
			Individual taskOrEvent = bpmnBaseOntology.getIndividual(generateIndividualName(element.attributeValue("id")));
			
			List<Node> outgoingSequenceFlows = node.selectNodes(OUTGOING_SEQUENCE_FLOW_XPATH);
			for (Node outgoingSequenceFlowNode : outgoingSequenceFlows) {
				Element outgoingSequenceFlow = (Element) outgoingSequenceFlowNode;
				Individual sequenceFlow = bpmnBaseOntology.getIndividual(generateIndividualName(outgoingSequenceFlow.getText()));
				
				sequenceFlow.addProperty(sequenceFlowSource, taskOrEvent);
			}
		}
		
		return bpmnBaseOntology;
	}

}
