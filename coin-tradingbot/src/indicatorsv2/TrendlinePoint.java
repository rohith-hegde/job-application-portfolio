package indicatorsv2;
import commonv2.*;
import configv2.Logs;
import exchangev2.*;
import rulesv2.*;

public class TrendlinePoint
{
	private Candle pointCandle; //worry only about this candle's timestamp (the X coordinate)
	private PriceType priceType;
	
	public TrendlinePoint(Candle pointC, PriceType pct)
	{
		pointCandle = pointC;
		priceType = pct;
		
	}
	
	public void setPointCandle(Candle c)
	{
		pointCandle = c;
	}
	
	public void setPriceType(PriceType pct)
	{
		priceType = pct;
	}
	
	public long getCandleTimestampS()
	{
		//System.out.println("TrendlinePoint.java line 30 - " + pointCandle);
		return (pointCandle.getStartTimeS() + pointCandle.getEndTimeS()) / 2;
	}
	
	public PriceType getPriceType()
	{
		return priceType;
	}
	
	//constructor and get/set methods for above variables
	
	public double getPointPrice() //get the Y coordinate
	{
		switch (priceType)
		{
			case LOW: return pointCandle.getLow().getRate();
			case HIGH: return pointCandle.getHigh().getRate();
			case OPEN: return pointCandle.getOpen().getRate();
			case CLOSE: return pointCandle.getClose().getRate();
			case WTDAVG: return pointCandle.getWeighted().getRate();
			default: return -1;
		}
	}
	
	public String toString()
	{
		return "{"
				+ "\"pointCandleStartTime\": \"" + Logs.log.unixTimestampStoDateStr(pointCandle.getStartTimeS()) + "\","
				+ "\"priceType\": \"" + priceType.toString() + "\","
				+ "\"pointPrice\": " + getPointPrice() + " " + pointCandle.getVolume()
			+ "}";
	}
}