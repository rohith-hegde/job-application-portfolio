package alertv2;

import java.util.Map;

public class VtextEmailAlertMessage extends EmailAlertMessage
{
	/**
	 * @param tmr the object which providers the message text
	 * @param vti the sender and receiver information
	 */
	public VtextEmailAlertMessage(TemplateMessageReader tmr, VtextInfo vti) 
	{
		super("", vti.getTextHeader() + "{content}", tmr, vti);
	}
	
	/**
	 * @param tmr the object which provides the message text
	 * @param vti the sender and receiver information
	 * @param templateReplacements any token string replacements
	 */
	public VtextEmailAlertMessage(TemplateMessageReader tmr, VtextInfo vti, Map<String, String> templateReplacements) 
	{
		super("", vti.getTextHeader() + "{content}", tmr, vti, templateReplacements);
	}
}
