package testv2;

import alertv2.AlertMessage;
import alertv2.ConsoleAlertMessage;
import alertv2.RuleTemplateReplacements;
import commonv2.MarketID;
import configv2.Logs;
import rulesv2.Rule;
import rulesv2.RuleWatcher;

public class TrendlineJobTestRuleWatcher extends RuleWatcher
{
	public TrendlineJobTestRuleWatcher(String name) {
		super(name);
	}

	public void doStrategy(MarketID mk)
	{
		for (AlertMessage a : super.getAlerts())
		{
			if (a instanceof ConsoleAlertMessage)
			{
				for (Rule r : super.getActivatedRules())
					if (r instanceof RuleTemplateReplacements)
						((ConsoleAlertMessage) a).putTemplateReplacements(((RuleTemplateReplacements)r).getTemplateReplacements());
			}
			
			try { a.send(); } catch (Exception e) { 
				Logs.log.error("TrendlineJobTestRuleWatcher.doStrategy(): error while sending alert message \'" + a.getTitle() + "\': " + e.getMessage());
				Logs.log.printException(e);
			}
		}
		
		Logs.log.debug("TrendlineJobTestRuleWatcher.doStrategy(): sent all alert messages and did all actions.");
	}
}
