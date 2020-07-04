package rulesv2;

import java.util.ArrayList;
import java.util.List;

import alertv2.Action;
import alertv2.AlertMessage;
import commonv2.Candle;
import commonv2.MarketID;
import recorderv2.ChartFeedable;


public abstract class RuleWatcher implements ChartFeedable<Candle>
{
	private List<AlertMessage> alerts;
	private List<Action> actions;
	private List<Rule> rules;
	private List<Rule> activatedRules;
	private String name;
	private boolean firstFeed;
	
	public RuleWatcher(String name)
	{
		this(new ArrayList<AlertMessage> (), new ArrayList<Action> (), new ArrayList<Rule> (), name);
	}
	
	public RuleWatcher(List<AlertMessage> alerts, List<Action> actions, List<Rule> rules, String name)
	{
		this.alerts = alerts;
		this.actions = actions;
		this.rules = rules;
		this.name = name;
		
		activatedRules = new ArrayList<Rule> ();
		firstFeed = true;
	}
	
	public void addRule(Rule rule)
	{
		rules.add(rule);
	}
	
	public void addAction(Action a)
	{
		actions.add(a);
	}
	
	public void addAlert(AlertMessage alert)
	{
		alerts.add(alert);
	}
	
	public List<AlertMessage> getAlerts()
	{
		return alerts;
	}
	
	public List<Action> getActions()
	{
		return actions;
	}
	
	public List<Rule> getActivatedRules()
	{
		return activatedRules;
	}
	
	public List<Rule> getRules()
	{
		return rules;
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * Feeds live or polled candlestick data to the rules which this chart watcher manages
	 * MarketID doesn't always have to be the same.
	 * @param graph
	 */
	public void feedChartData(List<Candle> graph, MarketID mk)
	{
		activatedRules.clear();
		for (Rule r : rules)
			r.feedChartData(graph);
		
		if (!firstFeed)
		{
			boolean ruleActivated = false;
			
			for (Rule r : getRules())
			{
				if (r.checkRule().isRuleActivated()) // && !ruleActivated)
				{
					ruleActivated = true;
					activatedRules.add(r);
				}
			}
			
			if (ruleActivated)
			{
				System.out.println("At least one rule activated");
				doStrategy(mk);
			}
		}
		
		firstFeed = false;
	}
	
	/**
	 * do trading strategy and send alerts
	 * @throws Exception
	 */
	public abstract void doStrategy(MarketID mk);
	
	/*
	 * Next: think about strategy handling
	 * 
	 */
	
	/*
	 * Next: implement 'watcher' and 'rule' class, then create different types of rules (volatility, close price below/above value, candle breaking trend line, etc.). But only implement volatility one for now, then finish HTML template. Then test system with sample message.
	 * rule result: stores the result of checking a rule. can be a binary decision or more complex.
	 */
}
