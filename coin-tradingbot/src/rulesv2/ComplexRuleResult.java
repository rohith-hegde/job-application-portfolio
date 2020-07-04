package rulesv2;

public abstract class ComplexRuleResult extends RuleResult
{
	public ComplexRuleResult(boolean ruleActivated)
	{
		super(ruleActivated);
	}
	
	/**
	 * start thread and return it
	 * TODO: make inputData varaible more specific (Broker object, PoloniexPrivate object...)
	 * @param inputData
	 * @return
	 */
	public abstract Thread executeComplexRuleAction(Object[] inputData);
}
