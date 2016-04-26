package pcc;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class FeedHandler extends DefaultHandler {

	// Sample code from http://www.tutorialspoint.com/java_xml/java_sax_parse_document.htm

	boolean hasTitle;
	boolean bTitle = false;
	boolean bPubDate = false;
	boolean bDuration = false;
	//boolean bURL = false;

	String title = null;

	String pubDate = null;
	String duration = null;
	String url = null;

	ArrayList<Episode> episodes = new ArrayList<Episode>();

	public String getTitle(){
		return title;
	}
	public ArrayList<Episode> getEpisodes(){
		return episodes;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equalsIgnoreCase("pubdate")){
			this.bPubDate = true;
		}else if (qName.equalsIgnoreCase ("itunes:duration")) {
			this.bDuration = true;
		}else if (qName.equalsIgnoreCase("enclosure")) {
			this.url = attributes.getValue("url");
		}else if (qName.equalsIgnoreCase("title") && !hasTitle){
			this.bTitle = true;
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if(bTitle){
			assert this.title == null : "Problem encountered while parsing XML feed title";
			this.title = new String(ch, start, length);
			this.bTitle = false;
			this.hasTitle = true;
		}else if(bPubDate){
			if(this.pubDate != null) throw new SAXException("Improperly formatted XML feed");
			this.pubDate = new String(ch, start, length);
			this.bPubDate = false;
		}else if(bDuration){
			if(this.duration != null) throw new SAXException("Improperly formatted XML feed");
			this.duration = new String(ch, start, length);
			this.bDuration = false;
		}
	}

	@Override 
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if(qName.equalsIgnoreCase("item")){
			episodes.add(new Episode(pubDate, duration, url));
			pubDate = null;
			duration = null;
			url = null;
		}
	}

}


