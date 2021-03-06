/**
 * 
 */
package bikescheme;

// import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
// import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author pbj
 *
 */
public class SystemTest {
    private static final String LS = System.getProperty("line.separator");
    private static Logger logger;
    
    private EventDistributor distributor;
    private EventCollector collector;
    
    private List<Event> expectedOutputEvents;
    
    /*
     * 
     * INSERT SYSTEM TESTS HERE
     * 
     * 
     * 
     */
    
    
    /**
     * 
     * Setup demonstration system configuration:
     * 
     * Clock clk ----------------->
     * HubTerminal ht <-----------> Hub  -------->   HubDisplay d
     *                              |   
     *                              |   
     *                              v
     * DSTouchScreen x.ts <---->  
     * CardReader x.cr <------->  DStation x   -------> KeyIssuer x.ki
     *                          |  x in {A,B}
     *                          |
     *                          v
     * KeyReader x.k.kr ---> DPoint x.k    ------> OKLight x.k.ok
     *                       for x.k in {A.1 ... A.5,
     *                                   B.1 ... B.3}
     *  
     *  This configuration is used in all the demonstration tests.
     *  
     *  It is inserted explicitly into each @Test block rather than the 
     *  @Before block so that alternate configurations can also be set up
     *  in this same test class.
     *   
     */
    public void setupDemoAddDStation() {
        input("1 07:00, HubTerminal, ht, addDStation, A,   0,   0, 5");
        input("1 07:00, HubTerminal, ht, addDStation, B, 400, 300, 3");
        input("1 07:00, HubTerminal, ht, addDStation, C, 200, 100, 15");
    }
    
    public void setupDemoSystemConfig() {
        input("1 07:00, HubTerminal, ht, addDStation, A,   0,   0, 5");
        input("1 07:00, HubTerminal, ht, addDStation, B, 400, 300, 3");
    }
    
    
    

    /**
     *  Run the "Register User" use case.
     * 
     */
    @Test
    public void testRegisterUser() {
        logger.info("Starting test: registerUser");

        logger.info("registerUser");
        
        setupDemoAddDStation();
        input("2 08:00, DSTouchScreen, A.ts, startReg, Alice");
        expect("2 08:00, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 08:00, CardReader, A.cr, enterCardAndPin");
        input("2 08:01, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 08:01, KeyIssuer, A.ki, keyIssued, A.ki-1");
    }
    
    @Test
    public void testRegisterUserMultiple() {
    	logger.info("Starting test: registerUserMultiple");
    	
    	setupDemoAddDStation();
    	input("2 08:00, DSTouchScreen, A.ts, startReg, Alice");
        expect("2 08:00, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 08:00, CardReader, A.cr, enterCardAndPin");
        input("2 08:01, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 08:01, KeyIssuer, A.ki, keyIssued, A.ki-1");
        
    	input("20 12:13, DSTouchScreen, C.ts, startReg, Bob");
    	expect("20 12:13, DSTouchScreen, C.ts, viewPrompt, Please enter your personal details.");
    	expect("20 12:13, CardReader, C.cr, enterCardAndPin");
    	input("20 12:13, CardReader, C.cr, checkCard, Bobauthcode");
    	expect("20 12:13, KeyIssuer, C.ki, keyIssued, C.ki-1");
    	
    	input("26 00:00, DSTouchScreen, B.ts, startReg, Cindy");
    	expect("26 00:00, DSTouchScreen, B.ts, viewPrompt, Please enter your personal details.");
    	expect("26 00:00, CardReader, B.cr, enterCardAndPin");
    	input("26 00:00, CardReader, B.cr, checkCard, Cindyauthcode");
    	expect("26 00:00, KeyIssuer, B.ki, keyIssued, B.ki-1");
    }
    
    @Test
    public void testRegisterUserForImmediateUse() {
    	setupDemoAddDStation();
    	input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 08:10, BikeLock, A.1.bl, locked");
        expect("2 08:10, OKLight, A.1.ok, flashed");
        
        input("2 08:00, DSTouchScreen, A.ts, startReg, Alice");
        expect("2 08:00, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 08:00, CardReader, A.cr, enterCardAndPin");
        input("2 08:01, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 08:01, KeyIssuer, A.ki, keyIssued, A.ki-1");
        
        input("2 09:30, KeyReader, A.1.kr, insertKey, A.ki-1");
        expect("2 09:30, BikeLock, A.1.bl, unlocked");
        expect("2 09:30, OKLight, A.1.ok, flashed");   
    }
    
