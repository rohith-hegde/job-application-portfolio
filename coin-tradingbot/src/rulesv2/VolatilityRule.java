package rulesv2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import commonv2.Utils;
import configv2.Logs;
import indicatorsv2.*;
import alertv2.*;
import commonv2.*;
import exchangev2.*;
import indicatorsv2.PriceCrossType;

/**
 * rule is activated when cached candle volatility exceeds certain ratio
 * @author Hammereditor
 */
public class VolatilityRule extends Rule implements RuleTemplateReplacements
{
	private double maxVol;
	private PriceType cpt;
	public boolean lastActivated;
	
	/**
	 * 
	 * @param name
	 * @param candleCacheNum
	 * @param candleTimeS
	 * @param maxVol The maximum ratio between the lowest candle close and the highest candle close 
	 * 		maxvol = 1.1 means price difference of $11+ and $10 will trigger the rule.
	 */
	public VolatilityRule(String name, int candleCacheNum, double maxVol, PriceType cpt) 
	{
		super(name, candleCacheNum);
		this.cpt = cpt;
		this.maxVol = maxVol;
		lastActivated = false;
	}

	public RuleResult checkRule()
	{
		List<Candle> cachedCandles = super.getCachedCandles();
		System.out.println("Volatility: " + getVolatility(cachedCandles));
		boolean activated = (getVolatility(cachedCandles) > maxVol ? true : false);
		
		boolean activationStatusFalseToTrue = (activated && !lastActivated);
		lastActivated = activated;
		return new RuleResult(activationStatusFalseToTrue);
	}
	
	public double getVolatility(List<Candle> candles)
	{
		double[] r = Utils.getMaxAndMinCandleRates(candles, cpt);
		double lowestWtAvg = r[0];
		double highestWtAvg = r[1];
		return (highestWtAvg / lowestWtAvg) - 1.0;
	}

	public Map<String, String> getTemplateReplacements() 
	{
		HashMap<String, String> r = new HashMap<String, String> ();
		r.put("volatilityCurrent", "" + Logs.changeDecimalPlaces(getVolatility(super.getCachedCandles()), 5));
		r.put("volatilityMax", "" + maxVol);
		r.put("priceType", cpt.name());
		return r;
	}
}
