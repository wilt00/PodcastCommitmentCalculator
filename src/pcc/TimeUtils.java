package pcc;

public class TimeUtils {

	public static long secondsHourComponent(long seconds){
		return seconds / 3600;
	}
	
	public static long secondsMinuteComponent(long seconds){
		return (seconds % 3600) / 60;
	}
	
	public static long secondsSecondComponent(long seconds){
		return seconds % 60;
	}
	
	// Totally unnecessary. But fun!
	public static String timeString(long seconds){
		String returnString = "";
		long hours = secondsHourComponent(seconds);
		long minutes = secondsMinuteComponent(seconds);
		long secondsC = secondsSecondComponent(seconds);
		
		if(hours != 0){
			returnString += hours;
			if(hours == 1){
				returnString += " hour";
			}else{
				returnString += " hours";
			}
		}
		if(hours != 0){
			if(secondsC == 0){
				returnString += "and ";
			}else{
				returnString += ", ";
			}
		}
		
		returnString += minutes;
		if(minutes == 1){
			returnString += " minute";
		}else{
			returnString += " minutes";
		}
		
		if(secondsC == 0){
			return returnString;
		}else{
			if(hours == 0){
				returnString += " and " + secondsC;
			}else{
				returnString += ", and" + secondsC;
			}
			if(secondsC == 1){
				returnString += " second, ";
			}else{
				returnString += " seconds, ";
			}
			return returnString;
		}
	}

}
