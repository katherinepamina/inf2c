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
public class DPoint implements KeyInsertionObserver {
    public static final Logger logger = Logger.getLogger("bikescheme");

    private KeyReader keyReader; 
    private OKLight okLight;
    private String instanceName;
    private int index;
    private boolean free;
    private Bike bike;
 
    /**
     * 
     * Construct a Docking Point object with a key reader and green ok light
     * interface devices.
     * 
     * @param instanceName a globally unique name
     * @param index of reference to this docking point  in owning DStation's
     *  list of its docking points.
     */
    public DPoint(String instanceName, int index) {

     // Construct and make connections with interface devices
        
        keyReader = new KeyReader(instanceName + ".kr");
        keyReader.setObserver(this);
        okLight = new OKLight(instanceName + ".ok");
        this.instanceName = instanceName;
        this.index = index;
        this.free = true;
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
    
    /** 
     * Dummy implementation of docking point functionality on key insertion.
     * 
     * Here, just flash the OK light.
     */
    public void keyInserted(String keyId) {
        logger.fine(getInstanceName());
        
        okLight.flash();       
    }
    
 

}
