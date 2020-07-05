package dbPerformance;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.github.koraktor.steamcondenser.servers.SteamPlayer;

import sourceQuery.ServerResult;
import sourceQuery.ServerResultPlayer;

public class RandomSR
{
	private InetSocketAddress host;
	private long timeRecordedMs;
	private ServerResultPlayer[] players;

	private boolean vacSecure;
	private boolean passworded;
	private boolean dedicated;
	private int playerCount;
	private int maxPlayers;
	private String gameVersion;
	private String mapName;
	private String difficulty;
	private String versusMode;
	private String title;
	private int operatingSystem;
	
	public RandomSR(long timeRecordedMs) throws IllegalArgumentException
	{
		//generate random host
		String[] ipAddr_frags = new String[]{"18", "192", "34", "7", "88"};
		int[] ports = new int[]{27015, 27017, 27013, 25444};
		String ipAddr = "";
		final int IPV4_DIGITS = 4;
		for (int i = 0; i < IPV4_DIGITS; i++) 
		{
			ipAddr += ipAddr_frags[(int)(Math.random() * (ipAddr_frags.length - 1))];
			if (i + 1 < IPV4_DIGITS)
				ipAddr += ".";
		}
		
		int randomPort = ports[(int)(Math.random() * (ports.length - 1))];
		host = new InetSocketAddress(ipAddr, randomPort);
		
		this.timeRecordedMs = timeRecordedMs; //System.currentTimeMillis() - (int)(Math.random() * 1296000); //from now to 15 days ago
		playerCount = (int)(Math.random() * 24);
		String[] playerNames = new String[]{"Hammereditor", "Cca", "guu123", "DronXa", "stevebutu", "Loxley"};
		players = new ServerResultPlayer[playerCount];
		
		for (int i = 0; i < players.length; i++) 
			players[i] = new ServerResultPlayer(playerNames[(int)(Math.random() * playerNames.length)], Math.random(), (int)(Math.random() * 40));
		
		if (Math.random() > 0.01)
			vacSecure = true;
		else
			vacSecure = false;
		
		if (Math.random() > 0.3)
			passworded = true;
		else
			passworded = false;
		
		if (Math.random() > 0.1)
			dedicated = true;
		else
			dedicated = false;
		
		maxPlayers = 24;
		String[] versions = new String[]{"3.3.7.0", "3.5.1.0", "3.4.8.0", "3.4.6.0"};
		gameVersion = versions[(int)(Math.random() * versions.length)];
		mapName = "Devtest";
		
		double dr = Math.random();
		if (dr > 0.9)
			difficulty = "Gold";
		else if (dr > 0.8)
			difficulty = "Hard";
		else if (dr > 0.125)
			difficulty = "Normal";
		else
			difficulty = "Easy";
		
		if (Math.random() > 0.4)
			versusMode = "PvP";
		else
			versusMode = "PvE";
		
		title = "";
		for (int i = 0; i < 50; i++) 
			title += (char)(Math.random() * 26 + 65);
		
		operatingSystem = 119;
	}

	/**
	 * Gets the host, in ipAddr:port form, where the port is the VSC port (not the player port)
	 * @return the host
	 */
	public InetSocketAddress getHost()
	{
		return host;
	}
	
	/**
	 * Gets the player port (which is the VSC port - 1)
	 * @return the player port
	 */
	public int getPlayerPort()
	{
		return host.getPort() - 1;
	}
	
	/**
	 * Gets the system timestamp (in Ms.) that the query was made
	 * @return the recorded time
	 */
	public long getTimeRecorded()
	{
		return timeRecordedMs;
	}
	
	/**
	 * Gets the array of players
	 * @return the players
	 */
	public ServerResultPlayer[] getPlayers()
	{
		return players.clone();
	}
	
	/**
	 * Gets the VAC status
	 * @return true if secure, false if insecure
	 */
	public boolean isVACsecured()
	{
		return vacSecure;
	}
	
	/**
	 * Gets the passworded status
	 * @return true if passworded, false if not
	 */
	public boolean isPassworded()
	{
		return passworded;
	}
	
	/**
	 * Gets the dedicated status
	 * @return true if dedicated, false if not
	 */
	public boolean isDedicated()
	{
		return dedicated;
	}
	
	/**
	 * Gets the playercount
	 * @return the num. of online players
	 */
	public int getPlayerCount()
	{
		return playerCount;
	}
	
	/**
	 * Gets the max. playercount
	 * @return the player limit
	 */
	public int getMaxPlayers()
	{
		return maxPlayers;
	}
	
	/**
	 * Gets the game version, in the format "x.x.x.x"
	 * @return the game version string
	 */
	public String getGameVersion()
	{
		return gameVersion;
	}
	
	/**
	 * Gets the name of the current map (Devtest/[your own])
	 * @return the name of the map
	 */
	public String getMapName()
	{
		return mapName;
	}
	
	/**
	 * Gets the difficulty (Easy/Normal/Hard/Gold)
	 * @return the difficulty string
	 */
	public String getDifficulty()
	{
		return difficulty;
	}
	
	/**
	 * Gets the versus mode (PvE or PvP)
	 * @return he vs. mode
	 */
	public String getVersusMode()
	{
		return versusMode;
	}
	
	/**
	 * Gets the title
	 * @return the title/name
	 */
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * Gets the operating system code. Windows = 119, Mac OS X = ?, Linux = ?
	 * @return the OS
	 */
	public int getOS()
	{
		return operatingSystem;
	}
	
	public String toString()
	{
		String s = "";
		s += host + "\n";
		s += timeRecordedMs + "\n";
		for (ServerResultPlayer srp : players)
			s += "\t" + srp.toString() + "\n";
		//s += players. + "\n";
		s += vacSecure + "\n";
		s += passworded + "\n";
		s += dedicated + "\n";
		s += playerCount + "\n";
		s += maxPlayers + "\n";
		s += gameVersion + "\n";
		s += mapName + "\n";
		s += difficulty + "\n";
		s += versusMode + "\n";
		s += title + "\n";
		s += operatingSystem + "\n";
		return s;
	}
}
