package sourceQuery;
import java.net.InetSocketAddress;
import java.util.HashMap;

import config.*;

/**
 * Manages result submissions for the ServerVSCthreads
 * @author Hammereditor
 */
public class ServerVSCmanager 
{
	private int waitTimeMs;
	private HashMap <InetSocketAddress, Object> callbackMap; // the Object can be an Exception or a ServerResult
	private InetSocketAddress[] hosts;
	
	/**
	 * Creates a new ServerVSCmanager
	 * @param hosts The array of the Unturned servers' VSC ipAddr:port (not the player port)
	 */
	public ServerVSCmanager(InetSocketAddress[] hosts)
	{
		this.hosts = hosts;
		callbackMap = new HashMap <InetSocketAddress, Object> ();
		waitTimeMs = Config.serverRCONmanager_waitTimeMs;
	}
	
	/**
	 * Queries the servers and waits a fixed amount of time for the results
	 * @return The map of the results
	 * @throws Exception If there was an error while querying the Steam master server
	 */
	public ServerResultsMap getResults() throws Exception
	{
		Thread[] svts = new Thread[hosts.length];
		for (int i = 0; i < hosts.length; i++)
		{
			ServerVSCthread svt = new ServerVSCthread(hosts[i], callbackMap);
			svts[i] = new Thread(svt);
			svts[i].start();
		}
		
		Thread.sleep(waitTimeMs);
		ServerResultsMap results = new ServerResultsMap();
		int successful = 0;
		
		for (int i = 0; i < hosts.length; i++)
		{
			Object currRO = callbackMap.get(hosts[i]);
			//Logs.debug("ServerVSCmanager.getResults(): host: " + hosts[i].toString() + ", currRO: " + currRO);
			svts[i].stop();
			
			if (currRO != null)
			{
				if (currRO instanceof Exception)
					Logs.warning("ServerVSCmanager.getResults(): Error while getting status for host \'" + hosts[i].toString() + "\': " + ((Exception)currRO).getMessage());
				else if (currRO instanceof ServerResult)
				{
					//Logs.debug("ServerVSCmanager.getResults(): host \'" + hosts[i].toString() + "\' successful.");
					ServerResult sr = (ServerResult)currRO;
					results.setResult(hosts[i], sr);
					successful++;
				}
			}
		}
		
		Logs.info("ServerVSCmanager.getResults(): Successful queries: " + successful + " / " + hosts.length);
		return results;
	}
}
