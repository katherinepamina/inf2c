/**
 * 
 */
package bikescheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 
 * Hub system.
 * 
 * 
 * @author pbj
 * 
 */
public class Hub implements AddDStationObserver, ViewStatsObserver {
	public static final Logger logger = Logger.getLogger("bikescheme");

	private HubTerminal terminal;
	private HubDisplay display;
	private BankServer bankServer;
	private Map<String, DStation> dockingStationMap;
	private ArrayList<User> users;
	private HashMap<String, User> keyIDToUserMap;
	private HashMap<String, Bike> bikeIDToBikeMap;

	/**
	 * 
	 * Construct a hub system with an operator terminal, a wall display and
	 * connections to a number of docking stations (initially 0).
	 * 
	 * Schedule update of the hub wall display every 5 minutes with docking
	 * station occupancy data.
	 * 
	 * @param instanceName
	 */
	public Hub() {

		// Construct and make connections with interface devices
		terminal = new HubTerminal("ht");
		terminal.setObserver(this, this);
		display = new HubDisplay("hd");
		bankServer = new BankServer("bs");
		dockingStationMap = new HashMap<String, DStation>();
		users = new ArrayList<User>();

		keyIDToUserMap = new HashMap<String, User>();
		bikeIDToBikeMap = new HashMap<String, Bike>();

		// Schedule timed notification for generating updates of
		// hub display.

		// The idiom of an anonymous class is used here, to make it easy
		// for hub code to process multiple timed notification, if needed.

		Clock.getInstance().scheduleNotification(
				new TimedNotificationObserver() {

					@Override
					public void processTimedNotification() {
						logger.fine("");

						String[] occupancyArray = generateOccupancyArray();
						// "DSName","East","North","Status","#Occupied","#DPoints"

						List<String> occupancyData = Arrays.asList(occupancyArray);
						display.showOccupancy(occupancyData);
					}

				}, Clock.getStartDate(), 0, 5);

		// Charge all users daily at midnight
		Clock.getInstance().scheduleNotification(
				new TimedNotificationObserver() {
					// function goes here
					@Override
					public void processTimedNotification() {
						logger.fine("");
						if (users == null) {
							return;
						}
						for (User u : users) {
							List<Session> todaySessions = u.getTodaySessions();
							int totalCost = 0;
							if (todaySessions != null) {
								for (Session s : todaySessions) {
									totalCost += s.getCost();
								}
								todaySessions.clear();
								bankServer.chargeUser(u.getBankCard().getAuthCode(), totalCost);
							}
						}
					}
				}, Clock.getStartDate(), 24, 0);
	}

	public void setDistributor(EventDistributor d) {

		// The clock device is connected to the EventDistributor here, even
		// though the clock object is not constructed here,
		// as no distributor is available to the Clock constructor.
		Clock.getInstance().addDistributorLinks(d);
		terminal.addDistributorLinks(d);
	}

	public void setCollector(EventCollector c) {
		display.setCollector(c);
		terminal.setCollector(c);
	}

	/**
     * 
     */
	@Override
	public void addDStation(String instanceName, int eastPos, int northPos,
			int numPoints) {
		logger.fine("");

		logger.fine("Adding Docking Station");
		DStation newDStation = new DStation(instanceName, eastPos, northPos,
				numPoints, this);
		dockingStationMap.put(instanceName, newDStation);

		// Now connect up DStation to event distributor and collector.

		EventDistributor d = terminal.getDistributor();
		EventCollector c = display.getCollector();

		newDStation.setDistributor(d);
		newDStation.setCollector(c);
	}

	// generates Occupancy array based off DStation statuses
	private String[] generateOccupancyArray() {
		String[] occupancyArray = new String[dockingStationMap.keySet().size() * 6];
		int counter = 0;
		for (String instName : dockingStationMap.keySet()) {
			DStation station = dockingStationMap.get(instName);
			int east = station.getEastPos();
			int north = station.getNorthPos();
			int numFree = station.getNumFreePoints();
			int numDPoints = station.getNumPoints();
			int numOccupied = numDPoints - numFree;
			double occupancy = (numOccupied + 0.0) / numDPoints;
			String status = "";
			if (occupancy > .85) {
				status = "HIGH";
			} else if (occupancy < .15) {
				status = "LOW";
			} else {
				status = "OK";
			}

			occupancyArray[counter] = instName;
			occupancyArray[counter + 1] = Integer.toString(east);
			occupancyArray[counter + 2] = Integer.toString(north);
			occupancyArray[counter + 3] = status;
			occupancyArray[counter + 4] = Integer.toString(numOccupied);
			occupancyArray[counter + 5] = Integer.toString(numDPoints);

			counter += 6;
		}

		return occupancyArray;
	}

	public DStation getDStation(String instanceName) {
		return dockingStationMap.get(instanceName);
	}

	public Map<String, DStation> getDStationMap() {
		return dockingStationMap;
	}

	public User getUserByKeyID(String keyID) {
		return keyIDToUserMap.get(keyID);
	}

	public void addUser(User u, String keyID) {
		users.add(u);
		keyIDToUserMap.put(keyID, u);
	}

	public int getNumUsers() {
		return users.size();
	}

	public int getNumDStations() {
		return dockingStationMap.size();
	}

	// return whether a keyID describes a registered user rather than a staff
	// member
	public boolean isUser(String keyID) {
		return keyIDToUserMap.containsKey(keyID);
	}

	public Bike getBikeByBikeID(String bikeID) {
		return bikeIDToBikeMap.get(bikeID);
	}

	public Bike createNewBike(String bikeID) {
		Bike newBike = new Bike(bikeID);
		bikeIDToBikeMap.put(bikeID, newBike);

		return newBike;
	}

	public void removeBikeFromMap(String bikeID) {
		bikeIDToBikeMap.remove(bikeID);
	}

	public HubTerminal getHubTerminal() {
		return terminal;
	}

	public void viewStatsReceived(String activityType) {
		if (activityType.equals("faultyLocations")) {
			reportFaultyLocations();

		}
	}

	private void reportFaultyLocations() {
		ArrayList<String> faultyList = new ArrayList<String>();

		
		for (Bike bike : bikeIDToBikeMap.values()) {
			if (bike.isFaulty()) {
				String stationName = bike.getCurrentDStation();
				DStation currStation = dockingStationMap.get(stationName);
				faultyList.add(stationName);
				faultyList.add(Integer.toString(currStation.getEastPos()));
				faultyList.add(Integer.toString(currStation.getNorthPos()));
				faultyList.add(bike.getCurrentDPointName());				
			}
		}
		
		display.showFaultyLocations(faultyList);
	}

}