    @Test
    public void testViewUserActivity() {
    	logger.info("Starting test: viewUserActivity");
    	
    	setupDemoAddDStation();
        
        input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 08:10, BikeLock, A.1.bl, locked");
        expect("2 08:10, OKLight, A.1.ok, flashed");
    	
    	input("2 08:00, DSTouchScreen, A.ts, startReg, Alice");
    	expect("2 08:00, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 08:00, CardReader, A.cr, enterCardAndPin");
        input("2 08:01, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 08:01, KeyIssuer, A.ki, keyIssued, A.ki-1");
        
        input("2 09:30, KeyReader, A.1.kr, insertKey, A.ki-1");
        expect("2 09:30, BikeLock, A.1.bl, unlocked");
        expect("2 09:30, OKLight, A.1.ok, flashed");
        
        input("2 09:43, BikeSensor, B.3.bs, dockBike, 011");
        expect("2 09:43, BikeLock, B.3.bl, locked");
        expect("2 09:43, OKLight, B.3.ok, flashed");
        
        
        input("2 10:00, DSTouchScreen, B.ts, viewActivity, A.ki-1");
        expect("2 10:00, DSTouchScreen, B.ts, viewPrompt, Please insert your key");
        input("2 10:00, KeyReader, B.kr, insertKey, A.ki-1");
        expect("2 10:00, DSTouchScreen, B.ts, viewUserActivity, ordered-tuples, 4, HireTime, HireDS, ReturnDS, Duration (min),"
        		+ "2 09:30, A, B, 13");	
    }
  
    @Test
    public void testViewUserActivity2() {
    	logger.info("Starting test: testViewUserActivity2");
    	
    	setupDemoAddDStation();
        
        input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 08:10, BikeLock, A.1.bl, locked");
        expect("2 08:10, OKLight, A.1.ok, flashed");
        
        input("2 08:29, BikeSensor, A.4.bs, dockBike, 012");
        expect("2 08:29, BikeLock, A.4.bl, locked");
        expect("2 08:29, OKLight, A.4.ok, flashed");
    	
    	input("2 08:00, DSTouchScreen, A.ts, startReg, Alice");
    	expect("2 08:00, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 08:00, CardReader, A.cr, enterCardAndPin");
        input("2 08:01, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 08:01, KeyIssuer, A.ki, keyIssued, A.ki-1");
        
        input("2 09:30, KeyReader, A.1.kr, insertKey, A.ki-1");
        expect("2 09:30, BikeLock, A.1.bl, unlocked");
        expect("2 09:30, OKLight, A.1.ok, flashed");
        
        input("2 09:43, BikeSensor, B.3.bs, dockBike, 011");
        expect("2 09:43, BikeLock, B.3.bl, locked");
        expect("2 09:43, OKLight, B.3.ok, flashed");
        
        input("2 10:00, KeyReader, B.3.kr, insertKey, A.ki-1");
        expect("2 10:00, BikeLock, B.3.bl, unlocked");
        expect("2 10:00, OKLight, B.3.ok, flashed");
        
        // Passing in keyid first because unsure of how to retrieve otherwise
        input("2 10:20, DSTouchScreen, B.ts, viewActivity, A.ki-1");
        expect("2 10:20, DSTouchScreen, B.ts, viewPrompt, Please insert your key");
        input("2 10:20, KeyReader, B.kr, insertKey, A.ki-1");
        expect("2 10:20, DSTouchScreen, B.ts, viewUserActivity, ordered-tuples, 4, HireTime, HireDS, ReturnDS, Duration (min),"
        		+ "2 09:30, A, B, 13,"
        		+ "2 10:00, B, Not returned, 20");	
    }
   
    @Test
    // Show only the user's activity
    public void testViewUserActivity3() {
    	setupDemoAddDStation();
logger.info("Starting test: viewUserActivity2");
    	
    	setupDemoAddDStation();
        
    	// Add bikes to DPoints A.1 and A.4
        input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 08:10, BikeLock, A.1.bl, locked");
        expect("2 08:10, OKLight, A.1.ok, flashed");
        
        input("2 08:29, BikeSensor, A.4.bs, dockBike, 012");
        expect("2 08:29, BikeLock, A.4.bl, locked");
        expect("2 08:29, OKLight, A.4.ok, flashed");
    	
        // Register a user, Alice
    	input("2 08:00, DSTouchScreen, A.ts, startReg, Alice");
    	expect("2 08:00, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 08:00, CardReader, A.cr, enterCardAndPin");
        input("2 08:01, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 08:01, KeyIssuer, A.ki, keyIssued, A.ki-1");
        
        // Alice hires and returns bike
        input("27 09:30, KeyReader, A.1.kr, insertKey, A.ki-1");
        expect("27 09:30, BikeLock, A.1.bl, unlocked");
        expect("27 09:30, OKLight, A.1.ok, flashed");
        
        input("27 09:45, BikeSensor, B.3.bs, dockBike, 011");
        expect("27 09:45, BikeLock, B.3.bl, locked");
        expect("27 09:45, OKLight, B.3.ok, flashed");
        
        // Alice hires another bike
        input("27 10:00, KeyReader, B.3.kr, insertKey, A.ki-1");
        expect("27 10:00, BikeLock, B.3.bl, unlocked");
        expect("27 10:00, OKLight, B.3.ok, flashed");
        
        // Register another user, Cindy
        input("27 00:00, DSTouchScreen, B.ts, startReg, Cindy");
    	expect("27 00:00, DSTouchScreen, B.ts, viewPrompt, Please enter your personal details.");
    	expect("27 00:00, CardReader, B.cr, enterCardAndPin");
    	input("27 00:00, CardReader, B.cr, checkCard, Cindyauthcode");
    	expect("27 00:00, KeyIssuer, B.ki, keyIssued, B.ki-1");
    	
    	// Cindy uses bike at DPoint A.4
    	input("27 09:30, KeyReader, A.4.kr, insertKey, B.ki-1");
        expect("27 09:30, BikeLock, A.4.bl, unlocked");
        expect("27 09:30, OKLight, A.4.ok, flashed");
        
        // Alice checks her daily activity
        input("27 10:20, DSTouchScreen, B.ts, viewActivity, A.ki-1");
        expect("27 10:20, DSTouchScreen, B.ts, viewPrompt, Please insert your key");
        input("27 10:20, KeyReader, B.kr, insertKey, A.ki-1");
        expect("27 10:20, DSTouchScreen, B.ts, viewUserActivity, ordered-tuples, 4, HireTime, HireDS, ReturnDS, Duration (min),"
        		+ "27 09:30, A, B, 15,"
        		+ "27 10:00, B, Not returned, 20");	
        
        // Cindy checks her daily activity
        input("27 10:20, DSTouchScreen, C.ts, viewActivity, B.ki-1");
        expect("27 10:20, DSTouchScreen, C.ts, viewPrompt, Please insert your key");
        input("27 10:20, KeyReader, C.kr, insertKey, B.ki-1");
        expect("27 10:20, DSTouchScreen, C.ts, viewUserActivity, ordered-tuples, 4, HireTime, HireDS, ReturnDS, Duration (min),"
        		+ "27 09:30, A, Not returned, 50");	
    }
    
