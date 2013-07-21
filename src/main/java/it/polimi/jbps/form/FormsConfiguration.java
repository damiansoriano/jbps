package it.polimi.jbps.form;

import it.polimi.jbps.actions.Action;
import it.polimi.jbps.io.Json2ModelAction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.hp.hpl.jena.ontology.OntModel;

public class FormsConfiguration {
	
	@Getter
	private Map<String, List<Action>> configuration;
	
	private FormsConfiguration() { }
	
	public static FormsConfiguration createFromFile(String filePath, OntModel ontologyModel) throws IOException {
		File file = new File(filePath);
		String json = Files.toString(file, Charsets.UTF_8);
		
		return createFromJson(json, ontologyModel);
	}
	
	public static FormsConfiguration createFromJson(String jsonFormDefinition, OntModel ontologyModel) throws IOException {
		Json2ModelAction json2Model = new Json2ModelAction();
		FormsConfiguration formsConfiguration = new FormsConfiguration();
		formsConfiguration.configuration = json2Model.parseFormsConfiguration(jsonFormDefinition, ontologyModel);
		return formsConfiguration;
	}
	
}
