package recorderv2;

import java.util.ArrayList;
import java.util.List;
import commonv2.*;
import exchangev2.*;
import commonv2.Utils;
import configv2.Config;
import configv2.Logs;
import net.hammereditor.designutilities.design.RunnableThrowableCallback;

/**
 * poll the API for trades. keep track of the trade ID's, and repeatedly request more trade data until all new trades for the last X minutes are covered
 */
public class BitfinexPollingTradeFeeder extends ChartFeeder<Trade>
{
	/**
	 * not an actual trade. tradeID and time are used to determine how many trades to feed, both initially and for every cycle thereafter
	 */
	private Trade lastTrade;
	/**
	 * how many trades to request each cycle, after the initial feed is done
	 */
	private BitfinexPublicDirect bpd;
	private DBconnector dbc;
	
	/**
	 * 
	 * @param onError
	 * @param dataIntervalS
	 * @param manualLastTrade if you want to manually set the last trade ID to start from. make sure to set the tradeID and timeMs of this
	 * @throws Exception
	 */
	public BitfinexPollingTradeFeeder(DBcredentials dbcr, RunnableThrowableCallback onError, int dataIntervalS, Trade manualLastTrade) throws Exception
	{
		super(onError, dataIntervalS);
		bpd = new BitfinexPublicDirect();
		lastTrade = manualLastTrade;
		dbc = new DBconnector(dbcr);
	}
	
	public BitfinexPollingTradeFeeder(DBcredentials dbcr, RunnableThrowableCallback onError, int dataIntervalS) throws Exception
	{
		this(dbcr, onError, dataIntervalS, null);
	}

	public void runThread() throws Exception 
	{
		if ((boolean)Config.config.get("bptf_waitAfterInitialFeed"))
		{
			Logs.log.debug(this.getClass().getName() + ".runThread(): doing wait after initial feed...");
			long timeUntilNextCycleMs = Utils.getMostRecentDataIntervalTimestampMultipleMs(super.getDataIntervalS()) - System.currentTimeMillis();
			try { Thread.sleep(timeUntilNextCycleMs); } catch (Exception e) { }
		}
		else
			Logs.log.warning(this.getClass().getName() + ".runThread(): NOT DOING WAIT AFTER INITIAL FEED...");
		
		long cycleTimeMissedMs = 0;
		
		while (!super.isStopped())
		{
			long startTime = System.currentTimeMillis();
			//Logs.log.debug(this.getClass().getName() + ".runThread(): doing poll loop...");
			
			/*//get data
			List<MarketID> mkl = super.getConsumingMarketIDs();
			
			for (MarketID mk : mkl)
			{
				try
				{
					Logs.log.debug(this.getClass().getName() + ".runThread(): getting trade data for market \'" + mk + "\'...");
					if (cycleTimeMissedMs > 0)
						Logs.log.debug(this.getClass().getName() + ".runThread(): missed " + cycleTimeMissedMs + " ms. of pervious trade data.");
					long startMs = startTime - getDataIntervalS() * 1000 - cycleTimeMissedMs;
					
					//get the most recent trade from the API, and set int missedTrades = (mostRecentTradeID - lastTraderadeID) / 2
					Trade mostRecentT = lastTrade;
					
					for (MarketID mk : super.getConsumingMarketIDs())
					{
						List<Trade> tempT = bpd.getDescendingTradeHistory(1, 1, mk);
						
						if (tempT.size() > 0)
						{
							Trade tt = tempT.get(0);
							if (tt.getTradeID() > mostRecentT.getTradeID())
								mostRecentT = tt;
						}
					}
					
					long maxMissedTrades = mostRecentT.getTradeID() - lastTrade.getTradeID();
					//long candlePeriodMs = getDataIntervalS() * 1000;'
					List<Trade> data = new ArrayList<Trade> ();
					boolean found = false;
					
					while (!found)
					{
						bpd.getDescendingTradeHistory(startMs, tradeRequestSize, mk);
					}
					
					// = getPastTradesAfterID(lastTrade.getTradeID(), querySize, market)
					//List data = getExchangeData(startMs, startTime, mk);
					super.feedChartData(mk, data);
					cycleTimeMissedMs = 0; //executed if no error
				}
				catch (Exception e) {
					Logs.log.error(this.getClass().getName() + ".runThread(): error while getting exchange data for marketID \'" + mk + "\': " + e.getMessage());
					Logs.log.printException(e);
					cycleTimeMissedMs += getDataIntervalS() * 1000;
				}
			}*/
			
			try { feed(); } catch (Exception e) { throw new Exception("Error while feeding data to DB", e); }
			long timeSpentMs = System.currentTimeMillis() - startTime;
			Logs.log.debug(this.getClass().getName() + ".runThread(): done with poll loop. time used: " + timeSpentMs + " ms.");
			
			//try { Thread.sleep(getDataIntervalS() * 1000 - timeSpentMs); } catch (Exception e) { }
			long timeUntilNextCandleClosedMs = Utils.getMostRecentDataIntervalTimestampMultipleMs(super.getDataIntervalS()) - System.currentTimeMillis();
			//System.out.println(timeUntilNextCandleClosedMs);
			try { Thread.sleep(timeUntilNextCandleClosedMs); } catch (Exception e) { }
		}
	}

