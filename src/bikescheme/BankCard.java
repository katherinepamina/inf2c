package bikescheme;
/*
*
* BankCard class for each user
*
*/

public class BankCard {
	private int cardNumber;
	private String authorizationCode;
	public BankCard(int num, String code) {
		this.cardNumber = num;
		this.authorizationCode = code;
	}
	public String getAuthCode() {
		return authorizationCode;
	}
}
