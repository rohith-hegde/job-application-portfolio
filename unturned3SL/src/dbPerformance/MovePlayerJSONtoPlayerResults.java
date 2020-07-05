package dbPerformance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import config.Logs;

public class MovePlayerJSONtoPlayerResults 
{
	public static void main(String[] args) throws Exception
	{
		//at home: 225 results/minute
		//= 324000 results/day
		
		
		//1: query all the statusresults
		//2: for each statusresult row:
			//use the PlayerListHash to query the playerlist row
			//parse the playerlist's JSON and have it ready as a JSONArray
		
			//generate a random PlayerListID
			//update the statusresult row with it, and remove the PlayerListHash
			//insert a new row into the playerresults table: with the PlayerListID and the player's information
		
		//3: repeat the process
		
		//StringBuilder sqlStr_update = new StringBuilder(); //all SQL queries for updating the statusresult table
		//StringBuilder sqlStr_insert = new StringBuilder();
		
		int STATUS_START = 20, STATUS_END = 40;
		int MAX_ACTIVE_THREADS = 20;
		String DB_HOSTNAME = "127.0.0.1";
		
		if (args.length >= 4)
		{
			STATUS_START = Integer.parseInt(args[0]);
			STATUS_END = Integer.parseInt(args[1]);
			MAX_ACTIVE_THREADS = Integer.parseInt(args[2]);
			DB_HOSTNAME = args[3];
		}
		else
		{
			System.out.println("Usage: [starting row in statusresults] [ending row in statusresults] [maximum threads at a time] [IP address/hostname of database server]");
			return;
		}
		
		try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            throw new Exception("DB driver not found", e);
        }
		
		int rowNum = STATUS_START;
		long startTime = System.currentTimeMillis();
		long time_afterStatusResultsSelected = System.currentTimeMillis();
		
		for (int i = 0; i < STATUS_END; i += MAX_ACTIVE_THREADS)
		{
			String dbConn_uri = "jdbc:mysql://" + DB_HOSTNAME + ":3008/unturned3sl?useServerPrepStmts=false&rewriteBatchedStatements=true", dbConn_user = "Administrator", dbConn_password = "!s31bR";
			Connection dbConn = DriverManager.getConnection(dbConn_uri, dbConn_user, dbConn_password);
			
			Statement stmt_statusR = dbConn.createStatement();
			ResultSet rs_statusR = stmt_statusR.executeQuery("SELECT PlayerListHash, TimeRecordedMs FROM statusresults LIMIT " + (i) + ", " + (MAX_ACTIVE_THREADS) + ";");
			time_afterStatusResultsSelected = System.currentTimeMillis();
			
			Thread[] rmThreads = new Thread[MAX_ACTIVE_THREADS];
			ResultMover[] rms = new ResultMover[MAX_ACTIVE_THREADS];
			int threadNum = 0;
			
			while (rs_statusR.next())
			{
				String playerListHash = rs_statusR.getString("PlayerListHash");
				if (playerListHash != null)
				{
					long timeRecordedMs = rs_statusR.getLong("TimeRecordedMs");
					rms[threadNum] = new ResultMover(dbConn_uri, dbConn_user, dbConn_password, rowNum, playerListHash, timeRecordedMs);
					rmThreads[threadNum] = new Thread(rms[threadNum]);
					rmThreads[threadNum].start();
				}
				
				rowNum++;
				threadNum++;
			}
			
			rs_statusR.close();
			stmt_statusR.close();
			long time_afterThreadsCreated = System.currentTimeMillis();
			
			//wait for all threads
			//StringBuilder statusResultUpdateStatements = new StringBuilder("START TRANSACTION;\n");
			StringBuilder playerResultInsertStatements = new StringBuilder("INSERT INTO playerresults (PlayerListID, PlayerName, Score, OnlineTimeS) VALUES ");
			
			for (Thread rmt : rmThreads)
				rmt.join(25000);
			for (int r = 0; r < rms.length; r++)
			{
				ResultMover rm = rms[r];
				playerResultInsertStatements.append(rm.getPlayerResultInsertStatement());
				//statusResultUpdateStatements.append(rm.getStatusResultUpdateStatement());
				/*if (r + 1 < rms.length)
				{
					playerResultInsertStatements.append(rm.getPlayerResultInsertStatement() + ", ");
					Logs.debug("Inserted comma at " + r + ".");
				}*/
			}
			
			long time_afterStatementsAppended = System.currentTimeMillis();
			Statement stmt_statusResultUpdate = dbConn.createStatement();
			for (ResultMover rm : rms)
				stmt_statusResultUpdate.addBatch(rm.getStatusResultUpdateStatement());
			
			stmt_statusResultUpdate.executeBatch();
			stmt_statusResultUpdate.close();
			long time_afterStatusResultsUpdated = System.currentTimeMillis();
			
			Statement stmt_playerResultUpdate = dbConn.createStatement();
			String playerResultRaw = playerResultInsertStatements.toString();
			playerResultRaw = playerResultRaw.substring(0, playerResultRaw.length() - 2) + ";";
			//Logs.debug(playerResultRaw);
			stmt_playerResultUpdate.executeUpdate(playerResultRaw);
			stmt_playerResultUpdate.close();
			long time_afterPlayerResultsInserted = System.currentTimeMillis();
			
			long timeTo_selectStatusResults = time_afterStatusResultsSelected - startTime;
			long timeTo_createThreads = time_afterThreadsCreated - time_afterStatusResultsSelected;
			long timeTo_appendStatements = time_afterStatementsAppended - time_afterThreadsCreated;
			long timeTo_updateStatusResults = time_afterStatusResultsUpdated - time_afterStatementsAppended;
			long timeTo_insertPlayerResults = time_afterPlayerResultsInserted - time_afterStatusResultsUpdated;
			double timeTotalMs = (System.currentTimeMillis() - startTime) * 1.0 / 1000;
			
			Logs.info("Total results inserted: " + (i + MAX_ACTIVE_THREADS) + ". Total time: " + timeTotalMs + " s. Time to select status results: " + timeTo_selectStatusResults + " ms. , time to create threads: " + timeTo_createThreads + " ms. , time to append statements: " + timeTo_appendStatements + " ms. , time to update status results: " + timeTo_updateStatusResults + " ms. , time to insert player results: " + timeTo_insertPlayerResults + " ms.");
			dbConn.close();
			System.gc();
			Thread.sleep(2000);
		}
	}
}
