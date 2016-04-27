package pcc;

import org.xml.sax.SAXException;

/**
 * Thrown to indicate that SAX Parsing can terminate early.
 * Bad form to do this with an exception, but doesn't seem to be any other way.
 * That's what StackOverflow told me, anyway.
 * @author toher
 *
 */
public class SAXTerminatorException extends SAXException {

	private static final long serialVersionUID = 6046320118338049865L;

	public SAXTerminatorException() {
	}

	public SAXTerminatorException(String message) {
		super(message);
	}

	public SAXTerminatorException(Exception e) {
		super(e);
	}

	public SAXTerminatorException(String message, Exception e) {
		super(message, e);
	}

}
