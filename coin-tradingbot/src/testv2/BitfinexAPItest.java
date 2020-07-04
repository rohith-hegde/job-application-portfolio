package testv2;

import java.util.Arrays;

import commonv2.ExchangeID;
import commonv2.MarketID;
import commonv2.MarketID.MarketType;
import commonv2.Trade;
import configv2.Config;
import configv2.Logs;
import net.hammereditor.designutilities.design.RunnableThrowableCallback;
import recorderv2.BitfinexPollingTradeFeeder;
import recorderv2.ChartFeeder;
import recorderv2.DBconnector;
import recorderv2.DBcredentials;
import recorderv2.TradeRecorder;

public class BitfinexAPItest {

	public static void main(String[] args) throws Exception
	{
		AlertTestMain.loadTestConfiguration();
		DBcredentials dbi = new DBcredentials((String)Config.config.get("db_hostname"), (int)Config.config.get("db_port"), (String)Config.config.get("db_username"), (String)Config.config.get("db_password"), (String)Config.config.get("db_schema"));
		
		System.out.println(Arrays.toString(ExchangeID.values()));
		testTradeFeeding(dbi);
	}
	
	private static void testTradeFeeding(DBcredentials dbi) throws Exception
	{
		RunnableThrowableCallback cfErrorCB = new RunnableThrowableCallback() {
			public void onError(Exception arg0) {
				System.out.println("error: " + arg0.getMessage());
				arg0.printStackTrace();
			}
		};
		
		ChartFeeder<Trade> cf = new BitfinexPollingTradeFeeder(dbi, cfErrorCB, 60);
		//first test without ChartFeedCache, then with 21387418
		MarketID bfxBtcUsd = new MarketID(ExchangeID.BITFINEX, "USD", "BTC", MarketType.REGULAR);
		cf.addConsumer(bfxBtcUsd, new TradeRecorder(dbi));
		
		cf.initialFeed(-1);
		Logs.log.info(BitfinexAPItest.class.getName() + ".testTradeFeeding(): initial feed done. starting thread...");
		new Thread(cf).start();
	}

}
