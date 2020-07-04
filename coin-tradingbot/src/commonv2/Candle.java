package commonv2;

import java.util.ArrayList;
import java.util.List;

import configv2.Logs;


/**
 * Represents candlestick data as a group of 5 trades
 * @author Hammereditor
 */
public class Candle
{
	private Trade open, weighted, close, high, low;
	private long startTimeS, endTimeS;

	public Candle(MarketID market, long startTimeS, long periodS, double highRate, double lowRate, double openRate, double closeRate, double volumeMasterC, double volumeTradeC, double weightedAvgRate)
	{
		this.startTimeS = startTimeS;
		this.endTimeS = startTimeS + periodS - 1;
		final double nullMasterCvolume = 0.00000001;
		
		//open trade
		long open_timeS = startTimeS + 1;
		long open_tradeID = 1;
		double open_rate = openRate;
		double open_masterCtotal = nullMasterCvolume, open_tradeCtotal = open_masterCtotal / open_rate;
		open = new Trade(open_tradeID, market, open_timeS, "candle open", open_rate, open_tradeCtotal, open_masterCtotal);
		
		//low trade
		long low_timeS = startTimeS + 2;
		long low_tradeID = 2;
		double low_rate = lowRate;
		double low_masterCtotal = nullMasterCvolume, low_tradeCtotal = low_masterCtotal / low_rate;
		low = new Trade(low_tradeID, market, low_timeS, "candle low", low_rate, low_tradeCtotal, low_masterCtotal);
		
		//'weighted' trade which represents the weighted average of the candle
		long weighted_timeS = startTimeS + (periodS / 2);
		long weighted_tradeID = 3;
		double weighted_rate = weightedAvgRate;
		double weighted_masterCtotal = volumeMasterC; //- (nullMasterCvolume * 4);
		double weighted_tradeCtotal = volumeTradeC;
		weighted = new Trade(weighted_tradeID, market, weighted_timeS, "candle weighted", weighted_rate, weighted_tradeCtotal, weighted_masterCtotal);
		
		//high trade
		long high_timeS = startTimeS + periodS - 2;
		long high_tradeID = 4;
		double high_rate = highRate;
		double high_masterCtotal = nullMasterCvolume, high_tradeCtotal = high_masterCtotal / high_rate;
		high = new Trade(high_tradeID, market, high_timeS, "candle high", high_rate, high_tradeCtotal, high_masterCtotal);
		
		//close trade
		long close_timeS = startTimeS + periodS - 1;
		long close_tradeID = 5;
		double close_rate = closeRate;
		double close_masterCtotal = nullMasterCvolume, close_tradeCtotal = close_masterCtotal / close_rate;
		close = new Trade(close_tradeID, market, close_timeS, "candle close", close_rate, close_tradeCtotal, close_masterCtotal);
	}
	
	public List<Trade> asTradeList()
	{
		List<Trade> l = new ArrayList<Trade> ();
		l.add(open);
		l.add(low);
		l.add(weighted);
		l.add(high);
		l.add(close);
		
		return l;
	}
	
	public List<Trade> weightedAsTradeList()
	{
		List<Trade> l = new ArrayList<Trade> ();
		l.add(weighted);
		return l;
	}
	
	public Trade getOpen() {
		return open;
	}

	public Trade getWeighted() {
		return weighted;
	}

	public Trade getClose() {
		return close;
	}

	public Trade getHigh() {
		return high;
	}

	public Trade getLow() {
		return low;
	}
	
	/**
	 * 
	 * @return candle's volume in master currency
	 */
	public double getVolume()
	{
		return weighted.getMasterTotal();
	}
	
	public long getStartTimeS() {
		return startTimeS;
	}

	public long getEndTimeS() {
		return endTimeS;
	}
	
	public String toString()
	{
		return "{closeTime: " + Logs.log.unixTimestampStoDateStr(getEndTimeS()) + " closePrice: " + close.getRate() + "}";
	}
}
