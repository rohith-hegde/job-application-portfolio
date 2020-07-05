package sourceQuery;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.github.koraktor.steamcondenser.servers.SteamPlayer;

import config.Config;

/**
 * Represents a source query's results for one Unturned server
 * @author Hammereditor
 */
public class ServerResult 
{
	private InetSocketAddress host;
	private long timeRecordedMs;
	private ServerResultPlayer[] players;
	private String playerListHash;

	private boolean vacSecure;
	private boolean passworded;
	private boolean dedicated;
	private byte playerCount;
	private byte maxPlayers;
	private String gameVersion;
	private String mapName;
	private String difficulty;
	private String versusMode;
	private String title;
	private byte operatingSystem;
	
	/**
	 * Creates an uninitialized ServerResult object
	 */
	public ServerResult()
	{
		
	}
	
	/**
	 * Constructs a new ServerResult object with pre-defined values. The playerListHash is generated.
	 * @param host The ipAddr:port host, where port is the VSC port (not the player port)
	 * @param timeRecordedMs The UTC system time, in ms., when the result was recorded
	 * @param players Self-explanatory
	 * @param vacSecure Self-explanatory
	 * @param passworded Self-explanatory
	 * @param dedicated Self-explanatory
	 * @param playerCount Self-explanatory
	 * @param maxPlayers Self-explanatory
	 * @param gameVersion Self-explanatory
	 * @param mapName Self-explanatory
	 * @param difficulty Self-explanatory
	 * @param versusMode Self-explanatory
	 * @param title Self-explanatory
	 * @param operatingSystem Self-explanatory
	 */
	public ServerResult(InetSocketAddress host, long timeRecordedMs, ServerResultPlayer[] players, boolean vacSecure, boolean passworded, boolean dedicated, byte playerCount, byte maxPlayers, String gameVersion, String mapName, String difficulty, String versusMode, String title, byte operatingSystem)
	{
		this.host = host;
		this.timeRecordedMs = timeRecordedMs;
		this.players = players;
		this.vacSecure = vacSecure;
		this.passworded = passworded;
		this.dedicated = dedicated;
		this.playerCount = playerCount;
		this.maxPlayers = maxPlayers;
		this.gameVersion = gameVersion;
		this.mapName = mapName;
		this.difficulty = difficulty;
		this.versusMode = versusMode;
		this.title = title;
		this.operatingSystem = operatingSystem;
		
		this.playerListHash = generatePlayerListHash();
	}
	
	/**
	 * 
	 * @param serverInfoMap The HashMap with the raw server information
	 * @param host The ipAddr:port host, where port is the VSC port (not the player port)
	 * @throws IllegalArgumentException If some values in the raw result Map are missing
	 */
	public ServerResult (HashMap <String, Object> serverInfoMap, HashMap <String, SteamPlayer> playerMap, InetSocketAddress host) throws IllegalArgumentException
	{
		this.host = host;
		this.timeRecordedMs = System.currentTimeMillis();
		
		try
		{
			operatingSystem = (byte)serverInfoMap.get("operatingSystem"); //0
			String tags = (String)serverInfoMap.get("serverTags"); //1
			if (tags.contains("PVP"))
				versusMode = "PvP";
			else
				versusMode = "PvE";
			
			if (tags.contains("EASY"))
				difficulty = "Easy";
			else if (tags.contains("NORMAL"))
				difficulty = "Normal";
			else if (tags.contains("HARD"))
				difficulty = "Hard";
			else
				difficulty = "Gold";
			
			maxPlayers = (byte)serverInfoMap.get("maxPlayers"); //3
			
			vacSecure = (boolean)serverInfoMap.get("secure"); //6
			title = (String)serverInfoMap.get("serverName");
			mapName = (String)serverInfoMap.get("mapName");
			gameVersion = (String)serverInfoMap.get("gameVersion");
			passworded = (boolean)serverInfoMap.get("passwordProtected");
			
			if ((byte)serverInfoMap.get("dedicated") == 100)
				dedicated = true;
			else
				dedicated = false;
			playerCount = (byte)serverInfoMap.get("numberOfPlayers");
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Server result map is corrupt", e);
		}
		
		try
		{
			Collection <SteamPlayer> pmValues = playerMap.values();
			Iterator<SteamPlayer> playerIter = pmValues.iterator();
			players = new ServerResultPlayer[pmValues.size()];
			
			for (int i = 0; i < pmValues.size(); i++)
			{
				String currPstr = playerIter.next().toString();
				String username = currPstr.split("#0 \"")[1].split("\"")[0];
				int score = Integer.parseInt(currPstr.split("Score: ")[1].split(",")[0]);
				float onlineTime = Float.parseFloat(currPstr.split("Time: ")[1].split(",")[0]);
				
				ServerResultPlayer currP = new ServerResultPlayer(username, onlineTime, score);
				players[i] = currP;
			}
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Server player map is corrupt", e);
		}
		
		this.playerListHash = generatePlayerListHash();
	}
	
	/**
	 * Gets a quarter of the SHA-2 hash of the unescaped player JSON string
	 * @return The first 16 hexadecimal characters of the hash (or 64 bits)
	 */
	private String generatePlayerListHash()
	{
		String sha2 = Config.generateHash(getPlayerJSON());
		return sha2.substring(0, 16);
	}
	
	/**
	 * Gets the list of players, as JSON
	 * @return The unescaped JSON string of the players. Example: {\"players\": [{\"name\": "userABC\", \"score\": \"7\", \"onlineTimeS\": \"345.12\"}, ...]}
	 */
	public String getPlayerJSON()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{\"players\": [");
		
		for (int i = 0; i < players.length; i++)
		{
			sb.append("{\"name\": \"" + players[i].getUsername() + "\", \"score\": \"" + players[i].getScore() + "\", \"onlineTimeS\": \"" + players[i].getOnlineTimeS() + "\"}");
			if (i + 1 < players.length)
				sb.append(",");
		}
		
		sb.append("]}");
		return sb.toString();
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
	public byte getPlayerCount()
	{
		return playerCount;
	}
	
	/**
	 * Gets the max. playercount
	 * @return the player limit
	 */
	public byte getMaxPlayers()
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
	 * Gets a quarter of the SHA-2 hash of the unescaped player JSON string
	 * @return The first 16 hexadecimal characters of the hash (or 64 bits)
	 */
	public String getPlayerListHash()
	{
		return playerListHash;
	}
	
	/**
	 * Gets the operating system code. Windows = 119, Mac OS X = ?, Linux = ?
	 * @return the OS
	 */
	public byte getOS()
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
