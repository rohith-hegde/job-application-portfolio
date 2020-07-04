package indicatorsv2;

import java.util.List;

import commonv2.Candle;
import commonv2.ExchangeID;
import commonv2.MarketID;
import commonv2.MarketID.MarketType;
import configv2.Logs;
import exchangev2.PoloniexPublic;
import testv2.AlertTestMain;

public class ExponentialMovingAverage extends MovingAverage
{
	private double lastEma;
	private static final double offset = 0;
	private long firstCandleTime;
	
	public ExponentialMovingAverage(int period, PriceType candlePriceType)
	{
		super(period, candlePriceType);
		lastEma = 0;
		firstCandleTime = 0;
	}

	public double calculateMovingAverage(List<Candle> candles) //first element is oldest
	{
		//double ema = 0, lastEma = 0;
		double ema = 0; //, lastEma = candles.get(0).getClose().getRate();
		//lastEma = 0;
		int newCandles = getNewCandleNum(candles);
		//System.out.println(newCandles);
		//int lastEmaPos = 0;
		//if (newCandles > -1)
			//lastEmaPos = newCandles;
			
		firstCandleTime = candles.get(candles.size() - 1).getStartTimeS();
		
		/*
		 * *******************************************************************
		 * SET CORRECT lastEma BASED ON NUMBER OF NEW CANDLES
		 *  *******************************************************************
		 */
		
		if (lastEma == 0 || newCandles > 1)
			lastEma = super.getCandleRate(candles.get(0));
		if (candles.size() != getPeriod())
			Logs.log.warning(this.getClass().getName() + ".calculateMovingAverage(): Incorrect candle amount: " + candles.size() + ".\nCandles: " + candles);
		
		for (int i = 0; i < candles.size(); i++)
		{
			Candle c = candles.get(i);
			ema = calculateEMA(super.getCandleRate(c), getPeriod(), lastEma); //getWeighted()
			//System.out.println("ExponentialMovingAverage.[cma](): MA " + i + ": " + ema);
			lastEma = ema;	
		}
		
		return ema + offset;
	}
	
	private int getNewCandleNum(List<Candle> candles)
	{
		boolean found = false;
		for (int i = candles.size() - 1; i >= 0 && !found; i--)
		{
			Candle c = candles.get(i);
			if (c.getStartTimeS() == firstCandleTime)
				return candles.size() - i - 1;
		}
		
		return -1;
	}
	
	public void setLastEma(double e)
	{
		lastEma = e;
	}

	private double calculateEMA(double rate, double dayNum, double lastEma)
	{
		double k = 2 / (dayNum + 1);
		return rate * k + lastEma * (1 - k); //factor*price + (1-factor)*runningEMA;
	}
	
	public String toString()
	{
		return super.toString() + ", type: exponential";
	}
	
	public String description()
	{
		String s = super.description();
		s = s.replaceFirst("MA", "EMA");
		return s;
	}
	
	public static void main(String[] args) throws Exception
	{
		AlertTestMain.loadTestConfiguration();
		PoloniexPublic pp = new PoloniexPublic();
		List<Candle> candles = pp.getCandlestickHistory(System.currentTimeMillis() - 1 * 4 * 3600 * 1000, System.currentTimeMillis() - 0 * 14 * 3600 * 1000, 5 * 60 * 1000, new MarketID(ExchangeID.POLONIEX, "BTC", "ETH", MarketType.MARGIN));
		System.out.println("EMA: " + new ExponentialMovingAverage(20, PriceType.CLOSE).getMovingAverage(candles));
		System.out.println("candle list size: " + candles.size() + " " + candles);
		//System.out.println("SMA: " + new SimpleMovingAverage(120).getMovingAverage(candles));
	}
}
