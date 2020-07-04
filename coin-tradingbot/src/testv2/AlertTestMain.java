package testv2;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.openqa.selenium.Dimension;

import indicatorsv2.*;
import alertv2.*;
import commonv2.*;
import commonv2.MarketID.MarketType;
import exchangev2.*;

import commonv2.Utils;
import net.hammereditor.designutilities.design.RunnableThrowableCallback;
import net.hammereditor.designutilities.errors.ValueNotFoundException;
import recorderv2.ChartFeeder;
import recorderv2.PollingCandleChartFeeder;
import recorderv2.PoloniexChartDescription;
import recorderv2.PoloniexChartDescription.PCDcandlePeriod;
import recorderv2.PoloniexChartDescription.PCDtimespan;
import rulesv2.MovingAverageDifferenceRule;
import rulesv2.MovingAverageRule;
import rulesv2.PriceRule;
import rulesv2.Rule;
import rulesv2.RuleWatcher;
import rulesv2.VolatilityRule;
import configv2.*;
import indicatorsv2.ExponentialMovingAverage;
import indicatorsv2.MovingAverage;
import indicatorsv2.SimpleMovingAverage;

public class AlertTestMain
{
	public static void main(String[] args) throws Exception 
	{
		test();
		/*long startTime = System.currentTimeMillis();
		File saveFolder = new File("\\\\ANNASERVER\\hammer-net\\images\\tradingbot/");
		MarketID market = new MarketID(ExchangeID.POLONIEX, "BTC", "MAID", MarketType.MARGIN);
		PoloniexScreenshotGenerator psg = new PoloniexScreenshotGenerator(market, 
				new PoloniexChartDescription(PCDtimespan.DAY4, PCDcandlePeriod.MINUTE15), saveFolder);
		
		File f = psg.saveScreenshot();
		System.out.println("Saved screenshot \'" + f.getName() + "\' in " + (System.currentTimeMillis() - startTime) + "ms.");*/
		
	}
	
	public static void test() throws Exception
	{
		Config.config.addDependentKey("psg_chromeDriverPath", "psg_chromeInstallationExePath", "psg_initialPageLoadWaitTimeMs"); //"psg_screenshotSaveFolder");
		loadTestConfiguration();
		PriceType pt = PriceType.CLOSE;
		final int candleTimeS = 5 * 60;
		MarketID poloniexEthBtcMargin = new MarketID(ExchangeID.POLONIEX, "BTC", "ETH", MarketType.MARGIN);
		
		List<RuleWatcher> rws = new ArrayList<RuleWatcher> ();
		rws.add(testVolatilityRW_tradingbot(candleTimeS, pt));
		//rws.add(testVolatilityRW_traditional(candleTimeS, pt));
		//rws.add(testPriceRWbull(candleTimeS, pt));
		//rws.add(testPriceRWbear(candleTimeS, pt));
		//rws.add(testMovingAverageRW(candleTimeS, pt));
		//rws.add(testMovingAverageDiffRW(candleTimeS, pt));
		
		ExchangePublic ex = new PoloniexPublic();
		ChartFeeder cf = null;
		
		cf = new PollingCandleChartFeeder(
			new RunnableThrowableCallback() {
				public void onError(Exception arg0) {
					Logs.log.printException(arg0);
				}
			}, candleTimeS, ex);
		
		int initialCandleFeedNum = Integer.MIN_VALUE;
		for (RuleWatcher rw : rws)
		{
			for (Rule r : rw.getRules())
				if (r.getCandleCacheNum() > initialCandleFeedNum)
					initialCandleFeedNum = r.getCandleCacheNum();
			cf.addConsumer(poloniexEthBtcMargin, rw);
		}
		
		cf.initialFeed(initialCandleFeedNum);
		new Thread(cf).start();
		System.out.println("Started chart feeder.");
		System.out.println("Most recent time multiple: " + Utils.getMostRecentDataIntervalTimestampMultipleMs(5 * 60));
	}
	
