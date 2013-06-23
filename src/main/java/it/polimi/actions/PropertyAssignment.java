package it.polimi.actions;

import it.polimi.PropertyType;
import lombok.Getter;
import lombok.Setter;

public class PropertyAssignment {
	
	@Getter @Setter
	private String propertyURI;
	
	@Getter @Setter
	private String propertyValue;
	
	@Getter @Setter
	private PropertyType propertyType;
	
	public boolean isObjectProperty() {
		return propertyType.equals(PropertyType.OBJECT_PROPERTY);
	}
	
	public boolean isDataProperty() {
		return propertyType.equals(PropertyType.DATA_PROPERTY);
	}

	@Override
	public Object clone()  {
		PropertyAssignment propertyAssignment = new PropertyAssignment();
		
		propertyAssignment.propertyURI = this.propertyURI;
		propertyAssignment.propertyValue = this.propertyValue;
		propertyAssignment.propertyType = this.propertyType;
		
		return propertyAssignment;
	}
	
	
	
}
