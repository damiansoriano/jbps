package it.polimi.jbps.model;

import it.polimi.jbps.entities.JBPSClass;
import it.polimi.jbps.entities.JBPSIndividual;
import it.polimi.jbps.entities.JBPSProperty;

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

public interface ModelFacade {
	
	Optional<JBPSClass> getClassFromURI(String classURI);
	
	List<JBPSIndividual> getAllIndividuals();
	
	JBPSIndividual getIndividual(String individualURI);
	
	JBPSProperty getProperty(String propertyURI);
	
	Map<Property, RDFNode> getProperties(JBPSIndividual individualURI);
	
	List<JBPSClass> getAllClasses(JBPSIndividual individual);
	
	List<JBPSClass> getAllDirectClasses(JBPSIndividual individual);
	
	Map<JBPSIndividual, List<JBPSClass>> allClassesByIndividuals(List<JBPSIndividual> individual);
	
	Map<JBPSIndividual, List<JBPSClass>> directClassesByIndividuals(List<JBPSIndividual> individual);
	
	List<JBPSIndividual> getIndividualsOfClass(JBPSClass jbpsClass, boolean direct);
}
