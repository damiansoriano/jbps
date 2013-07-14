package it.polimi.jbps.actions;

import static com.google.common.collect.Lists.newLinkedList;
import it.polimi.jbps.entities.JBPSClass;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Action {
	
	@Getter @Setter
	private ActionType actionType;
	
	@Getter @Setter
	private JBPSClass jbpsClass;
	
	@Getter @Setter
	private String individualURI;
	
	@Getter @Setter
	private List<PropertyAssignment> propertyAssignments;
	
	public Action() {
		propertyAssignments = newLinkedList();
		individualURI = "";
	}
	
	public String getClassURI() {
		return jbpsClass.getURI();
	}
	
	@Override
	public Object clone()  {
		Action action = new Action();
		
		action.actionType = this.actionType;
		action.jbpsClass = this.jbpsClass;
		action.individualURI = this.individualURI;
		action.propertyAssignments = this.propertyAssignments;
		
		return action;
	}
}
