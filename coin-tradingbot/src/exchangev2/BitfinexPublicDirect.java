package exchangev2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import commonv2.Candle;
import commonv2.ExchangeID;
import commonv2.MarketID;
import commonv2.MarketID.MarketType;
import commonv2.Trade;
import configv2.Config;
import configv2.Logs;
import net.hammereditor.designutilities.errors.ValueNotFoundException;
import testv2.AlertTestMain;

/**
 * di
 * @author Anna
 *
 */
public class BitfinexPublicDirect extends ExchangePublic 
{
	public static long timeOffsetMs = 4 * 60 * 60 * 1000; // = -4 * ; //default value= 4 hours. used to make sure the stored times are UTC
	
	public BitfinexPublicDirect() throws ValueNotFoundException
	{
		//timeOffsetMs = ((int)Config.config.get("localTimeDiffFromUTChours")) * 60 * 60 * 1000;
		Config.config.addDependentKey("pp_utcTimeOffsetFromLocalMin");
		//Config.config.addDependentKey("pp_returnOnlyClosedCandles");
		timeOffsetMs = (int)Config.config.get("localTimeDiffFromUTCmin") * 60 * 1000;
		//returnOnlyClosedCandles = (boolean)Config.config.get("pp_returnOnlyClosedCandles");
	}
	
	/**
	 * Does not work
	 */
	public List<Trade> getTradeHistory(long startMs, long endMs, MarketID market) throws Exception 
	{
		/*JSONArray rootObj = null;
		List<Trade> trades = new ArrayList<Trade> ();
				
		try
		{
			int sec = (int)(endMs - startMs) / 1000;
			final double tradesPerSec = 1.0;
			int tradeLimitNum = (int)(sec * tradesPerSec);
			
			final String url = "https://api.bitfinex.com/v1/trades/" + market.getTradeCurrency() + "" + market.getMasterCurrency() + "/?timestamp=" + ((startMs - timeOffsetMs) / 1000) + "&limit_trades=" + tradeLimitNum;
			System.out.println(url);
			rootObj = (JSONArray)simpleJsonGET(url);
		}
		catch (Exception e) {
			throw new Exception("Error while getting API data", e);
		}
		
		boolean lastTradeInTimeRange = true;
		
		for (int i = 0; i < rootObj.size() && lastTradeInTimeRange; i++)
		{
			try
			{
				JSONObject tradeObj = (JSONObject)rootObj.get(i);
				long tradeID = (long)tradeObj.get("tid");
				long timeMs = (long)tradeObj.get("timestamp") * 1000;
				lastTradeInTimeRange = timeMs <= endMs;
				
				if (lastTradeInTimeRange)
				{
					String initialOrderType = (String)tradeObj.get("type");
					double rate = Double.parseDouble((String)tradeObj.get("price"));
					double tradeTotal = Double.parseDouble((String)tradeObj.get("amount"));
					double masterTotal = rate * tradeTotal;
					
					Trade trade = new Trade(tradeID, market, timeMs, initialOrderType, rate, tradeTotal, masterTotal); // + timeOffsetMs
					trades.add(trade);
					//System.out.println(trade);
					//master = USD, trade = BTC
				}
			}
			catch (Exception e) {
				throw new Exception("Error while parsing trade data", e);
			}
		}
		
		//default order returned by API is newest first, so reverse the trades
		List<Trade> revTrades = new ArrayList<Trade> ();
		for (int i = trades.size() - 1; i >= 0; i--)
			revTrades.add(trades.get(i));
		return revTrades;*/
		
		return null;
	}
	
	/**
	 * 
	 * @param startMs
	 * @param maxTrades
	 * @param market
	 * @return trade list with oldest-first order (index 0 is oldest)
	 * @throws Exception
	 */
	public List<Trade> getDescendingTradeHistory(long startMs, int maxTrades, MarketID market) throws Exception
	{
		JSONArray rootObj = null;
		List<Trade> trades = new ArrayList<Trade> ();
				
		try
		{
			final String url = "https://api.bitfinex.com/v1/trades/" + market.getTradeCurrency() + "" + market.getMasterCurrency() + "/?timestamp=" + ((startMs - timeOffsetMs) / 1000) + "&limit_trades=" + maxTrades;
			//System.out.println(url);
			rootObj = (JSONArray)simpleJsonGET(url);
		}
		catch (Exception e) {
			throw new Exception("Error while getting API data", e);
		}
		
		for (int i = 0; i < rootObj.size(); i++)
		{
			try
			{
				JSONObject tradeObj = (JSONObject)rootObj.get(i);
				long tradeID = (long)tradeObj.get("tid");
				long timeMs = (long)tradeObj.get("timestamp") * 1000;
				
				String initialOrderType = (String)tradeObj.get("type");
				double rate = Double.parseDouble((String)tradeObj.get("price"));
				double tradeTotal = Double.parseDouble((String)tradeObj.get("amount"));
				double masterTotal = rate * tradeTotal;
				
				Trade trade = new Trade(tradeID, market, timeMs, initialOrderType, rate, tradeTotal, masterTotal); // + timeOffsetMs
				trades.add(trade);
			}
			catch (Exception e) {
				throw new Exception("Error while parsing trade data", e);
			}
		}
		
		//default order returned by API is oldest first (at index 0), so reverse the trades
		/*List<Trade> revTrades = new ArrayList<Trade> ();
		for (int i = trades.size() - 1; i >= 0; i--)
			revTrades.add(trades.get(i));
		return revTrades;*/
		
		return trades;
	}