	private static RuleWatcher testVolatilityRW_traditional(int candleTimeS, PriceType pt) throws ValueNotFoundException
	{
		final int volRcandles = 24;
		final double volRminVol = 0.008;
		
		RuleWatcher rw = new AlertTestRuleWatcher("Test volatility rule watcher");
		Rule volR = new VolatilityRule("Short-term volatility (8C)", volRcandles, volRminVol, pt); //2% change
		rw.addRule(volR);
		//Rule priceR = new PriceRule("Short-term price change", PriceCrossType.BELOW, 0.02700, PriceType.CLOSE);
		//rw.addRule(priceR);
		
		Map<String, String> templateReplacements = new HashMap<String, String> ();
		final String title = "Tradingbot alert | High volatility in {marketID}";
		final String content = "Price volatility for last " + volRcandles + " candles (" + (candleTimeS * volRcandles) + " s.) is <b>{volatilityCurrent}</b>, which is above a <b>{volatilityMax}</b> change for this time period!";
		templateReplacements.put("iconFilename", "bearMarketIcon2.png");
		
		EmailInfo ei = new EmailInfo("Tradingbot", "nil@null.nl", "Administrator", "support@hammereditor.net", "Tradingbot alert | " + title);
		//EmailAlertMessage emailAlert = new EmailAlertMessage(title, content, ei, "templateScreenshot1.html", templateReplacements);
		ScreenshotEmailAlertMessage emailAlert = new ScreenshotEmailAlertMessage(title, content, ei, "templateScreenshot1.html", templateReplacements);
		emailAlert.setPSGinfo(PCDtimespan.HOUR24, PCDcandlePeriod.MINUTE5, new File("\\\\ANNASERVER\\hammer-net\\images\\tradingbot/"));
		rw.addAlert(emailAlert);
		return rw;
	}
	
	private static RuleWatcher testVolatilityRW_tradingbot(int candleTimeS, PriceType pt) throws Exception
	{
		final int volRcandles = 24;
		final double volRminVol = 0.008;
		
		RuleWatcher rw = new AlertTestRuleWatcher("Test volatility rule watcher");
		Rule volR = new VolatilityRule("Short-term volatility (8C)", volRcandles, volRminVol, pt); //2% change
		rw.addRule(volR);
		//Rule priceR = new PriceRule("Short-term price change", PriceCrossType.BELOW, 0.02700, PriceType.CLOSE);
		//rw.addRule(priceR);
		
		Map<String, String> templateReplacements = new HashMap<String, String> ();
		final String title = "Tradingbot alert | High volatility in {marketID}";
		final String content = "Price volatility for last " + volRcandles + " candles (" + (candleTimeS * volRcandles) + " s.) is <b>{volatilityCurrent}</b>, which is above a <b>{volatilityMax}</b> change for this time period!";
		templateReplacements.put("iconFilename", "bearMarketIcon2.png");
		
		MarketID poloniexEthBtcMargin = new MarketID(ExchangeID.POLONIEX, "BTC", "ETH", MarketType.MARGIN);
		File screenshotSaveFolder = new File("\\\\ANNASERVER\\hammer-net\\images\\tradingbot/psg/");
		PoloniexScreenshotGenerator psg = new PoloniexScreenshotGenerator(poloniexEthBtcMargin, new PoloniexChartDescription(PCDtimespan.HOUR24, PCDcandlePeriod.MINUTE5), screenshotSaveFolder);
		String screenshotFilename = psg.saveScreenshot().getName();
		templateReplacements.put("screenshotFilename", screenshotFilename);
		
		TemplateMessageReader tmrEam = new FileTemplateMessageReader("templateScreenshot1.html");
		TemplateMessageReader tmrVta = new StringTemplateMessageReader("{content}");
		File saveLocation = new File("\\\\ANNASERVER\\hammer-net\\images\\tradingbot/eas/");
		String saveLocationPublicURL = "http://192.168.1.50:8081/hammer-net/images/tradingbot/eas/"; //"http://www.hammereditor.net/images/tradingbot/"; \\\\ANNASERVER\\hammer-net\\images\\tradingbot/
		VtextEmailAlertMessage vta = new VtextEmailAlertMessage(tmrVta, new VtextInfo("6097512804", "-btc- "));
		TradingbotAlertMessage emailAlert = new TradingbotAlertMessage(title, content, tmrEam, saveLocation, saveLocationPublicURL, vta, templateReplacements);
		
		rw.addAlert(emailAlert);
		return rw;
	}
	
