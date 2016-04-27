//Podcast Commitment Calculator

package pcc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;
import org.xml.sax.SAXException;


public class PodcastCommitmentCalculator {
	
	private int weeks;
	private long weeksEpochOffset;		// The epoch time n weeks ago
	public static final int DEFAULTWEEKS = 4;

	private ArrayList<String> feedStrings = new ArrayList<String>();
	private ArrayList<URL> feedURLs = new ArrayList<URL>();
	private ArrayList<Podcast> podcasts = new ArrayList<Podcast>();
	
	private int commitTimeSeconds;
	private int skipped;
	private HashSet<Podcast> skippedPodcasts = new HashSet<Podcast>();

	public int getCommitTimeSeconds(){return this.commitTimeSeconds;}
	public int getSkipped(){return this.skipped;}
	public HashSet<Podcast> getSkippedPodcasts(){return this.skippedPodcasts;}

	public PodcastCommitmentCalculator(File file, int weeks) 
			throws IOException, UnrecoverableParseException, SAXException{
		this.weeks = weeks;
		this.weeksEpochOffset = Instant.now().getEpochSecond() - (weeks * 604800);
		
		try{
			// feedStrings is populated with list of urls (strings) from passed file
			openList(file);
		}
		catch(IOException ioe){throw ioe;}
		catch(SAXException se){throw se;}
		catch(UnrecoverableParseException upe){throw upe;}

		// URLs are verified; good urls are added to feedURLs
		this.feedURLs = FeedParser.checkURLs(this.feedStrings);
		
		try{
			// XML files at urls are parsed, ArrayList podcasts is populated
			this.podcasts = FeedParser.parseFeeds(this.feedURLs, weeksEpochOffset);
		}catch(UnrecoverableParseException upe){
			throw upe;
		}
		
		calcTime();
		// TODO: Handle feeds w/out duration provided, e.g HI

	}

	private void openList(File file) throws IOException, SAXException, UnrecoverableParseException {
		if(!file.canRead()){
			throw new IOException("Cannot read file");
		}	
		
		if(file.toString().toLowerCase().endsWith(".opml")){
			
			try{
				this.feedStrings = FeedParser.parseOPML(new BufferedInputStream(new FileInputStream(file)));
			}
			catch(SAXException se){throw se;}
			catch(UnrecoverableParseException upe){throw upe;}
			
		}else{
			
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
	}

	

	

	private void calcTime(){
		int time = 0;
		int skipped = 0;
		for(Podcast p : podcasts){
			int totalEpisodes = 0;
			int totalDuration = 0;
			long podAvgTime = 0;
			for(Episode e : p.getEpisodes()){
				if(!e.hasDate()){
					totalEpisodes++;
					skipped ++;
					this.skippedPodcasts.add(p);
				}else{
					if(e.getDate() < this.weeksEpochOffset){
						break;
					}
					totalEpisodes++;
					
					if(e.hasDuration()){
						totalDuration += e.getDuration();
					}else{
						skipped ++;
						this.skippedPodcasts.add(p);
					}
				}
			}
			System.out.println(p.getTitle() + ":");
			System.out.println("\t" + totalEpisodes + " episodes in last " + this.weeks + " weeks");
			podAvgTime = Math.round(totalDuration / weeks);
			System.out.println("\t" + "Average weekly time: " + TimeUtils.timeString(podAvgTime));
			if(!p.getHasAllDuration()){
				System.out.println("\tNote: this podcast feed is missing some data. Times may not be accurate.");
			}
			time += podAvgTime;
			
		}
		
		this.commitTimeSeconds = time;
		this.skipped = skipped;
	}
	
	
	
	
	@Test
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
		System.out.println("Parsing...");
		
		// Flags: 
		//  -i - interactive
		//  -w - # of weeks
		//       multiple files
		//       stdin
		
		File feedFile = null;
		try{
			String feedFileString = args[0];
			feedFile = new File(feedFileString);
		}catch(ArrayIndexOutOfBoundsException aioobe){
			System.out.println("Please specify a list of feeds.");
			return;
		}
		
		int weeks;
		if(args.length >= 2){
			try{
				weeks = Integer.parseInt(args[1]);
			}catch(NumberFormatException nfe){
				System.out.println("Please enter a valid number of weeks to use when calculating average");
				return;
			}
		}else{
			weeks = DEFAULTWEEKS;
		}

		PodcastCommitmentCalculator pcc = null;
		try{
			pcc = new PodcastCommitmentCalculator(feedFile, weeks);		
		}catch(UnrecoverableParseException upe){
			System.out.println("There was a problem setting up your parser. Quitting...");
			upe.printStackTrace();
			return;
		}catch(IOException ioe){
			System.out.println("We couldn't read that file. Please specify a different file.");
			return;
		}catch(SAXException se){
			System.out.println("Something is wrong with your OPML file. Unable to parse.");
			return;
		}

		
		System.out.println("\n\nEvery week, it will take you approximately:");
		System.out.println("\t" + TimeUtils.secondsHourComponent(pcc.getCommitTimeSeconds()) + " Hours,");
		System.out.println("\t" + TimeUtils.secondsMinuteComponent(pcc.getCommitTimeSeconds()) + " Minutes");
		//System.out.println("\t" + (pcc.getCommitTimeSeconds() % 60) + " Seconds");
		// ^ Might add option to add second precision in future
		System.out.println("to listen to these podcasts\n");
		
		System.out.println("We had to skip " + pcc.getSkipped() + " episodes from these podcasts: ");
		for(Podcast p : pcc.getSkippedPodcasts()){
			System.out.println("\t" + p.getTitle());
		}
		System.out.println("because we couldn't get enough information from their feed.");	
	}

}
