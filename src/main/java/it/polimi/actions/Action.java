package it.polimi.actions;

import static com.google.common.collect.Lists.newLinkedList;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Action {
	
	@Getter @Setter
	private ActionType actionType;
	
	@Getter @Setter
	private String classURI;
	
	@Getter @Setter
	private String individualURI;
	
	@Getter @Setter
	private List<PropertyAssignment> actions;
	
	public Action() {
		actions = newLinkedList();
		individualURI = null;
	}
}
