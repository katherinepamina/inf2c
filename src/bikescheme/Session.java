package bikescheme;

import java.util.Date;

public class Session {
	private Date start;
	private Date end;
	private int numExtensions;
	
	public Session() {
		start();
		numExtensions = 0;
		paid = false;
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
		int 
		// Subtract 15 min for each extension
		// 1 pound for first 30 min
		// 2 pound for subsequent 30 min
		
	}
}