	private static RuleWatcher testPriceRWbull(int candleTimeS, PriceType pt) throws ValueNotFoundException
	{
		RuleWatcher rw = new AlertTestRuleWatcher("Test price rule watcher");
		
		
		Rule priceR3 = new PriceRule("Time to make money", PriceCrossType.ABOVE, 0.026, PriceType.CLOSE);
		Rule priceR4 = new PriceRule("Double top formation breached", PriceCrossType.ABOVE, 0.02535, PriceType.CLOSE);
		rw.addRule(priceR3);
		rw.addRule(priceR4);
		
		Map<String, String> templateReplacements = new HashMap<String, String> ();
		final String title = "Tradingbot alert | Price change in {marketID}";
		final String content = "{priceType} price for last candle is <b>{currentPrice}</b>, which is <b>{priceCrossType} {rate}</b> for this time period!";
		templateReplacements.put("iconFilename", "bullMarketIcon.png");
		
		EmailInfo ei = new EmailInfo("Tradingbot", "nil@null.nl", "Administrator", "support@hammereditor.net", "Tradingbot alert | " + title);
		TemplateMessageReader tmr = new FileTemplateMessageReader("templateScreenshot1.html");
		EmailAlertMessage emailAlert = new EmailAlertMessage(title, content, tmr, ei, templateReplacements);
		rw.addAlert(emailAlert);
		return rw;
	}
	
	private static RuleWatcher testPriceRWbear(int candleTimeS, PriceType pt) throws ValueNotFoundException
	{
		RuleWatcher rw = new AlertTestRuleWatcher("Test price rule watcher");
		
		Rule priceR = new PriceRule("End of June 9 rally", PriceCrossType.BELOW, 0.02475, PriceType.CLOSE);
		//Rule priceR2 = new PriceRule("Market crash!!", PriceCrossType.BELOW, 0.02425, PriceType.CLOSE);
		rw.addRule(priceR);
		//rw.addRule(priceR2);
		
		Map<String, String> templateReplacements = new HashMap<String, String> ();
		final String title = "Tradingbot alert | Price change in {marketID}";
		final String content = "{priceType} price for last candle is <b>{currentPrice}</b>, which is <b>{priceCrossType} {rate}</b> for this time period!";
		templateReplacements.put("iconFilename", "bearMarketIcon2.png");
		
		EmailInfo ei = new EmailInfo("Tradingbot", "nil@null.nl", "Administrator", "support@hammereditor.net", "Tradingbot alert | " + title);
		//EmailAlertMessage emailAlert = new EmailAlertMessage(title, content, ei, "templateScreenshot1.html", templateReplacements);
		ScreenshotEmailAlertMessage emailAlert = new ScreenshotEmailAlertMessage(title, content, ei, "templateScreenshot1.html", templateReplacements);
		emailAlert.setPSGinfo(PCDtimespan.HOUR24, PCDcandlePeriod.MINUTE5, new File("\\\\ANNASERVER\\hammer-net\\images\\tradingbot/"));
		rw.addAlert(emailAlert);
		return rw;
	}
	
	private static RuleWatcher testMovingAverageRW(int candleTimeS, PriceType pt) throws ValueNotFoundException
	{
		RuleWatcher rw = new AlertTestRuleWatcher("Test moving average rule watcher");
		MovingAverage quickMA = new ExponentialMovingAverage(5, pt);
		MovingAverage slowMA = new SimpleMovingAverage(20, pt);
		int maGreatestP = quickMA.getPeriod() > slowMA.getPeriod() ? quickMA.getPeriod() : slowMA.getPeriod();
		MovingAverageRule mar = new MovingAverageRule("Test moving average rule", maGreatestP, quickMA, slowMA);
		
		Map<String, String> templateReplacements = new HashMap<String, String> ();
		final String title = "Tradingbot alert | Moving average cross in {marketID}";
		final String content = "{quickMAdesc} ({quickMAlastRes}) and {slowMAdesc} ({slowMAlastRes}) cross result is <b>{lastCrossover}</b>!";
		//MA rule determines market icon
		rw.addRule(mar);
		
		EmailInfo ei = new EmailInfo("Tradingbot", "nil@null.nl", "Administrator", "support@hammereditor.net", "Tradingbot alert | " + title);
		//EmailAlertMessage emailAlert = new EmailAlertMessage(title, content, ei, "templateScreenshot1.html", templateReplacements);
		ScreenshotEmailAlertMessage emailAlert = new ScreenshotEmailAlertMessage(title, content, ei);
		emailAlert.setPSGinfo(PCDtimespan.HOUR24, PCDcandlePeriod.MINUTE5, new File("\\\\ANNASERVER\\hammer-net\\images\\tradingbot/"), new PSGmaInfo(20, 5, -1));
		rw.addAlert(emailAlert);
		return rw;
	}
	
