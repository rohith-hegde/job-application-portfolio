package rulesv2;

import indicatorsv2.MovingAverage;

public class MovingAverageDifferenceRuleNumeric extends MovingAverageDifferenceRule 
{

	public MovingAverageDifferenceRuleNumeric(String name, int candleCacheNum,
			MovingAverage quickMA, MovingAverage slowMA, double maDiffPcnt) {
		super(name, candleCacheNum, quickMA, slowMA, maDiffPcnt);
		// TODO Auto-generated constructor stub
	}
	
	public NumericRuleResult checkRule()
	{
		SignedRuleResult srr = super.checkRule();
		return new NumericRuleResult(srr.isRuleActivated(), srr.isResultBullish(), super.getLastMadp());
	}
}
