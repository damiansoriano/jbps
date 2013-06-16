package it.polimi.bpmn;

import static com.google.common.collect.Lists.newLinkedList;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class UserTaskRepository {
	
	protected final OntModel ontologyModel;
	
	protected final String userTaskURI = "http://dkm.fbk.eu/index.php/BPMN_Ontology#user_task";
	
	public UserTaskRepository(OntModel ontologyModel) {
		this.ontologyModel = ontologyModel;
	}
	
	public List<Individual> getUserTasks() {
		List<Individual> userTasks = newLinkedList();
		OntClass userTaskClass = ontologyModel.createClass(userTaskURI);
		
		ExtendedIterator<? extends OntResource> userTaskInstances = userTaskClass.listInstances(true);
		while(userTaskInstances.hasNext()) {
			OntResource userTask = userTaskInstances.next();
			if (userTask.isIndividual()) { userTasks.add(userTask.asIndividual()); }
		}
		
		return userTasks;
	}
}




