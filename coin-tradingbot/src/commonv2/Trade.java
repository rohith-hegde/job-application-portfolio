package commonv2;

import java.util.Date;

public class Trade implements Comparable<Trade>
{
	private long tradeID;
	private MarketID market;
	private long timeMs;
	private String initiatingOrderType;
	private double rate, tradeTotal, masterTotal;
	
	public Trade(long tradeID, MarketID market, long timeMs, String initiatingOrderType, double rate, double tradeTotal, double masterTotal)
	{
		this.tradeID = tradeID;
		this.market = market;
		this.timeMs = timeMs;
		this.initiatingOrderType = initiatingOrderType;
		this.rate = rate;
		this.tradeTotal = tradeTotal;
		this.masterTotal = masterTotal;
	}
	
	public long getTradeID()
	{
		return tradeID;
	}
	
	public MarketID getMarket() {
		return market;
	}
	public long getTimeMs() {
		return timeMs;
	}
	public void setTimeMs(long v)
	{
		timeMs = v;
	}
	public int getTimeS() {
		return (int)(timeMs / 1000);
	}
	public String getInitiatingOrderType() {
		return initiatingOrderType;
	}
	public double getRate() {
		return rate;
	}
	public double getTradeTotal() {
		return tradeTotal;
	}
	public double getMasterTotal() {
		return masterTotal;
	}
	
	public String toString()
	{
		return "{"
				+ "\"tradeID\": " + tradeID + ","
				+ "\"market\": \"" + market + "\","
				+ "\"initiatingOrderType\": \"" + initiatingOrderType + "\","
				+ "\"timeMs\": " + timeMs + ","
				+ "\"timeDate\": " + new Date(timeMs) + ","
				+ "\"rate\": " + rate + ","
				+ "\"tradeTotal\": " + tradeTotal + ","
				+ "\"masterTotal\": " + masterTotal + ""
				+ "}";
	}

	@Override
	public int compareTo(Trade arg0) 
	{
		return (int)(getTimeMs() - arg0.getTimeMs());
	}

}
