package it.polimi.utils;

import static com.google.common.collect.Lists.newLinkedList;

import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyUtils {
	
	public static List<Individual> getIndividuals(OntModel ontologyModel, String classURI) {
		List<Individual> userTasks = newLinkedList();
		OntClass userTaskClass = ontologyModel.createClass(classURI);
		
		ExtendedIterator<? extends OntResource> userTaskInstances = userTaskClass.listInstances(true);
		while(userTaskInstances.hasNext()) {
			OntResource userTask = userTaskInstances.next();
			if (userTask.isIndividual()) { userTasks.add(userTask.asIndividual()); }
		}
		
		return userTasks;
	}
	
}
