package recorderv2;


import java.util.List;

import commonv2.Candle;
import commonv2.MarketID;
import commonv2.Utils;
import configv2.Config;
import configv2.Logs;
import exchangev2.ExchangePublic;
import net.hammereditor.designutilities.design.RunnableThrowableCallback;

public class PollingCandleChartFeeder extends ChartFeeder<Candle> 
{
	private ExchangePublic ex;
	
	public PollingCandleChartFeeder(RunnableThrowableCallback onError, int candlePeriodS, ExchangePublic exchange) throws Exception
	{
		super(onError, candlePeriodS);
		ex = exchange;
		//ex = new ExchangePublic();
		//Logs.log.debug("PollingChartFeeder.CONSTRUCT(): created Poloniex object.");
	}

	public void runThread() throws Exception
	{
		if ((boolean)Config.config.get("pccf_waitAfterInitialFeed"))
		{
			Logs.log.debug("PollingChartFeeder.runThread(): doing wait after initial feed...");
			long timeUntilNextCandleClosedMs = Utils.getMostRecentDataIntervalTimestampMultipleMs(super.getDataIntervalS()) - System.currentTimeMillis();
			try { Thread.sleep(timeUntilNextCandleClosedMs); } catch (Exception e) { }
		}
		else
			Logs.log.warning("PollingChartFeeder.runThread(): NOT DOING WAIT AFTER INITIAL FEED...");
		
		long cycleTimeMissedMs = 0;
		
		while (!super.isStopped())
		{
			long startTime = System.currentTimeMillis();
			Logs.log.debug("PollingChartFeeder.runThread(): doing poll loop...");
			//get data
			List<MarketID> mkl = super.getConsumingMarketIDs();
			
			for (MarketID mk : mkl)
			{
				try
				{
					Logs.log.debug("PollingChartFeeder.runThread(): getting candlestick data for market \'" + mk + "\'...");
					if (cycleTimeMissedMs > 0)
						Logs.log.debug("PollingChartFeeder.runThread(): missed " + cycleTimeMissedMs + " ms. of pervious candlestick data.");
					
					long startMs = startTime - getDataIntervalS() * 1000 - cycleTimeMissedMs;
					long candlePeriodMs = getDataIntervalS() * 1000;
					List<Candle> data = ex.getCandlestickHistory(startMs, startTime, candlePeriodMs, mk);
					//List data = getExchangeData(startMs, startTime, mk);
					super.feedChartData(mk, data);
					cycleTimeMissedMs = 0; //executed if no error
				}
				catch (Exception e) {
					Logs.log.error("PollingChartFeeder.runThread(): error while getting exchange data for marketID \'" + mk + "\': " + e.getMessage());
					Logs.log.printException(e);
					cycleTimeMissedMs += getDataIntervalS() * 1000;
				}
			}
			
			long timeSpentMs = System.currentTimeMillis() - startTime;
			Logs.log.debug("PollingChartFeeder.runThread(): done with poll loop. time used: " + timeSpentMs + " ms.");
			
			//try { Thread.sleep(getDataIntervalS() * 1000 - timeSpentMs); } catch (Exception e) { }
			long timeUntilNextCandleClosedMs = Utils.getMostRecentDataIntervalTimestampMultipleMs(super.getDataIntervalS()) - System.currentTimeMillis();
			//System.out.println(timeUntilNextCandleClosedMs);
			try { Thread.sleep(timeUntilNextCandleClosedMs); } catch (Exception e) { }
		}
	}
	
	public void initialFeed(int candleNum) throws Exception
	{
		List<MarketID> mkl = super.getConsumingMarketIDs();
		long startTime = System.currentTimeMillis();
		Logs.log.debug("PollingChartFeeder.initialFeed(): getting data...");
		
		for (MarketID mk : mkl)
		{
			try
			{
				Logs.log.debug("PollingChartFeeder.initialFeed(): getting candlestick data for market \'" + mk + "\'...");
				List<Candle> data = ex.getCandlestickHistory(startTime - (getDataIntervalS() * candleNum * 1000), startTime, getDataIntervalS() * 1000, mk);
				super.feedChartData(mk, data);
			}
			catch (Exception e) {
				Logs.log.error("PollingChartFeeder.initialFeed(): error while getting exchange data for marketID \'" + mk + "\': " + e.getMessage());
				throw new Exception("Error while getting poloniex data", e);
			}
		}
	}
}
