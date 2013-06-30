package it.polimi.jbps.bpmn.simulation;

import lombok.Getter;
import lombok.Setter;

public class SimulationTransition {
	
	@Getter @Setter
	protected String transitionURI;
	
	public SimulationTransition() { }
	
	public SimulationTransition(String transitionURI) {
		this.transitionURI = transitionURI;
	}
	
	@Override
	public String toString() {
		return transitionURI;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((transitionURI == null) ? 0 : transitionURI.hashCode());
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
		SimulationTransition other = (SimulationTransition) obj;
		if (transitionURI == null) {
			if (other.transitionURI != null)
				return false;
		} else if (!transitionURI.equals(other.transitionURI))
			return false;
		return true;
	}
}
