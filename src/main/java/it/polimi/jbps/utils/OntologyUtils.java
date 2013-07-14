package it.polimi.jbps.utils;

import static com.google.common.collect.Lists.newLinkedList;
import static it.polimi.jbps.utils.ObjectUtils.isNotNull;

import java.util.List;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyUtils {
	
	public static List<Individual> getIndividuals(OntModel ontologyModel, String classURI) {
		return getIndividuals(ontologyModel, classURI, true);
	}
	
	public static List<Individual> getIndividuals(OntModel ontologyModel, String classURI, boolean direct) {
		List<Individual> userTasks = newLinkedList();
		OntClass userTaskClass = ontologyModel.createClass(classURI);
		
		ExtendedIterator<? extends OntResource> userTaskInstances = userTaskClass.listInstances(direct);
		while(userTaskInstances.hasNext()) {
			OntResource userTask = userTaskInstances.next();
			if (userTask.isIndividual()) { userTasks.add(userTask.asIndividual()); }
		}
		
		return userTasks;
	}
	
	public static List<Individual> getIndividualsInDomain(OntModel ontologyModel, Property property, Individual rangeIndividual) {
		List<Individual> range = newLinkedList();
		StmtIterator listStatements = ontologyModel.listStatements(null, property, rangeIndividual);
		
		while (listStatements.hasNext()) {
			Statement statement = listStatements.next();
			Resource subject = statement.getSubject();
			
			Individual individual = ontologyModel.getIndividual(subject.getURI());
			if (isNotNull(individual)) { range.add(individual); }
		}
		
		return range;
	}
	
	public static List<Individual> getIndividualsInRange(OntModel ontologyModel, Individual domainIndividual, Property property) {
		List<Individual> range = newLinkedList();
		NodeIterator nodeIterator = domainIndividual.listPropertyValues(property);
		
		while (nodeIterator.hasNext()) {
			RDFNode rdfNode = nodeIterator.next();
			Individual individual = ontologyModel.getIndividual(rdfNode.asNode().getURI());
			if (isNotNull(individual)) { range.add(individual); }
		}
		
		return range;
	}
	
	public static OntModel getOntologyFromFile(String filePath) {
		Model model = FileManager.get().loadModel(filePath);
		OntModel ontologyModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, model);
		return ontologyModel;
	}
}
