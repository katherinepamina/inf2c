package bikescheme;

public class Bike {
	private int bikeId;
	private boolean faulty;
	private User currentUser;
	
	public Bike(int bid) {
		bikeId = bid;
		faulty = false;
		currentUser = null;
	}
	
	public int getBikeId() {
		return bikeId;
	}
	
	public boolean isFaulty() {
		return faulty;
	}
	
	public void markFaulty(boolean f) {
		faulty = f;
	}
	
	public void setCurrentUser(User u) {
		currentUser = u;
	}
	
	public User getCurrentUser() {
		return currentUser;
	}
}
