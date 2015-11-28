package bikescheme;

import java.util.ArrayList;
import java.util.List;

public class User {
	BankCard bankcard;
	Key key;
	private List<Session> sessions;
	
	public User(BankCard b, Key k) {
		bankcard = b;
		key = k;
		sessions = new ArrayList<Session>();
	}
}
