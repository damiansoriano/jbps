package it.polimi.jbps.web.controller;

import it.polimi.jbps.exception.ResourceNotFoundException;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hp.hpl.jena.ontology.OntModel;

@Controller
@Log4j
public class OntologiesController {
	
	private OntModel modelOntology;
	private Map<String, OntModel> bpmnOntologyByLane;
	
	public OntologiesController(OntModel modelOntology, Map<String, OntModel> bpmnOntologyByLane) {
		this.modelOntology = modelOntology;
		this.bpmnOntologyByLane = bpmnOntologyByLane;
	}
	
	@RequestMapping(value = "/dumpModelOntology")
    public void instances(HttpServletRequest request, ModelMap model, HttpServletResponse response) throws IOException {
		log.info(request);
		modelOntology.write(response.getWriter());
    }
	
	@RequestMapping(value = "/{lane}/dumpBPMOntology")
    public void dumpLaneOntology(@PathVariable String lane, HttpServletRequest request, ModelMap model, HttpServletResponse response) throws IOException {
		log.info(request);
		if (bpmnOntologyByLane.containsKey(lane)) {
			bpmnOntologyByLane.get(lane).write(response.getWriter());
		} else {
			throw new ResourceNotFoundException();
		}
	}
}