    @Test
    public void testViewOccupancyDemoConfig() {
    	logger.info("Starting test: view occupancy simple");

        setupDemoSystemConfig();
        input ("2 08:00, Clock, clk, tick");
        expect("2 08:00, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,    0,    0,    LOW,    0,       5," 
                + "     B,  400,  300,    LOW,    0,       3");
    }
    
    @Test
    public void testAddBrandNewBike() {
    	logger.info("Starting test: add brand new bike");

        setupDemoSystemConfig();
        //bike is being added by staff since no users have been registered
        input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 08:10, BikeLock, A.1.bl, locked");
        expect("2 08:10, OKLight, A.1.ok, flashed");
        
    }
    
    
    //tests adding a bike, hiring a bike, responsiveness of view occupancy
    @Test
    public void testHireBikeSingle() {
    	logger.info("Starting test: hire single bike and view occupancy pre/post hire");

        setupDemoSystemConfig();
        //bike is being added by staff since no users have been registered
        input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 08:10, BikeLock, A.1.bl, locked");
        expect("2 08:10, OKLight, A.1.ok, flashed");
        
        
        input ("2 08:10, Clock, clk, tick");
        expect("2 08:10, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,    0,    0,     OK,         1,       5," 
                + "     B,  400,  300,    LOW,         0,       3");
        
        
        input ("2 09:10, DSTouchScreen, A.ts, startReg, Alice");
        expect("2 09:10, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 09:10, CardReader, A.cr, enterCardAndPin");
        input ("2 09:11, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 09:11, KeyIssuer, A.ki, keyIssued, A.ki-1");
        
        input("2 09:30, KeyReader, A.1.kr, insertKey, A.ki-1");
        expect("2 09:30, BikeLock, A.1.bl, unlocked");
        expect("2 09:30, OKLight, A.1.ok, flashed");
        input ("2 09:35, Clock, clk, tick");
        expect("2 09:35, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,    0,    0,    LOW,         0,       5," 
                + "     B,  400,  300,    LOW,         0,       3");
        
        
    }
    
    @Test
    public void testHireAndReturnBikeSingle() {
    	testHireBikeSingle();
    	input("2 09:43, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 09:43, BikeLock, A.1.bl, locked");
        expect("2 09:43, OKLight, A.1.ok, flashed");
    }
    
    
    //tests adding a bike, hiring a bike, responsiveness of view occupancy
    @Test
    public void testHireBikeMultiple() {
    	logger.info("Starting test: hire bike multiple and view occupancy pre/post");

        setupDemoSystemConfig();
        //bike is being added by staff since no users have been registered
        input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 08:10, BikeLock, A.1.bl, locked");
        expect("2 08:10, OKLight, A.1.ok, flashed");
        
        input("2 08:29, BikeSensor, A.4.bs, dockBike, 012");
        expect("2 08:29, BikeLock, A.4.bl, locked");
        expect("2 08:29, OKLight, A.4.ok, flashed");
        
        input ("2 08:30, Clock, clk, tick");
        expect("2 08:30, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,    0,    0,     OK,         2,       5," 
                + "     B,  400,  300,    LOW,         0,       3");
        
        input("2 08:43, BikeSensor, B.2.bs, dockBike, 013");
        expect("2 08:43, BikeLock, B.2.bl, locked");
        expect("2 08:43, OKLight, B.2.ok, flashed");
        
        input ("2 08:45, Clock, clk, tick");
        expect("2 08:45, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,    0,    0,    OK,         2,       5," 
                + "     B,  400,  300,    OK,         1,       3");
        
        
        input ("2 09:10, DSTouchScreen, A.ts, startReg, Alice");
        expect("2 09:10, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 09:10, CardReader, A.cr, enterCardAndPin");
        input ("2 09:11, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 09:11, KeyIssuer, A.ki, keyIssued, A.ki-1");
        
        input ("2 09:11, DSTouchScreen, B.ts, startReg, John");
        expect("2 09:11, DSTouchScreen, B.ts, viewPrompt, Please enter your personal details.");
        expect("2 09:11, CardReader, B.cr, enterCardAndPin");
        input ("2 09:12, CardReader, B.cr, checkCard, John-card-auth");
        expect("2 09:12, KeyIssuer, B.ki, keyIssued, B.ki-1");
        
        input("2 09:30, KeyReader, B.2.kr, insertKey, B.ki-1");
        expect("2 09:30, BikeLock, B.2.bl, unlocked");
        expect("2 09:30, OKLight, B.2.ok, flashed");
        input ("2 09:35, Clock, clk, tick");
        expect("2 09:35, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,    0,    0,     OK,         2,       5," 
                + "     B,  400,  300,    LOW,         0,       3");
        
        
    }
    
