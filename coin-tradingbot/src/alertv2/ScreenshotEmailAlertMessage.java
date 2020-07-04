package alertv2;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import commonv2.MarketID;
import exchangev2.PSGmaInfo;
import net.hammereditor.designutilities.errors.ValueNotFoundException;
import recorderv2.PoloniexChartDescription;
import recorderv2.PoloniexChartDescription.PCDcandlePeriod;
import recorderv2.PoloniexChartDescription.PCDtimespan;

public class ScreenshotEmailAlertMessage extends EmailAlertMessage 
{
	private PoloniexScreenshotGenerator psg;
	private MarketID market;
	private PCDtimespan pcdt; 
	private PCDcandlePeriod pcdcp; 
	private File saveFolder;
	private PSGmaInfo mai;

	public ScreenshotEmailAlertMessage(String title, String content, EmailInfo esri, String templateFilename, Map<String, String> templateReplacements) throws ValueNotFoundException 
	{
		super(title, content, new FileTemplateMessageReader(templateFilename), esri, templateReplacements);
		//psg = new PoloniexScreenshotGenerator(market, new PoloniexChartDescription(pcdt, pcdcp), saveFolder);
	}
	
	public ScreenshotEmailAlertMessage(String title, String content, EmailInfo esri, Map<String, String> templateReplacements) throws ValueNotFoundException 
	{
		super(title, content, new FileTemplateMessageReader("templateScreenshot1.html"), esri, templateReplacements);
		//psg = new PoloniexScreenshotGenerator(market, new PoloniexChartDescription(pcdt, pcdcp), saveFolder);
	}
	
	public ScreenshotEmailAlertMessage(String title, String content, EmailInfo esri, String templateFilename) throws ValueNotFoundException 
	{
		this(title, content, esri, templateFilename, new HashMap<String, String> ());
		//psg = new PoloniexScreenshotGenerator(market, new PoloniexChartDescription(pcdt, pcdcp), saveFolder);
	}
	
	public ScreenshotEmailAlertMessage(String title, String content, EmailInfo esri) throws ValueNotFoundException 
	{
		this(title, content, esri, "templateScreenshot1.html");
		//psg = new PoloniexScreenshotGenerator(market, new PoloniexChartDescription(pcdt, pcdcp), saveFolder);
	}
	
	public void setPSGinfo(PCDtimespan pcdt, PCDcandlePeriod pcdcp, File saveFolder, PSGmaInfo mai)
	{
		setPSGinfo(pcdt, pcdcp, saveFolder);
		this.mai = mai;
	}
	
	public void setPSGinfo(PCDtimespan pcdt, PCDcandlePeriod pcdcp, File saveFolder)
	{
		this.pcdcp = pcdcp;
		this.pcdt = pcdt;
		this.saveFolder = saveFolder;
	}
	
	public void setPSGmarket(MarketID mk)
	{
		market = mk;
	}
	
	public void send() throws Exception
	{
		try
		{
			psg = new PoloniexScreenshotGenerator(market, new PoloniexChartDescription(pcdt, pcdcp), saveFolder);
			psg.setMAinfo(mai);
			super.putTemplateReplacement("screenshotFilename", psg.saveScreenshot().getName());
		} catch (Exception e) {
			throw new Exception("Error while obtaining graph screenshot", e);
		}
		
		super.send();
	}
}