	/**
	 * Does not work
	 */
	public List<Candle> getCandlestickHistory(long startMs, long endMs, long periodMs, MarketID market) throws Exception 
	{
		/*List<Trade> trades = null;
		try { trades = getTradeHistory(startMs, endMs, market); } catch (Exception e) {
			throw new Exception("Error while getting trade history", e);
		}
		
		List<Candle> candles = new ArrayList<Candle> ();
		List<Trade> tradesForCurrentCandle;
		
		for (long currCandleEndMs = startMs; currCandleEndMs < endMs; currCandleEndMs += periodMs)
		{
			tradesForCurrentCandle = new ArrayList<Trade> ();
			boolean tradeIsInCC = true;
			Logs.log.debug(this.getClass().getName() + ".getCandlestickHistory(): current candle start time: " + startMs + ", end time: " + endMs);
			Logs.log.debug(this.getClass().getName() + ".getCandlestickHistory(): trade list size: " + trades.size());
			
			while (tradeIsInCC)
			{
				if (trades.size() > 0)
				{
					Trade currT = trades.remove(0);
					tradeIsInCC = currT.getTimeMs() < currCandleEndMs;
					Logs.log.debug(this.getClass().getName() + ".getCandlestickHistory(): currT time: " + currT.getTimeMs() + ", trade belongs to candle? " + tradeIsInCC);
					
					if (tradeIsInCC)
						tradesForCurrentCandle.add(currT);
				}
				else
					tradeIsInCC = false;
			}
			
			candles.add(getCandleFromTrades(tradesForCurrentCandle, startMs, periodMs, market));
		}
		
		return candles;*/
		
		return null;
	}
	
	private static Candle getCandleFromTrades(List<Trade> tr, long startMs, long periodMs, MarketID mk)
	{
		System.out.println(tr);
		double openRate = tr.get(0).getRate();
		double closeRate = tr.get(tr.size() - 1).getRate();
		double highRate = Double.MIN_VALUE;
		double lowRate = Double.MAX_VALUE;
		
		double totalPV = 0;
		double volumeMaster = 0;
		double volumeTrade = 0;
		
		for (Trade t : tr)
		{
			if (t.getRate() > highRate)
				highRate = t.getRate();
			if (t.getRate() < lowRate)
				lowRate = t.getRate();
			
			totalPV += t.getRate() * t.getTradeTotal();
			volumeMaster += t.getMasterTotal();
			volumeTrade += t.getTradeTotal();
		}
		
		double weightedAvg = totalPV / tr.size();
		return new Candle(mk, startMs, periodMs, highRate, lowRate, openRate, closeRate, volumeMaster, volumeTrade, weightedAvg);
	}

	public static void main(String[] args) throws Exception  
	{
		/*HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://api.bitfinex.com/v1" + "/trades/" + "BTCUSD" + "/?limit_trades=" + "5");
		HttpResponse res = client.execute(request);
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
			
		System.out.println(result.toString());*/
		
		/*List<Trade> trades = bp.getTradeHistory(System.currentTimeMillis() - (1 * 30 * 1000), System.currentTimeMillis(), market);
		
		System.out.println(trades);
		System.out.println(trades.size());
		*/
		
		AlertTestMain.loadTestConfiguration();
		BitfinexPublicDirect bp = new BitfinexPublicDirect();
		MarketID market = new MarketID(ExchangeID.BITFINEX, "USD", "BTC", MarketType.MARGIN);
		
		long startTimeS = 1466773800;
		int periodS = 60;
		List<Trade> testTL = bp.getTradeHistory(startTimeS * 1000, (startTimeS + periodS) * 1000, market);
		Candle c = getCandleFromTrades(testTL, startTimeS * 1000, periodS * 1000, market);
		
		//System.out.printf("Test candle: volume: %.3f, ", c.getVolume());
		/*List<Candle> candles = bp.getCandlestickHistory(System.currentTimeMillis() - (6 * 60 * 1000), System.currentTimeMillis(), 3 * 60 * 1000, market);
		System.out.println(candles.size());
		
		for (Candle c : candles)
		{
			System.out.println(c);
		}*/
	}

}

