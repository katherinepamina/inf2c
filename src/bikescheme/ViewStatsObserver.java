package bikescheme;

/**
 * Interface for any class with objects that need to receive 
 * notifications  concerning viewStats messages input to 
 * Hub IO devices.
 * 
 * @author pbj
 *
 */

public interface ViewStatsObserver {
	void viewStatsReceived(String activityType);
}