    // tests adding a bike, removing a bike, responsiveness of view occupancy
    @Test
    public void testRemoveBike() {
    	logger.info("Starting test: remove bike");
    	setupDemoSystemConfig();
    	// bike added by staff since no users have been registered
    	input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
    	expect("2 08:10, BikeLock, A.1.bl, locked");
    	expect("2 08:10, OKLight, A.1.ok, flashed");
    	
    	input("2 08:29, BikeSensor, A.4.bs, dockBike, 012");
        expect("2 08:29, BikeLock, A.4.bl, locked");
        expect("2 08:29, OKLight, A.4.ok, flashed");
        
        input ("2 08:30, Clock, clk, tick");
        expect("2 08:30, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,    0,    0,     OK,         2,       5," 
                + "     B,  400,  300,    LOW,         0,       3");
        
        input("2 08:43, BikeSensor, B.2.bs, dockBike, 013");
        expect("2 08:43, BikeLock, B.2.bl, locked");
        expect("2 08:43, OKLight, B.2.ok, flashed");
        
        input ("2 08:45, Clock, clk, tick");
        expect("2 08:45, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,    0,    0,    OK,        2,       5," 
                + "     B,  400,  300,    OK,         1,       3");
        
        input("2 08:50, KeyReader, A.1.kr, insertKey, staffkey");
        expect("2 08:50, BikeLock, A.1.bl, unlocked");
        expect("2 08:50, OKLight, A.1.ok, flashed");
        
        input("2 08:50, KeyReader, A.4.kr, insertKey, staffkey");
        expect("2 08:50, BikeLock, A.4.bl, unlocked");
        expect("2 08:50, OKLight, A.4.ok, flashed");
        
        input ("2 08:50, Clock, clk, tick");
        expect("2 08:50, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,    0,    0,   LOW,         0,       5," 
                + "     B,  400,  300,    OK,         1,       3");
    	
    }
    
  //tests adding a bike, hiring a bike, responsiveness of view occupancy
    @Test
    public void testReturnBikeMultiple() {
    	logger.info("Starting test: return bike multiple and view occupancy pre/post");

        setupDemoSystemConfig();
        //bike is being added by staff since no users have been registered
        input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 08:10, BikeLock, A.1.bl, locked");
        expect("2 08:10, OKLight, A.1.ok, flashed");
        
        input("2 08:29, BikeSensor, A.4.bs, dockBike, 012");
        expect("2 08:29, BikeLock, A.4.bl, locked");
        expect("2 08:29, OKLight, A.4.ok, flashed");
        
        input ("2 08:30, Clock, clk, tick");
        expect("2 08:30, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,  0,   0,   OK,        2,       5," 
                + "     B,  400,  300,    LOW,         0,       3");
        
        input("2 08:43, BikeSensor, B.2.bs, dockBike, 013");
        expect("2 08:43, BikeLock, B.2.bl, locked");
        expect("2 08:43, OKLight, B.2.ok, flashed");
        
        input ("2 08:45, Clock, clk, tick");
        expect("2 08:45, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,  0,   0,   OK,        2,       5," 
                + "     B,  400,  300,    OK,         1,       3");
        
        
        input ("2 09:10, DSTouchScreen, A.ts, startReg, Alice");
        expect("2 09:10, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 09:10, CardReader, A.cr, enterCardAndPin");
        input ("2 09:11, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 09:11, KeyIssuer, A.ki, keyIssued, A.ki-1");
        
        input ("2 09:11, DSTouchScreen, B.ts, startReg, John");
        expect("2 09:11, DSTouchScreen, B.ts, viewPrompt, Please enter your personal details.");
        expect("2 09:11, CardReader, B.cr, enterCardAndPin");
        input ("2 09:12, CardReader, B.cr, checkCard, John-card-auth");
        expect("2 09:12, KeyIssuer, B.ki, keyIssued, B.ki-1");
        
        input("2 09:30, KeyReader, B.2.kr, insertKey, B.ki-1");
        expect("2 09:30, BikeLock, B.2.bl, unlocked");
        expect("2 09:30, OKLight, B.2.ok, flashed");
        
        input ("2 09:35, Clock, clk, tick");
        expect("2 09:35, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,  0,   0,   OK,        2,       5," 
                + "     B,  400,  300,    LOW,         0,       3");
        
        input("2 09:43, BikeSensor, A.3.bs, dockBike, 013");
        expect("2 09:43, BikeLock, A.3.bl, locked");
        expect("2 09:43, OKLight, A.3.ok, flashed");
        
        input ("2 09:45, Clock, clk, tick");
        expect("2 09:45, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,  0,   0,   OK,        3,       5," 
                + "     B,  400,  300,    LOW,         0,       3");
        
    }
    
    
  //tests adding a bike, hiring a bike, responsiveness of view occupancy
    @Test
    public void testViewOccupancyHigh() {
    	logger.info("Starting test: test high occupancy");

        setupDemoSystemConfig();
        //bike is being added by staff since no users have been registered
        input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 08:10, BikeLock, A.1.bl, locked");
        expect("2 08:10, OKLight, A.1.ok, flashed");
        
        input("2 08:29, BikeSensor, A.4.bs, dockBike, 012");
        expect("2 08:29, BikeLock, A.4.bl, locked");
        expect("2 08:29, OKLight, A.4.ok, flashed");

        input("2 09:43, BikeSensor, A.3.bs, dockBike, 013");
        expect("2 09:43, BikeLock, A.3.bl, locked");
        expect("2 09:43, OKLight, A.3.ok, flashed");
        
        //new bikes added to A
        input("2 11:10, BikeSensor, A.5.bs, dockBike, 016");
        expect("2 11:10, BikeLock, A.5.bl, locked");
        expect("2 11:10, OKLight, A.5.ok, flashed");
        
        input("2 11:45, BikeSensor, A.2.bs, dockBike, 015");
        expect("2 11:45, BikeLock, A.2.bl, locked");
        expect("2 11:45, OKLight, A.2.ok, flashed");
        
        
        input ("2 12:00, Clock, clk, tick");
        expect("2 12:00, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     A,  0,   0,   HIGH,        5,       5," 
                + "     B,  400,  300,    LOW,         0,       3");
        
    }
    
