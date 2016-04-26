package pcc;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.xml.sax.SAXException;

class Episode {
	// Format of <pubDate> looks like:
	// Wed, 13 Apr 2016 19:10:00 -0400
	private long date;

	// <itunes:duration> tag might be number of seconds in episode OR might be HH:MM:SS
	private int duration;
	// May as well be int; 68-year-long podcast episodes are not expected
	// Value of int cannot be null, so duration is -1 when invalid duration provided

	// <enclosure url="urlgoeshere?someotherstuff" [optional tags] />
	// Method gets contents of url tag
	private URL url;
	
	public long getDate(){return this.date;}
	public int getDuration(){return this.duration;}
	public URL getUrl(){return this.url;}
	
	public boolean hasDate(){
		if(this.date > 0){				
			return true;
		}
		return false;
	}
	public boolean hasDuration(){
		if(this.duration > 0){			// Note that duration should not be 0, so should exclude both 0 and -1
			return true;
		}
		return false;
	}
	public boolean hasUrl(){
		if(this.url == null){
			return false;
		}
		return true;
	}

	public Episode(String dateString, String durationString, String urlString) throws SAXException{

		if(dateString == null){
			this.date = -1;
		}else{
			// Thread safe
			// Code from: https://stackoverflow.com/questions/6687433/convert-date-format-to-epoch
			int lastCharType = Character.getType(dateString.charAt(dateString.length() - 1));
			TemporalAccessor t = null;
			if(lastCharType == Character.UPPERCASE_LETTER || lastCharType == Character.LOWERCASE_LETTER){
				t = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss zzz").parse(dateString);
			}else if(lastCharType == Character.DECIMAL_DIGIT_NUMBER){
				t = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z").parse(dateString);
			}else{
				throw new SAXException("Unexpected date formatting: " + dateString);
			}
			
			LocalDateTime ldt = LocalDateTime.from(t);
			this.date = ldt.toEpochSecond(ZoneOffset.UTC);
			// Not bothering with time zone offset, because it doesn't matter
		}

		if(durationString == null){
			this.duration = -1;
		} else if(durationString.contains(":")){
			try{
				String[] durationStringSplit = durationString.split(":");		
				if(durationStringSplit.length >= 3){
					int hours = Integer.valueOf(durationStringSplit[0]);
					int minutes = Integer.valueOf(durationStringSplit[1]);
					int seconds = Integer.valueOf(durationStringSplit[2]);
					this.duration = (hours * 3600) + (minutes * 60) + seconds;
				}else{
					int minutes = Integer.valueOf(durationStringSplit[0]);
					int seconds = Integer.valueOf(durationStringSplit[1]);
					this.duration = (minutes * 60) + seconds;
				}
			}catch(NumberFormatException nfe){
				nfe.printStackTrace();
				System.out.println(durationString);
				this.duration = -1;
			}
			
		} else {
			this.duration = Integer.valueOf(durationString);
		}
		
		if(urlString == null){
			this.url = null;
		}else{
			try{
				if(urlString.contains("?")){
					this.url = new URL(urlString.substring(0, urlString.indexOf('?')));
				}else{
					this.url = new URL(urlString);
				}
			}catch(MalformedURLException mue){
				mue.printStackTrace();
				this.url = null;
			}
		}	
	}
	
}
