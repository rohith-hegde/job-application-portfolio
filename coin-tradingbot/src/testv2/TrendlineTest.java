package testv2;

import java.util.List;
import java.util.TimeZone;

import alertv2.AlertMessage;
import alertv2.ConsoleAlertMessage;
import commonv2.Candle;
import commonv2.ExchangeID;
import commonv2.MarketID;
import commonv2.MarketID.MarketType;
import configv2.Config;
import configv2.Logs;
import exchangev2.PoloniexPublic;
import indicatorsv2.PriceCrossType;
import indicatorsv2.PriceType;
import indicatorsv2.Trendline;
import indicatorsv2.TrendlinePoint;
import net.hammereditor.designutilities.design.RunnableThrowableCallback;
import net.hammereditor.designutilities.errors.ValueNotFoundException;
import recorderv2.ChartFeeder;
import rulesv2.Rule;
import rulesv2.RuleWatcher;
import rulesv2.TrendlineRule;


public class TrendlineTest 
{
	public static void main(String[] args) throws Exception
	{
		loadTestConfiguration();
		MarketID poloniexEthBtcMargin = new MarketID(ExchangeID.POLONIEX, "BTC", "ETH", MarketType.MARGIN);
		PoloniexPublic exchange = new PoloniexPublic();
		
		//get the pre-recorded exchange data
		final long july9_0400_utc = 1468036800000L;
		//final long july11_0400_utc = 1468209600000l;
		final long july11_0800_utc = 1468209600000L + (4 * 60 * 60 * 1000);
		
		final long candlePeriodS = 30 * 60;
		List<Candle> chartData = exchange.getCandlestickHistory(july9_0400_utc, july11_0800_utc, candlePeriodS * 1000, poloniexEthBtcMargin);
		
		//define the trendlines
		Trendline line_oneT = getLine(chartData, "oneT", poloniexEthBtcMargin);
		Trendline line_oneB = getLine(chartData, "oneB", poloniexEthBtcMargin);
		Trendline line_twoT = getLine(chartData, "twoT", poloniexEthBtcMargin);
		Trendline line_twoB = getLine(chartData, "twoB", poloniexEthBtcMargin);
		Trendline line_threeB = getLine(chartData, "threeB", poloniexEthBtcMargin);
		
		//define the rules
		Rule lineCrossed_oneT = new TrendlineRule("Trendline oneT crosses upward", 1, line_oneT, PriceCrossType.ABOVE, 1468099800, 1468144800);
		Rule lineCrossed_oneB = new TrendlineRule("Trendline oneB crosses downward", 1, line_oneB, PriceCrossType.BELOW, 1468099800, 1468114200 + (4 * 30 * 60));
		Rule lineCrossed_twoT = new TrendlineRule("Trendline twoT crosses upward", 1, line_twoT, PriceCrossType.ABOVE, 1468144800, 1468188000);
		Rule lineCrossed_twoB = new TrendlineRule("Trendline twoB crosses downward", 1, line_twoB, PriceCrossType.BELOW, 1468153800, 1468188000);
		Rule lineCrossed_threeB = new TrendlineRule("Trendline threeB crosses downward", 1, line_threeB, PriceCrossType.BELOW, 1468099800, 1468188000);
		
		//create the rule watcher and alert message
		RuleWatcher testRW = new TrendlineJobTestRuleWatcher("Trendline job test rule watcher");
		AlertMessage alert = new ConsoleAlertMessage("Rule triggered", "Rule \'{name}\' was activated at time {latestCandleTime} by candle \'{latestCandle}\'!");
		testRW.addAlert(alert);
		
		testRW.addRule(lineCrossed_oneT);
		testRW.addRule(lineCrossed_oneB);
		testRW.addRule(lineCrossed_twoT);
		testRW.addRule(lineCrossed_twoB);
		testRW.addRule(lineCrossed_threeB);
		
		//create testCF, which feeds the rules the pre-recorded candles
		ChartFeeder<Candle> testCF = new TrendlineJobTestChartFeeder(new RunnableThrowableCallback() {
			public void onError(Exception arg0) {
				Logs.log.error("trendlinerule job test chart feeder error: " + arg0.getMessage());
				Logs.log.printException(arg0);
			}
		}, (int)candlePeriodS, chartData);
		
		//determine how many candles to initially feed
		int initialCandleFeedNum = Integer.MIN_VALUE;
		for (Rule r : testRW.getRules()) 
			if (r.getCandleCacheNum() > initialCandleFeedNum)
				initialCandleFeedNum = r.getCandleCacheNum();
		testCF.addConsumer(poloniexEthBtcMargin, testRW);
		
		//start the test
		testCF.initialFeed(initialCandleFeedNum);
		Logs.log.info("trendlinerule job test started");
		new Thread(testCF).start();
		
		//now watch for 'Rule activated' messages in the console.
		//CORRECT TEST RESULT: 
			//lineCrossed_oneT is activated at Jul 10 09:30
			//lineCrossed_oneB is activated at Jul 10 03:00
			//lineCrossed_twoT is NOT activated
			//lineCrossed_twoB is NOT activated
	}
	
