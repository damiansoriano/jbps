package it.polimi.jbps.actions;

import it.polimi.jbps.PropertyType;
import it.polimi.jbps.entities.JBPSIndividual;
import it.polimi.jbps.entities.JBPSProperty;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class PropertyAssignment {
	
	@Getter @Setter
	protected JBPSProperty jbpsProperty;
	
	@Getter @Setter
	protected String propertyValue;
	
	@Getter @Setter
	protected PropertyType propertyType;
	
	@Getter @Setter
	private List<JBPSIndividual> possibleAssignments;
	
	public boolean isObjectProperty() {
		return propertyType.equals(PropertyType.OBJECT_PROPERTY);
	}
	
	public boolean isDataProperty() {
		return propertyType.equals(PropertyType.DATA_PROPERTY);
	}
	
	public String getPropertyURI() {
		return jbpsProperty.getURI();
	}

	@Override
	public Object clone()  {
		PropertyAssignment propertyAssignment = new PropertyAssignment();
		
		propertyAssignment.jbpsProperty = this.jbpsProperty;
		propertyAssignment.propertyValue = this.propertyValue;
		propertyAssignment.propertyType = this.propertyType;
		propertyAssignment.possibleAssignments = this.possibleAssignments;
		
		return propertyAssignment;
	}
	
	
	
	
	
}
