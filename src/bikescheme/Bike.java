package bikescheme;

import java.util.Date;

public class Bike {
	private String bikeId;
	private boolean faulty;
	private User currentUser; 
	private String currentDStationName;
	private String currentDPointName;
	private Date startTime;
	private int currentTimeOut;
	
	public Bike(String bid) {
		bikeId = bid;
		faulty = false;
		currentUser = null;
	}
	
	public String getBikeId() {
		return bikeId;
	}
	
	public boolean isFaulty() {
		return faulty;
	}
	
	public void markFaulty(boolean f) {
		faulty = f;
	}
	
	public void setCurrentUser(User u) {
		currentUser = u;
	}
	
	public User getCurrentUser() {
		return currentUser;
	}
	
	public void setCurrentDStation(String instName) {
		currentDStationName = instName;
	}
	
	public String getCurrentDStation() {
		return currentDStationName;
	}
	
	public void setCurrentDPointName(String instName) {
		currentDPointName = instName;
	}
	
	public String getCurrentDPointName() {
		return currentDPointName;
	}
	
	public void setStartTime() {
		startTime = Clock.getInstance().getDateAndTime();
	}
	
	public void clearStartTime() {
		startTime = null;
	}
	
	//if bike has been out for longer than 24 hours
	public boolean isOutTooLong() {
		if (startTime == null) {
			return false;
		}
		Date currentTime = Clock.getInstance().getDateAndTime();
		int minDiff = Clock.minutesBetween(startTime, currentTime);
		
		return minDiff > 1440; //if true, then it has been longer than 24 hours
		
	}
	
	public void updateCurrentTimeOut() {
		Date currentTime = Clock.getInstance().getDateAndTime();
		currentTimeOut = Clock.minutesBetween(startTime, currentTime);
	}
	
	public int getCurrentTimeOut() {
		return currentTimeOut;
	}
	
	public void resetCurrentTimeOut() {
		currentTimeOut = 0;
	}
}
