package sourceQuery;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Stores multiple ServerResult objects so that they can be easily accessed like a list. There can be multiple SR's with the same host.
 * @author Hammereditor
 */
public class ServerResultsList 
{
	private ArrayList <ServerResult> results;
	
	/**
	 * Constructs a new, empty ServerResultsList
	 */
	public ServerResultsList()
	{
		results = new ArrayList <ServerResult> ();
	}
	
	/**
	 * Constructs a ServerResultsList from the server results in the specified ServerResultsMap
	 * @param srm The map of results, which will be copied
	 */
	public ServerResultsList(ServerResultsMap srm)
	{
		results = new ArrayList <ServerResult> ();
		ServerResult[] mapRes = (new ServerResultsMap(srm)).getAllResults();
		for (ServerResult sr : mapRes)
			results.add(sr);
	}
	
	/**
	 * Creates this SRL as a copy of another one
	 * @param orig The original SRL
	 */
	public ServerResultsList(ServerResultsList orig)
	{
		results = (ArrayList <ServerResult>)orig.results.clone();
	}
	
	/**
	 * Returns the ServerResults for the specified host
	 * @param host The ipAddr:port, with the port being the VSC port (not the player port)
	 * @return An array of ServerResults
	 */
	public ServerResult[] getResultsForHost(InetSocketAddress host)
	{
		ArrayList <ServerResult> matches = new ArrayList <ServerResult> ();
		for (int i = 0; i < results.size(); i++) 
		{
			ServerResult currSR = results.get(i);
			if (currSR.getHost().getAddress().getHostAddress().equalsIgnoreCase(host.getAddress().getHostAddress()) && currSR.getHost().getPort() == host.getPort())
				matches.add(currSR);
		}
		
		return (ServerResult[])matches.toArray();
	}
	
	/**
	 * Adds a ServerResult
	 * @param sr The SR
	 */
	public void addResult(ServerResult sr)
	{
		results.add(sr);
	}
	
	/**
	 * Gets the ServerResults
	 * @return An array of all the ServerResult objects in this object
	 */
	public ServerResult[] getAllResults()
	{
		ServerResult[] resArray = new ServerResult[results.size()];
		return results.toArray(resArray);
	}
	
	/**
	 * Gets the hosts
	 * @return An array of the ipAddr:port hosts, with the port being the VSC port (not the player port)
	 */
	public InetSocketAddress[] getAllHosts()
	{
		InetSocketAddress[] hosts = new InetSocketAddress[results.size()];
		for (int i = 0; i < hosts.length; i++) 
			hosts[i] = results.get(i).getHost();
		return hosts;
	}
	
	/**
	 * Gets the number of results
	 * @return The number of ServerResult objects
	 */
	public int getResultCount()
	{
		return results.size();
	}
	
	/**
	 * Deletes all ServerResults
	 */
	public void clearAllResults()
	{
		results = new ArrayList <ServerResult> ();
	}
}
