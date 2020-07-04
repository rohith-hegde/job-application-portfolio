package rulesv2;

import java.util.HashMap;
import java.util.Map;

import configv2.Logs;
import indicatorsv2.MovingAverage;

public class MovingAverageDifferenceRule extends MovingAverageRule
{
	private double minDiffPcnt;
	private Boolean lastDifferenceOverMin;
	private double lastMadp;

	/**
	 * 
	 * @param name
	 * @param candleCacheNum
	 * @param quickMA
	 * @param slowMA
	 * @param maxDiffPcnt max. difference, in percent (1.01 = 1%) of moving averages before rule is activated
	 */
	public MovingAverageDifferenceRule(String name, int candleCacheNum, MovingAverage quickMA, MovingAverage slowMA, double maxDiffPcnt) 
	{
		super(name, candleCacheNum, quickMA, slowMA);
		this.minDiffPcnt = maxDiffPcnt;
		lastDifferenceOverMin = false; //null;
		lastMadp = 0;
	}
	
	/**
	 * @return activated if MA difference > max. status has changed
	 */
	public SignedRuleResult checkRule() 
	{
		super.checkRule();
		double slowMAres = super.getSlowMA().getMovingAverage(super.getCachedCandles());
		double quickMAres = super.getQuickMA().getMovingAverage(super.getCachedCandles());
		Crossover cr = (quickMAres < slowMAres ? Crossover.QUICKUNDERSLOW : Crossover.SLOWUNDERQUICK);
		
		double madp;
		boolean bullish;
		
		if (cr == Crossover.QUICKUNDERSLOW)
		{
			madp = slowMAres / quickMAres - 1.0;
			bullish = false;
		}
		else
		{
			
			madp = quickMAres / slowMAres - 1.0;
			bullish = true;
		}
		
		System.out.println("madp: " + madp + " quickMA: " + quickMAres + " / slowMA: " + slowMAres + " minDiff: " + minDiffPcnt);
		
		boolean diffUnderMin = (madp <= minDiffPcnt ? true : false);
		boolean activated = (lastDifferenceOverMin != null && diffUnderMin != lastDifferenceOverMin); // && diffUnderMin);
		lastDifferenceOverMin = diffUnderMin;
		lastMadp = madp;
		return new SignedRuleResult(activated, bullish);
	}

	public double getMaxDiffPcnt()
	{
		return minDiffPcnt;
	}
	
	public double getLastMadp()
	{
		return lastMadp;
	}
	
	public Map<String, String> getTemplateReplacements() 
	{
		Map<String, String> r = super.getTemplateReplacements();
		r.put("minDiff", "" + Logs.changeDecimalPlaces(minDiffPcnt, 6));
		r.put("madp", "" + Logs.changeDecimalPlaces(lastMadp, 6));
		r.put("expectedCrossover", "" + ((super.getLastCrossover() == Crossover.QUICKUNDERSLOW) ? Crossover.SLOWUNDERQUICK.name() : Crossover.QUICKUNDERSLOW.name()));
		r.put("converging", (lastDifferenceOverMin ? "converging" : "diverging"));
		
		//double quickMAlastRes = Double.parseDouble(r.get("quickMAlastRes"));
		//double slowMAlastRes = Double.parseDouble(r.get("slowMAlastRes"));
		r.put("iconFilename", "" + ((super.getLastCrossover() == Crossover.QUICKUNDERSLOW) ? "bullMarketIcon.png" : "bearMarketIcon2.png"));
		return r;
	}
}
