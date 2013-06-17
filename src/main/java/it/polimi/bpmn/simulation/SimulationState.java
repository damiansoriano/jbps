package it.polimi.bpmn.simulation;

import lombok.Getter;
import lombok.Setter;

public class SimulationState {
	
	@Getter @Setter
	protected String stateURI;
	
	public SimulationState() { }
	
	public SimulationState(String stateURI) {
		this.stateURI = stateURI;
	}

}
