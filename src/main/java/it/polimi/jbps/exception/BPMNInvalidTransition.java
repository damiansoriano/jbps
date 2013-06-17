package it.polimi.jbps.exception;

public class BPMNInvalidTransition extends Exception {

	private static final long serialVersionUID = 1115565919056612994L;

	public BPMNInvalidTransition() {
		super();
	}

	public BPMNInvalidTransition(String message, Throwable cause) {
		super(message, cause);
	}

	public BPMNInvalidTransition(String message) {
		super(message);
	}

	public BPMNInvalidTransition(Throwable cause) {
		super(cause);
	}
	
}
