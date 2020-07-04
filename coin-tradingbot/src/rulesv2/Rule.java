package rulesv2;

import java.util.LinkedList;
import java.util.List;

import commonv2.Candle;

public abstract class Rule //implements ChartFeedable
{
	private int candleCacheNum; //number of candles to cache and make decisions on
	private String name;
	private List<Candle> cachedCandles;
	
	public Rule(String name, int candleCacheNum)
	{
		this.candleCacheNum = candleCacheNum;
		this.name = name;
		cachedCandles = new LinkedList<Candle> ();
	}
	
	public void feedChartData(List<Candle> graph)
	{
		//cachedCandles will never be half full
		//Logs.log.debug("Rule.feedChartData(): graph size: " + graph.size());
		
		if (cachedCandles.size() > 0)
			addNewCandles(graph);
		else
			cachedCandles.addAll(graph);
		
		/*if (cachedCandles.size() >= candleCacheNum)
			for (int i = 0; i < graph.size(); i++)
				cachedCandles.remove();*/
		//Logs.log.debug("Rule.feedChartData(): new start time and end time: " + cachedCandles.get(0).getStartTimeS() + " to " + cachedCandles.get(candleCacheNum - 1).getStartTimeS());
	}
	
	public int getCandleCacheNum() {
		return candleCacheNum;
	}

	public String getName() {
		return name;
	}

	public List<Candle> getCachedCandles() {
		return cachedCandles;
	}
	
	private void addNewCandles(List<Candle> newL)
	{
		//LinkedList<Candle> u = new LinkedList<Candle> ();
		//u.addAll(oldL);
		/*
		 * cachedCandles: {[86000], [86100], [86200], [86300]}. candleCacheNum: 4
		 * newL: [86200], [86300], [86400]. 
		 * 
		 * cachedCandles: {[86000], [86100], [86200], [86300]}. candleCacheNum: 4
		 * newL: [86400]. 
		 * 
		 * cachedCandles: {[86000], [86100], [86200], [86300]}. candleCacheNum: 4
		 * newL: [86400]. 
		 * cachedCandles WILL ALWAYS BE INITIALLY FILLED TO THE TOP
		 */
		
		//System.out.println("j");
		//System.out.println("Old candles: " + cachedCandles);
		
		for (int n = 0; n < newL.size(); n++)
		{
			Candle nC = newL.get(n);
			boolean removed = false;
			
			for (int i = cachedCandles.size() - 1; i >= 0 && !removed; i--)
			{
				Candle oC = cachedCandles.get(i);
				if (oC.getStartTimeS() == nC.getStartTimeS())
				{
					cachedCandles.remove(i);
					removed = true;
				}
			}
		}
		
		for (Candle c : newL)
			cachedCandles.add(c);
		while (cachedCandles.size() > candleCacheNum)
			cachedCandles.remove(0);
		
		//System.out.println("New candles: " + cachedCandles);
		
		/*for (int i = oldL.size() - 1; i >= 0; i--)
		{
			Candle c = newL.get(i);
			for (int n = 0; n < newL.size(); n++)
				if (newL.get(n).getStartTimeS() == c.getStartTimeS())
					oldL.remove(i);
		}*/
	}
	
	public abstract RuleResult checkRule();
}
