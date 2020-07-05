package config;

import java.io.File;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Stores the cached configuration from the JSON config file
 * @author Hammereditor
 */
public class Config 
{
	public static InetSocketAddress db_host;
	public static String db_username;
	public static String db_password;
	public static String db_schemaName;

	public static boolean logStatements = false;
	public static int serverRCONmanager_waitTimeMs;
	public static int main_masterQueryIntervalMs;
	public static int serverRCONthread_queryRetries;
	public static int serverRCONthread_queryRetryDelay;
	public static String logs_logFilePath = "C:\\HammerHost\\unturned3sl_playerMoveLog.txt"; //"C:\\Unturnedhost\\Unturned3SL\\playerMoveLog.txt";
	
	public static void loadConfiguration(String configPath) throws Exception
	{
		File cf = new File(configPath);
		Scanner cfIn = new Scanner(cf);
		StringBuilder sb = new StringBuilder();
		
		while (cfIn.hasNext())
			sb.append(cfIn.nextLine());
		cfIn.close();
		
		JSONParser p = new JSONParser();
		JSONObject rootObj = (JSONObject)p.parse(sb.toString());
		String db_host_ipAddr = (String)rootObj.get("db_host_ipAddr");
		logStatements = Boolean.parseBoolean((String)rootObj.get("logStatements"));
		int db_host_port = Integer.parseInt((String)rootObj.get("db_host_port"));
		db_host = new InetSocketAddress(db_host_ipAddr, db_host_port);
		
		db_username = (String)rootObj.get("db_username");
		db_password = (String)rootObj.get("db_password");
		db_schemaName = (String)rootObj.get("db_schemaName");
		serverRCONmanager_waitTimeMs = Integer.parseInt((String)rootObj.get("serverRCONmanager_waitTimeMs"));
		main_masterQueryIntervalMs = Integer.parseInt((String)rootObj.get("main_masterQueryIntervalMs"));
		serverRCONthread_queryRetries = Integer.parseInt((String)rootObj.get("serverRCONthread_queryRetries"));
		serverRCONthread_queryRetryDelay = Integer.parseInt((String)rootObj.get("serverRCONthread_queryRetryDelay"));
		logs_logFilePath = (String)rootObj.get("logs_logFilePath");
	}
	
	private static String getHex( byte [] raw ) {
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
	
	public static final String generateHash(String str)
	{
	  String hash = "";
	  try
	  {
	    char[] dataRaw = str.toCharArray();
	    byte[] data = new byte[dataRaw.length];
	    for (int i = 0; i < dataRaw.length; i++) {
	      data[i] = ((byte)dataRaw[i]);
	    }
	    MessageDigest md = MessageDigest.getInstance("SHA-256");
	    md.update(data);
	    /*byte[] digest = md.digest();
	    for (int i = 0; i < digest.length; i++) {
	      hash += (char)(digest[i]);
	    }*/
	    hash = getHex(md.digest());
	  }
	  catch (Exception e)
	  {
	    e.printStackTrace();
	    //System.out.println("generateHash(): There was an error while identifying " + str);
	  }
	  return hash;
	}
}
