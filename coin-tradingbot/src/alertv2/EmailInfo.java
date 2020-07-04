package alertv2;

public class EmailInfo 
{
	private String senderName, senderReplyToAddress;
	private String receiverName, receiverReplyToAddress;
	private String subject;
	
	public EmailInfo(String senderName, String senderReplyToAddress, String receiverName, String receiverReplyToAddress, String subject)
	{
		this.senderName = senderName;
		this.senderReplyToAddress = senderReplyToAddress;
		this.receiverName = receiverName;
		this.receiverReplyToAddress = receiverReplyToAddress;
		this.subject = subject;
	}

	public String getSenderName() {
		return senderName;
	}

	public String getSenderReplyToAddress() {
		return senderReplyToAddress;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public String getReceiverReplyToAddress() {
		return receiverReplyToAddress;
	}

	public String getSubject() {
		return subject;
	}
}
