package it.polimi.form;

import it.polimi.actions.Action;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class FormsConfiguration {
	
	@Getter @Setter
	private Map<String, List<Action>> configuration;
	
}
