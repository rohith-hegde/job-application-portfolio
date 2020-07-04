package alertv2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class PushedAlertMessage extends AlertMessage
{
	private PushedCredentials pc;
	
	public PushedAlertMessage(String title, String content, PushedCredentials pc)
	{
		super(title, content);
		this.pc = pc;
	}

	public void send() throws Exception
	{
		String title = super.getTitle().toUpperCase();
		String message = title + ": " + super.getContent();
		if (message.length() > 140)
			message = message.substring(0, 140);
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost("https://api.pushed.co/1/push");

		// add header
		post.setHeader("User-Agent", "Tradingbot alerts 0.1");
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		
		urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));
		urlParameters.add(new BasicNameValuePair("cn", ""));
		urlParameters.add(new BasicNameValuePair("locale", ""));
		urlParameters.add(new BasicNameValuePair("caller", ""));
		urlParameters.add(new BasicNameValuePair("num", "12345"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		HttpResponse response = client.execute(post);
		
		StringBuilder res = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String l = null;
		
		try
		{
			while ((l = br.readLine()) != null)
			{
				res.append(l);
				res.append("\n");
			}
		}
		catch (Exception e) {
			throw new Exception("Error while receiving output", e);
		}
		
		if (response.getStatusLine().getStatusCode() != 200)
			throw new Exception("Server returned error: " + response.getStatusLine());
	}
}
