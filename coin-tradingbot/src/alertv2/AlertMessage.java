package alertv2;

public abstract class AlertMessage 
{
	private String title;
	private String content;
	
	public AlertMessage(String title, String content)
	{
		this.title = title;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public abstract void send() throws Exception;
}
