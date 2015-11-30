package bikescheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class for charging users 
 * @author paminalin
 *
 */

public class BankServer extends AbstractOutputDevice {
	public BankServer(String instanceName) {
		super(instanceName);
	}
	
	public void chargeUser(String authCode, int amount) {
		logger.fine(getInstanceName());
        
        String deviceClass = "BankServer";
        String deviceInstance = getInstanceName();
        String messageName = "chargeUsers";
        
        List<String> messageArgs = new ArrayList<String>();
        String[] preludeArgs = 
            {"unordered-tuples","2","AuthorizationCode",
             "Amount"};
        List<String> chargeData = new ArrayList<String>();
        chargeData.add(authCode);
        chargeData.add(Integer.toString(amount));
        messageArgs.addAll(Arrays.asList(preludeArgs));
        messageArgs.addAll(chargeData);
        
        super.sendEvent(
            new Event(
                Clock.getInstance().getDateAndTime(), 
                deviceClass,
                deviceInstance,
                messageName,
                messageArgs));	
	}
}
