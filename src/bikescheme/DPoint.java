/**
 * 
 */
package bikescheme;

import java.util.Date;
import java.util.logging.Logger;

/**
 * 
 * Docking Point for a Docking Station.
 * 
 * @author pbj
 * 
 */
public class DPoint implements KeyInsertionObserver, BikeDockingObserver, FaultButtonObserver {
	public static final Logger logger = Logger.getLogger("bikescheme");

	private KeyReader keyReader;
	private OKLight okLight;
	private FaultLight faultLight;
	private String instanceName;
	private int index;
	private boolean free;
	private Bike bike;
	private BikeLock lock;
	private BikeSensor sensor;
	private DStation station;
	private FaultButton faultButton;
	private Date mostRecentDock;

	/**
	 * 
	 * Construct a Docking Point object with a key reader and green ok light
	 * interface devices.
	 * 
	 * @param instanceName
	 *            a globally unique name
	 * @param index
	 *            of reference to this docking point in owning DStation's list
	 *            of its docking points.
	 */
	public DPoint(String instanceName, int index, DStation station) {

		// Construct and make connections with interface devices

		keyReader = new KeyReader(instanceName + ".kr");
		keyReader.setObserver(this);
		okLight = new OKLight(instanceName + ".ok");
		faultLight = new FaultLight(instanceName + ".fl");
		lock = new BikeLock(instanceName + ".bl");
		sensor = new BikeSensor(instanceName + ".bs");
		sensor.setObserver(this);
		faultButton = new FaultButton(instanceName + ".fb");
		faultButton.setObserver(this);
		this.instanceName = instanceName;
		this.index = index;
		this.free = true;
		this.station = station;
	}

	public void setDistributor(EventDistributor d) {
		keyReader.addDistributorLinks(d);
		sensor.addDistributorLinks(d);
		faultButton.addDistributorLinks(d);
	}

	public void setCollector(EventCollector c) {
		okLight.setCollector(c);
		lock.setCollector(c);
		faultLight.setCollector(c);
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

	public void addBike(Bike b) {
		if (isFree()) {
			bike = b;
			free = false;
		}
	}

	public Bike getBike() {
		return bike;
	}

	public void bikeDocked(String bikeID) {
		if (!isFree()) {
			return;
		}
		lock.lock();
		okLight.flash();
		mostRecentDock = Clock.getInstance().getDateAndTime();

		Bike rBike = getBikeByBikeID(bikeID);

		if (rBike == null) { //bike is being added, create new bike
			Bike newBike = createNewBike(bikeID);
			bike = newBike;
			setCurrentLocationBike();
			free = false;
			return;
		}
		
		User returningUser = rBike.getCurrentUser();

		if (returningUser != null) { //user is returning bike
			bike = rBike;
			bike.clearStartTime();
			bike.resetCurrentTimeOut();
			setCurrentLocationBike();
			free = false;
			returnBike(returningUser);
		}
	}
	
	private void returnBike(User rUser) {
		rUser.endCurrentSession(station);
	}

	public void keyInserted(String keyId) {
		logger.fine(getInstanceName());
		if (bike != null && !isFree()) {
			//if it is a registered user, let them rent it. Otherwise it is a staff member
			if (isUser(keyId)) {
				hireBike(keyId);
				
			} else {
				removeBike();
			}
		}
	}
	
	//function to handle bike removal by staff member
	private void removeBike() {
		free = true;
		removeBikeFromMap(bike.getBikeId());
		bike = null;
		okLight.flash();
		lock.unlock();
	}

	private void hireBike(String keyID) {
		User rentingUser = getUserByKeyID(keyID);
		if (rentingUser!=null && bike != null && !isFree()) {
			bike.setCurrentUser(rentingUser);
			bike.setStartTime();
			rentingUser.startNewSession(station);
			lock.unlock();
			okLight.flash();
			free = true;
			bike = null;
		}
	}

	private User getUserByKeyID(String keyID) {
		return station.getUserByKeyID(keyID);
	}

	private boolean isUser(String keyID) {
		return station.isUser(keyID);
	}
	
	private Bike getBikeByBikeID(String bikeID) {
		return station.getBikeByBikeID(bikeID);
	}
	
	private Bike createNewBike(String bikeID) {
		return station.createNewBike(bikeID);
	}
	
	public void removeBikeFromMap(String bikeID) {
		station.removeBikeFromMap(bikeID);
	}
	
	//sets current location of docked bike
	private void setCurrentLocationBike() {
		bike.setCurrentDStation(station.getInstanceName());
		
		//we're saying the DPointName is the instance name not the index
		//which starts at 0
		bike.setCurrentDPointName(Integer.toString(this.index + 1));
	}
	
	public void faultButtonPressed() {
		// only report fault if there is a bike docked at the point
		if (bike == null) {
			return;
		}
		// check that the time hasn't been more than 2 min
		Date now = Clock.getInstance().getDateAndTime();
		int diffInMinutes = Math.abs(Clock.minutesBetween(mostRecentDock, now));
		if (diffInMinutes > 2) {
			return;
		}
		bike.markFaulty(true);
		faultLight.flash();
	}

}
