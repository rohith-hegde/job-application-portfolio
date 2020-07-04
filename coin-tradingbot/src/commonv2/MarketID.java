package commonv2;

import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;

public class MarketID 
{
	private ExchangeID exchangeID;
	private MarketType marketType;
	private String masterCurrency, tradeCurrency;
	private int hash;
	
	public enum MarketType {
		REGULAR, MARGIN
	}
	
	public MarketType getMarketType() {
		return marketType;
	}
	public ExchangeID getExchangeID() {
		return exchangeID;
	}
	public void setExchangeID(ExchangeID exchangeID) {
		this.exchangeID = exchangeID;
	}
	
	public String getMasterCurrency() {
		return masterCurrency;
	}
	public void setMasterCurrency(String masterCurrency) {
		this.masterCurrency = masterCurrency;
	}
	public String getTradeCurrency() {
		return tradeCurrency;
	}
	public void setTradeCurrency(String tradeCurrency) {
		this.tradeCurrency = tradeCurrency;
	}
	public MarketID(ExchangeID exchangeID, String masterCurrency, String tradeCurrency, MarketType marketType)
	{
		this.exchangeID = exchangeID;
		this.masterCurrency = masterCurrency;
		this.tradeCurrency = tradeCurrency;
		this.marketType = marketType;
	}
	
	public String quickDescription()
	{
		String exchangeName = exchangeID.toString().toLowerCase();
		return toFirstLetterUppercase(exchangeName) + " " + masterCurrency + "/" + tradeCurrency + " " + marketType.toString().toLowerCase();
	}
	
	private static String toFirstLetterUppercase(String s)
	{
		String f = s.substring(0, 1);
		return f + s.substring(1);
	}
	
	public String toString()
	{
		return "{"
				+ "\"exchangeID\": \"" + exchangeID + "\","
				+ "\"masterCurrency\": \"" + masterCurrency + "\","
				+ "\"tradeCurrency\": \"" + tradeCurrency + "\""
				+ "}";
	}
	
	public int hashCode()
	{
		MessageDigest md = DigestUtils.getMd5Digest();
		md.update(toString().getBytes());
		byte[] res = md.digest();
		BigInteger n = new BigInteger(res);
		hash = n.intValue();
		return n.intValue();
	}
}
