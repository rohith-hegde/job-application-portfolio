package indicatorsv2;
import java.util.List;

import commonv2.Candle;


public class SimpleMovingAverage extends MovingAverage 
{
	public SimpleMovingAverage(int period, PriceType cpt)
	{
		super(period, cpt);
	}

	public double calculateMovingAverage(List<Candle> candles)
	{
		double total = 0;
		for (Candle c : candles)
			total += super.getCandleRate(c); //getWeighted()
		return total / candles.size();
	}
	
	public String toString()
	{
		return super.toString() + ", type: simple";
	}
	
	public String description()
	{
		String s = super.description();
		s = s.replaceFirst("MA", "SMA");
		return s;
	}
}
