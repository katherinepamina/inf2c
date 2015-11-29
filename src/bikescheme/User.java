package bikescheme;

import java.util.ArrayList;
import java.util.List;

public class User {
	String userId;
	String personalDetails;
	String lastName;
	BankCard bankcard;
	Key key;
	Session currentSession;
	private ArrayList<Session> allSessions;
	private ArrayList<Session> todaySessions;
	
	public User(String id, String pd, BankCard b, Key k) {
		userId = id;
		personalDetails = pd;
		bankcard = b;
		key = k;
		allSessions = new ArrayList<Session>();
		todaySessions = new ArrayList<Session>();
	}
	
	public void startNewSession(DStation s) {
		Session sesh = new Session();
		sesh.start(s);
		currentSession = sesh;
	}
	public void endCurrentSession(DStation s) {
		currentSession.end(s);
		allSessions.add(currentSession);
		todaySessions.add(currentSession);
		currentSession = null;
	}
	public ArrayList<Session> getTodaySessions() {
		return todaySessions;
	}
	public ArrayList<Session> getAllSessions() {
		return allSessions;
	}
	public Session getCurrentSession() {
		return currentSession;
	}
}
