package it.polimi.jbps.web.config;

import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.bpmn.simulation.OntologySimulator;
import it.polimi.jbps.engine.Engine;
import it.polimi.jbps.engine.SimpleEngine;
import it.polimi.jbps.form.Form;
import it.polimi.jbps.form.FormsConfiguration;
import it.polimi.jbps.io.Json2ModelAction;
import it.polimi.jbps.model.OntologyModelManipulator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.hp.hpl.jena.ontology.OntModel;

@Configuration
@Lazy
public class AppConfig {
	
	private final static String createPurchaseOrderURI = "http://www.semanticweb.org/ontologies/2013/5/PurchaseRequest.owl#createPurchaseOrder";
	
	private final Map<String, String> bpmnOntologyPaths;
	private final String modelOntologyPath;
	private final Map<String, String> formAssociationPaths;
	private final Map<String, String> lanesDescriptions;
	
	public AppConfig(Map<String, String> bpmnOntologyPaths, String modelOntologyPath,
			Map<String, String> formAssociationPaths, Map<String, String> lanesDescriptions) {
		this.bpmnOntologyPaths = bpmnOntologyPaths;
		this.modelOntologyPath = modelOntologyPath;
		this.formAssociationPaths = formAssociationPaths;
		this.lanesDescriptions = lanesDescriptions;
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
    public OntModel modelOntology() {
    	return getOntologyFromFile(modelOntologyPath);
    }
    
    @Bean
    public Map<String, Engine> engines() throws IOException {
    	Map<String, Engine> engines = newHashMap();
    	
    	for (String lane : bpmnOntologyPaths.keySet()) {
    		String laneBPMNPath = bpmnOntologyPaths.get(lane);
    		String laneFormPath = formAssociationPaths.get(lane);
    		
    		OntModel bpmnOntology = getOntologyFromFile(laneBPMNPath);
    		OntModel modelOntology = modelOntology();
    		File formAssociationFile = new File(laneFormPath);
    		String formAssociationJson = Files.toString(formAssociationFile, Charsets.UTF_8);
    		
    		Json2ModelAction json2ModelAction = new Json2ModelAction();
    		List<Action> actions = json2ModelAction.parseJson(formAssociationJson, modelOntology);
    		
    		Map<String, List<Action>> configurationMap = newHashMap();
    		configurationMap.put(createPurchaseOrderURI, actions);
    		FormsConfiguration formConfiguration = new FormsConfiguration();
    		formConfiguration.setConfiguration(configurationMap);
    		Form form = new Form(formConfiguration);
    		
    		SimpleEngine simpleEngine = new SimpleEngine(
    				new OntologySimulator(bpmnOntology),
    				new OntologyModelManipulator(modelOntology, form));
    		
    		engines.put(lane, simpleEngine);
    	}
		return engines;
    }
}
