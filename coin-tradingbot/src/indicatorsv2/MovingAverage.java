package indicatorsv2;

import java.util.ArrayList;
import java.util.List;

import commonv2.Candle;
import commonv2.Utils;

public abstract class MovingAverage 
{
	private int period; //number of candles to consider
	private PriceType candleRateType;
	
	public MovingAverage(int period, PriceType candleRateType)
	{
		this.period = period;
		this.candleRateType = candleRateType;
	}
	
	public int getPeriod()
	{
		return period;
	}
	
	public double getCandleRate(Candle c)
	{
		return Utils.getCandleRate(c, candleRateType);
	}
	
	public double getMovingAverage(List<Candle> candles)
	{
		List<Candle> candlesN = new ArrayList<Candle> (candles);
		int extraCandles = candlesN.size() - period - 0;
		//System.out.println("Extra candle list: " + candles);
		for (int i = 0; i < extraCandles; i++)
			candlesN.remove(0);
		//System.out.println("New: " + candles);
		//System.out.println(extraCandles);
		return calculateMovingAverage(candlesN);
	}
	
	public abstract double calculateMovingAverage(List<Candle> candles);
	
	public String toString()
	{
		return "period: " + period + ", priceType: " + candleRateType.name();
	}
	
	public String description()
	{
		return period + "-candle " + candleRateType.name().toLowerCase() + " MA";
	}
}
