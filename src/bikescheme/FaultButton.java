package bikescheme;

/**
 * Input device for reporting faults
 * 
 * 
 *
 */
public class FaultButton extends AbstractInputDevice {


    private FaultButtonObserver observer;
    
    /**
     * @param instanceName  
     */
    public FaultButton(String instanceName) {
        super(instanceName);   
    }
    
    /*
     * 
     *  METHODS FOR HANDLING TRIGGERING INPUT
     *  
     */
    
    /**
     * @param o
     */
    public void setObserver(FaultButtonObserver o) {
        observer = o;
    }
    
    /** 
     *    Select device action based on input event message
     *    
     *    @param e
     */
    @Override
    public void receiveEvent(Event e) {
        
        if (e.getMessageName().equals("reportFault") 
                && e.getMessageArgs().size() == 0) {
            
            //String bikeId = e.getMessageArg(0);
            reportFault();
            
        } else {
            super.receiveEvent(e);
        }
    }
    
    /**
     * 
     * 
     * 
     */
    public void reportFault() {
        logger.fine(getInstanceName());
        
        observer.faultButtonPressed();
    }

    /*
     * 
     *  METHODS FOR HANDLING NON-TRIGGERING INPUT
     *  
     */
    
 


}
