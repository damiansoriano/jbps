package it.polimi.jbps.io;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.ConstantsUtils.parsePropertyType;
import static it.polimi.jbps.utils.ObjectUtils.isNotNull;
import static it.polimi.jbps.utils.ObjectUtils.isNull;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.actions.ActionType;
import it.polimi.jbps.actions.PropertyAssignment;
import it.polimi.jbps.entities.JBPSClass;
import it.polimi.jbps.entities.JBPSProperty;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

public class Json2ModelAction {
	
	protected final ObjectMapper mapper;
	
	protected final String FORMS = "forms";
	
	protected final String FORM = "form";
	protected final String STATE = "state";
	protected final String ACTIONS = "actions";
	
	protected final String INSERT = "insert";
	protected final String UPDATE = "update";
	protected final String DELETE = "delete";
	protected final String CLASS_URI = "classURI";
	protected final String VARIABLE_NAME = "variableName";
	protected final String INDIVIDUAL_URI = "individualURI";
	protected final String PROPERTY_VALUES = "propertyValues";
	protected final String PROPERTY_TYPE = "propertyType";
	protected final String PROPERTY_URI = "propertyURI";
	protected final String PROPERTY_VALUE = "propertyValue";
	
	public Json2ModelAction() {
		mapper = new ObjectMapper();
	}
	
	public Map<String, List<Action>> parseFormsConfiguration(String json, OntModel ontologyModel) throws JsonParseException, JsonMappingException, IOException {
		Map<String, List<Action>> formsConfiguration = newHashMap();
		
		JsonNode rootNode = mapper.readValue(json, JsonNode.class);
		
		Iterator<JsonNode> formsIterator = rootNode.get(FORMS).elements();
		
		while(formsIterator.hasNext()) {
			JsonNode formJson = formsIterator.next().get(FORM);
			String state = formJson.get(STATE).textValue();
			Optional<List<Action>> parseJson = parseJson(formJson.get(ACTIONS), ontologyModel);
			if (parseJson.isPresent()) {
				formsConfiguration.put(state, parseJson.get());
			}
		}
		
		return formsConfiguration;
	}
	
	public Optional<List<Action>> parseJson(JsonNode actionsNode, OntModel ontologyModel) throws JsonParseException, JsonMappingException, IOException {
		List<Action> actions = newLinkedList();
		
		if (actionsNode.has(INSERT)) {
			JsonNode inserts = actionsNode.get(INSERT);
			actions.addAll(parseJsonInner(inserts, ActionType.INSERT, ontologyModel));
		}
		if (actionsNode.has(UPDATE)) {
			JsonNode updates = actionsNode.get(UPDATE);
			actions.addAll(parseJsonInner(updates, ActionType.UPDATE, ontologyModel));
		}
		if (actionsNode.has(DELETE)) {
			JsonNode deletes = actionsNode.get(DELETE);
			actions.addAll(parseJsonInner(deletes, ActionType.DELETE, ontologyModel));
		}
		
		if (isNull(actions)) { return Optional.absent(); }
		return Optional.of(actions);
	}
	
	private List<Action> parseJsonInner(JsonNode operationDefinition, ActionType actionType, OntModel ontologyModel)
			throws JsonParseException, JsonMappingException, IOException {
		List<Action> actions = newLinkedList();
		
		Iterator<JsonNode> operationDefinitionIterator = operationDefinition.elements();
		while (operationDefinitionIterator.hasNext()) {
			JsonNode insert = operationDefinitionIterator.next();
			if (isNull(insert)) { continue; }
			
			Action action = new Action();
			action.setActionType(actionType);
			
			String classURI = insert.get(CLASS_URI).textValue();
			
			OntClass ontClass = ontologyModel.getOntClass(classURI);
			
			action.setJbpsClass(new JBPSClass(ontClass));
			
			if (insert.has(VARIABLE_NAME)) {
				action.setVariableName(insert.get(VARIABLE_NAME).textValue());
			}
			
			if(isNotNull(insert.get(INDIVIDUAL_URI))) {
				action.setIndividualURI(insert.get(INDIVIDUAL_URI).textValue());
			}
			
			Iterator<JsonNode> propertyValuesIterator = insert.get(PROPERTY_VALUES).elements();
			while(propertyValuesIterator.hasNext()) {
				JsonNode propertyValue = propertyValuesIterator.next();
				action.getPropertyAssignments().add(parsePropertyAssignment(propertyValue, ontologyModel));
			}
			
			actions.add(action);
		}
		
		return actions;
	}
	
	public PropertyAssignment parsePropertyAssignment(JsonNode jsonNode, OntModel ontologyModel) {
		PropertyAssignment propertyAssignment = new PropertyAssignment();
		
		String propertyURI = jsonNode.get(PROPERTY_URI).textValue();
		OntProperty property = ontologyModel.getOntProperty(propertyURI);
		propertyAssignment.setJbpsProperty(new JBPSProperty(property));
		
		String propertyType = jsonNode.get(PROPERTY_TYPE).textValue();
		propertyAssignment.setPropertyType(parsePropertyType(propertyType));
		
		if (isNotNull(jsonNode.get(PROPERTY_VALUE))) {
			propertyAssignment.setPropertyValue(jsonNode.get(PROPERTY_VALUE).textValue());
		}
		
		return propertyAssignment;
	}
	
}
