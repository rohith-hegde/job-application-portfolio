package rulesv2;

public class RuleResult 
{
	private boolean ruleActivated;
	
	public RuleResult(boolean ruleActivated)
	{
		this.ruleActivated = ruleActivated;
	}

	public boolean isRuleActivated()
	{
		return ruleActivated;
	}
	
	public String toString()
	{
		return "ruleActivated: " + ruleActivated;
	}
}
