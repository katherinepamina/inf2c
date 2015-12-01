/**
 * 
 */
package bikescheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
		Clock.createInstance();

		// Schedule timed notification for generating updates of
		// hub display.

		// The idiom of an anonymous class is used here, to make it easy
		// for hub code to process multiple timed notification, if needed.

		Clock.getInstance().scheduleNotification(
				new TimedNotificationObserver() {

					@Override
					public void processTimedNotification() {
						logger.fine("update occupancy data view");

						ArrayList<String> occupancyData = generateOccupancyArray();
						// "DSName","East","North","Status","#Occupied","#DPoints"

						display.showOccupancy(occupancyData);
					}

				}, Clock.parse("1 00:00"), 0, 5);
		
		// Charge all users daily at midnight
		Clock.getInstance().scheduleNotification(
				new TimedNotificationObserver() {
					
					@Override
					public void processTimedNotification() {
						logger.fine("Charging users at midnight");
						if (users == null) {
							return;
						}
						for (User u : users) {
							List<Session> todaySessions = u.getTodaySessions();
							if (todaySessions != null) {
								int totalCost = 0;
								for (Session s : todaySessions) {
									totalCost += s.getCost();
								}
								todaySessions.clear();
								bankServer.chargeUser(u.getBankCard().getAuthCode(), totalCost);
							}
						}
					}
				}, Clock.parse("1 00:00"), 24, 0);
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
		bankServer.setCollector(c);
	}

	/**
     * 
     */
	@Override
	public void addDStation(String instanceName, int eastPos, int northPos,
			int numPoints) {

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
	private ArrayList<String> generateOccupancyArray() {
		ArrayList<String> occupancyList = new ArrayList<String>();
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

			occupancyList.add(instName);
			occupancyList.add(Integer.toString(east));
			occupancyList.add(Integer.toString(north));
			occupancyList.add(status);
			occupancyList.add(Integer.toString(numOccupied));
			occupancyList.add(Integer.toString(numDPoints));

		}

		return occupancyList;
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
		} else if (activityType.equals("reportLostBikes")) {
			reportLostBikes();
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
	
	//report bikes that have been rented out for over 24 hours
	//sorted by how long they have been rented out using a Comparator
	private void reportLostBikes() {
		updateCurrentBikeTimes();
		ArrayList<Bike> bikeList = new ArrayList<Bike>(bikeIDToBikeMap.values());
		
		Collections.sort(bikeList, new Comparator<Bike>() {

			public int compare(Bike b1, Bike b2) {
				if (b1.getCurrentTimeOut() > b2.getCurrentTimeOut()) {
					return -1;
				} else if (b1.getCurrentTimeOut() < b2.getCurrentTimeOut()) {
					return 1;
				} else {
					return 0;
				}
			}
			
		});
		ArrayList<String> lostBikes = new ArrayList<String>();
		Iterator<Bike> iter = bikeList.iterator();
		//only leave those out for over 24 hours
		while (iter.hasNext()) {
			Bike b = iter.next();
			if (b.getCurrentTimeOut() <= 1440) {
				iter.remove();
			} else {
				lostBikes.add(b.getBikeId());
				lostBikes.add(Integer.toString(b.getCurrentTimeOut()));
			}
		}
		
		display.showLostBikes(lostBikes);
	}
	
	//update times across all bikes that have been hired at this time
	private void updateCurrentBikeTimes() {
		for (Bike b : bikeIDToBikeMap.values()) {
			b.updateCurrentTimeOut();
		}
	}

}
