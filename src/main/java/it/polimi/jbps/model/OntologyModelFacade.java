package it.polimi.jbps.model;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.ObjectUtils.isNull;
import it.polimi.jbps.entities.JBPSClass;
import it.polimi.jbps.entities.JBPSIndividual;
import it.polimi.jbps.entities.JBPSProperty;

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
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
		
		ExtendedIterator<Resource> listRDFTypes = individual.getIndividual().listRDFTypes(direct);
		while (listRDFTypes.hasNext()) {
			Resource rdfType = listRDFTypes.next();
			if (rdfType instanceof OntClass) {
				allClasses.add(new JBPSClass((OntClass) rdfType));
			}
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

	@Override
	public Optional<JBPSClass> getClassFromURI(String classURI) {
		OntClass ontClass = ontologyModel.getOntClass(classURI);
		if (isNull(ontClass)) {
			return Optional.absent();
		}
		return Optional.of(new JBPSClass(ontClass));
	}

	@Override
	public List<JBPSIndividual> getIndividualsOfClass(JBPSClass jbpsClass,
			boolean direct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JBPSIndividual getIndividual(String individualURI) {
		return new JBPSIndividual(ontologyModel.getIndividual(individualURI));
	}

	@Override
	public Map<Property, RDFNode> getProperties(JBPSIndividual individual) {
		Map<Property, RDFNode> map = newHashMap();
		Selector selector = new SimpleSelector(individual.getIndividual().asResource(), (Property)null, (RDFNode)null);
		StmtIterator listStatements = ontologyModel.listStatements(selector);
		
		while(listStatements.hasNext()) {
			Statement statement = listStatements.next();
			Property property = statement.getPredicate();
			RDFNode object = statement.getObject();
			map.put(property, object);
		}
		
		return map;
	}

	@Override
	public JBPSProperty getProperty(String propertyURI) {
		return new JBPSProperty(ontologyModel.getOntProperty(propertyURI));
	}
}
