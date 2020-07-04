package rulesv2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alertv2.RuleTemplateReplacements;
import commonv2.Candle;
import commonv2.ExchangeID;
import commonv2.MarketID;
import commonv2.MarketID.MarketType;
import configv2.Logs;
import exchangev2.PoloniexPublic;
import testv2.AlertTestMain;
import indicatorsv2.ExponentialMovingAverage;
import indicatorsv2.MovingAverage;
import indicatorsv2.PriceType;
import indicatorsv2.SimpleMovingAverage;

public class MovingAverageRule extends Rule implements RuleTemplateReplacements
{
	private MovingAverage quickMA;
	private MovingAverage slowMA;
	private Crossover lastCrossover;
	private double quickMAlastRes, slowMAlastRes;
	
	protected enum Crossover { SLOWUNDERQUICK /*buy*/, QUICKUNDERSLOW /*sell*/ } 
	
	public MovingAverageRule(String name, int candleCacheNum, MovingAverage quickMA, MovingAverage slowMA)
	{
		super(name, candleCacheNum);
		this.quickMA = quickMA;
		this.slowMA = slowMA;
		lastCrossover = null;
		quickMAlastRes = 0;
		slowMAlastRes = 0;
	}
	
	public void debugMAs()
	{
		List<Candle> cachedCandles = new ArrayList<Candle>(super.getCachedCandles());
		double slowMAres = slowMA.getMovingAverage(cachedCandles);
		double quickMAres = quickMA.getMovingAverage(cachedCandles);
		
		System.out.println("slowMAres: " + slowMAres + " quickMAres: " + quickMAres);
		//System.out.println(cachedCandles);
	}

	public SignedRuleResult checkRule()
	{
		List<Candle> cachedCandles = new ArrayList<Candle>(super.getCachedCandles());
		double slowMAres = slowMA.getMovingAverage(cachedCandles);
		double quickMAres = quickMA.getMovingAverage(cachedCandles);
		slowMAlastRes = slowMAres;
		quickMAlastRes = quickMAres;
		
		//System.out.println("slowMAres: " + slowMAres + " quickMAres: " + quickMAres);
		Crossover c = (slowMAres < quickMAres ? Crossover.SLOWUNDERQUICK : Crossover.QUICKUNDERSLOW);
		SignedRuleResult res;
		
		if (lastCrossover != c && lastCrossover != null) //time to make decision. if this is the first time, will be null
		{
			boolean bullish = (c == Crossover.SLOWUNDERQUICK ? true : false);
			res = new SignedRuleResult(true, bullish);
		}
		else
			res = new SignedRuleResult(false, false);
		//res = new SignedRuleResult(true, true);
		
		lastCrossover = c;
		if (res.isRuleActivated())
			System.out.println("slowMAres: " + slowMAres + " quickMAres: " + quickMAres);
		return res;
	}
	
	public MovingAverage getQuickMA() {
		return quickMA;
	}

	public MovingAverage getSlowMA() {
		return slowMA;
	}
	
	public Crossover getLastCrossover()
	{
		return lastCrossover;
	}
	
	public static void main(String[] args) throws Exception
	{
		/*
		 * If MA is 120 candles, and total candles fed is under that (right after MainClass restarts) should old data be fed first?
		 * Test process: find a chart time window where initially S under Q (bull), then S crosses over Q (bear), then S is back under Q (bull). 
		 * feed 120 or so old candles. There are 5 test cases. Make sure bullish boolean and ruleActivated are correct
		 * 
		 * 
		 * SMA WORKS BUT NOT EMA
		 */
		
		//testRuleCache();
		testRule();
		
		
	}
	
