package codewriter;

import java.net.InetAddress;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mysql.jdbc.Connection;

import config.Config;

public class SQLcodeWriter 
{
	public static void main(String[] args) throws Exception
	{
		//System.out.println(codeForCheckingDailyTotalPlayerCount());
		List <StatementInfo> statStmts = codeForCheckingDailyTotalPlayerCount(); //codeForCheckingDailyServerCount(); //codeForCheckingDailyTotalPlayerCount();
		//String ipAddr = InetAddress.getByName("eu2.hammereditor.net").getHostAddress();
		
		//List <StatementInfo> nodeCstmts = codeForCheckingAveragePlayercountOnNode(ipAddr, 2);
		printFormattedStatResults(statStmts);
		//printFormattedStatResults(nodeCstmts);
	}
	
	private static void printFormattedStatResults(List <StatementInfo> statStmts) throws Exception
	{
		try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            throw new Exception("DB driver not found", e);
        }
		
		Connection dbConn = (Connection)DriverManager.getConnection("jdbc:mysql://eu.hammereditor.net:3008/unturned3sl", "Administrator", "!s31bRb2c348a3eu");
		Statement stmt = dbConn.createStatement();
		
		for (int i = 0; i < statStmts.size(); i++)
		{
			int exCount = -1;
			
			while (exCount != 0)
			{
				try
				{
					StatementInfo currSI = statStmts.get(i);
					ResultSet rs = stmt.executeQuery(currSI.getSqlStatement());
					rs.next();
					int count = rs.getInt(1);
					
					System.out.println(count);
					rs.close();
					exCount = 0;
				}
				catch (Exception e) {
					System.out.println(exCount + "th error while getting result " + i);
					exCount++;
				}
			}
				
		}
		
		//System.out.println(statStmts.size() + " results");
		
		stmt.close(); dbConn.close();
	}
	
	/*private static String codeForCheckingDailyServerCount()
	{
		String base = "SELECT COUNT(*) FROM statusresults WHERE TimeRecordedMs ";
		StringBuilder queries = new StringBuilder();
		long currT = 1429315200 * 1000; //System.currentTimeMillis();
		int days = 5;
		final long MS_IN_DAY = 86400 * 1000;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
		
		for (int i = 0; i < days; i++)
		{
			long maxT = currT - (MS_IN_DAY * i);
			long minT = currT - (MS_IN_DAY * (i + 1));
			String query = base + ">= " + minT + " AND TimeRecordedMs < " + maxT + ";";
			queries.append(query + "\n");
		}
		
		return queries.toString();
	}*/
	
	/**
	 * 
	 * @return element contents: [full statement, TimeRecordedMs start, TimeRecordedMs end, formatted start, formatted end]
	 */
	private static List <StatementInfo> codeForCheckingDailyTotalPlayerCount()
	{
		String base = "SELECT SUM(PlayerCount) FROM statusresults WHERE TimeRecordedMs ";
		ArrayList <StatementInfo> stmts = new ArrayList <StatementInfo> ();
		
		long currT = System.currentTimeMillis(); //1429315200L * 1000;
		int days = 477;
		final long MS_IN_DAY = 86400 * 1000;
		
		for (int i = 0; i < days; i++)
		{
			long maxT = currT - (MS_IN_DAY * i);
			long minT = currT - (MS_IN_DAY * (i + 1));
			String query = base + ">= " + minT + " AND TimeRecordedMs < " + maxT + ";";
			
			
			StatementInfo info = new StatementInfo(query, minT, maxT);
			stmts.add(info);
		}
		
		Collections.reverse(stmts);
		return stmts;
	}
	
	private static List <StatementInfo> codeForCheckingDailyServerCount()
	{
		String base = "SELECT COUNT(*) FROM statusresults WHERE TimeRecordedMs ";
		ArrayList <StatementInfo> stmts = new ArrayList <StatementInfo> ();
		
		long currT = System.currentTimeMillis();// 1429315200L * 1000;
		int days = 477;
		int startDay = 0;
		final long MS_IN_DAY = 86400 * 1000;
		
		for (int i = startDay; i < days; i++)
		{
			long maxT = currT - (MS_IN_DAY * i);
			long minT = currT - (MS_IN_DAY * (i + 1));
			String query = base + ">= " + minT + " AND TimeRecordedMs < " + maxT + ";";
			
			StatementInfo info = new StatementInfo(query, minT, maxT);
			stmts.add(info);
		}
		
		Collections.reverse(stmts);
		return stmts;
	}
	
	private static List <StatementInfo> codeForCheckingAveragePlayercountOnNode(String nodeIPaddr, int pastXdays)
	{
		String base = "SELECT (AVG(PlayerCount) * 6) FROM statusresults WHERE Host_IPaddr = \'" + nodeIPaddr + "\' AND TimeRecordedMs ";
		ArrayList <StatementInfo> stmts = new ArrayList <StatementInfo> ();
		
		long currT = System.currentTimeMillis();// 1429315200L * 1000;
		final long MS_IN_HOUR = 3600 * 1000;
		
		for (int h = 0; h < 24 * pastXdays; h++)
		{
			long maxT = currT - (MS_IN_HOUR * h);
			long minT = currT - (MS_IN_HOUR * (h + 1));
			String query = base + ">= " + minT + " AND TimeRecordedMs < " + maxT + ";";
			System.out.println(query);
			
			StatementInfo info = new StatementInfo(query, minT, maxT);
			stmts.add(info);
		}
		
		Collections.reverse(stmts);
		return stmts;
	}
}
