/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Marc de Verdelhan & respective authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ta4jexamples;

import java.util.List;

import cryptsyrecorder.DBquerier;
import eu.verdelhan.ta4j.AnalysisCriterion;
import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Rule;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.TradingRecord;
import eu.verdelhan.ta4j.analysis.CashFlow;
import eu.verdelhan.ta4j.analysis.criteria.AverageProfitableTradesCriterion;
import eu.verdelhan.ta4j.analysis.criteria.RewardRiskRatioCriterion;
import eu.verdelhan.ta4j.analysis.criteria.TotalProfitCriterion;
import eu.verdelhan.ta4j.analysis.criteria.VersusBuyAndHoldCriterion;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.trading.rules.CrossedDownIndicatorRule;
import eu.verdelhan.ta4j.trading.rules.CrossedUpIndicatorRule;
import eu.verdelhan.ta4j.trading.rules.StopGainRule;
import eu.verdelhan.ta4j.trading.rules.StopLossRule;
import ta4jexamples.loaders.CsvTradesLoader;
import test.DataQuery;
import tradingobjs.TimeSeriesTrades;
import tradingobjs.Trade;

/**
 * Quickstart for ta4j.
 * <p>
 * Global example.
 */
public class Quickstart {

    public static void main(String[] args) throws Exception
    {

        // Getting a time series (from any provider: CSV, web service, etc.)
        //TimeSeries series = DataQuery.loadCryptsyBQCseries(3, 300); // //CsvTradesLoader.loadBitstampSeries();
    	
    	final int marketID = 132, tickTimeS = 600;
    	final long endTimeS = System.currentTimeMillis() / 1000, startTimeS = endTimeS - (3600 * 336);
    	
    	DBquerier dbq = new DBquerier(marketID);
    	List <Trade> trades = dbq.getTrades(startTimeS, endTimeS);
    	TimeSeriesTrades tst = new TimeSeriesTrades(startTimeS * 1000, endTimeS * 1000, tickTimeS, trades);
    	TimeSeries series = tst.getTimeSeries();

        // Getting the close price of the ticks
        Decimal firstClosePrice = series.getTick(0).getClosePrice();
        System.out.println("First close price: " + firstClosePrice.toDouble());
        // Or within an indicator:
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        // Here is the same close price:
        System.out.println(firstClosePrice.isEqual(closePrice.getValue(0))); // equal to firstClosePrice

        // Getting the simple moving average (SMA) of the close price over the last 5 ticks
       // SMAIndicator shortSma = new SMAIndicator(closePrice, 3);
        SMAIndicator shortSma = new SMAIndicator(closePrice, 30);
        // Here is the 5-ticks-SMA value at the 42nd index
        //System.out.println("5-ticks-SMA value at the 42nd index: " + shortSma.getValue(42).toDouble());

        // Getting a longer SMA (e.g. over the 30 last ticks)
        SMAIndicator longSma = new SMAIndicator(closePrice, 90);
        
        //SMAIndicator longerSma = new SMAIndicator(closePrice, 12);


        // Ok, now let's building our trading rules!

        // Buying rules
        // We want to buy:
        //  - if the 5-ticks SMA crosses over 30-ticks SMA
        //  - or if the price goes below a defined price (e.g $800.00)
        Rule buyingRule = new CrossedUpIndicatorRule(shortSma, longSma);
                //.or(new CrossedDownIndicatorRule(closePrice, Decimal.valueOf("0.00000400")));
        
        // Selling rules
        // We want to sell:
        //  - if the 5-ticks SMA crosses under 30-ticks SMA 
        //  - or if if the price looses more than 10%
        //  - or if the price earns more than 10%
        Rule sellingRule = new CrossedDownIndicatorRule(shortSma, longSma);
        
        // Running our juicy trading strategy...
        TradingRecord tradingRecord = series.run(new Strategy(buyingRule, sellingRule));
        System.out.println("Number of trades for our strategy: " + tradingRecord.getTradeCount());


        // Analysis

        // Getting the cash flow of the resulting trades
        CashFlow cashFlow = new CashFlow(series, tradingRecord);
        
        // Getting the profitable trades ratio
        AnalysisCriterion profitTradesRatio = new AverageProfitableTradesCriterion();
        System.out.println("Profitable trades ratio: " + profitTradesRatio.calculate(series, tradingRecord));
        // Getting the reward-risk ratio
        AnalysisCriterion rewardRiskRatio = new RewardRiskRatioCriterion();
        System.out.println("Reward-risk ratio: " + rewardRiskRatio.calculate(series, tradingRecord));

        // Total profit of our strategy
        // vs total profit of a buy-and-hold strategy
        AnalysisCriterion vsBuyAndHold = new VersusBuyAndHoldCriterion(new TotalProfitCriterion());
        System.out.println("Our profit vs buy-and-hold profit: " + vsBuyAndHold.calculate(series, tradingRecord));

        // Your turn!
        TotalProfitCriterion tpc = new TotalProfitCriterion();
        System.out.println("Total profit: " + tpc.calculate(series, tradingRecord));
    }
}
