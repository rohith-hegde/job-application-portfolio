package main;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Vector;

import com.github.koraktor.steamcondenser.servers.MasterServer;

import config.*;
import sourceQuery.*;

/**
 * Manages the configuration, master query loop, and last result cache
 * @author Hammereditor
 */
public class Main 
{
	private static ServerResultsMap lastResults;
	private static boolean stop = false;
	
	/**
	 * Gets the latest VSC results
	 * @return The ServerResultsMap of the latest results
	 */
	public static ServerResultsMap getLastResults()
	{
		return new ServerResultsMap(lastResults);
	}
	
	/**
	 * Inserts the last ServerResults into the database table
	 * @throws Exception If there was an error while storing it
	 */
	private static void saveLastResultsToDB() throws Exception
	{
		DatabaseQuerier dq = new DatabaseQuerier();
		ServerResultsList srl = new ServerResultsList(lastResults);
		dq.addServerResults(srl);
		dq.disconnect();
		Logs.debug("Main.saveLastResultsToDB(): Successfully inserted " + srl.getResultCount() + " results into DB.");
	}
	
	/**
	 * Updates the SRM with the latest results
	 * @throws Exception If connecting to the master server failed, or if the SVM query failed
	 */
	private static void queryMaster() throws Exception
	{
		//get list of Unturned servers
		MasterServer ms = null;
		Vector <InetSocketAddress> rawHosts = null;
		
		try
		{
			ms = new MasterServer("hl2master.steampowered.com", 27011);
			ms.initSocket();
			rawHosts = ms.getServers(MasterServer.REGION_ALL, "\\gamedir\\unturned");
		}
		catch (Exception e)
		{
			throw new Exception("Error while connecting to master server", e);
		}
		
		Logs.debug("Main.queryMaster(): Successfully got list of 3.x.x.x servers from Steam master.");
		rawHosts.removeAll(Collections.singleton(null));
		
		InetSocketAddress[] hosts = new InetSocketAddress[rawHosts.size()];
		for (int i = 0; i < rawHosts.size(); i++)
			hosts[i] = rawHosts.get(i);
		ServerVSCmanager svm = new ServerVSCmanager(hosts);
		ServerResultsMap srm = null;
		
		try { srm = svm.getResults(); }
		catch (Exception e) { throw new Exception("Error while running ServerVSCmanager", e); }
		Logs.debug("Main.queryMaster(): Successfully got VSC results from hosts.");
		lastResults = srm;
	}
	
	/**
	 * Stops the main method from iterating next time
	 */
	public static void stop()
	{
		stop = true;
	}
	
	public static void main(String[] args) throws Exception
	{
		/*Config.logs_logFilePath = "C:\\Unturnedhost\\unturned3sl\\log_server.txt";
		Config.serverRCONmanager_waitTimeMs = 2500;
		Config.serverRCONthread_queryRetries = 4;
		Config.serverRCONthread_queryRetryDelay = 250;
		
		queryMaster();
		//System.out.println(lastResults.getAllResults());
		*/
		
		try
		{
			String configPath = "C:\\HammerHost\\Unturned\\Unturned3SL\\config_webApplication.json";
			Config.loadConfiguration(configPath);
		} catch (Exception e) {
			Logs.fatal("Main.main(): Error while loading configuration: ");
			Logs.printException(e);
			return;
		}
		
		long iterStartTimeMs, iterEndTimeMs;
		while (!stop)
		{
			iterStartTimeMs = System.currentTimeMillis();
			
			try { queryMaster(); } 
			catch (Exception e) 
			{
				Logs.error("Main.main(): Error while querying master server: ");
				Logs.printException(e);
			}
			
			try { saveLastResultsToDB(); } 
			catch (Exception e) 
			{
				Logs.error("Main.main(): Error while saving results to DB: ");
				Logs.printException(e);
			}
			
			System.gc();
			iterEndTimeMs = System.currentTimeMillis();
			Thread.sleep(Math.abs(Config.main_masterQueryIntervalMs - (iterEndTimeMs - iterStartTimeMs)));
		}
	}
}
