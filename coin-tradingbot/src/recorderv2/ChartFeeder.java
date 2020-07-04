package recorderv2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import commonv2.MarketID;


import net.hammereditor.designutilities.design.RunnableThrowable;
import net.hammereditor.designutilities.design.RunnableThrowableCallback;
import net.hammereditor.designutilities.design.Stoppable;

public abstract class ChartFeeder<T> extends RunnableThrowable implements Stoppable
{
	private Map <MarketID, List<ChartFeedable<T>>> chartDataConsumers;
	private boolean stop;
	private int dataIntervalS;
	
	///////////////////////////////////////////////////
	// Chart feeder work to do: READ CAREFULLY
	//
	// • At the end of the day, a chart feeder should be created with a certain candle period. this should be implemented inside this class. getCP(), setCP() and private int dataIntervalS
	//   Polling chart feeder shouldn't have to implement this
	// • This means all data consumers (Rule watchers, etc.) should work with only the candle period which the chart feeder was created with
	// • Rule objects shouldn't have to worry about time intervals. They should also look through the inputted list in "feedChartData()", and replace old candles with new ones
	// • RuleWatchers should define rules in the constructor and set their names properly. Then, in the doStrategy() method, a RuleWatcher should call on these names and behave according to the rules's results
	//
	// • Ultimately, the program lifecycle goes like this: main class.main(String[] a) creates 5min. chart feeder, 30min. chart feeder, etc. MainClass then creates new RuleWatcher consumers, consumers which 
	//   save candle data to the DB, consumers which display candle data on a webpage, etc. MainClass then adds these data consumers to their appropriate ChartFeeder. MainClass starts the ChartFeeder threads, then 
	//	the initialization is complete! The ChartFeeder threads can then get candle data and feed it to the consumers. Until a shutdown event or user intervention tells MainClass to stop the chart feeders.
	// • 
	///////////////////////////////////////////////////
	 
	public ChartFeeder(RunnableThrowableCallback onError, int dataIntervalS)
	{
		super(onError);
		chartDataConsumers = new HashMap <MarketID, List<ChartFeedable<T>>> ();
		stop = false;
		this.dataIntervalS = dataIntervalS;
	}
	
	public void stop()
	{
		stop = true;
	}
	
	public int getDataIntervalS() {
		return dataIntervalS;
	}

	public void setDataIntervalS(int dataIntervalS) {
		this.dataIntervalS = dataIntervalS;
	}

	protected boolean isStopped()
	{
		return stop;
	}
	
	public Map<MarketID, List<ChartFeedable<T>>> getDataConsumers()
	{
		return chartDataConsumers;
	}
	
	public void addConsumer(MarketID mk, ChartFeedable<T> f)
	{
		List <ChartFeedable<T>> l = chartDataConsumers.get(mk);
		if (l == null)
		{
			l = new ArrayList<ChartFeedable<T>> ();
			chartDataConsumers.put(mk, l);
		}
		l.add(f);
	}
	
	public boolean removeConsumer(ChartFeedable<T> f)
	{
		Set<MarketID> ids = chartDataConsumers.keySet();
		Iterator<MarketID> it = ids.iterator();
		
		while (it.hasNext())
		{
			MarketID mk = it.next();
			List<ChartFeedable<T>> cfl = chartDataConsumers.get(mk);
			
			if (cfl.contains(f))
			{
				cfl.remove(f);
				if (cfl.size() == 0)
					chartDataConsumers.put(mk, null);
				return true;
			}
		}
		
		return false;
	}
	
	public List<MarketID> getConsumingMarketIDs()
	{
		//get markets to poll
		List<MarketID> mkl = new ArrayList<MarketID> ();
		Set<MarketID> ids = getDataConsumers().keySet();
		
		Iterator<MarketID> it = ids.iterator();
		while (it.hasNext())
			mkl.add(it.next());
		
		//Logs.log.debug("ChartFeeder.getConsumingMarketIDs(): list: " + mkl.toString());
		return mkl;
	}
	
	public abstract void runThread() throws Exception;	
	
	public abstract void initialFeed(int dataNum) throws Exception;
	
	public void feedChartData(MarketID mk, List<T> data) throws Exception
	{
		List<ChartFeedable<T>> cfl = chartDataConsumers.get(mk);
		for (ChartFeedable<T> cf : cfl)
			cf.feedChartData(data, mk);
	}
	
	//+2 -1 -1 0 +1 +2
	//+1 -3 -1 0 +1
}
