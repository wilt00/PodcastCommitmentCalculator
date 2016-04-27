package pcc;

import java.util.ArrayList;

class Podcast {
	private String title;
	private ArrayList<Episode> episodes; 
	private boolean hasAllDuration;
	private boolean hasAllDate;
	
	public String getTitle(){return this.title;}
	public ArrayList<Episode> getEpisodes(){return this.episodes;}
	public boolean getHasAllDuration(){return this.hasAllDuration;}
	public boolean getHasAllDate(){return this.hasAllDate;}

	public Podcast(String title, ArrayList<Episode> episodes, long weeksEpochOffset){
		this.title = title;
		this.episodes = episodes;
		hasAllDuration = true;
		hasAllDate = true;
		for (Episode e : episodes){
			if(e.getDate() < weeksEpochOffset){break;}
			if(!e.hasDuration()){
				hasAllDuration = false;
			}
			if(!e.hasDate()){
				hasAllDate = false;
			}
		}
	}

}
