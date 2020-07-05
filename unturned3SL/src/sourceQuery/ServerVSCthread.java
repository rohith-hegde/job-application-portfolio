package sourceQuery;
import java.net.InetSocketAddress;
import java.util.HashMap;
import com.github.koraktor.steamcondenser.servers.SourceServer;
import com.github.koraktor.steamcondenser.servers.SteamPlayer;
import config.*;

/**
 * Executes a single Valve src. query on a single Unturned server
 * @author Hammereditor
 */
public class ServerVSCthread implements Runnable
{
	private InetSocketAddress host;
	private int queryRetries;
	private int queryRetryDelay;
	private HashMap <InetSocketAddress, Object> callbackMap;
	 
	/**
	 * Constructs a new ServerVSCthread, which is ready to run
	 * @param host The ipAddr:port of this Unturned server's VSC port (not the player port)
	 * @param callbackMap The HashMap to put the result into
	 */
	public ServerVSCthread(InetSocketAddress host, HashMap <InetSocketAddress, Object> callbackMap)
	{
		this.host = host;
		this.queryRetries = Config.serverRCONthread_queryRetries;
		this.queryRetryDelay = Config.serverRCONthread_queryRetryDelay;
		this.callbackMap = callbackMap;
	}
	
	/**
	 * Executes the query and puts the result into the callback map
	 */
	public void run()
	{
		SourceServer currSS = null;
		try
		{
			currSS = new SourceServer(host.getAddress().getHostAddress(), host.getPort());
			currSS.initSocket();
		} catch (Exception e) {
			callbackMap.put(host, new Exception("Error while connecting to Unturned server with VSC", e));
			return;
		}
		
		int attempts = 0;
		HashMap <String, Object> serverInfoMap = null;
		HashMap <String, SteamPlayer> playerMap = null;
		
		while (attempts < queryRetries && serverInfoMap == null)
		{
			try
			{
				try { serverInfoMap = currSS.getServerInfo(); } catch (Exception e) {} 
				if (serverInfoMap == null)
					attempts++;
				//else, successfully created map
			} catch (Exception e) { attempts++; }
			
			try { Thread.sleep(queryRetryDelay); } catch (Exception e) { }
		}
		
		if (serverInfoMap == null)
		{
			callbackMap.put(host, new Exception("Unable to get basic server info map"));
			return;
		}
		
		attempts = 0;
		while (attempts < queryRetries && playerMap == null)
		{
			try
			{
				try { playerMap = currSS.getPlayers(); } catch (Exception e) {} 
				if (playerMap == null)
					attempts++;
				//else, successfully created map
			} catch (Exception e) { attempts++; }
			
			try { Thread.sleep(queryRetryDelay); } catch (Exception e) { }
		}
		
		if (playerMap == null)
		{
			callbackMap.put(host, new Exception("Unable to get player map"));
			return;
		}
		
		//create ServerResult object
		Object sr;
		try { sr = new ServerResult(serverInfoMap, playerMap, host); } 
		catch (IllegalArgumentException e) { 
			sr = new Exception("Error creating ServerResult object", e);
		}
		
		callbackMap.put(host, sr);
	}
}
