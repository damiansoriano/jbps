package it.polimi.jbps.entities;

import lombok.Getter;

public class SimulationTransition {
	
	@Getter
	protected JBPSIndividual transition;
	
	public SimulationTransition(JBPSIndividual transition) {
		this.transition = transition;
	}
	
	@Override
	public String toString() {
		return transition.toString();
	}
	
	public String getTransitionURI() { 
		return transition.getURI();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((transition == null) ? 0 : transition.getURI().hashCode());
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
		return other.getTransitionURI().equals(this.getTransitionURI());
	}
}
