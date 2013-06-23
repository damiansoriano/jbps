package it.polimi.jbps.exception;

public class InvalidPropertyAssignment extends Exception {

	private static final long serialVersionUID = 1115565919056612994L;

	public InvalidPropertyAssignment() {
		super();
	}

	public InvalidPropertyAssignment(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPropertyAssignment(String message) {
		super(message);
	}

	public InvalidPropertyAssignment(Throwable cause) {
		super(cause);
	}
	
}
