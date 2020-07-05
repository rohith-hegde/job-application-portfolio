package dbPerformance;

import java.sql.*;

import main.DatabaseQuerier;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import config.Logs;

public class ResultMover implements Runnable 
{
	private String dbConn_uri, dbConn_user, dbConn_password;
	private Connection dbConn;
	private int rowNum;
	private String playerListHash;
	private long timeRecordedMs;
	
	private String statusResultUpdateStatement;
	private String playerResultInsertStatement;
	
	public ResultMover(String dbConn_uri, String dbConn_user, String dbConn_password, int rowNum, String playerListHash, long timeRecordedMs) throws Exception
	{
		try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            throw new Exception("DB driver not found", e);
        }
		
		this.dbConn_uri = dbConn_uri;
		this.dbConn_user = dbConn_user;
		this.dbConn_password = dbConn_password;
		//this.dbConn = DriverManager.getConnection(dbConn_uri, dbConn_user, dbConn_password);
		this.rowNum = rowNum;
		this.playerListHash = playerListHash;
		this.timeRecordedMs = timeRecordedMs;
	}
	
	public void run()
	{
		try
		{
			long time_start = System.currentTimeMillis();
			dbConn = DriverManager.getConnection(dbConn_uri, dbConn_user, dbConn_password);
			Statement stmt_playerListR = dbConn.createStatement();
			ResultSet rs_playerListR = stmt_playerListR.executeQuery("SELECT PlayerJSON FROM playerlists WHERE PlayerListHash = \'" + playerListHash + "\' LIMIT 1;");
			long time_afterPlayerJSONqueried = System.currentTimeMillis();
			
			long playerListID = (long)(Math.random() * Long.MAX_VALUE);
			//Statement stmt_statusResultUpdate = dbConn.createStatement();
			//stmt_statusResultUpdate.executeUpdate("UPDATE statusresults SET PlayerListID = " + playerListID + " WHERE TimeRecordedMs = " + timeRecordedMs + " AND PlayerListHash = \'" + playerListHash + "\';");
			//stmt_statusResultUpdate.close();
			statusResultUpdateStatement = "UPDATE statusresults SET PlayerListID = " + playerListID + " WHERE TimeRecordedMs = " + timeRecordedMs + " AND PlayerListHash = \'" + playerListHash + "\'";
			long time_afterPlayerListIDupdated = System.currentTimeMillis();
			
			if (rs_playerListR.next())
			{
				String playerJSONstr = rs_playerListR.getString("PlayerJSON");
				JSONParser p = new JSONParser();
				JSONObject rootObj = (JSONObject)p.parse(playerJSONstr);
				JSONArray playerJSON = (JSONArray)rootObj.get("players");
				long time_afterParse = System.currentTimeMillis();
				
				//StringBuilder sb_playerResults = new StringBuilder("INSERT INTO playerresults (PlayerListID, PlayerName, Score, OnlineTimeS) VALUES ");
				StringBuilder sb_playerResults = new StringBuilder();
				
				for (int i = 0; i < playerJSON.size(); i++)
				{
					JSONObject playerObj = (JSONObject)playerJSON.get(i);
					String name = (String)playerObj.get("name");
					int score = Integer.parseInt((String)playerObj.get("score"));
					float onlineTimeS = Float.parseFloat((String)playerObj.get("onlineTimeMs"));
					
					sb_playerResults.append("(" + playerListID + ", \'" + DatabaseQuerier.escapeSQL(name) + "\', " + score + ", " + onlineTimeS + ")");
					//if (i + 1 < playerJSON.size())
						sb_playerResults.append(", ");
				}
				
				long time_afterPlayerResultsAssembly = System.currentTimeMillis();
				playerResultInsertStatement = sb_playerResults.toString();
				long time_afterPlayerResultsInsert = System.currentTimeMillis();
				
				long timeTo_queryPlayerJSON = time_afterPlayerJSONqueried - time_start;
				long timeTo_updatePlayerListID = time_afterPlayerListIDupdated - time_afterPlayerJSONqueried;
				long timeTo_parse = time_afterParse - time_afterPlayerListIDupdated;
				long timeTo_createPlayerResultsInsertStmt = time_afterPlayerResultsAssembly - time_afterParse;
				long timeTo_insertPlayerResults = time_afterPlayerResultsInsert - time_afterPlayerResultsAssembly;
				long timeTo_total = time_afterPlayerResultsInsert - time_start;
				Logs.info("MovePlayerJSONtoPlayerResults.main(): row " + rowNum + " in statusresults with PlayerListHash " + playerListHash + ". Total time: " + timeTo_total + " ms. Time to query playerJSON: " + timeTo_queryPlayerJSON + " ms., time to update player list ID: " + timeTo_updatePlayerListID + "ms., time to parse: " + timeTo_parse + ", ms., time to append playerresults statement: " + timeTo_createPlayerResultsInsertStmt + " ms., time to insert playerresult rows: " + timeTo_insertPlayerResults + " ms.");
			}
			else
				throw new Exception("No row from playerlists table exists for PlayerListHash " + playerListHash);
			
			rs_playerListR.close();
			stmt_playerListR.close();
			dbConn.close();
		}
		catch (Exception e)
		{
			Logs.warning("MovePlayerJSONtoPlayerResults.main(): Error while processing statusresult " + rowNum + ":");
			e.printStackTrace();
		}
	}
	
	public String getStatusResultUpdateStatement()
	{
		return statusResultUpdateStatement;
	}
	
	public String getPlayerResultInsertStatement()
	{
		return playerResultInsertStatement;
	}
}