	private static Trendline getLine(List<Candle> chartData, String name, MarketID market) throws ValueNotFoundException
	{
		Candle startPointC = null, endPointC = null;
		PriceType startPointPriceType = null, endPointPriceType = null;
		final long timeOffsetS = 0 * 60 * 60 * -1; //(int)Config.config.get("localTimeDiffFromUTCmin") * 60;
		//System.out.println("offset: " + timeOffsetS);
		
		switch (name)
		{
			case "oneT":
				startPointC = getCandleFromList(chartData, 1468099800 - timeOffsetS);
				endPointC = getCandleFromList(chartData, 1468132200 - timeOffsetS);
				startPointPriceType = PriceType.HIGH;
				endPointPriceType = PriceType.HIGH;
				break;
				
			case "oneB":
				startPointC = getCandleFromList(chartData, 1468101600 - timeOffsetS);
				endPointC = getCandleFromList(chartData, 1468114200 - timeOffsetS);
				//endPointC = getCandleFromList(chartData, 1468114200 + (3 * 30 * 60) - timeOffsetS);
				startPointPriceType = PriceType.LOW;
				endPointPriceType = PriceType.OPEN;
				break;
				
			case "twoT":
				startPointC = getCandleFromList(chartData, 1468144800 - timeOffsetS);
				endPointC = getCandleFromList(chartData, 1468168200 - timeOffsetS);
				startPointPriceType = PriceType.HIGH;
				endPointPriceType = PriceType.HIGH;
				break;
				
			case "twoB":
				startPointC = getCandleFromList(chartData, 1468153800 - timeOffsetS);
				endPointC = getCandleFromList(chartData, 1468188000 - timeOffsetS);
				startPointPriceType = PriceType.LOW;
				endPointPriceType = PriceType.LOW;
				break;
				
			case "threeB":
				startPointC = getCandleFromList(chartData, 1468101600 - timeOffsetS);
				endPointC = getCandleFromList(chartData, 1468114200 + (3 * 30 * 60) - timeOffsetS);
				//endPointC = getCandleFromList(chartData, 1468114200 + (3 * 30 * 60) - timeOffsetS);
				startPointPriceType = PriceType.LOW;
				endPointPriceType = PriceType.CLOSE;
				break;
		}
		
		//System.out.println("Test.java line 125- " + startPointC);
		//System.out.println("Test.java line 126- " + endPointC);
		
		TrendlinePoint startPoint = new TrendlinePoint(startPointC, startPointPriceType);
		TrendlinePoint endPoint = new TrendlinePoint(endPointC, endPointPriceType);
		return new Trendline(startPoint, endPoint);
	}
	
	 private static Candle getCandleFromList(List<Candle> chartData, long startTimeS) throws ValueNotFoundException
	 {
		 final long timeOffsetS = (int)Config.config.get("localTimeDiffFromUTCmin") * 60 * 0;
		 for (Candle c : chartData)
		 {
			 if (c.getStartTimeS() == startTimeS - timeOffsetS)
				 return c;
		 }
		 
		 Logs.log.warning("Test.getCandleFromList(): no candle found for time \'" + startTimeS + " / " + Logs.log.unixTimestampStoDateStr(startTimeS) + "\'");
		 return null; //not found
	 }

	
	public static void loadTestConfiguration()
	{
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		Config.config.put("log_logFilePath", "C:/tradingbot/test.log");
		Config.config.put("log_level", 5);
		Config.config.put("log_writeToFile", false);
		Config.config.put("localTimeDiffFromUTCmin", 0 * 60);
		
		//Config.config.put("psg_chromeDriverPath", "C:/Users/Hammereditor/workspace/chromedriver.exe");
		//Config.config.put("psg_chromeInstallationExePath", "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe");
		//Config.config.put("psg_initialPageLoadWaitTimeMs", 2500);
		//Config.config.put("psg_defaultChromeWindowSize", new Dimension(1680, 1050));
		//Config.config.put("psg_movingAverageInfoDefault", new PSGmaInfo(20, 5, -1));
		
		Config.config.put("pp_utcTimeOffsetFromLocalMin", 0 * 60);
		Config.config.put("pp_returnOnlyClosedCandles", true);
		
		//Config.config.put("pccf_waitAfterInitialFeed", false);
		//Config.config.put("bptf_waitAfterInitialFeed", false);
		//Config.config.put("eam_templateMessageFolder", "C:/Users/Hammereditor/workspace/Git/tradingbot/v2/alertv2/resources/");
		
		/*Config.config.put("db_hostname", "AnnaServer");
		Config.config.put("db_port", 3306);
		Config.config.put("db_username", "root");
		Config.config.put("db_password", "<censored>");
		Config.config.put("db_schema", "tradingbot");*/
	}
}
