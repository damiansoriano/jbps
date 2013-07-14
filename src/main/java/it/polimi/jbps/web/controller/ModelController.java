package it.polimi.jbps.web.controller;

import java.util.List;

import it.polimi.jbps.entities.JBPSIndividual;
import it.polimi.jbps.model.ModelFacade;
import it.polimi.jbps.model.OntologyModelFacade;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hp.hpl.jena.ontology.OntModel;

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
}
