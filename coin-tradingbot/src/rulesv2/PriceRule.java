package rulesv2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alertv2.RuleTemplateReplacements;
import commonv2.Utils;
import configv2.Logs;
import commonv2.*;
import exchangev2.*;
import indicatorsv2.*;

public class PriceRule extends Rule implements RuleTemplateReplacements
{
	private PriceType validPT;
	private PriceCrossType pct;
	private double rate;
	public boolean lastActivated;
	
	public PriceRule(String name, PriceCrossType pct, double price, PriceType validPT)
	{
		this(name, pct, price);
		this.validPT = validPT;
	}
	
	private PriceRule(String name, PriceCrossType pct, double price)
	{
		super(name, 1);
		this.pct = pct;
		rate = price;
		lastActivated = false;
	}

	public RuleResult checkRule() 
	{
		boolean activated = false;
		
		switch (pct)
		{
			case BELOW: 
				double minRate = getCurrentPrice();
				if (minRate < rate) activated = true; 
				break;
			case ABOVE: 
				double maxRate = getCurrentPrice();
				if (maxRate > rate) activated = true; 
				break;
		}
		
		if (activated)
			System.out.println("PriceRule.checkRule(): candle price \'" + getCurrentPrice() + "\' is " + pct.name() + " " + rate);
		
		/*switch (pt) {
			case WTDAVG: priceToUse = c.getWeighted().getRate(); break;
			case CLOSE: priceToUse = c.getClose().getRate(); break;
			case LOW: priceToUse = c.getLow().getRate(); break;
			case HIGH: priceToUse = c.getHigh().getRate(); break;
		}*/
		
		/*switch (pct) {
			case BELOW: 
				if (priceToUse < rate) activated = true; break;
			case ABOVE: 
				if (priceToUse > rate) activated = true; break;
		}*/
		
		boolean activationStatusFalseToTrue = (activated && !lastActivated);
		lastActivated = activated;
		return new RuleResult(activationStatusFalseToTrue);
	}
	
	public double getCurrentPrice()
	{
		Candle c = super.getCachedCandles().get(0);
		return Utils.getCandleRate(c, validPT);
	}
	
	public PriceType getValidPT() {
		return validPT;
	}

	public void setValidPT(PriceType validPT) {
		this.validPT = validPT;
	}

	public PriceCrossType getPct() {
		return pct;
	}

	public void setPct(PriceCrossType pct) {
		this.pct = pct;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public Map<String, String> getTemplateReplacements() 
	{
		HashMap<String, String> r = new HashMap<String, String> ();
		r.put("rate", "" + Logs.changeDecimalPlaces(rate, 8));
		r.put("priceCrossType", "" + pct.name());
		r.put("priceType",  "" + validPT.name());
		r.put("currentPrice",  "" + Logs.changeDecimalPlaces(getCurrentPrice(), 8));
		
		double priceDiffPcnt = getCurrentPrice() / rate * 100;
		r.put("currentPriceDiffPcnt", Logs.changeDecimalPlaces(priceDiffPcnt, 2));
		return r;
	}
}
