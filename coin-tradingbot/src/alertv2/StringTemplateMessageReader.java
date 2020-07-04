package alertv2;

public class StringTemplateMessageReader implements TemplateMessageReader
{
	private String templateMessage;
	
	public String getTemplateMessage() {
		return templateMessage;
	}

	public void setTemplateMessage(String templateMessage) {
		this.templateMessage = templateMessage;
	}

	public StringTemplateMessageReader(String tm)
	{
		this.templateMessage = tm;
	}

	public String read() throws Exception 
	{
		return templateMessage;
	}
}
