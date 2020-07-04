package alertv2;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import commonv2.MarketID;
import configv2.Config;
import configv2.Logs;
import exchangev2.PSGmaInfo;
import net.hammereditor.designutilities.errors.ValueNotFoundException;
import recorderv2.PoloniexChartDescription;

public class PoloniexScreenshotGenerator 
{
	private String screenshotFilename;
	private MarketID market;
	private PoloniexChartDescription pcd;
	private File screenshotSaveFolder;
	private Dimension windowSize;
	private PSGmaInfo maInfo;
	
	public PoloniexScreenshotGenerator(MarketID market, PoloniexChartDescription pcd, File screenshotSaveFolder) throws ValueNotFoundException
	{
		this(market, pcd, screenshotSaveFolder, (Dimension)Config.config.get("psg_defaultChromeWindowSize"));
	}
	
	public PoloniexScreenshotGenerator(MarketID market, PoloniexChartDescription pcd, File screenshotSaveFolder, Dimension windowSize) throws ValueNotFoundException
	{
		this.market = market;
		this.pcd = pcd;
		this.screenshotSaveFolder = screenshotSaveFolder;
		this.screenshotFilename = null;
		this.windowSize = windowSize;
		this.maInfo = null; //(PSGmaInfo)Config.config.get("psg_movingAverageInfoDefault");
	}
	
	public PoloniexScreenshotGenerator(MarketID market, PoloniexChartDescription pcd, File screenshotSaveFolder, String screenshotFilename) throws ValueNotFoundException
	{
		this(market, pcd, screenshotSaveFolder);
		this.screenshotFilename = screenshotFilename;
	}
	
	public PoloniexScreenshotGenerator(MarketID market, PoloniexChartDescription pcd, File screenshotSaveFolder, String screenshotFilename, Dimension windowSize) throws ValueNotFoundException
	{
		this(market, pcd, screenshotSaveFolder, windowSize);
		this.screenshotFilename = screenshotFilename;
	}
	
	public void setMAinfo(PSGmaInfo mai)
	{
		this.maInfo = mai;
	}
	
