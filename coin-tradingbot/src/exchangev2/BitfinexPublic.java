package exchangev2;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import commonv2.Candle;
import commonv2.MarketID;
import commonv2.Trade;
import configv2.Config;
import recorderv2.DBconnector;
import recorderv2.DBcredentials;

/**
 * uses DB trades to generate candles and act like a regular chart feeder
 *
 */
public class BitfinexPublic extends ExchangePublic
{
	private DBconnector dbc;
	private int queryLimit;
	public static long timeOffsetMs = 4 * 60 * 60 * 1000; // = -4 * ; //default value= 4 hours. used to make sure the stored times are UTC
	private boolean returnOnlyClosedCandles;
	
	public BitfinexPublic(DBcredentials dbcr) throws Exception
	{
		try { dbc = new DBconnector(dbcr); } catch (Exception e) {
			throw new Exception("Error while connecting to database", e);
		}
		
		Config.config.addDependentKey("localTimeDiffFromUTCmin");
		Config.config.addDependentKey("bp_returnOnlyClosedCandles");
		timeOffsetMs = (int)Config.config.get("localTimeDiffFromUTCmin") * 60 * 1000;
		returnOnlyClosedCandles = (boolean)Config.config.get("bp_returnOnlyClosedCandles");
		queryLimit = 50000;
	}
	
	public BitfinexPublic(DBcredentials dbcr, int queryLimit) throws Exception
	{
		this(dbcr);
		this.queryLimit = queryLimit;
	}

	public List<Trade> getTradeHistory(long startMs, long endMs, MarketID market) throws Exception 
	{
		List<Trade> trades = dbc.getTradeHistory(market, startMs, endMs, queryLimit, true);
		for (Trade t : trades)
			t.setTimeMs(t.getTimeMs() - timeOffsetMs);
		return trades;
	}

	public List<Candle> getCandlestickHistory(long startMs, long endMs, long periodMs, MarketID market) throws Exception 
	{
		List<Trade> trades = null;
		try { trades = getTradeHistory(startMs, endMs, market); } catch (Exception e) {
			throw new Exception("Error while getting time-adjusted trade list", e);
		}
		
		//int candleNum = (int)((endMs - startMs) / periodMs);
		//if (!returnOnlyClosedCandles)
		//	candleNum += 1;
		
		List<Candle> candles = new ArrayList<Candle> ();
		long firstCandleStartMs = ((startMs / periodMs) * periodMs) + periodMs;
		long lastCandleStartMs = ((endMs / periodMs) * periodMs);
		
		int candleNum = (int)((lastCandleStartMs - firstCandleStartMs) / periodMs);
		if (!returnOnlyClosedCandles)
			candleNum += 1;
		List<Trade> c_trades = new ArrayList<Trade> ();
		
		for (int i = 0; i < candleNum; i++)
		{
			long c_startS = (firstCandleStartMs + (i * periodMs)) / 1000;
			long nextC_startS = (firstCandleStartMs + ((i + 1) * periodMs)) / 1000;
			long c_periodS = periodMs / 1000;
			
			while (trades.get(0).getTimeS() >= c_startS && trades.get(0).getTimeS() < nextC_startS)
				c_trades.add(trades.remove(0));
			
			candles.add(createCandle(market, c_startS, c_periodS, c_trades));
			c_trades.clear();
		}
		
		return candles;
	}
	
	private static Candle createCandle(MarketID mk, long startTimeS, long periodS, List<Trade> trades)
	{
		double openRate = trades.get(0).getRate();
		double closeRate = trades.get(trades.size() - 1).getRate();
		
		double highRate = 0; //Double.MIN_VALUE;
		double lowRate = 0; //Double.MAX_VALUE;
		double volumeTradeCur = 0;
		double volumeMasterCur = 0;
		double weightedRateMasterTotal = 0;
		
		for (Trade t : trades)
		{
			volumeMasterCur += t.getMasterTotal();
			volumeTradeCur += t.getTradeTotal();
			weightedRateMasterTotal += t.getRate() * t.getMasterTotal();
			
			if (t.getRate() > highRate)
				highRate = t.getRate();
			if (t.getRate() < lowRate)
				lowRate = t.getRate();
		}
		
		double weightedRate = weightedRateMasterTotal / volumeMasterCur;
		return new Candle(mk, startTimeS, periodS, highRate, lowRate, openRate, closeRate, volumeMasterCur, volumeTradeCur, weightedRate);
	}
	
	public void disconnect()
	{
		try {
			dbc.disconnect();
		} catch (IllegalStateException e) {
		} catch (SQLException e) {
		}
	}
}
