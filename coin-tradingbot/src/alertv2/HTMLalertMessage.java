package alertv2;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import configv2.Config;
import configv2.Logs;

public abstract class HTMLalertMessage extends TemplatedAlertMessage
{
	private TemplateMessageReader tmr;

	public HTMLalertMessage(String title, String content, TemplateMessageReader tmr)
	{
		super(title, content);
		this.tmr = tmr;
	}
	
	public HTMLalertMessage(String title, String content, TemplateMessageReader tmr, Map<String, String> templateReplacements)
	{
		super(title, content, templateReplacements);
		this.tmr = tmr;
	}

	/**
	 * replaces '{title}' with 'AlertMessage.getTitle()' and '{content}' with 'AlertMessage.getContent()' and '{time}' with 'Logs.getUTCtimeStr()'
	 * in addition to TemplatedAlertMessage.getTemplateReplacements()
	 */
	public void send() throws Exception 
	{
		String html = null;
		String title = super.getTitle(), content = super.getContent();
		try { html = tmr.read(); } catch (Exception e) { throw new Exception("Error while reading template message", e); }
		
		try
		{
			String titleC = new String(title), contentC = new String(content), time = Logs.getUTCtimeStr();
			
			for (String k : getTemplateReplacements().keySet())
			{
				String value = getTemplateReplacements().get(k);
				titleC = titleC.replaceAll("\\{" + k + "\\}", value);
				contentC = contentC.replaceAll("\\{" + k + "\\}", value);
			}
			
			//if (!getTemplateReplacements().containsKey("title"))
				putTemplateReplacement("title", titleC);
			//if (!getTemplateReplacements().containsKey("content"))
				putTemplateReplacement("content", contentC);
			//if (!getTemplateReplacements().containsKey("time"))
			putTemplateReplacement("time", time);
			//getTemplateReplacements().put("iconFilename", )
			
			for (String k : getTemplateReplacements().keySet())
			{
				String value = getTemplateReplacements().get(k);
				html = html.replaceAll("\\{" + k + "\\}", value);
			}
			
			super.setTitle(titleC);
			super.setContent(html);
		}
		catch (Exception e) {
			Logs.log.error(this.getClass().getName() + ".send(): error while replacing template strings: " + e.getMessage());
			Logs.log.printException(e);
		}
		
		try { sendHTMLmessage(); } catch (Exception e) { throw new Exception("Error while sending HTML message", e); }
		super.setTitle(title);
		super.setContent(content);
	}

	public abstract void sendHTMLmessage() throws Exception;
}
