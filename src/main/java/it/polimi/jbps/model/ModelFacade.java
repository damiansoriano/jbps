package it.polimi.jbps.model;

import it.polimi.jbps.entities.JBPSClass;
import it.polimi.jbps.entities.JBPSIndividual;

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;

public interface ModelFacade {
	
	Optional<JBPSClass> getClassFromURI(String classURI);
	
	List<JBPSIndividual> getAllIndividuals();
	
	List<JBPSClass> getAllClasses(JBPSIndividual individual);
	
	List<JBPSClass> getAllDirectClasses(JBPSIndividual individual);
	
	Map<JBPSIndividual, List<JBPSClass>> allClassesByIndividuals(List<JBPSIndividual> individual);
	
	Map<JBPSIndividual, List<JBPSClass>> directClassesByIndividuals(List<JBPSIndividual> individual);
	
	List<JBPSIndividual> getIndividualsOfClass(JBPSClass jbpsClass, boolean direct);
}
