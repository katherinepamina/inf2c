/**
 * 
 */
package bikescheme;


/**
 * Model of a terminal with a keyboard, mouse and monitor.
 * 
 * @author pbj
 *
 */
public class HubTerminal extends AbstractIODevice {

    /**
     * 
     * @param instanceName  
     */
    public HubTerminal(String instanceName) {
        super(instanceName);   
    }
    
    // Fields and methods for device input function
    
    private AddDStationObserver dStationObserver;
    private ViewStatsObserver viewStatsObserver;
    
    public void setObserver(AddDStationObserver o, ViewStatsObserver o1) {
        dStationObserver = o;
        viewStatsObserver = o1;
    }
    
    /** 
     *    Select device action based on input event message
     *    
     *    @param e
     */
    @Override
    public void receiveEvent(Event e) {
        
        if (e.getMessageName().equals("addDStation") 
                && e.getMessageArgs().size() == 4) {
            
            String instanceName = e.getMessageArgs().get(0);
            int eastPos = Integer.parseInt(e.getMessageArg(1));
            int northPos =  Integer.parseInt(e.getMessageArg(2));
            int numPoints =  Integer.parseInt(e.getMessageArg(3));
            
            addDStation(instanceName, eastPos, northPos, numPoints);
            
        } else if (e.getMessageName().equals("viewStats")) {
        	
        	String activityType = e.getMessageArgs().get(0);
        	viewStatsReceived(activityType);
        	
        } else {
            super.receiveEvent(e);
        } 
    }
    /**
     * Handle request to add a new docking station
     */
    public void addDStation(
            String instanceName, 
            int eastPos, 
            int northPos,
            int numPoints) {
        logger.fine(getInstanceName());
        
        
        dStationObserver.addDStation(instanceName, eastPos, northPos, numPoints);
    }
    
    
    // Insert here support for operations generating output on the 
    // touch screen display.
    /**
     * Handle request to view stats
     */
    public void viewStatsReceived(String activityType) {
        logger.fine(getInstanceName());
        
        viewStatsObserver.viewStatsReceived(activityType);
    }
   
}
