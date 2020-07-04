package exchangev2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.hammereditor.designutilities.errors.ValueNotFoundException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import commonv2.Candle;
import commonv2.MarketID;
import commonv2.Trade;
import configv2.Config;

public class PoloniexPublic extends ExchangePublic 
{
	public static long timeOffsetMs = 4 * 60 * 60 * 1000; // = -4 * ; //default value= 4 hours. used to make sure the stored times are UTC
	private boolean returnOnlyClosedCandles;
	
	public PoloniexPublic() throws ValueNotFoundException
	{
		//timeOffsetMs = ((int)Config.config.get("localTimeDiffFromUTChours")) * 60 * 60 * 1000;
		Config.config.addDependentKey("localTimeDiffFromUTCmin");
		Config.config.addDependentKey("pp_returnOnlyClosedCandles");
		timeOffsetMs = (int)Config.config.get("localTimeDiffFromUTCmin") * 60 * 1000;
		returnOnlyClosedCandles = (boolean)Config.config.get("pp_returnOnlyClosedCandles");
	}
	
	/**
	 * Get trade list with period offset
	 * @param candleTimeS
	 */
	public List<Trade> getTradeHistory(long startMs, long endMs, MarketID market, int candleTimeS) throws Exception
	{
		return getTradeHistory(startMs - (candleTimeS * 1000), endMs - (candleTimeS * 1000), market);
	}
	
	@Override
	public List<Trade> getTradeHistory(long startMs, long endMs, MarketID market) throws Exception 
	{
		JSONArray rootObj = null;
		List<Trade> trades = new ArrayList<Trade> ();
				
		try
		{
			final String url = "https://poloniex.com/public?command=returnTradeHistory&currencyPair=" + market.getMasterCurrency() + "_" + market.getTradeCurrency() + "&start=" + (startMs / 1000) + "&end=" + (endMs / 1000);
			System.out.println(url);
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
				long tradeID = (long)tradeObj.get("tradeID");
				String date = (String)tradeObj.get("date");
				String initialOrderType = (String)tradeObj.get("type");
				double rate = Double.parseDouble((String)tradeObj.get("rate"));
				double tradeTotal = Double.parseDouble((String)tradeObj.get("amount"));
				double masterTotal = Double.parseDouble((String)tradeObj.get("total"));
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date dateObj = sdf.parse(date);
				long timeMs = dateObj.getTime();
				
				Trade trade = new Trade(tradeID, market, timeMs - timeOffsetMs, initialOrderType, rate, tradeTotal, masterTotal); //timeMs + timeOffsetMs
				trades.add(trade);
				//System.out.println(trade);
			}
			catch (Exception e) {
				throw new Exception("Error while parsing trade data", e);
			}
		}
		
		return trades;
	}
	
	public List<Candle> getCandlestickHistory(long startMs, long endMs, long periodMs, MarketID market) throws Exception
	{
		JSONArray rootObj = null;
		List<Candle> candles = new ArrayList<Candle> ();
		///boolean resultZeroed = false;
		//int zeroedResultRetryNum = 0;
		
		if (returnOnlyClosedCandles)
		{
			startMs -= periodMs;
			endMs -= periodMs;
		}
				
		try
		{
			final String url = "https://poloniex.com/public?command=returnChartData&currencyPair=" + market.getMasterCurrency() + "_" + market.getTradeCurrency() + "&start=" + (startMs / 1000) + "&end=" + (endMs / 1000) + "&period=" + (periodMs / 1000);
			System.out.println(url);
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
				long timeMs = (long)tradeObj.get("date") * 1000;
				timeMs += timeOffsetMs;
				
				double highRate = new Double(tradeObj.get("high").toString());
				double lowRate = new Double(tradeObj.get("low").toString());
				double openRate = new Double(tradeObj.get("open").toString());
				double closeRate = new Double(tradeObj.get("close").toString());
				double weightedAvgRate = new Double(tradeObj.get("weightedAverage").toString());
				//double volumeMasterC = (double)tradeObj.get("volume");
				
				double volumeMasterC = 0;
				
				Object tvObj = tradeObj.get("volume");
				if (tvObj.toString().equals("0"))
					volumeMasterC = (long)tvObj;
				else
					volumeMasterC = (double)tvObj;
				
				//double volumeTradeC = (double)tradeObj.get("quoteVolume");
				double volumeTradeC = 0;
				
				Object tcObj = tradeObj.get("quoteVolume");
				if (tcObj instanceof Long)
					volumeTradeC = (long)tcObj;
				else
					volumeTradeC = (double)tcObj;
				
				//Candle c = new Candle(market, timeMs, periodMs, highRate, lowRate, openRate, closeRate, volumeMasterC, volumeTradeC, weightedAvgRate);
				Candle c = new Candle(market, timeMs / 1000, periodMs / 1000, highRate, lowRate, openRate, closeRate, volumeMasterC, volumeTradeC, weightedAvgRate);
				candles.add(c);
			}
			catch (Exception e) {
				throw new Exception("Error while parsing trade data", e);
			}
		}
		
		return candles;
	}
	
	
}
