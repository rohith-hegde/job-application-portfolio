package sourceQuery;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.github.koraktor.steamcondenser.servers.MasterServer;

import config.Config;
import config.Logs;

public class Tester 
{

	public static void main(String[] args) throws Exception
	{
		if (Math.random() < 0.999)
		{
			getAllListedServers();
			return;
		}
		
		Config.serverRCONthread_queryRetries = 5;
		Config.serverRCONthread_queryRetryDelay = 250;
		
		InetSocketAddress[] hosts = new InetSocketAddress[]{new InetSocketAddress("ca.hammereditor.net", 27022)};
		HashMap <InetSocketAddress, Object> queryResults = new HashMap <InetSocketAddress, Object> ();
		Thread[] svts = new Thread[hosts.length];
		
		for (int i = 0; i < hosts.length; i++)
		{
			ServerVSCthread svt = new ServerVSCthread(hosts[i], queryResults);
			svts[i] = new Thread(svt);
			svts[i].start();
		}
		
		Thread.sleep(2500);
		/*Object[] resultObjs = queryResults.values().toArray();
		
		for (int i = 0; i < resultObjs.length; i++)
		{
			Object currRO = resultObjs[i];*/
		
		for (int i = 0; i < hosts.length; i++)
		{
			Object currRO = queryResults.get(hosts[i]);
			svts[i].stop();
			
			if (currRO == null)
			{
				System.out.println("hosts[" + i + "]: Error: Thread did not finish in time");
			}
			else if (currRO instanceof Exception)
			{
				Exception currE = (Exception)currRO;
				System.out.println("hosts[" + i + "]: Error: " + currE.getMessage());
				currE.printStackTrace();
			}
			else
			{
				ServerResult sr = (ServerResult)currRO;
				System.out.println("hosts[" + i + "]: " + sr.toString());
			}
			
			System.out.println("Reverse DNS for hosts[" + i + "]: " + InetAddress.getByName(hosts[i].getHostString()).getCanonicalHostName());
		}
	}
	
	public static Vector <InetSocketAddress> getAllListedServers() throws Exception
	{
		//get list of Unturned servers
		MasterServer ms = null;
		Vector <InetSocketAddress> rawHosts = null;
		
		try
		{
			ms = new MasterServer("hl2master.steampowered.com", 27011);
			ms.initSocket();
			//rawHosts = ms.getServers(MasterServer.REGION_ALL, "\\gamedir\\rust");
			rawHosts = ms.getServers();
		}
		catch (Exception e)
		{
			throw new Exception("Error while connecting to master server", e);
		}
		
		Logs.debug("Main.queryMaster(): Successfully got list of 3.x.x.x servers from Steam master.");
		//rawHosts.removeAll(Collections.singleton(null));
		System.out.println("Server count: " + rawHosts.size());
		return rawHosts;
	}

}
