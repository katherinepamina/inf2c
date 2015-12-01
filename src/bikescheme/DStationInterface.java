package bikescheme;

public interface DStationInterface {
	
	public void setDistributor(EventDistributor d);
	
	public void setCollector(EventCollector c);
	
	public int getEastPos();
	
	public int getNorthPos();
	
	public int getNumFreePoints();
	
	public int getNumPoints();

	public String getInstanceName();
	
}
