package it.polimi.actions;

import java.util.List;

import com.hp.hpl.jena.ontology.Individual;

import it.polimi.PropertyType;
import lombok.Getter;
import lombok.Setter;

public class PropertyAssignment {
	
	@Getter @Setter
	protected String propertyURI;
	
	@Getter @Setter
	protected String propertyValue;
	
	@Getter @Setter
	protected PropertyType propertyType;
	
	@Getter @Setter
	private List<Individual> possibleAssignments;
	
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
		propertyAssignment.possibleAssignments = this.possibleAssignments;
		
		return propertyAssignment;
	}
	
	
	
}
