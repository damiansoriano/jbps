package it.polimi.io;

import static com.google.common.collect.Lists.newLinkedList;
import static it.polimi.utils.ConstantsUtils.parsePropertyType;
import static it.polimi.utils.ObjectUtils.isNull;
import static it.polimi.utils.ObjectUtils.isNotNull;
import it.polimi.actions.Action;
import it.polimi.actions.PropertyAssignment;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json2ModelAction {
	
	protected final String INSERT = "insert";
	protected final String CLASS_URI = "classURI";
	protected final String INDIVIDUAL_URI = "individualURI";
	protected final String PROPERTY_VALUES = "propertyValues";
	protected final String PROPERTY_TYPE = "propertyType";
	protected final String PROPERTY_URI = "propertyURI";
	protected final String PROPERTY_VALUE = "propertyValue";
	
	public List<Action> parseJson(String json) throws JsonParseException, JsonMappingException, IOException {
		List<Action> actions = newLinkedList();
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(json, JsonNode.class);
		
		JsonNode inserts = rootNode.get(INSERT);
		if (isNull(inserts)) { return actions; }
		
		Iterator<JsonNode> insertIterator = inserts.elements();
		while (insertIterator.hasNext()) {
			JsonNode insert = insertIterator.next();
			if (isNull(insert)) { continue; }
			
			Action action = new Action();
			
			String classURI = insert.get(CLASS_URI).textValue();
			action.setClassURI(classURI);
			
			if(isNotNull(insert.get(INDIVIDUAL_URI))) {
				action.setIndividualURI(insert.get(INDIVIDUAL_URI).textValue());
			}
			
			Iterator<JsonNode> propertyValuesIterator = insert.get(PROPERTY_VALUES).elements();
			while(propertyValuesIterator.hasNext()) {
				JsonNode propertyValue = propertyValuesIterator.next();
				action.getActions().add(parsePropertyAssignment(propertyValue));
			}
			
			actions.add(action);
		}
		
		return actions;
	}
	
	public PropertyAssignment parsePropertyAssignment(JsonNode jsonNode) {
		PropertyAssignment propertyAssignment = new PropertyAssignment();
		
		String propertyType = jsonNode.get(PROPERTY_TYPE).textValue();
		String propertyURI = jsonNode.get(PROPERTY_URI).textValue();
		String propertyValue = jsonNode.get(PROPERTY_VALUE).textValue();
		
		propertyAssignment.setPropertyType(parsePropertyType(propertyType));
		propertyAssignment.setPropertyURI(propertyURI);
		propertyAssignment.setPropertyValue(propertyValue);
		
		return propertyAssignment;
	}
	
}
