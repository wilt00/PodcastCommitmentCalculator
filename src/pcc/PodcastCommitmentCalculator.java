//Podcast Commitment Calculator

package pcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class PodcastCommitmentCalculator {
	
	private int weeks;
	private long weeksEpochOffset;
	public static final int DEFAULTWEEKS = 4;

	private ArrayList<String> feedStrings = new ArrayList<String>();
	private ArrayList<URL> feedURLs = new ArrayList<URL>();
	private ArrayList<Podcast> podcasts = new ArrayList<Podcast>();
	
	private int commitTimeSeconds;
	private int skipped;

	public int getCommitTimeSeconds(){return this.commitTimeSeconds;}
	//public Duration getCommitTime(){return this.commitTime;}
	public int getSkipped(){return this.skipped;}

	public PodcastCommitmentCalculator(File file, int weeks) 
			throws IOException, ParserConfigurationException, SAXException{
		this.weeks = weeks;
		this.weeksEpochOffset = Instant.now().getEpochSecond() - (weeks * 604800);
		
		try{
			openList(file);
			// feedStrings is populated with list of urls (strings) from passed file
		}catch(IOException ioe){
			throw ioe;
		}

		checkURLs();
		// URLs are verified; good urls are added to feedURLs
		try{
			parseFeeds();
			// XML files at urls are parsed, ArrayList podcasts is populated
		}catch(ParserConfigurationException pce){
			throw pce;
		}catch(SAXException se){
			throw se;
		}
		
		calcTime();
		// TODO: Handle feeds w/out duration provided, e.g HI

	}

	private void openList(File file) throws IOException {
		if(!file.canRead()){
			throw new IOException("Cannot read file");
		}
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null){
				feedStrings.add(line);
				line = br.readLine();
			}
		}catch(IOException ioe){
			throw ioe;
		}finally{
			try{
				br.close();
			}catch(Exception e){}
		}
	}

	private void checkURLs(){
		for (String s : feedStrings){
			try{
				feedURLs.add(new URL(s));
			}catch(MalformedURLException mue){
				if(!s.equals("")){
					System.out.println("\"" + s + "\" is not recognized as a valid url. Skipping");
				}
			}
		}
	}

	private void parseFeeds() throws ParserConfigurationException, SAXException{
		
		
		
		SAXParser saxParser = null;
		try{
			saxParser = SAXParserFactory.newInstance().newSAXParser();
		}catch(ParserConfigurationException pce){
			throw pce;
		}catch(SAXException se){
			throw se;
		}
		FeedHandler uh;

		for(URL u : feedURLs){
			System.out.println("Now parsing: " + u.toString());
			uh = new FeedHandler();

			InputStream uStream = null;
			try{
				uStream = u.openStream();
				saxParser.parse(uStream, uh);
				podcasts.add(new Podcast(uh.getTitle(), uh.getEpisodes()));
			}catch(IOException ioe){
				//ioe.printStackTrace();
				if(u.toString().startsWith("https")){
					//TODO: Support HTTPS
					// http://www.javaworld.com/article/2077600/learn-java/java-tip-96--use-https-in-your-java-client-code.html
					System.out.println("HTTPS is currently unsupported. Sorry. Skipping...");
				}else{
					System.out.println("Feed at " + u.toString() + " is unavailable. Skipping...");
				}
			}finally{
				try{
					uStream.close();
				}catch(Exception e){}
			}

		}
	}

	private void calcTime(){
		int time = 0;
		int skipped = 0;
		for(Podcast p : podcasts){
			int totalDuration = 0;
			for(Episode e : p.getEpisodes()){
				if(!e.hasDate()){
					skipped ++;
				}else{
					if(e.getDate() < this.weeksEpochOffset){
						break;
					}
					
					if(e.hasDuration()){
						totalDuration += e.getDuration();
					}else{
						skipped ++;
					}
				}
			}
			time += Math.round(totalDuration / weeks);
		}
		
		this.commitTimeSeconds = time;
		this.skipped = skipped;
	}
	
	public void testFeeds(){
		for(Podcast p : podcasts){
			System.out.println(p.getTitle());
			for(Episode e : p.getEpisodes()){
				System.out.println("\t"+e.getDuration() + " "+ e.getDate() + " " + e.getUrl());
			}
		}	
	}
	
	public static void main(String[] args){
		
		System.out.println("Welcome to the Podcast Commitment Calculator!");
		System.out.println("Keeping you honest since 2016!");
		System.out.println("Parsing...");

		PodcastCommitmentCalculator pcc = null;

		try{
			if(args.length >= 2){
				pcc = new PodcastCommitmentCalculator(new File(args[0]), Integer.parseInt(args[1]));
			}else{
				pcc = new PodcastCommitmentCalculator(new File(args[0]),DEFAULTWEEKS);
			}
			
		}catch (ArrayIndexOutOfBoundsException aioobe){
			System.out.println("Please specify a list of feeds.");
			return;
		}catch(NumberFormatException nfe){
			System.out.println("Please enter a valid number of weeks to use when calculating average");
			return;
		}catch (Exception e){
			//TODO: Per-exception handling?
			e.printStackTrace();
			return;
		}
		
		//pcc.testFeeds();
		
		System.out.println("Every week, it will take you:");
		System.out.println("\t" + (pcc.getCommitTimeSeconds() / 3600) + " Hours,");
		System.out.println("\t" + ((pcc.getCommitTimeSeconds() % 3600) / 60) + " Minutes, and,");
		System.out.println("\t" + (pcc.getCommitTimeSeconds() % 60) + " Seconds");
		System.out.println("to listen to these podcasts");
		
		System.out.println("We had to skip " + pcc.getSkipped() + " podcasts because we couldn't get enough "
				+ "information from their feed.");	
	}

}
