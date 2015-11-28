/**
 * 
 */
package bikescheme;

import java.util.logging.Logger;

/**
 *  
 * Docking Point for a Docking Station.
 * 
 * @author pbj
 *
 */
public class DPoint implements KeyInsertionObserver, BikeDockingObserver {
    public static final Logger logger = Logger.getLogger("bikescheme");

    private KeyReader keyReader; 
    private OKLight okLight;
    private String instanceName;
    private int index;
    private boolean free;
    private Bike bike;
    private BikeLock lock;
    private BikeSensor sensor;
    private DStation station;
 
    /**
     * 
     * Construct a Docking Point object with a key reader and green ok light
     * interface devices.
     * 
     * @param instanceName a globally unique name
     * @param index of reference to this docking point  in owning DStation's
     *  list of its docking points.
     */
    public DPoint(String instanceName, int index, DStation station) {

     // Construct and make connections with interface devices
        
        keyReader = new KeyReader(instanceName + ".kr");
        keyReader.setObserver(this);
        okLight = new OKLight(instanceName + ".ok");
        lock = new BikeLock(instanceName + ".bl");
        sensor = new BikeSensor(instanceName + ".bs");
        this.instanceName = instanceName;
        this.index = index;
        this.free = true;
        this.station = station;
    }
       
    public void setDistributor(EventDistributor d) {
        keyReader.addDistributorLinks(d); 
    }
    
    public void setCollector(EventCollector c) {
        okLight.setCollector(c);
        
    }
    
    public String getInstanceName() {
        return instanceName;
    }
    public int getIndex() {
        return index;
    }
    
    public boolean isFree() {
    	return free;
    }
    public void setFree(boolean f) {
    	free = f;
    }
    
    public void addBike(Bike b) {
    	if (isFree()) {
    		bike = b;
    		free = false;
    	}  
    }
    public Bike getBike() {
    	return bike;
    }
    
    public void bikeDocked(String keyID) {
    	okLight.flash();
    	lock.lock();
    	
    	//check if user is a registered user rather than a staff member
    	if () { //return bike requirement
    		User returningUser = getUserByKeyID(keyID);
    		returningUser.endCurrentSession();
    	}
    }
    

    public void keyInserted(String keyId) {
        logger.fine(getInstanceName());
        
        hireBike(keyId);
             
    }
    
    private void hireBike(String keyID) {
    	User rentingUser = getUserByKeyID(keyID);
    	rentingUser.startNewSession();
    	lock.unlock();
    	okLight.flash();
    	free = false;
    	bike = null;
    }
    
    private User getUserByKeyID(String keyID) {
    	return station.getUserByKeyID(keyID);
    }
    
    
    
 

}
