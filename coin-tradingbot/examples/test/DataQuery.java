package test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ta4jexamples.loaders.CsvTicksLoader;
import ta4jexamples.loaders.CsvTradesLoader;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;

public class DataQuery
{
	
	private static String db_ipAddr = "eu.hammereditor.net";
	private static int db_port = 3008;
	private static String db_schemaName = "cryptsy_tradebot";
	private static String db_username = "Tradebot";
	private static String db_password = "!s31bRb2c459f0tb";
	

	public static void main(String[] args) throws Exception
	{
		//new Long(null);
		//new Long("null");
		new Long("4353456345");
		
		int limit = 100, marketID = 3;
		Utils.setTrustAllCerts();
		//getTrades(limit, marketID);
		getTradesWithAuth(3);
	}
	
	public static void getTradesWithAuth(int marketID) throws Exception
	{
		URL url = new URL("https://api.cryptsy.com/api?Key=688ce5ccc4e030aa49cd4e34e28d8a0b0f04a526");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();           
		
		conn.setDoOutput( true );
		conn.setInstanceFollowRedirects( false );
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
		conn.setRequestProperty( "charset", "utf-8");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");
		
		DataOutputStream connOut = new DataOutputStream(conn.getOutputStream());
		connOut.writeBytes("Sign=f66506dd8eb11738a35307f130f1ee1e6e79d38d40ddd70108a875a1737d981852290dada1838b0d&nonce=1&method=markettrades");
		connOut.flush(); connOut.close();
		
		BufferedReader netIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String currL = null;
		while ((currL = netIn.readLine()) != null)
			System.out.println(currL);
		netIn.close();
		conn.disconnect();
		
	}
	
	public static void getTrades(int limit, int marketID) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		
		URLConnection url = new URL("https://api.cryptsy.com/api/v2/markets/" + marketID + "/tradehistory?limit=" + limit).openConnection();
		url.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");
		BufferedReader netIn = new BufferedReader(new InputStreamReader(url.getInputStream()));
		
		String currL = null;
		while ((currL = netIn.readLine()) != null)
			sb.append(currL);
		netIn.close();
		
		JSONParser p = new JSONParser();
		JSONObject rootObj = (JSONObject)p.parse(sb.toString());
		JSONArray dataArray = (JSONArray)rootObj.get("data");
		StringBuilder sqlSB = new StringBuilder("INSERT INTO trades VALUES ");
		
		for (int i = 0; i < dataArray.size(); i++)
		{
			JSONObject currTradeObj = (JSONObject)dataArray.get(i);
			sqlSB.append("(");
			
			sqlSB.append(currTradeObj.get("tradeid") + ", ");
			sqlSB.append(currTradeObj.get("timestamp") + ", ");
			sqlSB.append("\'" + currTradeObj.get("initiate_ordertype") + "\', ");
			sqlSB.append(currTradeObj.get("tradeprice") + ", ");
			sqlSB.append(currTradeObj.get("quantity") + ", ");
			sqlSB.append(currTradeObj.get("total") + ", ");
			sqlSB.append("" + marketID);
			
			sqlSB.append(")");
			if (i + 1 < dataArray.size())
				sqlSB.append(", ");
		}
		
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection("jdbc:mysql://" + db_ipAddr + ":" + db_port + "/" + db_schemaName, db_username, db_password);
		stmt = conn.createStatement();
		
		String insertStmtStr = sqlSB.toString();
		stmt.executeUpdate(insertStmtStr);
		try { stmt.close(); conn.close(); } catch (Exception e) {}
		
	}
	
	public static TimeSeries loadCryptsyBQCseries(int marketID, int tickTimeS)
	{
		try
		{
			Connection conn = null;
			Statement stmt = null;
			conn = DriverManager.getConnection("jdbc:mysql://" + db_ipAddr + ":" + db_port + "/" + db_schemaName, db_username, db_password);
			stmt = conn.createStatement();
			
			String stmtStr = "SELECT * FROM trades WHERE MarketID = " + marketID + ";"; //AND OrderType = \'Sell\';";
			ResultSet res = stmt.executeQuery(stmtStr); //naturally ordered from old to new
			JSONArray resArray = new JSONArray();
			
			while (res.next())
			{
				JSONObject currResult = new JSONObject();
				currResult.put("MarketID", res.getLong("MarketID"));
				currResult.put("TradeID", res.getLong("TradeID"));
				currResult.put("Timestamp", res.getLong("Timestamp"));
				currResult.put("OrderType", res.getString("OrderType"));
				currResult.put("PricePerCoin", res.getDouble("PricePerCoin"));
				currResult.put("CoinQty", res.getDouble("CoinQty"));
				currResult.put("TotalPriceBTC", res.getDouble("TotalPriceBTC"));
				resArray.add(currResult);
				System.out.println(currResult.toJSONString());
			}
			
			try { stmt.close(); conn.close(); } catch (Exception e) {}
			
			//convert array to TimeSeries object
			
			// Getting the first and last trade timestamps
			JSONObject firstTradeObj = (JSONObject)resArray.get(0), lastTradeObj = (JSONObject)resArray.get(resArray.size() - 1);
            DateTime beginTime = new DateTime((Long)firstTradeObj.get("Timestamp") * 1000);
            DateTime endTime = new DateTime((Long)lastTradeObj.get("Timestamp") * 1000);
            
            System.out.println("Creating tick objects... begin time: " + beginTime + ", end time: " + endTime);
            List<Tick> ticks = null;
         // Building the empty ticks (every 300 seconds, yeah welcome in Bitcoin world)
            ticks = CsvTradesLoader.buildEmptyTicks(beginTime, endTime, tickTimeS);
            System.out.println(ticks.size());
            // Filling the ticks with trades
            for (int i = 0; i < resArray.size(); i++) 
            {
            	JSONObject currTradeObj = (JSONObject)resArray.get(i);
                DateTime tradeTimestamp = new DateTime((long)currTradeObj.get("Timestamp") * 1000);
               
                for (Tick tick : ticks) {
                    if (tick.inPeriod(tradeTimestamp)) {
                    	 double pricePerCoin = (double)currTradeObj.get("PricePerCoin");
                    	 double coinQty = (double)currTradeObj.get("CoinQty");
                        tick.addTrade(coinQty, pricePerCoin);
                    }
                }
            }
            
            // Removing still empty ticks
            CsvTradesLoader.removeEmptyTicks(ticks);
            System.out.println(ticks.size());
            return new TimeSeries("cryptsy_market_" + marketID + "_trades", ticks);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
