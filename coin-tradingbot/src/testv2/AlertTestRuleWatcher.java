package testv2;
import java.io.File;

import alertv2.Action;
import alertv2.AlertMessage;
import alertv2.EmailAlertMessage;
import alertv2.RuleTemplateReplacements;
import alertv2.ScreenshotEmailAlertMessage;
import alertv2.TemplatedAlertMessage;
import commonv2.MarketID;
import recorderv2.PoloniexChartDescription.PCDcandlePeriod;
import recorderv2.PoloniexChartDescription.PCDtimespan;
import rulesv2.Rule;
import rulesv2.RuleWatcher;
import configv2.Logs;

public class AlertTestRuleWatcher extends RuleWatcher 
{
	public AlertTestRuleWatcher(String name) 
	{
		super(name);
		//determine rules
		
		/////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
	}

	public void doStrategy(MarketID mk)
	{
		for (AlertMessage a : super.getAlerts())
		{
			if (a instanceof TemplatedAlertMessage)
			{
				((TemplatedAlertMessage)a).putTemplateReplacement("marketID", mk.quickDescription());
				
				for (Rule r : super.getRules())
				{
					if (r instanceof RuleTemplateReplacements)
						((TemplatedAlertMessage)a).putTemplateReplacements(((RuleTemplateReplacements)r).getTemplateReplacements());
				}
				
				//System.out.println("doStrategy(): put marketId template replacement");
				if (a instanceof ScreenshotEmailAlertMessage)
				{
					ScreenshotEmailAlertMessage se = ((ScreenshotEmailAlertMessage)a);
					se.setPSGmarket(mk);
					
				}
			}
				
			try { a.send(); } catch (Exception e) { 
				Logs.log.error("AlertTestRuleWatcher.doStrategy(): error while sending alert message \'" + a.getTitle() + "\': " + e.getMessage());
				Logs.log.printException(e);
			}
		}
		
		for (Action a : super.getActions())
		{
			try { a.doAction(); } catch (Exception e) { 
				Logs.log.error("AlertTestRuleWatcher.doStrategy(): error while doing action \'" + a.getDescription() + "\': " + e.getMessage());
				Logs.log.printException(e);
			}
		}
		
		Logs.log.debug("AlertTestRuleWatcher.doStrategy(): sent all alert messages and did all actions.");
	}
}
