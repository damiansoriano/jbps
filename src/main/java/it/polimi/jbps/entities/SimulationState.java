package it.polimi.jbps.entities;

import lombok.Getter;

public class SimulationState {
	
	@Getter
	protected JBPSIndividual state;
	
	public SimulationState(JBPSIndividual state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return state.toString();
	}
	
	public String getStateURI() { 
		return state.getURI();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state == null) ? 0 : state.getURI().hashCode());
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
		return other.getStateURI().equals(this.getStateURI());
	}
}
