package bikescheme;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Session {
	private Date start;
	private Date end;
	private int numExtensions;
	
	public Session() {
		start();
		numExtensions = 0;
	}
	
	public void start() {
		start = Clock.getInstance().getDateAndTime();
	}
	
	public void end() {
		end = Clock.getInstance().getDateAndTime();
	}
	
	public void addExtension() {
		numExtensions++;
	}
	public int getNumExtensions() {
		return numExtensions;
	}
	
	public int cost() {
		int diffInMinutes  = Clock.minutesBetween(start, end);
		
		// Subtract 15 min for each extension
		diffInMinutes = diffInMinutes - 15 * numExtensions;
		
		
		// 1 pound for first 30 min
		int cost = 1;
		diffInMinutes -= 30;
		
		// 2 pound for subsequent 30 min
		cost += 2 * Math.ceil(diffInMinutes / 30);
		return cost;
	}
}
