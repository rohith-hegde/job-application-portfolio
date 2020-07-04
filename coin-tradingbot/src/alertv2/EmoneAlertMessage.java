package alertv2;

import java.io.File;
import java.util.Map;

public class TradingbotAlertMessage extends SavedWebpageAlertMessage
{
	private VtextEmailAlertMessage vta;
	private String saveLocationFolderPublicURL;

	/**
	 * 
	 * @param title whatever text you want to replace any '{title}' brackets with
	 * @param content whatever text you want to replace any '{content}' brackets with
	 * @param tmr this provides the HTML code of the webpage that the mobile user will access
	 * @param saveLocation the file path to save the webpage to. will get overwritten every time this alert is activated
	 * @param saveLocationPublicURL the publicly accessible HTTP URL for the saved webpage
	 * @param vta the mobile text message alert to be sent to the tradingbot service
	 * @param templateReplacements
	 */
	public TradingbotAlertMessage(String title, String content, TemplateMessageReader tmr, File saveLocation, String saveLocationPublicURL, VtextEmailAlertMessage vta, Map<String, String> templateReplacements) 
	{
		super(title, content, tmr, saveLocation, templateReplacements);
		this.saveLocationFolderPublicURL = saveLocationPublicURL;
		this.vta = vta;
	}
	
	/**
	 * 
	 * @param title whatever text you want to replace any '{title}' brackets with
	 * @param content whatever text you want to replace any '{content}' brackets with
	 * @param tmr this provides the HTML code of the webpage that the mobile user will access
	 * @param saveLocation the file path to save the webpage to. will get overwritten every time this alert is activated
	 * @param saveLocationPublicURL the publicly accessible HTTP URL for the saved webpage
	 * @param vta the mobile text message alert to be sent to the tradingbot service
	 */
	public TradingbotAlertMessage(String title, String content, TemplateMessageReader tmr, File saveLocation, String saveLocationPublicURL, VtextEmailAlertMessage vta) 
	{
		super(title, content, tmr, saveLocation);
		this.saveLocationFolderPublicURL = saveLocationPublicURL;
		this.vta = vta;
	}
	
	public void sendHTMLmessage() throws Exception
	{
		super.sendHTMLmessage();
		vta.putTemplateReplacement("content", saveLocationFolderPublicURL + super.getLastSavedFilename());
		vta.send();
	}
}
