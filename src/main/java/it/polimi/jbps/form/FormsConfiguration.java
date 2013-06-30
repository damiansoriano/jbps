package it.polimi.jbps.form;

import static com.google.common.collect.Maps.newHashMap;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.io.Json2ModelAction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import lombok.Getter;
import lombok.Setter;

public class FormsConfiguration {
	
	@Getter @Setter
	private Map<String, List<Action>> configuration;
	
	public static FormsConfiguration createFromFiles(Map<String, String> stateFileMap) throws IOException {
		Json2ModelAction json2Model = new Json2ModelAction();
		
		Map<String, List<Action>> stateActionsMap = newHashMap();
		
		for (String state : stateFileMap.keySet()) {
			File file = new File(stateFileMap.get(state));
			String json = Files.toString(file, Charsets.UTF_8);
			List<Action> actions = json2Model.parseJson(json);
			stateActionsMap.put(state, actions);
		}
		FormsConfiguration formsConfiguration = new FormsConfiguration();
		formsConfiguration.configuration = stateActionsMap;
		return formsConfiguration;
	}
	
}
