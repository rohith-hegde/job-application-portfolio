package recorderv2;
import java.util.List;

import commonv2.MarketID;
import commonv2.Trade;

public class TradeRecorder implements ChartFeedable<Trade>
{
	private DBconnector dbc;
	
	public TradeRecorder(DBcredentials dbCreds) throws Exception
	{
		dbc = new DBconnector(dbCreds);
	}
	
	/*protected void recordTradeHistory(long startMs, long endMs) throws Exception
	{
		List<Trade> trades = null;
		
		try { trades = exchange.getTradeHistory(startMs, endMs, market); } catch (Exception e) {
			throw new Exception("Error while getting trade data from market", e);
		}
		
		try { dbc.storeTradeHistory(market, trades); } catch (Exception e) {
			throw new Exception("Error while storing trades in DB", e);
		}
		
		//additional real-time trade output
		if (to != null)
			to.acceptTrades(trades);
	}*/
	
	private void recordTradeHistory(List<Trade> graph) throws Exception
	{
		dbc.storeTradeHistory(graph);
	}
	
	public void disconnect()
	{
		try {
			dbc.disconnect();
		} catch (Exception e) {
		} 
	}

	public void feedChartData(List<Trade> graph, MarketID mk) throws Exception
	{
		//going to assume all trades in the list have the same marketID as 'mk' and ignore marketID
		try { recordTradeHistory(graph); } catch(Exception e) {
			throw new Exception("Error while saving trade data to DB", e);
		}
	}
}
