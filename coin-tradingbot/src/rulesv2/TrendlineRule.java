package rulesv2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indicatorsv2.*;
import alertv2.*;
import commonv2.*;
import configv2.Logs;
import exchangev2.*;
import indicatorsv2.PriceCrossType;

/**
 * 
 *
 */
public class TrendlineRule extends Rule implements RuleTemplateReplacements
{
	private Trendline line;
	private PriceCrossType pct;
	public boolean lastActivated;
	
	private long checkStartTimeS, checkEndTimeS;
	
	/*private TrendlinePoint startPoint;
	private TrendlinePoint endPoint;
	
	private long startPointX;
	private double startPointY;
	
	private long endPointX;
	private double endPointY;

	
	private double lineSlope;
	private double lineYIntercept;*/
	/**
	 * 
	 * @param name don't worry about this
	 * @param candleCacheNum don't worry about this
	 * @param line the trendline to use
	 * @param pct PriceCrossType.ABOVE = return true when close price crosses above the line. PriceCrossType.BELOW = return true when close price crosses below the line.
	 */
	public TrendlineRule(String name, int candleCacheNum, Trendline line, PriceCrossType pct, long checkStartTimeS, long checkEndTimeS) 
	{
		super(name, candleCacheNum);
		this.pct = pct;
		this.line = line;
		this.checkEndTimeS = checkEndTimeS;
		this.checkStartTimeS = checkStartTimeS;
		lastActivated = false;
		
		Logs.log.debug("TrendlineRule(): created with name: \'" + name + "\'. Line: " + line.toString());
		
		
		/*startPointX = startPoint.getCandleTimestamp();
		startPointY = startPoint.getPointPrice();
		
		
		endPointX = endPoint.getCandleTimestamp();
		endPointY = endPoint.getPointPrice();
		
		lineSlope = getSlope(startPointX, startPointY, endPointX, endPointY);
		lineYIntercept = getYIntercept(lineSlope, startPointX, startPointY);*/
	}
	
	/*private double getY(long x)
	{
		return ((lineSlope * x) + lineYIntercept);
	}
	
	private double getSlope(long x1, double y1, long x2, double y2)
	{
		return (double)((y2-y1)/(x2-x1));
	}

	private double getYIntercept(double slope, long anyX, double anyY)
	{
		long inverseX = -1 * anyX;
		//double inverseY = -1 * anyY;
		
		double b = slope * inverseX;
		b += anyY;
		
		return b;
	}*/
	
	public RuleResult checkRule()
	{
		boolean ruleActivated = false;
		//RuleResult res = new RuleResult(false); //default: line not crossed in the manner of PCT
		List<Candle> newData = super.getCachedCandles();
		
		/////////////////////////////////////////////////////////////////////////////////////
		///  	MODIFY THIS CLASS SO THAT THE CREATOR CAN SPECIFY A START TIME AND END TIME THAT CANDLES MUST BE INSIDE
		///
		////////////////////////////////////////////////////////////////////////////////////
		
		for (Candle can : newData)
		{
			Trade closeT = can.getClose();
			//int unixTime = close.getTimeS();     //x for candle close\
			long candleStartTimeS = can.getStartTimeS();
			long candleEndTimeS = can.getEndTimeS();

			if (candleStartTimeS >= checkStartTimeS && candleEndTimeS <= checkEndTimeS)
			{
				double closePrice = closeT.getRate(); //y for candle close
				double predictedY = line.getY((candleStartTimeS + candleEndTimeS) / 2); //y that is predicted based on the candle's X (using the trendline)
				
				if(closePrice > predictedY) //above
				{
					if(pct == PriceCrossType.ABOVE)
					{
						//res = new RuleResult(true); //line crossed in manner of PCT
						ruleActivated = true;
					}
					else if(ruleActivated)
					{
						//res = new RuleResult(false);
						ruleActivated = false;
					}
						
				}
				else //below
				{
					if(pct == PriceCrossType.BELOW)
					{
						//res = new RuleResult(true); //line crossed in manner of PCT
						ruleActivated = true;
					}
					else if(ruleActivated)
					{
						//res = new RuleResult(false);
						ruleActivated = false;
					}
				}
				
			}
			//else
				//System.out.println("candle not in line range. candleStartTimeS: " + candleStartTimeS + " (" + Logs.log.unixTimestampStoDateStr(candleStartTimeS) + "), line start time: " + line.getStartPt().getCandleTimestampS() + " (" + Logs.log.unixTimestampStoDateStr(line.getStartPt().getCandleTimestampS()) + ")");
		}
		
		boolean activationStatusFalseToTrue = (ruleActivated && !lastActivated);
		lastActivated = ruleActivated;
		return new RuleResult(activationStatusFalseToTrue);
		//return new RuleResult(ruleActivated);
		//return true if the close price of any of these candles crossed the line in the manner pct is set to, false if not
	}
	
	public long getCheckStartTimeS() {
		return checkStartTimeS;
	}

	public void setCheckStartTimeS(long checkStartTimeS) {
		this.checkStartTimeS = checkStartTimeS;
	}

	public long getCheckEndTimeS() {
		return checkEndTimeS;
	}

	public void setCheckEndTimeS(long checkEndTimeS) {
		this.checkEndTimeS = checkEndTimeS;
	}

	/**
	 * this is here to make identifying the triggered rule easier
	 */
	public Map<String, String> getTemplateReplacements() 
	{
		Map<String, String> res = new HashMap<String, String> ();
		res.put("name", super.getName());
		
		Candle latestC = super.getCachedCandles().get(super.getCachedCandles().size() - 1);
		res.put("latestCandleTime", Logs.log.unixTimestampStoDateStr(latestC.getStartTimeS()));
		res.put("latestCandle", latestC.toString());
		return res;
	}
}
