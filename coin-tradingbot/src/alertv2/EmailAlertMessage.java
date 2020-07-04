package alertv2;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import configv2.Logs;

public class EmailAlertMessage extends HTMLalertMessage
{
	private EmailInfo esri;
	
	public EmailAlertMessage(String title, String content, TemplateMessageReader tmr, EmailInfo esri) 
	{
		super(title, content, tmr);
		this.esri = esri;
	}
	
	public EmailAlertMessage(String title, String content, TemplateMessageReader tmr, EmailInfo esri, Map<String, String> templateReplacements) 
	{
		super(title, content, tmr, templateReplacements);
		this.esri = esri;
	}

	public void sendHTMLmessage() throws Exception 
	{
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("api", "key-c48923d3900f83f17ab019523109caa1"));
		WebResource webResource = client.resource("https://api.mailgun.net/v3/hammerhostmail.hammereditor.net" + "/messages");
		MultivaluedMapImpl formData = new MultivaluedMapImpl();
		
		formData.add("from", esri.getSenderName() + " <" + esri.getSenderReplyToAddress() + ">");
		formData.add("to", esri.getReceiverName() + " <" + esri.getReceiverReplyToAddress() + ">");
		Logs.log.debug("EmailAlertMessage.send(): connected to mailgun API");
		formData.add("subject", super.getTitle());
		formData.add("html", super.getContent());
		
		try { webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData); } catch (Exception e) {
			throw new Exception("Error while sending e-mail", e);
		}
	}
}