	public File saveScreenshot() throws Exception
	{
		final String link = "https://poloniex.com/" + (market.getMarketType() == MarketID.MarketType.MARGIN ? "marginTrading" : "exchange") + "#" + market.getMasterCurrency() + "_" + market.getTradeCurrency();
	    //final File screenShot = new File("C:/tradingbot/screenshot.png").getAbsoluteFile();
		System.setProperty("webdriver.chrome.driver", (String)Config.config.get("psg_chromeDriverPath"));
		WebDriver driver = null;
		
		try
		{
			ChromeOptions options = new ChromeOptions();
		    //options.addArguments("ash-host-window-bounds", "0+0-1920x1080");
		    //options.addArguments("start-fullscreen");
		    Logs.log.debug("PoloniexScreenshotGenerator.saveScreenshot(): creating Chrome driver instance...");
		    options.setBinary((String)Config.config.get("psg_chromeInstallationExePath"));
		    driver = new ChromeDriver(options);
		    
		    Logs.log.debug("PoloniexScreenshotGenerator.saveScreenshot(): opening webpage \'" + link + "\'...");
		    driver.get(link);
		    driver.manage().window().setSize(windowSize);
		    Thread.sleep((int)Config.config.get("psg_initialPageLoadWaitTimeMs"));
		}
		catch (Exception e) {
			throw new Exception("Error while initially loading webpage", e);
		}
		
		try
		{
			Logs.log.debug("PoloniexScreenshotGenerator.saveScreenshot(): selecting time options...");
			WebElement chartLoadingEle = driver.findElement(By.id("chartLoading"));
		    waitUntilElementNotDisplayed(chartLoadingEle, 100, 100);
		    
		    String zoomButtonId = "";
		    String candlePeriodButtonId = "";
		    
		    switch (pcd.getPcdt())
		    {
		    	case MONTH1: zoomButtonId = "zoom744"; break;
		    	case WEEK2: zoomButtonId = "zoom336"; break;
		    	case WEEK1: zoomButtonId = "zoom168"; break;
		    	case DAY4: zoomButtonId = "zoom96"; break;
		    	case DAY2: zoomButtonId = "zoom48"; break;
		    	case HOUR24: zoomButtonId = "zoom24"; break;
		    	case HOUR6: zoomButtonId = "zoom6"; break;
		    }
		    
		    switch (pcd.getPcdcp())
		    {
		    	case DAY1: candlePeriodButtonId = "chartButton86400"; break;
		    	case HOUR4: candlePeriodButtonId = "chartButton14400"; break;
		    	case HOUR2: candlePeriodButtonId = "chartButton7200"; break;
		    	case MINUTE30: candlePeriodButtonId = "chartButton1800"; break;
		    	case MINUTE15: candlePeriodButtonId = "chartButton900"; break;
		    	case MINUTE5: candlePeriodButtonId = "chartButton300"; break;
		    }
		      
		    driver.findElement(By.id(zoomButtonId)).click();
		    TimeUnit.MILLISECONDS.sleep(50);
		    waitUntilElementNotDisplayed(chartLoadingEle, 100, 100);
		    driver.findElement(By.id(candlePeriodButtonId)).click();
		    TimeUnit.MILLISECONDS.sleep(50);
		    waitUntilElementNotDisplayed(chartLoadingEle, 100, 100);
		    
		    if (maInfo != null)
		    {
			    WebElement chartSettingsIcon = driver.findElement(By.className("fa-cog"));
			    chartSettingsIcon.click();
			    Thread.sleep(200);
			    
			    if (maInfo.getPeriod_simpleMa() > -1)
			    {
				    WebElement smaTextbox = driver.findElement(By.id("smaPeriod"));
				    if (smaTextbox.getText().isEmpty() || Integer.parseInt(smaTextbox.getText()) != maInfo.getPeriod_simpleMa())
				    {
					    smaTextbox.click();
					    smaTextbox.clear();
					    smaTextbox.sendKeys("" + maInfo.getPeriod_simpleMa());
				    }
			    }
			    else
			    {
			    	WebElement smaCheckbox = driver.findElement(By.id("smaCheckbox"));
			    	smaCheckbox.click();
			    }
			    
			    Thread.sleep(100);
			    
			    if (maInfo.getPeriod_expMa1() > -1)
			    {
				    WebElement emaTextbox = driver.findElement(By.id("emaPeriod"));
				    if (emaTextbox.getText().isEmpty() || Integer.parseInt(emaTextbox.getText()) != maInfo.getPeriod_simpleMa())
				    {
				    	emaTextbox.click();
				    	emaTextbox.clear();
				    	emaTextbox.sendKeys("" + maInfo.getPeriod_expMa1());
				    }
			    }
			    else
			    {
			    	WebElement emaCheckbox = driver.findElement(By.id("emaCheckbox"));
			    	emaCheckbox.click();
			    }
			    
			    Thread.sleep(100);
			    
			    if (maInfo.getPeriod_expMa2() > -1)
			    {
			    	WebElement ema2Checkbox = driver.findElement(By.id("ema2Checkbox"));
			    	ema2Checkbox.click();
				    WebElement ema2Textbox = driver.findElement(By.id("ema2Period"));
				    
				    if (ema2Textbox.getText().isEmpty() || Integer.parseInt(ema2Textbox.getText()) != maInfo.getPeriod_simpleMa())
				    {
					    ema2Textbox.click();
					    ema2Textbox.clear();
					    ema2Textbox.sendKeys("" + maInfo.getPeriod_expMa2());
				    }
			    }
			    else
			    {
			    	//this box is already unchecked by default, so do nothing
			    }
			    
			    Thread.sleep(100);
				    
			    chartSettingsIcon.click();
			    Thread.sleep(1000);
		    }
		}
		catch (Exception e) {
			throw new Exception("Error while selecting time options", e);
		}
		
		byte[] imgRaw = null;
		Rectangle ccePos = null;
		Logs.log.debug("PoloniexScreenshotGenerator.saveScreenshot(): capturing screenshot...");
		
		try
		{
		    WebElement chartContainerEle = driver.findElement(By.id("canvasContainer"));
		    Dimension cceSize = chartContainerEle.getSize();
		    Point cceLoc = chartContainerEle.getLocation();
		    ccePos = new Rectangle(cceLoc, cceSize);
		    TakesScreenshot cceTs = (TakesScreenshot)driver;
		    
		    imgRaw = cceTs.getScreenshotAs(OutputType.BYTES);
		    driver.close();
		    Runtime.getRuntime().exec("taskkill /IM chromedriver.exe /F");
		     
		    InputStream in = new ByteArrayInputStream(imgRaw);
		    BufferedImage bImageFromConvert = ImageIO.read(in);
		    bImageFromConvert = crop(bImageFromConvert, ccePos);
		}
		catch (Exception e) {
			throw new Exception("Error while saving screenshot to array", e);
		}
		
		try 
		{
			Date someDate = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
			String s = df.format(someDate);
			if (screenshotFilename == null)
				screenshotFilename = market.getMasterCurrency() + "_" + market.getTradeCurrency() + "_" + market.getMarketType() + "-" + (System.currentTimeMillis() / 1000) + "-" + s + ".png";
			
			File saveFile = new File(screenshotSaveFolder + "/" + screenshotFilename);
			if (!saveFile.exists())
			{
				saveFile.mkdirs();
				saveFile.createNewFile();
			}
			
			InputStream in = new ByteArrayInputStream(imgRaw);
			BufferedImage bImageFromConvert = ImageIO.read(in);
			bImageFromConvert = crop(bImageFromConvert, ccePos);
			ImageIO.write(bImageFromConvert, "png", saveFile);
			
			return saveFile;
		} 
		catch (Exception e) {
			throw new Exception("Error while writing screenshot to file", e);
		}
	}
	
