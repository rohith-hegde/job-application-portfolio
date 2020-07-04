package recorderv2;

import java.util.List;

import commonv2.MarketID;


public interface ChartFeedable<T>
{
	public void feedChartData(List<T> graph, MarketID mk) throws Exception;
}
