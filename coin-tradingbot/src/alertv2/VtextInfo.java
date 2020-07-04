package alertv2;

public class VtextInfo extends EmailInfo 
{
	private String textHeader;

	public String getTextHeader() {
		return textHeader;
	}

	/**
	 * 
	 * @param receiverMobileNumber the 10-digit telephone number of the receiver, without dashes, parenthesis or spaces. Verizon Wireless numbers only
	 * @param textHeader include the space after "-btc-"
	 */
	public VtextInfo(String receiverMobileNumber, String textHeader) 
	{
		super("Tradingbot", "alert@tradingbot.hammereditor.net", "Administrator", receiverMobileNumber + "@vtext.com", "");
		this.textHeader = textHeader;
	}
}
