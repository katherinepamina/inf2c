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
		start = new Date();
	}
	
	public void end() {
		end = new Date();
	}
	
	public void addExtension() {
		numExtensions++;
	}
	public int getNumExtensions() {
		return numExtensions;
	}
	
	public int cost() {
		long duration  = end.getTime() - start.getTime();
		long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		
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
