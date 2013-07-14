package it.polimi.jbps.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hp.hpl.jena.ontology.OntModel;

@Controller
@Log4j
public class OntologiesController {
	
	private OntModel modelOntology;
	
	public OntologiesController(OntModel modelOntology) {
		this.modelOntology = modelOntology;
	}
	
	@RequestMapping(value = "/dumpModelOntology")
    public void instances(HttpServletRequest request, ModelMap model, HttpServletResponse response) throws IOException {
		log.info(request);
		modelOntology.write(response.getWriter());
    }
}
