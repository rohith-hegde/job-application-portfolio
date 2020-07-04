package rulesv2;

public class NumericRuleResult extends SignedRuleResult 
{
	private double resultNumber;
	
	public NumericRuleResult(boolean ruleActivated, boolean bullish, double number) 
	{
		super(ruleActivated, bullish);
		resultNumber = number;
	}
	
	public double getResultNumber()
	{
		return resultNumber;
	}
	
	public String toString()
	{
		return super.toString() + " resultNumber: " + resultNumber;
	}
}