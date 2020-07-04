package alertv2;

import java.io.File;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import commonv2.Utils;
import configv2.Logs;

public class SavedWebpageAlertMessage extends HTMLalertMessage 
{
	private File saveLocationFolder;
	private String lastSavedFilename;
	
	public SavedWebpageAlertMessage(String title, String content, TemplateMessageReader tmr, File saveLocation)
	{
		super(title, content, tmr);
		this.saveLocationFolder = saveLocation;
		this.lastSavedFilename = "";
	}
	
	public SavedWebpageAlertMessage(String title, String content, TemplateMessageReader tmr, File saveLocation, Map<String, String> templateReplacements)
	{
		super(title, content, tmr, templateReplacements);
		this.saveLocationFolder = saveLocation;
		this.lastSavedFilename = "";
	}

	public void sendHTMLmessage() throws Exception
	{
		File saveLocationFile = null;
				
		try 
		{
			/*Date someDate = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");*/
			String ds = System.currentTimeMillis() / 1000 + "";
			String hash = Utils.generateHash(super.getContent());
			lastSavedFilename = ds + "-SWPAM_" + hash.substring(0, 16) + ".html"; //47 + "https://www.hammereditor.net/images/tradingbot/ = 97 characters
			
			saveLocationFile = new File(saveLocationFolder.getAbsolutePath() + "/" + lastSavedFilename);
			saveLocationFile.mkdirs();
			if (saveLocationFile.exists())
				saveLocationFile.delete();
			Logs.log.debug(this.getClass().getName() + ".sendHTMLmessage(): saveLocationFilepath: \'" + saveLocationFile.getAbsolutePath() + "\'");
			saveLocationFile.createNewFile();
		} 
		catch (Exception e) {
			throw new Exception("Error while creating saved HTML webpage file", e);
		}
		
		try
		{
			PrintWriter w = new PrintWriter(saveLocationFile);
			w.println(super.getContent());
			w.close();
		}
		catch (Exception e) {
			throw new Exception("Error while writing HTML to file", e);
		}
	}
	
	public String getLastSavedFilename()
	{
		return lastSavedFilename;
	}

}
