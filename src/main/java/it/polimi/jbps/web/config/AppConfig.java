package it.polimi.jbps.web.config;

import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.OntologyUtils.getOntologyFromFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import it.polimi.jbps.actions.Action;
import it.polimi.jbps.bpmn.simulation.SimulatorImpToDelete;
import it.polimi.jbps.form.Form;
import it.polimi.jbps.form.FormsConfiguration;
import it.polimi.jbps.io.Json2ModelAction;

import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${bpmnOntologyPath}")
	private String bpmnOntologyPath;
	
	@Value("${modelOntologyPath}")
	private String modelOntologyPath;
	
	@Value("${formAssociationPath}")
	private String formAssociationPath;
	
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
    
    @Bean
    public SimulatorImpToDelete simulator() throws IOException {
    	OntModel bpmnOntology = getOntologyFromFile(bpmnOntologyPath);
		OntModel modelOntology = getOntologyFromFile(modelOntologyPath);
		File formAssociationFile = new File(formAssociationPath);
		String formAssociationJson = Files.toString(formAssociationFile, Charsets.UTF_8);
		
		Json2ModelAction json2ModelAction = new Json2ModelAction();
		List<Action> actions = json2ModelAction.parseJson(formAssociationJson);
		
		Map<String, List<Action>> configurationMap = newHashMap();
		configurationMap.put(createPurchaseOrderURI, actions);
		FormsConfiguration formConfiguration = new FormsConfiguration();
		formConfiguration.setConfiguration(configurationMap);
		Form form = new Form(formConfiguration);
		return new SimulatorImpToDelete(bpmnOntology, modelOntology, form);
    }
}
