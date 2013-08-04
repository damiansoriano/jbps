package it.polimi.jbps.web.controller;

import it.polimi.jbps.entities.JBPSIndividual;
import it.polimi.jbps.model.ModelFacade;
import it.polimi.jbps.model.OntologyModelFacade;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

@Controller
@Log4j
public class ModelController {
	
	private ModelFacade modelFacade;
	
	public ModelController(OntModel ontology) {
		modelFacade = new OntologyModelFacade(ontology);
	}
	
	@RequestMapping(value = "/instances")
    public String instances(HttpServletRequest request, ModelMap model) {
		log.info(request);
		List<JBPSIndividual> allIndividuals = modelFacade.getAllIndividuals();
		model.addAttribute("individuals", allIndividuals);
		model.addAttribute("classesByIndividuals", modelFacade.directClassesByIndividuals(allIndividuals));
		
		return "instances";
    }
	
	@RequestMapping(value = "/instance/")
    public String startSimulation(@RequestParam String individualURI, HttpServletRequest request, ModelMap model) {
		log.info(request);
		
		JBPSIndividual individual = modelFacade.getIndividual(individualURI);
		
		Map<Property, RDFNode> properties = modelFacade.getProperties(individual);
		model.addAttribute("individual", individual);
		model.addAttribute("properties", properties);
		
		for (Property prop : properties.keySet()) {
			properties.get(prop).asResource().getURI();
		}
		
		return "instance";
	}
}
