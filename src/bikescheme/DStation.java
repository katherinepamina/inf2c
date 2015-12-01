/**
 * 
 */
package bikescheme;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  
 * Docking Station.
 * 
 * @author pbj
 *
 */
public class DStation implements KeyInsertionObserver, StartRegObserver, ViewActivityObserver, FindFreePointsObserver {
    public static final Logger logger = Logger.getLogger("bikescheme");

    private String instanceName;
    private int eastPos;
    private int northPos;
    
    private DSTouchScreen touchScreen;
    private CardReader cardReader; 
    private KeyIssuer keyIssuer;
    private KeyReader keyReader;
    private List<DPoint> dockingPoints;
    private Hub hub;
 
    /**
     * 
     * Construct a Docking Station object with touch screen, card reader
     * and key issuer interface devices and a connection to a number of
     * docking points.
     * 
     * If the instance name is <foo>, then the Docking Points are named
     * <foo>.1 ... <foo>.<numPoints> . 
     * 
     * @param instanceName
     */
    public DStation(
            String instanceName,
            int eastPos,
            int northPos,
            int numPoints, 
            Hub hub) {
        
     // Construct and make connections with interface devices
        
        this.instanceName = instanceName;
        this.eastPos = eastPos;
        this.northPos = northPos;
        this.hub = hub;
        
        touchScreen = new DSTouchScreen(instanceName + ".ts");
        touchScreen.setObserver(this);
        touchScreen.setViewActivityObserver(this);
        touchScreen.setFindFreePointsObserver(this);
        
        cardReader = new CardReader(instanceName + ".cr");
        
        keyIssuer = new KeyIssuer(instanceName + ".ki");
        
        keyReader = new KeyReader(instanceName + ".kr");
        keyReader.setObserver(this);
        
        dockingPoints = new ArrayList<DPoint>();
        
        for (int i = 1; i <= numPoints; i++) {
            DPoint dp = new DPoint(instanceName + "." + i, i - 1, this);
            dockingPoints.add(dp);
        }
    }
       
    void setDistributor(EventDistributor d) {
        touchScreen.addDistributorLinks(d); 
        cardReader.addDistributorLinks(d);
        keyReader.addDistributorLinks(d);
        for (DPoint dp : dockingPoints) {
            dp.setDistributor(d);
        }
    }
    
    void setCollector(EventCollector c) {
        touchScreen.setCollector(c);
        cardReader.setCollector(c);
        keyIssuer.setCollector(c);
        for (DPoint dp : dockingPoints) {
            dp.setCollector(c);
        }
    }
    
    /** 
     * Dummy implementation of docking station functionality for 
     * "register user" use case.
     * 
     * Method called on docking station receiving a "start registration"
     * triggering input event at the touch screen.
     * 
     * @param personalInfo
     */
    // Changed parameters to avoid string parsing
    public void startRegReceived(String personalInfo) {
        logger.fine("Starting on instance " + getInstanceName());
        
        touchScreen.showPrompt("Please enter your personal details.");
        
        cardReader.requestCard();  // Generate output event
        logger.fine("At position 1 on instance " + getInstanceName());
        
        cardReader.checkCard();    // Pull in non-triggering input event
        logger.fine("At position 2 on instance " + getInstanceName());
        
        // Create a new user
        String id = keyIssuer.issueKey(); // Generate output event
        BankCard card = new BankCard(2, "2"); // dummy card
        // For simplicity, key id == user id (since each user has only one key)
        Key key = new Key(id);
        User u = new User(id, personalInfo, card, key);
        
        hub.addUser(u, id);
        
        
        
    }
    
    
    public String getInstanceName() {
        return instanceName;
    }
    
    public int getEastPos() {
        return eastPos;
    }
    
    public int getNorthPos() {
        return northPos;
    }
    
    public int getNumFreePoints() {
    	int count = 0;
    	for (DPoint dp: dockingPoints) {
    		if (dp.isFree()) {
    			count++;
    		}
    	}
    	return count;
    }
    
    public int getNumPoints() {
    	return dockingPoints.size();
    }
 
    public User getUserByKeyID(String keyID) {
    	return hub.getUserByKeyID(keyID);
    }
    
    //check if user is a registered user rather than staff member
    public boolean isUser(String keyID) {
    	return hub.isUser(keyID);
    }
    
    public Bike getBikeByBikeID(String bikeID) {
    	return hub.getBikeByBikeID(bikeID);
    }
    
    public Bike createNewBike(String bikeID) {
    	return hub.createNewBike(bikeID);
    }
    
    public void removeBikeFromMap(String bikeID) {
    	hub.removeBikeFromMap(bikeID);
    }
    
    public void viewActivityReceived(String keyid) {
    	touchScreen.showPrompt("Please insert your key");
    	// Present summary
    	//"HireTime","HireDS","ReturnDS","Duration (min)"
    	User user = hub.getUserByKeyID(keyid);
    	if (user == null) {
    		return;
    	}
    	ArrayList<Session> userSessions = user.getTodaySessions();
    	ArrayList<String> displayData = new ArrayList<String>();
	    if (userSessions != null) {	
    		for (Session s: userSessions) {
	    		displayData.add(Clock.format(s.getStart()));
	    		displayData.add(s.getHireDS().getInstanceName());
	    		displayData.add(s.getReturnDS().getInstanceName());
	    		displayData.add(Integer.toString(s.getDuration()));
	    	}
    	}
    	if (user.getCurrentSession() != null) {
    		Session s = user.getCurrentSession();
    		displayData.add(Clock.format(s.getStart()));
    		displayData.add(s.getHireDS().getInstanceName());
    		displayData.add("Not returned");
    		displayData.add(Integer.toString(s.getDuration()));
    	}
    	touchScreen.showUserActivity(displayData);
    	
    }
    
    public void findFreePointsReceived() {
    	Map<String, DStation> allStations = hub.getDStationMap();
    	if (allStations == null) {
    		return;
    	}
    	List<String> freePoints = new ArrayList<String>();
    	for (DStation s: allStations.values()) {
    		int dist = getDist(eastPos, northPos, s.getEastPos(), s.getNorthPos());
    		if (dist <= 250 && s.getNumFreePoints() > 0) {
    			freePoints.add(s.getInstanceName());
    			freePoints.add(Integer.toString(s.getEastPos()));
    			freePoints.add(Integer.toString(s.getNorthPos()));
    			freePoints.add(Integer.toString(dist));
    		}
    	}
    	touchScreen.showFreePoints(freePoints);
    }
    
    private int getDist(int a1, int a2, int b1, int b2) {
    	// (East(+)/West(-), North(+)/South(-)
    	int longDiff;
    	int latDiff;
    	// Both are West or both are East
    	if ((a1<=0 && b1<=0) || (a1>=0 && b1>=0)) {
    		latDiff = Math.abs(a1 - b1);
    	}
    	// One is West, one is East
    	else {
    		latDiff = Math.abs(a1) + Math.abs(b1);
    	}
    	// Both are North or both are South
    	if ((a2<=0 && b2<=0) || (a2 >= 0 && b2 >= 0)) {
    		longDiff = Math.abs(a2 - b2);
    	}
    	else {
    		longDiff = Math.abs(a2) + Math.abs(b2);
    	}
    	return latDiff + longDiff;
    }
    
    public void keyInserted(String keyid) {
    	logger.fine(getInstanceName());
    }
}