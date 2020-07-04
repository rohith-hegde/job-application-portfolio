package exchangev2;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import commonv2.*;


public abstract class ExchangePublic
{
	
	/**
	 * Retreives a list of trades from the exchange, between the specified times, for the specified market
	 * @param startMs the trades's time will be >= this
	 * @param endMs the returned trades's time will be < this
	 * @param market the market on the exchange to use
	 * @return a list of trades, beginning with the oldest and ending with the newest
	 * @throws Exception
	 */
	public abstract List<Trade> getTradeHistory(long startMs, long endMs, MarketID market) throws Exception;
	
	/**
	 * Retreives a list of candlesticks from the exchange, between the specified times, for the specified market
	 * @param startMs the candles's time will be >= this
	 * @param endMs the returned candles's time will be < this
	 * @param market the market on the exchange to use
	 * @return a list of candles, beginning with the oldest and ending with the newest
	 * @throws Exception
	 */
	public abstract List<Candle> getCandlestickHistory(long startMs, long endMs, long periodMs, MarketID market) throws Exception;
	
	protected Object simpleJsonGET(String url) throws Exception
	{
		String json = null;
		
		try
		{
			json = Utils.getDataFromURL(url);
			//System.out.println(json);
		} catch (Exception e) {
			throw new Exception("Error while downloading trade history", e);
		}
		
		try
		{
			JSONParser p = new JSONParser();
			Object rootObj = p.parse(json);
			return rootObj;
		}
		catch (Exception e) {
			throw new Exception("Error while parsing JSON return result", e);
		}
	}
}
