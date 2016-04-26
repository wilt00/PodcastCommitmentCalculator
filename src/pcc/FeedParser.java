package pcc;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

class FeedParser {
	
	// These methods moved here so that all internet-facing methods are in one place - should make https easier (?)
	// Also contains all methods using xml parsers
	
	static ArrayList<URL> checkURLs(ArrayList<String> feedStrings){
		ArrayList<URL> feedURLs = new ArrayList<URL>();
		for (String s : feedStrings){
			try{
				feedURLs.add(new URL(s));
			}catch(MalformedURLException mue){
				if(!s.equals("")){
					System.out.println("\"" + s + "\" is not recognized as a valid url. Skipping");
				}
			}
		}
		return feedURLs;
	}
	
	static ArrayList<Podcast> parseFeeds(ArrayList<URL> feedURLs) throws UnrecoverableParseException{
		
		SAXParser saxParser = null;
		try{
			saxParser = SAXParserFactory.newInstance().newSAXParser();
		}catch(ParserConfigurationException pce){
			throw new UnrecoverableParseException("Problem encountered while setting up parser.", pce);
		}catch(SAXException se){
			throw new UnrecoverableParseException("Problem encountered while setting up parser.", se);
		}
		ArrayList<Podcast> podcasts = new ArrayList<Podcast>();
		FeedHandler uh;

		for(URL u : feedURLs){
			System.out.println("Now parsing: " + u.toString());
			uh = new FeedHandler();

			InputStream uStream = null;
			try{
				if(u.toString().startsWith("https")){
					uStream = openHttpsStream(u);
				}else{
					uStream = u.openStream();
				}
				saxParser.parse(uStream, uh);
				podcasts.add(new Podcast(uh.getTitle(), uh.getEpisodes()));
			}catch(ResolvedURLException rue){}
			catch(IOException ioe){
				
				
				ioe.printStackTrace();
				
				System.out.println("Feed at " + u.toString() + " is unavailable. Skipping...");

			}catch(SAXException se){
				se.printStackTrace();
				System.out.println("Error encountered while parsing feed. Skipping...");
			}finally{
				try{
					uStream.close();
				}catch(Exception e){}
			}

		}
		return podcasts;
	}
	
	private static InputStream openHttpsStream(URL u) throws IOException, ResolvedURLException{
		HttpsURLConnection hsuc = null;
		try{
			hsuc = (HttpsURLConnection)u.openConnection();
			hsuc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
			// Useragent: Chrome 41
			return hsuc.getInputStream();
		}catch(IOException ioe){
			int code = hsuc.getResponseCode();
			if(code == 429){
				//TODO: I don't think any of this actually works
				System.out.println("This server does not allow you to query it so often. Try again later. Skipping...");
				throw new ResolvedURLException("Response Code 429 - This server does not allow you to query it so often.");
			}
			throw ioe;
		}
	}
}
