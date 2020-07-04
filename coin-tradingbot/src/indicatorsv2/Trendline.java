package indicatorsv2;


/**
 * represents a trendline on the chart. the trendline is represented by two points.
 * each point's X coordinate is the end timestamp of the candle, and each point's Y coordinate is the low, open, close, high, or weighted price, depending on the PriceType variable. the user chooses the PriceType
 *
 */
public class Trendline 
{
	private TrendlinePoint startPt, endPt;
	
	public Trendline(TrendlinePoint startPoint, TrendlinePoint endPoint)
	{
		startPt = startPoint;
		endPt = endPoint;
	}
	
	public double getY(long x)
	{
		//double slope = getSlope(startPt.getCandleTimestampS(), startPt.getPointPrice(), endPt.getCandleTimestampS(), endPt.getPointPrice());
		double slope = getSlope();
		return ((slope * x) + getYIntercept(slope, startPt.getCandleTimestampS(), startPt.getPointPrice()));
	}
	
	public double getSlope()
	{
		long x1 = startPt.getCandleTimestampS(), x2 = endPt.getCandleTimestampS();
		double y1 = startPt.getPointPrice(), y2 = endPt.getPointPrice();
		return (double)((y2-y1)/(x2-x1));
	}

	public double getYIntercept(double slope, long anyX, double anyY)
	{
		long inverseX = -1 * anyX;
		//double inverseY = -1 * anyY;
		
		double b = slope * inverseX;
		b += anyY;
		
		return b;
	}
	
	public void setStartPt(TrendlinePoint startPoint)
	{
		startPt = startPoint;
	}
	
	public void setEndPt(TrendlinePoint endPoint)
	{
		endPt = endPoint;
	}
	
	public TrendlinePoint getStartPt()
	{
		return startPt;
	}
	
	public TrendlinePoint getEndPt()
	{
		return endPt;
	}
	//constructor and get/set methods for above variables
	
	public String toString()
	{
		return "{"
				+ "\"startPoint\": \"" + startPt + "\","
				+ "\"endPoint\": \"" + endPt + "\","
				+ "\"slope\": " + getSlope()
			+ "}";
	}
}