    @Test
    public void setupFreePointsSystemConfig() {
    	input("1 07:00, HubTerminal, ht, addDStation, A,   0,   0, 1");
        input("1 07:00, HubTerminal, ht, addDStation, B, 400, 300, 10");
        input("1 07:00, HubTerminal, ht, addDStation, C,   100,   50, 10");
        input("1 07:00, HubTerminal, ht, addDStation, D, 200, 50, 10");
        input("1 07:00, HubTerminal, ht, addDStation, E, -100, 25, 1");
        input("1 07:00, HubTerminal, ht, addDStation, F, -50, -50, 10");
        
        input("1 07:00, BikeSensor, A.1.bs, dockBike, 011");
        expect("1 07:00, BikeLock, A.1.bl, locked");
        expect("1 07:00, OKLight, A.1.ok, flashed");
        input("1 07:00, BikeSensor, B.1.bs, dockBike, 012");
        expect("1 07:00, BikeLock, B.1.bl, locked");
        expect("1 07:00, OKLight, B.1.ok, flashed");
        input("1 07:00, BikeSensor, E.1.bs, dockBike, 013");
        expect("1 07:00, BikeLock, E.1.bl, locked");
        expect("1 07:00, OKLight, E.1.ok, flashed");
    }
    
    @Test
    public void testFindFreePoints() {
    	logger.info("Starting test: testFindFreePoints");
    	
    	setupFreePointsSystemConfig();
    	
    	input("20 10:00, DSTouchScreen, A.ts, findFreePoints, A.ki-1");
    	expect("20 10:00, DSTouchScreen, A.ts, showFreePoints, unordered-tuples, 4," +
    			"Name, EastPos, NorthPos, Distance (meters)," +
    			"C, 100, 50, 150," +
    			"D, 200, 50, 250," +
    			"F, -50, -50, 100");
    	input("20 10:00, DSTouchScreen, E.ts, findFreePoints, A.ki-1");
    	expect("20 10:00, DSTouchScreen, E.ts, showFreePoints, unordered-tuples, 4," +
    			"Name, EastPos, NorthPos, Distance (meters)," +
    			"C, 100, 50, 225," +
    			"F, -50, -50, 125");
    }
    
