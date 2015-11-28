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
	private List<Session> allSessions;
	private List<Session> todaySessions;
	
	public User(String id, String pd, BankCard b, Key k) {
		userId = id;
		personalDetails = pd;
		bankcard = b;
		key = k;
		allSessions = new ArrayList<Session>();
		todaySessions = new ArrayList<Session>();
	}
	
	public void startNewSession() {
		Session s = new Session();
		currentSession = s;
	}
	public void endCurrentSession() {
		currentSession.end();
		allSessions.add(currentSession);
		todaySessions.add(currentSession);
		currentSession = null;
	}
}
