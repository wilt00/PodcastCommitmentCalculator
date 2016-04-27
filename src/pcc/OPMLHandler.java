package pcc;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OPMLHandler extends DefaultHandler {
	// Sample code from http://www.tutorialspoint.com/java_xml/java_sax_parse_document.htm
	
	boolean bXmlUrl = false;
	
	ArrayList<String> urlStrings = new ArrayList<String>();
	public ArrayList<String> getUrlStrings(){return this.urlStrings;}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {	
		if(qName.equalsIgnoreCase("outline")){
			urlStrings.add(attributes.getValue("xmlUrl"));
		}	
	}	
}
