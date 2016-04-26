package pcc;

/**
 * Thrown while configuring SAX XML Parser. This exception probably indicates problem with JVM.
 */
class UnrecoverableParseException extends Exception {

	
	private static final long serialVersionUID = 4698687270961388759L;

	public UnrecoverableParseException() {
	}

	public UnrecoverableParseException(String message) {
		super(message);
	}

	public UnrecoverableParseException(Throwable cause) {
		super(cause);
	}

	public UnrecoverableParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnrecoverableParseException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
