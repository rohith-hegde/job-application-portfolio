package main;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import config.Config;
import config.Logs;
import sourceQuery.ServerResult;
import sourceQuery.ServerResultPlayer;
import sourceQuery.ServerResultsList;

/**
 * Objects of this class contain utility functions to interact with the database
 * @author Hammereditor
 */
public class DatabaseQuerier 
{
	private Connection dbConn;
	
	/**
	 * Creates a new DatabaseQuerier and connects
	 * @throws Exception If there was an error connecting to the database
	 */
	public DatabaseQuerier() throws Exception
	{
		dbConn = null;
		connect();
	}
	
	/**
	 * Connects to the database
	 * @throws Exception If there was a database error
	 */
	private void connect() throws Exception
	{
		try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            throw new Exception("DB driver not found", e);
        }
		dbConn = DriverManager.getConnection("jdbc:mysql://" + Config.db_host.getAddress().getHostAddress() + ":" + Config.db_host.getPort() + "/" + Config.db_schemaName, Config.db_username, Config.db_password);
	}
	
	/**
	 * Disconnects from the database
	 * @throws IllegalStateException If the object is not connected yet
	 * @throws SQLException If there was an error disconnecting
	 */
	public void disconnect() throws IllegalStateException, SQLException
	{
		if (dbConn == null)
			throw new IllegalStateException("Not connected yet, so cannot disconnect");
		dbConn.close();
	}
	
	/**
	 * Replaces special characters to escape a SQL string
	 * @param src The unescaped string
	 * @return The escaped string
	 */
	public static String escapeSQL(String src)
	{
		src = src.replaceAll("\'", "\\\\\\\'");
		return src;
	}

	/**
	 * Gets a list of server results, for ONE host, from a start time to an end time.
	 * @param startTimeMs The starting UTC system time (ms.)
	 * @param endTimeMs The ending UTC system time (ms.)
	 * @param host The ipAddr:port host, where the port is the VSC port. Or "null" to get all hosts.
	 * @param frequency The time interval, in milliseconds, between results. 1 = all results are returned.
	 * @param limit The max. number of results to return
	 * @param getPlayers If true, query the list of players; else, don't get the players
	 * @return A ServerResultsList object with the results
	 * @throws Exception If there was a problem querying the DB
	 */
	public ServerResultsList getRangeOfServerResults(long startTimeMs, long endTimeMs, InetSocketAddress host, long frequency, int limit, boolean getPlayers) throws Exception
	{
		if (dbConn == null)
			throw new IllegalStateException("Not connected");
		
		String stmtStr = "SELECT * FROM statusresults WHERE TimeRecordedMs >= " + startTimeMs + " AND TimeRecordedMs <= " + endTimeMs;
		if (host != null)
			stmtStr += " AND Host_IPaddr = \'" + host.getAddress().getHostAddress() + "\' AND Host_Port = " + host.getPort();
		if (frequency != 1)
			stmtStr += " AND (TimeRecordedMs - " + startTimeMs + ") % " + frequency + " == 0";
		stmtStr += ";";
		
		Statement stmt = dbConn.createStatement();
		ResultSet rs = stmt.executeQuery(stmtStr);
		ServerResultsList srl = new ServerResultsList();
		int resultCount = 0;
		
		String playerName;
		int playerScore;
		float playerOnlineTimeS;
		
		InetSocketAddress sHost;
		long timeRecordedMs;
		ServerResultPlayer[] players = null;
		long playerResultID;
		boolean vacSecure;
		boolean passworded;
		boolean dedicated;
		byte playerCount;
		byte maxPlayers;
		String gameVersion;
		String mapName;
		String difficulty;
		String versusMode;
		String title;
		byte operatingSystem;
		
		while (rs.next() && resultCount < limit)
		{
			try
			{
				sHost = new InetSocketAddress(rs.getString("Host_IPaddr"), rs.getShort("Host_Port"));
				timeRecordedMs = rs.getLong("TimeRecordedMs");
				playerResultID = rs.getLong("PlayerResultID");
				
				if (getPlayers)
				{
					String stmtStrP = "SELECT * FROM playerresults WHERE PlayerResultID = " + playerResultID + ";";
					Statement stmtP = dbConn.createStatement();
					ResultSet rsP = stmtP.executeQuery(stmtStrP);
					int i = 0;
					
					while (rsP.next())
					{
						playerName = rsP.getString("PlayerName");
						playerScore = rsP.getShort("Score");
						playerOnlineTimeS = rsP.getFloat("OnlineTimeS");
						players[i] = new ServerResultPlayer(playerName, playerOnlineTimeS, playerScore);
						i++;
					}
				}
				else
					players = null;
				
				vacSecure = rs.getBoolean("VacSecure");
				passworded = rs.getBoolean("Passworded");
				dedicated = rs.getBoolean("Dedicated");
				playerCount = rs.getByte("PlayerCount");
				maxPlayers = rs.getByte("MaxPlayers");
				
				gameVersion = rs.getString("GameVersion");
				mapName = rs.getString("MapName");
				difficulty = rs.getString("Difficulty");
				versusMode = rs.getString("VersusMode");
				title = rs.getString("Title");
				operatingSystem = rs.getByte("OperatingSystem");
				
				ServerResult sr = new ServerResult(sHost, timeRecordedMs, players, vacSecure, passworded, dedicated, playerCount, maxPlayers, gameVersion, mapName, difficulty, versusMode, title, operatingSystem);
				srl.addResult(sr);
				resultCount++;
			}
			catch (Exception e)
			{
				Logs.warning("DatabaseQuerier.getRangeOfServerResults(): Error while parsing row for result: ");
				Logs.printException(e);
			}
		}
		
		try
		{
			rs.close();
			stmt.close();
		}
		catch (Exception e)
		{
			Logs.warning("DatabaseQuerier.getRangeOfServerResults(): Error while closing statement: ");
			Logs.printException(e);
		}
		
		return srl;
	}
	
	/*
	/**
	 * Gets a list of server results, for ONE host, from a start time to the present.
	 * @param startTime The starting UTC system time (ms.)
	 * @param host The ipAddr:port host, where the port is the VSC port
	 * @return A ServerResultsList object with the results
	 * @throws Exception If there was a problem querying the DB
	 
	public ServerResultsList getRangeOfServerResults(long startTime, InetSocketAddress host) throws Exception
	{
		return getRangeOfServerResults(startTime, Long.MAX_VALUE, host, 1);
	}*/
	
	/*/**
	 * Gets a list of every single server result, for ALL recorded hosts, from a start time to an end time.
	 * @param startTime The starting UTC system time (ms.)
	 * @param endTime The ending UTC system time (ms.)
	 * @return A ServerResultsList object with the results
	 * @throws Exception If there was a problem querying the DB
	 
	public ServerResultsList getRangeOfServerResults(long startTime, long endTime) throws Exception
	{
		return getRangeOfServerResults(startTime, endTime, null, 1, Integer.MAX_VALUE);
	}
	
	/**
	 * Gets a list of server results in intervals, for ALL recorded hosts, from a start time to an end time.
	 * @param startTime The starting UTC system time (ms.)
	 * @param endTime The ending UTC system time (ms.)
	 * @param frequency The time interval, in milliseconds, between results
	 * @return A ServerResultsList object with the results
	 * @throws Exception If there was a problem querying the DB
	 
	public ServerResultsList getRangeOfServerResults(long startTime, long endTime, long frequency) throws Exception
	{
		return getRangeOfServerResults(startTime, endTime, null, frequency, Integer.MAX_VALUE);
	}*/
	
	/**
	 * Inserts multiple results into the DB
	 * @param results The list of results
	 * @throws Exception If there was a problem querying the DB
	 */
	public void addServerResults(ServerResultsList resultList) throws Exception
	{
		if (dbConn == null)
			throw new IllegalStateException("Not connected");
		
		ServerResult[] results = resultList.getAllResults();
		Statement stmt_status = dbConn.createStatement();
		Statement stmt_players = dbConn.createStatement();
		ArrayList <String> stmtStrList = new ArrayList <String> ();
		
		StringBuilder stmtStr_status = new StringBuilder();
		stmtStr_status.append("INSERT INTO statusresults (Host_IPaddr, Host_Port, TimeRecordedMs, PlayerListHash, VacSecure, Passworded, Dedicated, PlayerCount, MaxPlayers, GameVersion, MapName, Difficulty, VersusMode, Title, OperatingSystem, PlayerListID) VALUES ");
		StringBuilder stmtStr_players = new StringBuilder();
		stmtStr_players.append("INSERT INTO playerresults (PlayerListID, PlayerName, Score, OnlineTimeS) VALUES ");
		
		String vacSecureValue;
		String passwordedValue;
		String dedicatedValue;
		long playerListID;
		ServerResultPlayer[] players;
		
		for (int i = 0; i < results.length; i++)
		{
			try
			{
				ServerResult sr = results[i];
				vacSecureValue = sr.isVACsecured() ? "1" : "0";
				passwordedValue = sr.isPassworded() ? "1" : "0";
				dedicatedValue = sr.isDedicated() ? "1" : "0";
				playerListID = (long)(Math.random() * Long.MAX_VALUE);
				players = sr.getPlayers();
				
				stmtStr_status.append("(\'" + sr.getHost().getAddress().getHostAddress() + "\', " + sr.getHost().getPort() + ", " + sr.getTimeRecorded() + ", \'\', " + vacSecureValue + ", " + passwordedValue + ", " + dedicatedValue + ", " + sr.getPlayerCount() + ", " + sr.getMaxPlayers() + ", \'" + sr.getGameVersion() + "\', \'" + escapeSQL(sr.getMapName()) + "\', \'" + sr.getDifficulty() + "\', \'" + sr.getVersusMode() + "\', \'" + escapeSQL(sr.getTitle()) + "\', " + sr.getOS() + ", " + playerListID + ")");
				for (int j = 0; j < players.length; j++)
				{
					ServerResultPlayer p = players[j];
					stmtStr_players.append("(" + playerListID + ", \'" + escapeSQL(p.getUsername()) + "\', " + p.getScore() + ", " + p.getOnlineTimeS() + ")");
					//if (j + 1 < players.length)
						stmtStr_players.append(",");
					//System.out.println("DatabaseQuerier.addServerResults(): stmtStr_players in loop: " + stmtStr_players.toString());
				}
					
				if (i + 1 < results.length)
				{
					stmtStr_status.append(", ");
					//stmtStr_players.append(", ");
				}	
			}
			catch (Exception e)
			{
				Logs.warning("DatabaseQuerier.addServerResults(): Error while adding server result, but continuing...: ");
				Logs.printException(e);
			}
		}
		
		stmtStr_status.append(";");
		stmtStr_players.deleteCharAt(stmtStr_players.length() - 1); //deletes last comma
		if (Config.logStatements)
		{
			Logs.debug("DatabaseQuerier.addServerResults(): stmtStr_status: " + stmtStr_status.toString());
			Logs.debug("DatabaseQuerier.addServerResults(): stmtStr_players: " + stmtStr_players.toString());
		}
		stmtStr_players.append(";");
		
		try 
		{ 
			long startMs = System.currentTimeMillis();
			//stmt.executeBatch();
			stmt_status.executeUpdate(stmtStr_status.toString());
			stmt_players.executeUpdate(stmtStr_players.toString());
			long endMs = System.currentTimeMillis();
			Logs.debug("DatabaseQuerier.addServerResults(): Inserting results took " + (endMs - startMs) + " milliseconds.");
		}
		catch (Exception e)
		{
			StringBuilder allStatements = new StringBuilder();
			for (String s : stmtStrList)
				allStatements.append(s + "\n");
			
			Logs.error("DatabaseQuerier.addServerResults(): Error while executing batch of updates. Statements: " + allStatements.toString());
			throw new Exception("Error while executing batch of updates", e);
		}
		
		try { stmt_status.close(); stmt_players.close(); }
		catch (Exception e)
		{
			Logs.warning("DatabaseQuerier.addServerResults(): Error while closing statement: ");
			Logs.printException(e);
		}
	}
	
	/**
	 * Inserts one result into the DB
	 * @param res The result
	 * @throws Exception If there was a problem querying the DB
	 */
	public void addServerResult(ServerResult res) throws Exception
	{
		ServerResultsList srl = new ServerResultsList();
		srl.addResult(res);
		addServerResults(srl);
	}
	
	/**
	 * Checks if a result exists in the DB, according to it's host and time recorded
	 * @param res The result
	 * @param maxTimeVariance The maximum amount of time the result's recorded time can vary
	 * @return true if at least one match exists, false if not
	 * @throws Exception If there was a problem querying the DB
	 */
	public boolean resultExists(ServerResult res, long maxTimeVariance) throws Exception
	{
		ServerResultsList srl = getRangeOfServerResults(Long.MIN_VALUE, Long.MAX_VALUE, null, 1, 1, false);
		return srl.getResultCount() > 0;
	}
}
