package sourceQuery;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Stores multiple ServerResult objects so that they can be easily accessed like a map. There can be only one SR for each host.
 * @author Hammereditor
 */
public class ServerResultsMap 
{
	private HashMap <InetSocketAddress, ServerResult> serverResults;
	
	/**
	 * Constructs a new, empty ServerResultsMap
	 */
	public ServerResultsMap()
	{
		serverResults = new HashMap <InetSocketAddress, ServerResult> ();
	}
	
	/**
	 * Creates this SRM as a copy of another one
	 * @param orig The original SRM
	 */
	public ServerResultsMap(ServerResultsMap orig)
	{
		this.serverResults = (HashMap<InetSocketAddress, ServerResult>)orig.serverResults.clone();
	}

	/**
	 * 
	 * @param host The ipAddr:port, with the port being the VSC port (not the player port)
	 * @param res The ServerResult object for that host
	 */
	public void setResult(InetSocketAddress host, ServerResult res)
	{
		serverResults.put(host, res);
	}
	
	/**
	 * Returns the ServerResult for the specified host
	 * @param host The ipAddr:port, with the port being the VSC port (not the player port)
	 * @return null if no result, or ServerResult if there is a result
	 */
	public ServerResult getResult(InetSocketAddress host)
	{
		return serverResults.get(host);
	}
	
	/**
	 * Gets the ServerResults (the values)
	 * @return An array of all the ServerResult objects in this object
	 */
	public ServerResult[] getAllResults()
	{
		InetSocketAddress[] hosts = getAllHosts();
		ServerResult[] results = new ServerResult[hosts.length];
		for (int i = 0; i < hosts.length; i++)
			results[i] = serverResults.get(hosts[i]);
		return results;
	}
	
	/**
	 * Gets the hosts (the keys)
	 * @return An array of the ipAddr:port hosts, with the port being the VSC port (not the player port)
	 */
	public InetSocketAddress[] getAllHosts()
	{
		InetSocketAddress[] hostArray = new InetSocketAddress[serverResults.keySet().toArray().length];
		Iterator <InetSocketAddress> hostIter = serverResults.keySet().iterator();
		int i = 0;
		while (hostIter.hasNext())
		{
			hostArray[i] = hostIter.next();
			i++;
		}
		
		return hostArray;
	}
	
	/**
	 * Gets the number of results
	 * @return The number of ServerResult objects
	 */
	public int getResultCount()
	{
		return serverResults.size();
	}
	
	/**
	 * Deletes all ServerResults
	 */
	public void clearAllResults()
	{
		serverResults = new HashMap <InetSocketAddress, ServerResult> ();
	}
}
