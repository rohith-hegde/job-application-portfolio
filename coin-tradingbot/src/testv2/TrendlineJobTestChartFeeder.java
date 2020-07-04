package testv2;

import java.util.ArrayList;
import java.util.List;

import commonv2.Candle;
import commonv2.MarketID;
import net.hammereditor.designutilities.design.RunnableThrowableCallback;
import recorderv2.ChartFeeder;

public class TrendlineJobTestChartFeeder extends ChartFeeder<Candle> 
{
	private List<Candle> chartData;

	public TrendlineJobTestChartFeeder(RunnableThrowableCallback onError, int dataIntervalS, List<Candle> chartData) 
	{
		super(onError, dataIntervalS);
		this.chartData = chartData;
	}

	/**
	 * feed the pre-recorded candles one at a time
	 */
	public void runThread() throws Exception 
	{
		List<Candle> cl = new ArrayList<Candle> ();
		while (chartData.size() > 0)
		{
			for (MarketID mk : super.getConsumingMarketIDs())
			{
				cl.add(chartData.remove(0));
				super.feedChartData(mk, cl);
				cl.clear();
			}
		}
	}

	public void initialFeed(int dataNum) throws Exception 
	{
		List<Candle> cl = new ArrayList<Candle> ();
		for (MarketID mk : super.getConsumingMarketIDs())
		{
			for (int i = 0; i < dataNum; i++)
				cl.add(chartData.remove(0));
			
			super.feedChartData(mk, cl);
			cl.clear();
		}
	}

}
