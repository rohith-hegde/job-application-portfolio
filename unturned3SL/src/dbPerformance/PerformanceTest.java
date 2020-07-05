package dbPerformance;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class PerformanceTest 
{
	//8000 rows / sec. insertion
	
	public static void main(String[] args) throws Exception
	{
		Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.1.2:3008/unturned3sl", "Administrator", "!s31bR");
		Statement stmt;
		StringBuilder stmtSB;
		
		for (int s = 0; s < 25000; s++)
		{
			stmt = conn.createStatement();
			stmtSB = new StringBuilder();
			stmtSB.append("INSERT INTO serverstatus (Host, TimeRecordedMs, Players, VacSecure, Passworded, Dedicated, PlayerCount, MaxPlayers, GameVersion, MapName, Difficulty, VersusMode, Title, OperatingSystem) VALUES ");
			RandomSR rsr;
			String playersStr;
			final int ROWS_PER_STATEMENT = 1000;
			
			for (int i = 0; i < ROWS_PER_STATEMENT; i++)
			{
				rsr = new RandomSR(System.currentTimeMillis());
				playersStr = "";
				for (int j = 0; j < rsr.getPlayers().length; j++) 
					playersStr += rsr.getPlayers()[j].toString() + "_";
				
				stmtSB.append("(\'" + rsr.getHost().getAddress().getHostAddress() + ":" + rsr.getHost().getPort() + "\', " + rsr.getTimeRecorded() + ", \'" + playersStr + "\', " + (rsr.isVACsecured() ? 1 : 0) + ", " + (rsr.isPassworded() ? 1 : 0) + ", " + (rsr.isDedicated() ? 1 : 0) + ", " + rsr.getPlayerCount() + ", " + rsr.getMaxPlayers() + ", \'" + rsr.getGameVersion() + "\', \'" + rsr.getMapName() + "\', \'" + rsr.getDifficulty() + "\', \'" + rsr.getVersusMode() + "\', \'" + rsr.getTitle() + "\', " + rsr.getOS() + ")");
				if (i + 1 < ROWS_PER_STATEMENT)
					stmtSB.append(", ");
			}
			
			//System.out.println(stmtSB.toString());
			stmt.executeUpdate(stmtSB.toString());
			System.out.println(System.currentTimeMillis() + ": " + ((s + 1) * ROWS_PER_STATEMENT) + " rows inserted...");
			stmt.close();
		}
	
		conn.close();
	}
}
