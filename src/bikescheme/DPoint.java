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
		lock = new BikeLock(instanceName + ".bl");
		sensor = new BikeSensor(instanceName + ".bs");
		sensor.setObserver(this);
		this.instanceName = instanceName;
		this.index = index;
		this.free = true;
		this.station = station;
	}

	public void setDistributor(EventDistributor d) {
		keyReader.addDistributorLinks(d);
		sensor.addDistributorLinks(d);
	}

	public void setCollector(EventCollector c) {
		okLight.setCollector(c);
		lock.setCollector(c);
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

	public void bikeDocked(String bikeID) {
		okLight.flash();
		lock.lock();

		Bike rBike = getBikeByBikeID(bikeID);

		if (rBike == null) { //bike is being added, create new bike
			Bike newBike = createNewBike(bikeID);
			bike = newBike;
			free = false;
			return;
		}
		
		User returningUser = rBike.getCurrentUser();

		if (returningUser != null) { //user is returning bike
			bike = rBike;
			free = false;
			returnBike(returningUser);

		}
	}
	
	private void returnBike(User rUser) {
		rUser.endCurrentSession(station);
	}

	public void keyInserted(String keyId) {
		logger.fine(getInstanceName());
		
		//if it is a registered user, let them rent it. Otherwise it is a staff member
		if (isUser(keyId)) {
			hireBike(keyId);
		} else {
			removeBike();
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
		if (rentingUser!=null && bike != null) {
			bike.setCurrentUser(rentingUser);
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

}
