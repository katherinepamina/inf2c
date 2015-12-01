package bikescheme;

import java.util.Map;

public interface HubInterface {

	void addUser(User u, String id);

	User getUserByKeyID(String keyID);

	boolean isUser(String keyID);

	Bike getBikeByBikeID(String bikeID);

	Bike createNewBike(String bikeID);

	void removeBikeFromMap(String bikeID);

	Map<String, DStationInterface> getDStationMap();

	
}
