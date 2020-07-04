package commonv2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import indicatorsv2.PriceType;

public class Utils 
{
	public static boolean allSSLcertsTrusted = false;
	
	public static long getMostRecentDataIntervalTimestampMultipleMs(int candlePeriodS)
	{
		long now = System.currentTimeMillis();
		long candlePeriodMs = candlePeriodS * 1000;
		return now + candlePeriodMs - (now % candlePeriodMs);
	}
	
	public static double getCandleRate(Candle c, PriceType cpt)
	{
		switch (cpt) {
			case CLOSE: return c.getClose().getRate();
			case WTDAVG: return c.getWeighted().getRate();
			case HIGH: return c.getHigh().getRate();
			case LOW: return c.getLow().getRate();
			case VOLUME: return c.getVolume();
			default: return c.getClose().getRate();
		}
	}
	
	/**
	 * 
	 * @param cl candle list
	 * @param cpt
	 * @return double[]{lowest, highest}
	 */
	public static double[] getMaxAndMinCandleRates(List<Candle> cl, PriceType cpt)
	{
		double lowestRate = Double.MAX_VALUE;
		double highestRate = Double.MIN_VALUE;
		
		for (Candle c : cl)
		{
			//double cWt = c.getWeighted().getRate();
			double cr = Utils.getCandleRate(c, cpt);
			System.out.print(cr + " ");
			
			if (cr < lowestRate)
			{
				lowestRate = cr;
				System.out.print("[new low] ");
			}
			if (cr > highestRate)
			{
				highestRate = cr;
				System.out.print("[new high] ");
			}
		}
		
		System.out.println();
		return new double[]{lowestRate, highestRate};
	}
	
	public static void setTrustAllCerts() throws Exception
	{
		TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType ) {	}
				public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType ) {	}
			}
		};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance( "SSL" );
			sc.init( null, trustAllCerts, new java.security.SecureRandom() );
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier( 
				new HostnameVerifier() {
					public boolean verify(String urlHostName, SSLSession session) {
						return true;
					}
				});
			
			allSSLcertsTrusted = true;
		}
		catch ( Exception e ) {
			throw new Exception("Error while installing manager", e);
		}
	}
	
	public static String getDataFromURL(String urlStr) throws Exception
	{
		StringBuilder sb = null;
		
		try
		{
			sb = new StringBuilder();
			URLConnection url = new URL(urlStr).openConnection();
			url.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");
			BufferedReader netIn = new BufferedReader(new InputStreamReader(url.getInputStream()));
			
			String currL = null;
			while ((currL = netIn.readLine()) != null)
				sb.append(currL);
			
			//System.out.println(sb.toString());
			netIn.close();
			return sb.toString();
		}
		catch (Exception e) {
			throw new Exception("Error while getting data", e);
		}
	}
	
	public static double valueToDouble(Object val)
	{
		double volumeBTC = 0.0;
		Object volumeBTCraw = val;
		if (volumeBTCraw instanceof Long)
			volumeBTC = ((long)volumeBTCraw) * 1.0;
		else
			volumeBTC = (double)volumeBTCraw;
		
		return volumeBTC;
	}
	
	public static String getHex( byte [] raw ) {
	     if ( raw == null ) {
	       return null;
	     }
	     final StringBuilder hex = new StringBuilder( 2 * raw.length );
	     for ( final byte b : raw ) {
	       hex.append("0123456789abcdef".charAt((b & 0xF0) >> 4))
	          .append("0123456789abcdef".charAt((b & 0x0F)));
	     }
	     return hex.toString();
	 }

	public static final String generateHash(File file) {
		String hash = "";
		try {
			FileInputStream fileReader = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fileReader.read(data);
			fileReader.close();

			/*
			 * char[] dataRaw = new char[fdata.length];
			 * 
			 * byte[] data = new byte[dataRaw.length]; for (int i = 0; i <
			 * dataRaw.length; i++) { data[i] = ((byte)dataRaw[i]); }
			 */
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(data);
			/*
			 * byte[] digest = md.digest(); for (int i = 0; i < digest.length;
			 * i++) { hash += (char)(digest[i]); }
			 */
			hash = getHex(md.digest());
		} catch (Exception e) {
			hash = "";
			e.printStackTrace();
		}
		return hash;
	}

	public static final String generateHash(String str) {
		String hash = "";
		try {
			char[] dataRaw = str.toCharArray();
			byte[] data = new byte[dataRaw.length];
			for (int i = 0; i < dataRaw.length; i++) {
				data[i] = ((byte) dataRaw[i]);
			}
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(data);
			/*
			 * byte[] digest = md.digest(); for (int i = 0; i < digest.length;
			 * i++) { hash += (char)(digest[i]); }
			 */
			hash = getHex(md.digest());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("generateHash(): There was an error while identifying " + str);
		}
		return hash;
	}
}
