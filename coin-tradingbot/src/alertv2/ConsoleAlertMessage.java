package alertv2;

/**
 * simple class which can be used to easily test if a rule was triggered
 *
 */
public class ConsoleAlertMessage extends TemplatedAlertMessage
{

	public ConsoleAlertMessage(String title, String content) 
	{
		super(title, content);
	}

	@Override
	public void send() throws Exception 
	{
		String titleC = new String(super.getTitle()), contentC = new String(super.getContent());
		
		for (String k : getTemplateReplacements().keySet())
		{
			String value = getTemplateReplacements().get(k);
			//System.out.println("console alert message.send(): " + k + " " + value);
			titleC = titleC.replaceAll("\\{" + k + "\\}", value);
			contentC = contentC.replaceAll("\\{" + k + "\\}", value);
		}
		
		//putTemplateReplacement("title", titleC);
		//putTemplateReplacement("content", contentC);
		System.out.println("\t\tCONSOLE ALERT MESSAGE: \n\t\t\t\tTitle: " + titleC + "\n\t\t\t\tContent: " + contentC + "\n");
	}
}
