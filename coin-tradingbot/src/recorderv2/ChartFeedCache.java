package recorderv2;

import java.util.ArrayList;
import java.util.List;

import commonv2.MarketID;
import net.hammereditor.designutilities.design.*;

public class ChartFeedCache<T> extends RunnableThrowable implements ChartFeedable<T>, Stoppable
{
	private long flushIntervalMs;
	private int cacheSize;
	private List<T> cachedData;
	private MarketID cachedMk;
	private ChartFeedable<T> cf;
	private boolean stop;

	public int getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public long getFlushIntervalMs() {
		return flushIntervalMs;
	}

	public void setFlushIntervalMs(long flushIntervalMs) {
		this.flushIntervalMs = flushIntervalMs;
	}
	
	public ChartFeedCache(ChartFeedable<T> cf, long flushIntervalMs, int cacheSize, RunnableThrowableCallback onError)
	{
		super(onError);
		this.flushIntervalMs = flushIntervalMs;
		this.cacheSize = cacheSize;
		this.cf = cf;
		cachedData = new ArrayList<T> ();
		cachedMk = null;
		stop = false;
	}

	public void feedChartData(List<T> dataSeries, MarketID mk) throws Exception
	{
		cachedData.addAll(dataSeries);
		int overSize = cachedData.size() - cacheSize;
		
		if (overSize >= 1)
		{
			List<T> oversizeFeed = new ArrayList<T> ();
			for (int i = 0; i < overSize; i++)
				oversizeFeed.add(cachedData.remove(0));
			cf.feedChartData(oversizeFeed, mk);
		}	
	}

	public void runThread() throws Exception 
	{
		while (!stop)
		{
			if (cachedData.size() >= cacheSize)
				cf.feedChartData(cachedData, cachedMk);
			try { Thread.sleep(flushIntervalMs); } catch (Exception e) { }
		}
	}
	
	/*==========================================================================================================================
	 * restructure chart feeding classes into trade feeding and candle feeding. make those classes extend a line of generic feeding superclasses.
	 * finish trade recording classes and test
	 * go back to BitfinexPublic class and finish debugging
	 * =========================================================================================================================
	 */

	public void stop() 
	{
		stop = true;
	}
}
