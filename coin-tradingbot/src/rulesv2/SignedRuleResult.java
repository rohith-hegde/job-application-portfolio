package rulesv2;

public class SignedRuleResult extends RuleResult
{
	private boolean bullish;
	
	public SignedRuleResult(boolean ruleActivated, boolean bullish)
	{
		super(ruleActivated);
		this.bullish = bullish;
	}
	
	public boolean isResultBullish()
	{
		return bullish;
	}
	
	public boolean equals(Object o)
	{
		if (!(o instanceof SignedRuleResult))
			return false;
		else 
		{
			SignedRuleResult r = (SignedRuleResult)o;
			return r.isResultBullish() == isResultBullish() && r.isRuleActivated() == isRuleActivated();
		}
	}
	
	public String toString()
	{
		return super.toString() + " resultBullish: " + isResultBullish();
	}
}
