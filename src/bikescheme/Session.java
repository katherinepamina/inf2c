package bikescheme;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Session {
	private Date start;
	private Date end;
	private int numExtensions;
	private DStation HireDS;
	private DStation ReturnDS;
	
	public Session() {
		numExtensions = 0;
	}
	
	public void start(DStation s) {
		start = Clock.getInstance().getDateAndTime();
		HireDS = s;
		
	}
	
	public void end(DStation s) {
		end = Clock.getInstance().getDateAndTime();
		ReturnDS = s;
	}
	
	public void addExtension() {
		numExtensions++;
	}
	public int getNumExtensions() {
		return numExtensions;
	}
	
	public DStation getHireDS() {
		return HireDS;
	}
	public DStation getReturnDS() {
		return ReturnDS;
	}
	
	public int getDuration() {
		if (end != null) {
			int diffInMinutes  = Clock.minutesBetween(start, end);
			return diffInMinutes;
		}
		else return -1;
	}
	
	public Date getStart() {
		return start;
	}
	public Date getEnd() {
		return end;
	}
	
	public int cost() {
		int diffInMinutes  = getDuration();
		
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