	public static void testRule() throws Exception
	{
		AlertTestMain.loadTestConfiguration();
		PoloniexPublic polo = new PoloniexPublic();
		MovingAverage emaS = new SimpleMovingAverage(60, PriceType.CLOSE);
		MovingAverage emaQ = new ExponentialMovingAverage(20, PriceType.CLOSE);
		
		int candleTimeS = 5 * 60;
		long endTimeMs = System.currentTimeMillis(); //- (2 * 24 * 3600 * 1000); //- (12 * 3600 * 1000);
		long startTimeMs = endTimeMs - (1 * 24 * 3600 * 1000); //g
		System.out.println("Start time to end time difference: " + (endTimeMs - startTimeMs) + " ms.");
		
		MarketID market = new MarketID(ExchangeID.POLONIEX, "BTC", "ETH", MarketType.MARGIN);
		MovingAverageRule mar = new MovingAverageRule("20x5min. EMA and 120x5min. EMA", emaS.getPeriod(), emaQ, emaS);
		List<Candle> cl = polo.getCandlestickHistory(startTimeMs - (1 * emaS.getPeriod() * candleTimeS * 1000), endTimeMs, candleTimeS * 1000, market);
		
		List<Candle> initialCL = new ArrayList<Candle> ();
		int period = emaS.getPeriod();
		for (int i = 0; i < 1 * period; i++)
			initialCL.add(cl.get(i));
		mar.feedChartData(initialCL);
		
		SignedRuleResult ruleRes = new SignedRuleResult(false, false);
		int maPrintPeriod = 30;
		
		for (int i = period; i < cl.size(); i++)
		{
			Candle c = cl.get(i);
			List<Candle> tempL = new ArrayList<Candle> ();
			tempL.add(c);
			mar.feedChartData(tempL);

			SignedRuleResult newRuleRes = mar.checkRule();
			//System.out.println(newRuleRes);
			if (newRuleRes.isRuleActivated()) //!newRuleRes.equals(ruleRes) )
			{
				String cTime = Logs.log.unixTimestampStoDateStr(c.getEndTimeS());
				System.out.println("Rule status changed to: " + newRuleRes + " at " + cTime);
			}
				
			/*if (i % maPrintPeriod == 0 && i >= maPrintPeriod)
			{
				//mar.debugMAs();
				System.out.println("Current candle close time: " + Logs.log.unixTimestampStoDateStr(c.getEndTimeS()));
				System.out.println();
			}*/
			ruleRes = newRuleRes;
		}
		
		/*System.out.println("Slow under quick correct result: bull");
		mar.feedChartData(cl);
		System.out.println(mar.checkRule().toString());
		
		System.out.println("Quick under slow correct result: bear");
		cl = polo.getCandlestickHistory(startTimeMs + (12 * 3600 * 1000), endTimeMs + (12 * 3600 * 1000), candleTimeS * 1000, market);
		mar.feedChartData(cl);
		System.out.println(mar.checkRule().toString());
		
		System.out.println("Slow under quick correct result: bull");
		cl = polo.getCandlestickHistory(startTimeMs + (14 * 3600 * 1000), endTimeMs + (14 * 3600 * 1000), candleTimeS * 1000, market);
		mar.feedChartData(cl);
		System.out.println(mar.checkRule().toString());
		
		System.out.println("Slow under quick correct result: bull");
		cl = polo.getCandlestickHistory(startTimeMs + (21 * 3600 * 1000), endTimeMs + (21 * 3600 * 1000), candleTimeS * 1000, market);
		mar.feedChartData(cl);
		System.out.println(mar.checkRule().toString());*/
	}
	
	public static void testRuleCache() throws Exception
	{
		AlertTestMain.loadTestConfiguration();
		PoloniexPublic polo = new PoloniexPublic();
		MovingAverage emaS = new ExponentialMovingAverage(6, PriceType.CLOSE);
		MovingAverage emaQ = new ExponentialMovingAverage(4, PriceType.CLOSE);
		
		
		int candleTimeS = 30 * 60;
		long endTimeMs = System.currentTimeMillis(); //- (2 * 24 * 3600 * 1000); //- (12 * 3600 * 1000);
		long startTimeMs = endTimeMs - (2 * 24 * 3600 * 1000); //
		System.out.println("Start time to end time difference: " + (endTimeMs - startTimeMs) + " ms.");
		
		MarketID market = new MarketID(ExchangeID.POLONIEX, "BTC", "ETH", MarketType.MARGIN);
		MovingAverageRule mar = new MovingAverageRule("20x5min. EMA and 120x5min. EMA", emaS.getPeriod(), emaQ, emaS);
		List<Candle> cl = polo.getCandlestickHistory(startTimeMs - (1 * emaS.getPeriod() * candleTimeS * 1000), endTimeMs, candleTimeS * 1000, market);
		
		List<Candle> initialCL = new ArrayList<Candle> ();
		/*for (int i = 0; i < 1 * emaS.getPeriod(); i++)
			initialCL.add(cl.get(i));
		mar.feedChartData(initialCL);
		*/
		for (int i = 0; i < cl.size(); i++)
		{
			Candle c = cl.get(i);
			List<Candle> tempL = new ArrayList<Candle> ();
			tempL.add(c);
			mar.feedChartData(tempL);
			mar.checkRule();
		}
	}
	
	public Map<String, String> getTemplateReplacements() 
	{
		HashMap<String, String> r = new HashMap<String, String> ();
		r.put("quickMA", "" + quickMA.toString());
		r.put("slowMA", "" + slowMA.toString());
		r.put("quickMAdesc", "" + quickMA.description());
		r.put("slowMAdesc", "" + slowMA.description());
		r.put("slowMAlastRes", "" + Logs.changeDecimalPlaces(slowMAlastRes, 7));
		r.put("quickMAlastRes", "" + Logs.changeDecimalPlaces(quickMAlastRes, 7));
		r.put("lastCrossover",  "" + (lastCrossover == null ? null : lastCrossover.name()));
		//r.put("lastCrossoverShortened",  "" + lastCrossover.name().replaceFirst("SLOW", "").replaceFirst("QUICK", ""));
		r.put("iconFilename", lastCrossover == Crossover.SLOWUNDERQUICK ? "bullMarketIcon.png" : "bearMarketIcon2.png");
		return r;
	}
}
