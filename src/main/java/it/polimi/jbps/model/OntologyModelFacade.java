package it.polimi.jbps.model;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Lists.newLinkedList;
import it.polimi.jbps.entities.JBPSClass;
import it.polimi.jbps.entities.JBPSIndividual;

import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyModelFacade implements ModelFacade {
	
	protected final OntModel ontologyModel;
	
	public OntologyModelFacade(OntModel ontologyModel) {
		this.ontologyModel = ontologyModel;
	}
	
	@Override
	public List<JBPSIndividual> getAllIndividuals() {
		List<JBPSIndividual> allIndividuals = newLinkedList();
		
		ExtendedIterator<Individual> individualIterator = ontologyModel.listIndividuals();
		while (individualIterator.hasNext()) {
			allIndividuals.add(new JBPSIndividual(individualIterator.next()));
		}
		
		return allIndividuals;
	}

	@Override
	public List<JBPSClass> getAllClasses(JBPSIndividual individual) {
		return getClasses(individual, false);
	}
	
	@Override
	public List<JBPSClass> getAllDirectClasses(JBPSIndividual individual) {
		return getClasses(individual, true);
	}
	
	protected List<JBPSClass> getClasses(JBPSIndividual individual, boolean direct) {
		List<JBPSClass> allClasses = newLinkedList();
		
		ExtendedIterator<OntClass> listOntClasses = individual.getIndividual().listOntClasses(direct);
		while (listOntClasses.hasNext()) {
			allClasses.add(new JBPSClass(listOntClasses.next()));
		}
		
		return allClasses;
	}

	@Override
	public Map<JBPSIndividual, List<JBPSClass>> allClassesByIndividuals(List<JBPSIndividual> individuals) {
		return classesByIndividuals(individuals, false);
	}

	@Override
	public Map<JBPSIndividual, List<JBPSClass>> directClassesByIndividuals(List<JBPSIndividual> individuals) {
		return classesByIndividuals(individuals, true);
	}
	
	protected Map<JBPSIndividual, List<JBPSClass>> classesByIndividuals(List<JBPSIndividual> individuals, boolean direct) {
		Map<JBPSIndividual, List<JBPSClass>> map = newHashMap();
		for (JBPSIndividual individual : individuals) {
			map.put(individual, getClasses(individual, direct));
		}
		return map;
	}
}
