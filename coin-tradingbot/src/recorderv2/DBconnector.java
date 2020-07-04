package recorderv2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commonv2.ExchangeID;
import commonv2.MarketID;
import commonv2.MarketID.MarketType;
import commonv2.Trade;
import configv2.Logs;

public class DBconnector 
{
	private Connection dbConn;
	private DBcredentials dbCred;
	private Map<MarketID, Integer> marketIDs;
	//private List<Trade> lastStoredTrades;
	
	/**
	 * Creates a new DBquerier and connects
	 * @throws Exception If there was an error connecting to the database
	 */
	public DBconnector(DBcredentials dbCred) throws Exception
	{
		this.dbCred = dbCred;
		dbConn = null;
		connect();
		initializeMarketIDs();
		//lastStoredTrades = new ArrayList<Trade> ();
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
		dbConn = DriverManager.getConnection("jdbc:mysql://" + dbCred.getHostname() + ":" + dbCred.getPort() + "/" + dbCred.getSchema(), dbCred.getUsername(), dbCred.getPassword());
	}
	
	private void initializeMarketIDs() throws Exception
	{
		marketIDs = new HashMap<MarketID, Integer> ();
		
		try
		{
			final String stmtStrF = "SELECT * FROM markets;";
			Statement stmt = dbConn.createStatement();
			ResultSet res = stmt.executeQuery(stmtStrF);
			
			while (res.next())
			{
				int marketID = res.getInt("MarketID");
				ExchangeID exchangeID = ExchangeID.valueOf(res.getString("ExchangeID") + "");
				MarketType mt = MarketType.valueOf(res.getString("MarketType"));
				String masterC = res.getString("MasterCurrency");
				String tradeC = res.getString("TradeCurrency");
				
				MarketID mk = new MarketID(exchangeID, masterC, tradeC, mt);
				marketIDs.put(mk, marketID);
			}
			
			res.close();
			stmt.close();
			//disconnect();
		}
		catch (Exception e) {
			throw new Exception("Error while querying DB", e);
		}
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
	 * 
	 * @param market if null, select trades for all markets
	 * @param startMs
	 * @param endMs
	 * @param limit max. trades to return
	 * @param ascending true = order by tradeID, with highest (i.e. most recent) one at end of list
	 * @return
	 * @throws Exception
	 */
	public List<Trade> getTradeHistory(MarketID market, long startMs, long endMs, int limit, boolean ascending) throws Exception
	{
		if (market != null && !marketIDs.containsKey(market))
			throw new Exception("Unrecognized market \'" + market.toString() + "\' not in database");
		List <Trade> trades = new ArrayList <Trade> ();
		
		try
		{
			String stmtStrF = "SELECT * FROM tradehistory WHERE";
			if (market != null)
				stmtStrF += " MarketID = " + marketIDs.get(market) + " AND";
			stmtStrF += " TimeMs >= " + startMs + " AND TimeMs < " + endMs + "";
			if (ascending)
				stmtStrF += " ORDER BY TradeID DESC";
			stmtStrF += " LIMIT " + limit + ";";
			
			Statement stmt = dbConn.createStatement();
			//System.out.println(stmtStrF);
			ResultSet res = stmt.executeQuery(stmtStrF);
			
			while (res.next())
			{
				long tradeID = res.getLong("TradeID");
				long timeMs = res.getLong("TimeMs");
				String initiatingOrderType = res.getString("InitiatingOrderType");
				double rate = res.getDouble("Rate");
				double tradeTotal = res.getDouble("TradeCurTotal");
				double masterTotal = res.getDouble("MasterCurTotal");
				
				Trade t = new Trade(tradeID, market, timeMs, initiatingOrderType, rate, tradeTotal, masterTotal);
				trades.add(t);
			}
			
			res.close();
			
			stmt.close();
			//disconnect();
		}
		catch (Exception e) {
			throw new Exception("Error while querying DB", e);
		}
		
		return trades;
	}
	
	public void storeTradeHistory(List<Trade> trades) throws Exception
	{
		if (trades.size() == 0)
			return;
		StringBuilder stmtStr = new StringBuilder("INSERT IGNORE INTO tradehistory VALUES ");
		
		for (int i = 0; i < trades.size(); i++)
		{
			Trade currT = trades.get(i);
			if (!containsMarket(currT.getMarket()))
				throw new Exception("Market \'" + currT.getMarket().toString() + "\' for trade \'" + currT.toString() + "\' not found in database");
				
			String tStr = "(" + getIDforMarket(currT.getMarket()) + ", " + currT.getTradeID() + ", \'" + currT.getInitiatingOrderType() + "\', " + currT.getTimeMs() + ", " + currT.getRate() + ", " + currT.getTradeTotal() + ", " + currT.getMasterTotal() + ")";
			//System.out.println(tStr);
			stmtStr.append(tStr);
			if (i + 1 < trades.size())
				stmtStr.append(", ");
		}
		
		stmtStr.append(";");
		
		StringBuilder tradeStr = new StringBuilder("\n");
		for (Trade t : trades)
			tradeStr.append(t.toString() + "\n");
		
		Logs.log.debug(this.getClass().getName() + ".storeTradeHistory(): storing trade list...: " + tradeStr.toString());
		
		try
		{
			Statement stmt = dbConn.createStatement();
			stmt.executeUpdate(stmtStr.toString());
			stmt.close();
		} 
		catch (Exception e) {
			//Logs.error("DBquerier[marketID:" + marketID + "].storeTrades(): " + stmtStr.toString());
			
			throw new Exception("Error while sending statement", e);
		}
	}
	
	private boolean containsMarket(MarketID mk)
	{
		for (MarketID m : marketIDs.keySet())
			if (m.toString().equals(mk.toString()))
				return true;
		return false;
	}
	
	private int getIDforMarket(MarketID mk)
	{
		for (MarketID m : marketIDs.keySet())
			if (m.toString().equals(mk.toString()))
				return marketIDs.get(m);
		return -1;
	}
}
