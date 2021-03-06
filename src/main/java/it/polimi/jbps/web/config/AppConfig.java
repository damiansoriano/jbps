package it.polimi.jbps.web.config;

import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import it.polimi.jbps.bpmn.simulation.OntologySimulator;
import it.polimi.jbps.engine.Engine;
import it.polimi.jbps.engine.SimpleEngine;
import it.polimi.jbps.form.Form;
import it.polimi.jbps.form.FormsConfiguration;
import it.polimi.jbps.model.ModelFacade;
import it.polimi.jbps.model.OntologyModelFacade;
import it.polimi.jbps.model.OntologyModelManipulator;

import java.io.IOException;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.hp.hpl.jena.ontology.OntModel;

@Configuration
@Lazy
@Log4j
public class AppConfig {
	
	private final Map<String, String> bpmnOntologyPaths;
	private final String modelOntologyPath;
	private final Map<String, String> formAssociationPaths;
	private final Map<String, String> lanesDescriptions;
	private final String baseURI;
	
	public AppConfig(Map<String, String> bpmnOntologyPaths, String modelOntologyPath,
			Map<String, String> formAssociationPaths, Map<String, String> lanesDescriptions,
			String baseURI) {
		
		log.info(String.format("bpmnOntologyPaths: %s", bpmnOntologyPaths));
		log.info(String.format("modelOntologyPath: %s", modelOntologyPath));
		log.info(String.format("formAssociationPaths: %s", formAssociationPaths));
		log.info(String.format("lanesDescriptions: %s", lanesDescriptions));
		
		this.bpmnOntologyPaths = bpmnOntologyPaths;
		this.modelOntologyPath = modelOntologyPath;
		this.formAssociationPaths = formAssociationPaths;
		this.lanesDescriptions = lanesDescriptions;
		this.baseURI = baseURI;
	}
	
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
    
    @Bean
    public Map<String, String> lanesDescriptions() {
    	return lanesDescriptions;
    }
    
    @Bean
    public Map<String, OntModel> bpmnOntologyByLane() {
    	Map<String, OntModel> bpmnOntologyByLane = newHashMap();
    	for (String lane : bpmnOntologyPaths.keySet()) {
    		bpmnOntologyByLane.put(lane, getOntologyFromFile(bpmnOntologyPaths.get(lane)));
    	}
    	return bpmnOntologyByLane;
    }
    
    @Bean
    public OntModel modelOntology() {
    	return getOntologyFromFile(modelOntologyPath);
    }
    
    @Bean
    public ModelFacade modelFacade() {
    	return new OntologyModelFacade(modelOntology());
    }
    
    @Bean
    public Map<String, Engine> engines() throws IOException {
    	Map<String, Engine> engines = newHashMap();
    	Map<String, OntModel> bpmnOntologyByLane = bpmnOntologyByLane();
    	
    	for (String lane : bpmnOntologyByLane.keySet()) {
    		String laneFormPath = formAssociationPaths.get(lane);
    		
    		OntModel modelOntology = modelOntology();
    		
    		Form form = new Form(FormsConfiguration.createFromFile(laneFormPath, modelOntology));
    		
    		SimpleEngine simpleEngine = new SimpleEngine(
    				new OntologySimulator(bpmnOntologyByLane.get(lane)),
    				new OntologyModelManipulator(modelOntology, form, baseURI),
    				modelFacade());
    		
    		engines.put(lane, simpleEngine);
    	}
		return engines;
    }
}