	/* 
	 * MAKE THIS AUTOMATICALLY WAIT FOR GRAPH TO LOAD ON PAGE LOAD, WITHOUT DELAY
	 * 
	 * */
	
	public static void main(String[] args) throws Exception
	{
		long startMs = System.currentTimeMillis();
		final String link = "https://poloniex.com/marginTrading#btc_maid";
	    final File screenShot = new File("C:/tradingbot/screenshot.png").getAbsoluteFile();

	    System.setProperty("webdriver.chrome.driver", "C:\\Users\\Hammereditor\\workspace\\chromedriver.exe");
	    ChromeOptions options = new ChromeOptions();
	    options.addArguments("ash-host-window-bounds", "0+0-1920x1080");
	    options.addArguments("start-fullscreen");
	    
	    System.out.println("Creating Firefox Driver");
	    options.setBinary("C:/Program Files (x86)/Google/Chrome/Application/chrome.exe");
	    final WebDriver driver = new ChromeDriver(options);
	    
	   // try {
	    	System.out.println("Opening page: " + link);
	      driver.get(link);

	      System.out.println("Wait a bit for the page to render");
	      TimeUnit.SECONDS.sleep(2);
	      
	      WebElement chartLoadingEle = driver.findElement(By.id("chartLoading"));
	      waitUntilElementNotDisplayed(chartLoadingEle, 100, 80);
	      
	      driver.findElement(By.id("zoom24")).click();
	      TimeUnit.MILLISECONDS.sleep(50);
	      waitUntilElementNotDisplayed(chartLoadingEle, 100, 30);
	      driver.findElement(By.id("chartButton300")).click();
	      TimeUnit.MILLISECONDS.sleep(50);
	      waitUntilElementNotDisplayed(chartLoadingEle, 100, 100);
	      
	      System.out.println("Taking Screenshot");
	      WebElement chartContainerEle = driver.findElement(By.id("canvasContainer"));
	      Dimension cceSize = chartContainerEle.getSize();
	      Point cceLoc = chartContainerEle.getLocation();
	      Rectangle ccePos = new Rectangle(cceLoc, cceSize);
	      TakesScreenshot cceTs = (TakesScreenshot)driver;
	      
	      byte[] imgRaw = cceTs.getScreenshotAs(OutputType.BYTES);
	      driver.close();
	      Runtime.getRuntime().exec("taskkill /IM chromedriver.exe /F");
	     
	      InputStream in = new ByteArrayInputStream(imgRaw);
	      BufferedImage bImageFromConvert = ImageIO.read(in);
	      bImageFromConvert = crop(bImageFromConvert, ccePos);
	      
	      //final File outputFile = chartContainerEle.getScreenshotAs(OutputType.FILE); //((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	      ImageIO.write(bImageFromConvert, "png", screenShot);
	      long time = System.currentTimeMillis() - startMs;
	      System.out.println("Screenshot saved: " + screenShot + ". time: " + time + " ms.");
	      
	    //}+
	}
	
	private static void waitUntilElementNotDisplayed(WebElement ele, int timeIncrementMs, int maxTries)
	{
		boolean eled = true;
		int t = 1;
		
		while (eled && t <= maxTries)
		{
			//eled = ele.isDisplayed();
			eled = !ele.getCssValue("display").equalsIgnoreCase("none");
			t++;
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("waitUntilElementNotDisplayed(): waited " + (t * timeIncrementMs) + " ms.");
	}
	
	public static BufferedImage crop(BufferedImage src, Rectangle rect)
	{
	    BufferedImage dest = new BufferedImage(rect.getWidth(), rect.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
	    Graphics g = dest.getGraphics();
	    g.drawImage(src, 0, 0, rect.getWidth(), rect.getHeight(), rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), null);
	    g.dispose();
	    return dest;
	}
	
	

}
