package configv2;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;













public class TestUtils {

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

	/*public static List<Candle> candlesFromURL(MarketID market) throws Exception
	{
		Scanner kbd = new Scanner(System.in);
		System.out.println("Input JSON code");
		String l = kbd.nextLine();
		kbd.close();
		
		JSONParser p = new JSONParser();
		JSONArray rootObj = (JSONArray)p.parse(l);
		List<Candle> cl = new ArrayList<Candle> ();
		
		long startTimeS = (long)((JSONObject)rootObj.get(0)).get("date");
		
		for (int i = 0; i < rootObj.size(); i++)
		{
			JSONObject o = (JSONObject)rootObj.get(i);
			Candle c = new Candle(market, startTimeS, periodS, highRate, lowRate, openRate, closeRate, volumeMasterC, volumeTradeC, weightedAvgRate)
		}
		
		
	}*/
	
	public static void trustAllCertificates()
	{
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { 
		    new X509TrustManager() {     
		        public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
		            return new X509Certificate[0];
		        } 
		        public void checkClientTrusted( 
		            java.security.cert.X509Certificate[] certs, String authType) {
		            } 
		        public void checkServerTrusted( 
		            java.security.cert.X509Certificate[] certs, String authType) {
		        }
		    } 
		}; 
	
		// Install the all-trusting trust manager
		try {
		    SSLContext sc = SSLContext.getInstance("SSL"); 
		    sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (GeneralSecurityException e) {
		} 
		// Now you can access an https URL without having the certificate in the truststore
	}
	
}
