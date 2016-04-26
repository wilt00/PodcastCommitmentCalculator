package pcc;

import java.util.ArrayList;

class Podcast {
	private String title;
	private ArrayList<Episode> episodes; 

	public Podcast(String title, ArrayList<Episode> episodes){
		this.title = title;
		this.episodes = episodes;
	}

	public String getTitle(){
		return this.title;
	}
	
	public ArrayList<Episode> getEpisodes(){
		return this.episodes;
	}
}
