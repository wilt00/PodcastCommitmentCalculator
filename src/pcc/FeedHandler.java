package pcc;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class FeedHandler extends DefaultHandler {

	// Sample code from http://www.tutorialspoint.com/java_xml/java_sax_parse_document.htm
	
	boolean endHeader = false;	// Indicates that items have begun; used to exclude header info
	boolean hasTitle = false;	// Indicates that title of podcast (not of episode) has been found
	
	boolean bTitle = false;		// Indicates that title of episode has been found
	boolean bPubDate = false;	// Indicates that publication date of episode has been found
	boolean bDuration = false;	// Indicates that duration of episode has been found
	//boolean bURL = false;
	
	long weeksEpochOffset = 0;

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
	
	public void setWeeksEpochOffset(long weo){
		this.weeksEpochOffset = weo;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if(endHeader){
			if(qName.equalsIgnoreCase("pubdate")){
				this.bPubDate = true;
			}else if (qName.equalsIgnoreCase ("itunes:duration")) {
				this.bDuration = true;
			}else if (qName.equalsIgnoreCase("enclosure")) {
				this.url = attributes.getValue("url");
			}
			return;
		}
		
		if(qName.equalsIgnoreCase("item")){
			this.endHeader = true;
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
			if(this.pubDate != null) throw new SAXException("Improperly formatted XML feed: \"" + new String(ch, start, length) + "\" is unexpected");
			this.pubDate = new String(ch, start, length);
			this.bPubDate = false;
		}else if(bDuration){
			if(this.duration != null) throw new SAXException("Improperly formatted XML feed: \"" + new String(ch, start, length) + "\" is unexpected");
			this.duration = new String(ch, start, length);
			this.bDuration = false;
		}
	}

	@Override 
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if(qName.equalsIgnoreCase("item")){
			episodes.add(new Episode(pubDate, duration, url));
			
			// Episode constructor is converting string pubDate to long anyway, so may as well use object
			Episode last = episodes.get(episodes.size() - 1);
			if(last.hasDate()){
				if(last.getDate() < this.weeksEpochOffset){
					episodes.remove(episodes.size() - 1);
					throw new SAXTerminatorException("Parsed to end of set time period");
				}
			}
			
			pubDate = null;
			duration = null;
			url = null;
		}
	}

}


