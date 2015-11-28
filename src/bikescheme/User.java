package bikescheme;

import java.util.ArrayList;
import java.util.List;

public class User {
	int userId;
	String firstName;
	String lastName;
	BankCard bankcard;
	Key key;
	Session currentSession;
	private List<Session> allSessions;
	private List<Session> todaySessions;
	
	public User(int id, String fn, String ln, BankCard b, Key k) {
		userId = id;
		firstName = fn;
		lastName = ln;
		bankcard = b;
		key = k;
		allSessions = new ArrayList<Session>();
		todaySessions = new ArrayList<Session>();
	}
	
	public void startNewSession() {
		Session s = new Session();
		allSessions.add(s);
		todaySessions.add(s);
		currentSession = s;
	}
	public void endCurrentSession() {
		currentSession.end();
		currentSession = null;
	}
}