	private static RuleWatcher testMovingAverageDiffRW(int candleTimeS, PriceType pt) throws ValueNotFoundException
	{
		RuleWatcher rw = new AlertTestRuleWatcher("Test moving average rule watcher");
		MovingAverage quickMA = new ExponentialMovingAverage(5, pt);
		MovingAverage slowMA = new SimpleMovingAverage(20, pt);
		int maGreatestP = quickMA.getPeriod() > slowMA.getPeriod() ? quickMA.getPeriod() : slowMA.getPeriod();
		MovingAverageDifferenceRule mar = new MovingAverageDifferenceRule("Test moving average diff. rule", maGreatestP, quickMA, slowMA, 0.006);
		
		Map<String, String> templateReplacements = new HashMap<String, String> ();
		final String title = "Tradingbot alert | Moving averages converging in {marketID}";
		final String content = "{quickMAdesc} ({quickMAlastRes}) and {slowMAdesc} ({slowMAlastRes}) are <b>{converging} to {expectedCrossover}</b>! Current difference is <b>{madp}</b>, which is under trigger diff. of {minDiff}.";
		//MA rule determines market icon
		rw.addRule(mar);
		
		EmailInfo ei = new EmailInfo("Tradingbot", "nil@null.nl", "Administrator", "support@hammereditor.net", "Tradingbot alert | " + title);
		//EmailAlertMessage emailAlert = new EmailAlertMessage(title, content, ei, "templateScreenshot1.html", templateReplacements);
		ScreenshotEmailAlertMessage emailAlert = new ScreenshotEmailAlertMessage(title, content, ei);
		emailAlert.setPSGinfo(PCDtimespan.HOUR24, PCDcandlePeriod.MINUTE5, new File("\\\\ANNASERVER\\hammer-net\\images\\tradingbot/"), new PSGmaInfo(20, 5, -1));
		rw.addAlert(emailAlert);
		return rw;
	}
	
	public static void loadTestConfiguration()
	{
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		Config.config.put("log_logFilePath", "C:/tradingbot/test.log");
		Config.config.put("log_level", 5);
		Config.config.put("log_writeToFile", false);
		Config.config.put("localTimeDiffFromUTCmin", -4 * 60);
		
		Config.config.put("psg_chromeDriverPath", "C:/Users/Hammereditor/workspace/chromedriver.exe");
		Config.config.put("psg_chromeInstallationExePath", "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe");
		Config.config.put("psg_initialPageLoadWaitTimeMs", 2500);
		Config.config.put("psg_defaultChromeWindowSize", new Dimension(1680, 1050));
		//Config.config.put("psg_movingAverageInfoDefault", new PSGmaInfo(20, 5, -1));
		
		//Config.config.put("pp_utcTimeOffsetFromLocalMin", 4 * 60);
		Config.config.put("pp_returnOnlyClosedCandles", true);
		Config.config.put("bp_returnOnlyClosedCandles", true);
		
		Config.config.put("pccf_waitAfterInitialFeed", false);
		Config.config.put("bptf_waitAfterInitialFeed", false);
		Config.config.put("eam_templateMessageFolder", "C:/Users/Hammereditor/workspace/Git/tradingbot/v2/alertv2/resources/");
		
		Config.config.put("db_hostname", "AnnaServer");
		Config.config.put("db_port", 3306);
		Config.config.put("db_username", "root");
		Config.config.put("db_password", "!s31bR");
		Config.config.put("db_schema", "tradingbot");
		
	}
}
