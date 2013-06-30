package it.polimi.jbps.bpmn.simulation;

import lombok.Getter;
import lombok.Setter;

public class SimulationState {
	
	@Getter @Setter
	protected String stateURI;
	
	public SimulationState() { }
	
	public SimulationState(String stateURI) {
		this.stateURI = stateURI;
	}

	@Override
	public String toString() {
		return stateURI;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((stateURI == null) ? 0 : stateURI.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimulationState other = (SimulationState) obj;
		if (stateURI == null) {
			if (other.stateURI != null)
				return false;
		} else if (!stateURI.equals(other.stateURI))
			return false;
		return true;
	}
	
	
	
}