	/**
	 * ignores the exchanges set in the market ID's of any consumers, and just uses bitfinex. however, pays attention to different coin markets on Bitfinex
	 * doesn't use 'dataNum' variable
	 */
	public void initialFeed(int dataNum) throws Exception 
	{
		//find the last recorded trade's ID, using the DB table. or skip this if lastTrade was already provided to the constructor
		//Trade lastTrade = dbc.getTradeHistory(null, 1, System.currentTimeMillis(), 1, true).get(0);
		
		try
		{
			if (lastTrade == null) //if not set manually using the constructor
			{
				List<Trade> lastTradeL = dbc.getTradeHistory(null, 1, System.currentTimeMillis(), 1, true);
				if (lastTradeL.size() > 0)
					lastTrade = lastTradeL.get(0);
				else
					lastTrade = new Trade(1, null, System.currentTimeMillis(), "null", 0.0, 0.0, 0.0);
			}
			
			Logs.log.debug(this.getClass().getName() + ".initialFeed(): set lastTrade to: " + lastTrade + ". calling feed()...");
		}
		catch (Exception e) {
			throw new Exception("Error while finding last trade in DB", e);
		}
		
		try { feed(); } catch (Exception e) { throw new Exception("Error while feeding data to DB", e); }
	}
	
	/**
	 * ignores the exchanges set in the market ID's of any consumers, and just uses bitfinex. however, pays attention to different coin markets on Bitfinex
	 */
	private void feed() throws Exception 
	{
		//get the most recent trade from the API, and set int missedTrades = (mostRecentTradeID - lastTraderadeID) / 2
		if (lastTrade == null)
			throw new Exception("Last recorded trade not set via initial feed or constructor");
		Trade mostRecentT = lastTrade;
		long startMs = System.currentTimeMillis();
		
		for (MarketID mk : super.getConsumingMarketIDs())
		{
			List<Trade> tempT = bpd.getDescendingTradeHistory(1, 1, mk);
			
			if (tempT.size() > 0)
			{
				Trade tt = tempT.get(0);
				if (tt.getTradeID() > mostRecentT.getTradeID())
					mostRecentT = tt;
			}
		}
		
		//Logs.log.debug(this.getClass().getName() + ".feed(): most recent trade from API among the consumers\'s marketID\'s: " + mostRecentT);
		
		//if (mostRecentT == null)
			//mostRecentT = new Trade(1, null, System.currentTimeMillis(), "null", 0.0, 0.0, 0.0);
		long maxMissedTrades = mostRecentT.getTradeID() - lastTrade.getTradeID();
		//Logs.log.debug(this.getClass().getName() + ".feed(): maximum missed trades: " + maxMissedTrades);
		final int bitfinexTradeQueryLimit = 49999;
		if (maxMissedTrades > bitfinexTradeQueryLimit)
			maxMissedTrades = bitfinexTradeQueryLimit;
		
		//query all missing trades, starting from the newest one, and ending when the trade with lastTrade's timestamp or ID is found. Max. query size is 49999
			//store the missed trades to DB
			//repeat this step for each market that consumers are using
		int tradeCt = 0;
		
		for (MarketID mk : super.getConsumingMarketIDs())
		{
			List<Trade> res;
			if (lastTrade.getTradeID() > 1)
				res = getPastTradesAfterID(lastTrade.getTradeID(), (int)maxMissedTrades, mk);
			else
				res = getPastTradesAfterID(lastTrade.getTradeID(), lastTrade.getTimeMs(), (int)maxMissedTrades, mk);
				
			//dbc.storeTradeHistory(res);
			super.feedChartData(mk, res);
			tradeCt += res.size();
			Logs.log.debug(this.getClass().getName() + ".feed(): got and fed " + res.size() + " trades to consumers of market \'" + mk.toString() + "\'. oldest trade time: " + (res.size() > 0 ? Logs.log.unixTimestampMsToDateStr(res.get(0).getTimeMs()) : "no trades in list") + ".");
		}
		
		Logs.log.debug(this.getClass().getName() + ".feed(): trade feed complete with " + tradeCt + " trades in " + (System.currentTimeMillis() - startMs) + " ms.");
		lastTrade = mostRecentT;
	}
	
	private List<Trade> getPastTradesAfterID(long tradeID, int querySize, MarketID market) throws Exception
	{
		return getPastTradesAfterID(tradeID, 0, querySize, market);
	}
	
	private List<Trade> getPastTradesAfterID(long tradeID, long timeMs, int querySize, MarketID market) throws Exception
	{
		boolean found = false;
		List<Trade> trades = new ArrayList<Trade> (querySize);
		
		//for (int q = 1; q <= maxQueries && !found; q++)
		//{
			List<Trade> tempTrades = bpd.getDescendingTradeHistory(1, querySize, market);
			//Logs.log.debug(this.getClass().getName() + ".getPastTradesAfterID(): examining " + tempTrades.size() + " trades...");
			
			//for (int t = tempTrades.size() - 1; t >= 0 && !found; t--)
			for (int t = 0; t < tempTrades.size() && !found; t++)
			{
				Trade tr = tempTrades.get(t);
				
				if (tr.getTradeID() <= tradeID || tr.getTimeMs() < timeMs)
				{
					found = true;
					Logs.log.debug(this.getClass().getName() + ".getPastTradesAfterID(): found last trade: " + tr.toString() + ". Exiting loop and not adding this trade...");
				}
				else
					trades.add(0, tr);
			}
		//}
			
		if (!found)
			Logs.log.warning(this.getClass().getName() + ".getPastTradesAfterID(): did not find all missed trades.");
		return trades;
	}
}
