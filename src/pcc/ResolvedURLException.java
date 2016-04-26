package pcc;

/**
 * Thrown during URL processing to indicate that a problem has arisen and been resolved, but that execution should skip this URL and move on to the next one.
 */
public class ResolvedURLException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1080395059670101700L;

	public ResolvedURLException() {
	}

	public ResolvedURLException(String message) {
		super(message);
	}

	public ResolvedURLException(Throwable cause) {
		super(cause);
	}

	public ResolvedURLException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResolvedURLException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
