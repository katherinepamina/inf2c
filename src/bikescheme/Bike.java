package bikescheme;

public class Bike {
	private int bikeId;
	private boolean faulty;
	public Bike(int bid) {
		bikeId = bid;
		faulty = false;
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
}