    @Test
    public void testFindFreePointsAddExtension() {
    	setupFreePointsSystemConfig();
    	
    	input("27 08:00, DSTouchScreen, A.ts, startReg, Alice");
        expect("27 08:00, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("27 08:00, CardReader, A.cr, enterCardAndPin");
        input("27 08:01, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("27 08:01, KeyIssuer, A.ki, keyIssued, A.ki-1");
    
    	// hire from A, return at E, E is full, add extension and return at F
    	input("27 09:30, KeyReader, A.1.kr, insertKey, A.ki-1");
        expect("27 09:30, BikeLock, A.1.bl, unlocked");
        expect("27 09:30, OKLight, A.1.ok, flashed");
        
        input("27 10:00, DSTouchScreen, E.ts, findFreePoints, A.ki-1");
    	expect("27 10:00, DSTouchScreen, E.ts, showFreePoints, unordered-tuples, 4," +
    			"Name, EastPos, NorthPos, Distance (meters)," +
    			"A,   0,   0,  125," +
    			"C, 100,  50,  225," +
    			"F, -50, -50,  125");
        
        input("27 10:10, BikeSensor, F.1.bs, dockBike, 011");
        expect("27 10:10, BikeLock, F.1.bl, locked");
        expect("27 10:10, OKLight, F.1.ok, flashed");
        
        input ("28 00:00, Clock, clk, tick");
    	expect("28 00:00, HubDisplay, hd, viewOccupancy, unordered-tuples, 6," +
    			"DSName, East, North, Status, #Occupied, #DPoints," +
    			"A,    0,   0,  LOW,  0,  1," +
    			"B,  400, 300,  LOW,  1, 10," +
    			"C,  100, 50,  LOW,  0, 10," +
    			"D,  200,  50,  LOW,  0, 10," +
    			"E, -100,  25, HIGH,  1,  1," +
    			"F,  -50, -50,  LOW,  1, 10");
    	expect("28 00:00, BankServer, bs, chargeUser, unordered-tuples, 2, AuthorizationCode, Amount, dummycode, 1");
        
    	
    }
    
    @Test
    public void testReportFaultWithinTimeLimit() {
    	logger.info("Starting test: testReportFault");
    	setupDemoSystemConfig();
 
        testHireAndReturnBikeSingle();
        input("2 09:44, FaultButton, A.1.fb, reportFault");
        expect("2 09:44, FaultLight, A.1.fl, flashed");
        
        input("2 09:52, HubTerminal, ht, viewStats, faultyLocations");
        expect("2 09:52, HubDisplay, hd, viewFaultyLocations, unordered-tuples, 4,"
                + "DSName, East, North, DPointIndex,"
                + "     A,  0,   0,   1");
        
        
    }
    
    @Test
    public void testReportFaultOutsideTimeLimit() {
    	logger.info("Starting test: testReportFault");
    	setupDemoSystemConfig();
 
        testHireAndReturnBikeSingle();
        input("2 09:46, FaultButton, A.1.fb, reportFault");
        
        input("2 09:52, HubTerminal, ht, viewStats, faultyLocations");
        expect("2 09:52, HubDisplay, hd, viewFaultyLocations, unordered-tuples, 4,"
                + "DSName, East, North, DPointIndex");
        
        
    }
    
    @Test
    public void testChargeUsers() {
    	logger.info("Starting test: chargeAllUsers");
    	setupDemoSystemConfig();
    	testHireAndReturnBikeSingle();
    	input ("3 00:00, Clock, clk, tick");
    	expect("3 00:00, HubDisplay, hd, viewOccupancy, unordered-tuples, 6," +
    			"DSName, East, North, Status, #Occupied, #DPoints," +
    			"A, 0, 0, OK, 1, 5," +
    			"B, 400, 300, LOW, 0, 3");
    	expect("3 00:00, BankServer, bs, chargeUser, unordered-tuples, 2, AuthorizationCode, Amount, dummycode, 1");
    }
    
    
    @Test
    public void testSingleReportFaultyLocationsViewStats() {
    	logger.info("Starting test: View Stats of Single Report Faulty Location");

        setupDemoSystemConfig();
        //bike is being added by staff since no users have been registered
        input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 08:10, BikeLock, A.1.bl, locked");
        expect("2 08:10, OKLight, A.1.ok, flashed");
        
        input("2 08:29, BikeSensor, A.4.bs, dockBike, 012");
        expect("2 08:29, BikeLock, A.4.bl, locked");
        expect("2 08:29, OKLight, A.4.ok, flashed");
        
        input("2 08:43, BikeSensor, B.2.bs, dockBike, 013");
        expect("2 08:43, BikeLock, B.2.bl, locked");
        expect("2 08:43, OKLight, B.2.ok, flashed");
        
        
        input ("2 09:10, DSTouchScreen, A.ts, startReg, Alice");
        expect("2 09:10, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 09:10, CardReader, A.cr, enterCardAndPin");
        input ("2 09:11, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 09:11, KeyIssuer, A.ki, keyIssued, A.ki-1");
        
        input("2 09:30, KeyReader, B.2.kr, insertKey, A.ki-1");
        expect("2 09:30, BikeLock, B.2.bl, unlocked");
        expect("2 09:30, OKLight, B.2.ok, flashed");
        
        input("2 09:50, BikeSensor, A.2.bs, dockBike, 013");
        expect("2 09:50, BikeLock, A.2.bl, locked");
        expect("2 09:50, OKLight, A.2.ok, flashed");
        
        //should mark A.2 as faulty
        input("2 09:51, FaultButton, A.2.fb, reportFault");
        expect("2 09:51, FaultLight, A.2.fl, flashed");
        
        input("2 09:52, HubTerminal, ht, viewStats, faultyLocations");
        expect("2 09:52, HubDisplay, hd, viewFaultyLocations, unordered-tuples, 4,"
                + "DSName, East, North, DPointIndex,"
                + "     A,  0,   0,   2");
    }
    
    @Test
    public void testMultipleReportFaultyLocationsViewStats() {
    	logger.info("Starting test: View Stats of Multiple Report Faulty Location");

        setupDemoSystemConfig();
        //bike is being added by staff since no users have been registered
        input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 08:10, BikeLock, A.1.bl, locked");
        expect("2 08:10, OKLight, A.1.ok, flashed");
        
        input("2 08:29, BikeSensor, A.4.bs, dockBike, 012");
        expect("2 08:29, BikeLock, A.4.bl, locked");
        expect("2 08:29, OKLight, A.4.ok, flashed");
        
        input("2 08:43, BikeSensor, B.2.bs, dockBike, 013");
        expect("2 08:43, BikeLock, B.2.bl, locked");
        expect("2 08:43, OKLight, B.2.ok, flashed");
        
        
        input ("2 09:10, DSTouchScreen, A.ts, startReg, Alice");
        expect("2 09:10, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 09:10, CardReader, A.cr, enterCardAndPin");
        input ("2 09:11, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 09:11, KeyIssuer, A.ki, keyIssued, A.ki-1");
        
        input("2 09:30, KeyReader, B.2.kr, insertKey, A.ki-1");
        expect("2 09:30, BikeLock, B.2.bl, unlocked");
        expect("2 09:30, OKLight, B.2.ok, flashed");
        
        input("2 09:50, BikeSensor, A.2.bs, dockBike, 013");
        expect("2 09:50, BikeLock, A.2.bl, locked");
        expect("2 09:50, OKLight, A.2.ok, flashed");
        
        //should mark A.2 as faulty
        input("2 09:51, FaultButton, A.2.fb, reportFault");
        expect("2 09:51, FaultLight, A.2.fl, flashed");
        
        input("2 15:16, KeyReader, A.1.kr, insertKey, A.ki-1");
        expect("2 15:16, BikeLock, A.1.bl, unlocked");
        expect("2 15:16, OKLight, A.1.ok, flashed");
        
        input("2 16:00, BikeSensor, B.3.bs, dockBike, 011");
        expect("2 16:00, BikeLock, B.3.bl, locked");
        expect("2 16:00, OKLight, B.3.ok, flashed");
        
        //should mark B.3 as faulty
        input("2 16:02, FaultButton, B.3.fb, reportFault");
        expect("2 16:02, FaultLight, B.3.fl, flashed");
        
        input("2 17:35, KeyReader, A.4.kr, insertKey, A.ki-1");
        expect("2 17:35, BikeLock, A.4.bl, unlocked");
        expect("2 17:35, OKLight, A.4.ok, flashed");
        
        input("2 18:02, BikeSensor, A.5.bs, dockBike, 012");
        expect("2 18:02, BikeLock, A.5.bl, locked");
        expect("2 18:02, OKLight, A.5.ok, flashed");
        
        //should mark a.5 as faulty
        input("2 18:02, FaultButton, A.5.fb, reportFault");
        expect("2 18:02, FaultLight, A.5.fl, flashed");
        
        
        input("2 19:02, HubTerminal, ht, viewStats, faultyLocations");
        expect("2 19:02, HubDisplay, hd, viewFaultyLocations, unordered-tuples, 4,"
                + "DSName, East, North, DPointIndex,"
                + "     A,  0,   0,   5,"
                + "     A,  0,   0,   2,"
                + "     B,  400,   300,   3");
    }
    
    @Test
    public void testReportLostBikes() {
    	logger.info("Starting test: View Stats of Lost Bikes");

        setupDemoSystemConfig();
        //bike is being added by staff since no users have been registered
        input("2 08:10, BikeSensor, A.1.bs, dockBike, 011");
        expect("2 08:10, BikeLock, A.1.bl, locked");
        expect("2 08:10, OKLight, A.1.ok, flashed");
        
        input("2 08:29, BikeSensor, A.4.bs, dockBike, 012");
        expect("2 08:29, BikeLock, A.4.bl, locked");
        expect("2 08:29, OKLight, A.4.ok, flashed");
        
        input("2 08:43, BikeSensor, B.2.bs, dockBike, 013");
        expect("2 08:43, BikeLock, B.2.bl, locked");
        expect("2 08:43, OKLight, B.2.ok, flashed");
        
        input("2 08:44, BikeSensor, B.3.bs, dockBike, 014");
        expect("2 08:44, BikeLock, B.3.bl, locked");
        expect("2 08:44, OKLight, B.3.ok, flashed");
        
        input ("2 09:10, DSTouchScreen, A.ts, startReg, Alice");
        expect("2 09:10, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 09:10, CardReader, A.cr, enterCardAndPin");
        input ("2 09:11, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 09:11, KeyIssuer, A.ki, keyIssued, A.ki-1");
        
        input ("2 09:15, DSTouchScreen, A.ts, startReg, John");
        expect("2 09:15, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 09:15, CardReader, A.cr, enterCardAndPin");
        input ("2 09:16, CardReader, A.cr, checkCard, John-card-auth");
        expect("2 09:16, KeyIssuer, A.ki, keyIssued, A.ki-2");
        
        input ("2 09:16, DSTouchScreen, A.ts, startReg, Baluga");
        expect("2 09:16, DSTouchScreen, A.ts, viewPrompt, Please enter your personal details.");
        expect("2 09:16, CardReader, A.cr, enterCardAndPin");
        input ("2 09:17, CardReader, A.cr, checkCard, Baluga-card-auth");
        expect("2 09:17, KeyIssuer, A.ki, keyIssued, A.ki-3");
        
        //rent 1 bike at 930 - Bike 013
        input("2 09:30, KeyReader, B.2.kr, insertKey, A.ki-1");
        expect("2 09:30, BikeLock, B.2.bl, unlocked");
        expect("2 09:30, OKLight, B.2.ok, flashed");
        
        //rent another at 1030 - Bike 012
        input("2 10:30, KeyReader, A.4.kr, insertKey, A.ki-2");
        expect("2 10:30, BikeLock, A.4.bl, unlocked");
        expect("2 10:30, OKLight, A.4.ok, flashed");
        
        //rent another at 1130 - Bike 011
        input("2 11:30, KeyReader, A.1.kr, insertKey, A.ki-3");
        expect("2 11:30, BikeLock, A.1.bl, unlocked");
        expect("2 11:30, OKLight, A.1.ok, flashed");
        
        
        input("3 11:31, HubTerminal, ht, viewStats, reportLostBikes");
        expect("3 11:31, HubDisplay, hd, viewLostBikes, unordered-tuples, 2,"
                + "BID, TimeOut,"
                + "     013,  1561,"
                + "     012,  1501,"
                + "     011,  1441");
        
    }
    /*
     * 
     * SUPPORT CODE FOR RUNNING TESTS
     * 
     * NOTHING HERE SHOULD NEED TOUCHING
     * 
     * 
     */
     
    /**
     * Utility method for specifying an input event to drive in.
     * 
     * For use in test methods in this class.
     * 
     * @param inputEventString
     */
    private void input(String inputEventString) {
        distributor.enqueue(new Event(inputEventString));
    }
    
    /**
     * Utility method for specifying an expected output event.
     * 
     * For use in test methods in this class.
     * 
     * Relies on test object field expectedOutputEvents for passing
     * argument output event to checking method. 
     * 
     * @param outputEventString
     */
    private void expect(String outputEventString) {
        expectedOutputEvents.add(new Event(outputEventString));
    }
    
    
    /**
     * Queue up input events at event distributor.
     * 
     * Intended for calling from other classes, when input events are
     * read from a file, for example.
     * 
     * @param es input events
     */
    public void enqueueInputEvents(List<Event> es) {
        for (Event e : es) {
            distributor.enqueue(e);
        }
    }
    
    
    /**
     * Set expected output events.  These are compared with actual 
     * output events after a test is run.
     * 
     * Intended for calling from other classes, when input events are
     * read from a file, for example.
     *
     * @param es expected output events
     */
    public void setExpectedOutputEvents(List<Event> es) {
        expectedOutputEvents = es;
    }
    
    
    /**
     * Initialise logging framework so all log records FINER and above
     * are reported.
     * 
     */
    @BeforeClass
    public static void setupLogger() {
         
        // Enable log record filtering at FINER level.
        logger = Logger.getLogger("bikescheme"); 
        logger.setLevel(Level.FINER);
        
        Logger rootLogger = Logger.getLogger("");
        Handler handler = rootLogger.getHandlers()[0];
        handler.setLevel(Level.FINER);
    }
    
    /**
     * Setup test environment and starting system configuration.
     * 
     * Starting system configuration consists of a Hub object and
     * no Docking Station objects.
     * 
     * Suitable for calling directly as well as from JUnit.
     */
    @Before
    public void setupTestEnvAndSystem() {
       
        // Initialise core event framework objects
        
        distributor = new EventDistributor();
        collector = new EventCollector(); 
        
        // Create a hub object with interface devices.
        
                Hub hub = new Hub();
                
        // Connect up hub interface devices to event framework
                
        hub.setDistributor(distributor);
        hub.setCollector(collector);
         
        // Initialise expected output
        
        expectedOutputEvents = new ArrayList<Event>();
    }
    
   
     /**
     * Run test and check results. 
     * 
     * Run this after input events have been loaded into event queue in 
     * event distributor and expected output events have been loaded into
     * expectedOutputEvents field of object this.
     * 
     * If called directly, not via JUnit runner, the AssertionError, thrown
     * when some assertion fails, should be caught.
     */ 
    @After
    public void runAndCheck() {
        List<Event> actualOutputEvents = runTestAndReturnResults();
        checkTestResults(expectedOutputEvents, actualOutputEvents);
    } 
    
    
    /**
     * Inject input events in distributor queue into system and return the
     * resulting output events.
     * 
     * This method can called directly as an alternative to runAndCheck
     * if results want to be seen, but not checked.
     * 
     * @return Output events from test run
     */
    public List<Event> runTestAndReturnResults() {

        distributor.sendEvents();
        List<Event> actualOutputEvents = collector.fetchEvents();
        return actualOutputEvents;
    }
    
    /**
     * Compare expected and actual output events.  
     * 
     * Uses Event.listEqual() to do the comparison.  This not the same as
     * the normal list equality. 
     * 
     * @see Event
     * 
     * @param expectedEvents
     * @param actualEvents
     */
    public void checkTestResults(
            List<Event> expectedEvents,  // Avoid field name expectedOutputEvents
            List<Event> actualEvents) {
            
        // Log output event sequences for easy comparison when different.

        
        StringBuilder sb = new StringBuilder();
        sb.append(LS);
        sb.append("Expected output events:");
        sb.append(LS);
        for (Event e : expectedEvents) {
            sb.append(e);
            sb.append(LS);
        }
        sb.append("Actual output events:");
        sb.append(LS);
        for (Event e : actualEvents) {
            sb.append(e);
            sb.append(LS);
        }
        logger.info(sb.toString());
        
        assertTrue("Expected and actual output events differ",
                Event.listEqual(expectedEvents, actualEvents));
               
    }
}
