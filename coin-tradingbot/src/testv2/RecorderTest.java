package testv2;

import commonv2.ExchangeID;
import commonv2.MarketID;
import commonv2.MarketID.MarketType;
import exchangev2.PoloniexPublic;
import recorderv2.DBcredentials;

public class RecorderTest
{

	public static void main(String[] args) throws Exception 
	{
		DBcredentials dbCreds = new DBcredentials("localhost", 3008, "tradingbot", "tradingbot", "tradingbot");
		PoloniexPublic poloEx = new PoloniexPublic();
		MarketID market = new MarketID(ExchangeID.POLONIEX, "BTC", "MAID", MarketType.REGULAR);
		TradeOutput to = new DebugTradeOutput();
		StaticTradeRecorder rec = new StaticTradeRecorder(dbCreds, poloEx, market, to, 300000, 1459129379000L);
		rec.determineTimeRangeAndRecord();
	}
}
