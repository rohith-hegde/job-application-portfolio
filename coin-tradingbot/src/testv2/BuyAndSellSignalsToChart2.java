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
package testv2;

import eu.verdelhan.ta4j.AnalysisCriterion;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.Rule;
import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.Trade;
import eu.verdelhan.ta4j.TradingRecord;
import eu.verdelhan.ta4j.analysis.CashFlow;
import eu.verdelhan.ta4j.analysis.criteria.AverageProfitableTradesCriterion;
import eu.verdelhan.ta4j.analysis.criteria.RewardRiskRatioCriterion;
import eu.verdelhan.ta4j.analysis.criteria.TotalProfitCriterion;
import eu.verdelhan.ta4j.analysis.criteria.VersusBuyAndHoldCriterion;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.simple.OpenPriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.trading.rules.CrossedDownIndicatorRule;
import eu.verdelhan.ta4j.trading.rules.CrossedUpIndicatorRule;
import eu.verdelhan.ta4j.trading.rules.StopGainRule;

import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import cryptsyrecorder.DBquerier;
import recorderv2.Candle;
import recorderv2.DBcredentials;
import recorderv2.ExchangeID;
import recorderv2.MarketID;
import recorderv2.PoloniexPublic;
import strategyv2.TimeTradeSeries;
import ta4jexamples.loaders.CsvTradesLoader;
import ta4jexamples.strategies.MovingMomentumStrategy;
import tradingobjs.TimeSeriesTrades;

/**
 * This class builds a graphical chart showing the buy/sell signals of a strategy.
 */
public class BuyAndSellSignalsToChart2 {

    /**
     * Builds a JFreeChart time series from a Ta4j time series and an indicator.
     * @param tickSeries the ta4j time series
     * @param indicator the indicator
     * @param name the name of the chart time series
     * @return the JFreeChart time series
     */
    private static org.jfree.data.time.TimeSeries buildChartTimeSeries(TimeSeries tickSeries, Indicator<Decimal> indicator, String name) {
        org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries(name);
        for (int i = 0; i < tickSeries.getTickCount(); i++) {
            Tick tick = tickSeries.getTick(i);
            chartTimeSeries.add(new Minute(tick.getEndTime().toDate()), indicator.getValue(i).toDouble());
        }
        return chartTimeSeries;
    }

    /**
     * Runs a strategy over a time series and adds the value markers
     * corresponding to buy/sell signals to the plot.
     * @param series a time series
     * @param strategy a trading strategy
     * @param plot the plot
     */
    private static void addBuySellSignals(TimeSeries series, Strategy strategy, XYPlot plot) {
        // Running the strategy
        List<Trade> trades = series.run(strategy).getTrades();
        
        // Adding markers to plot
        for (Trade trade : trades) {
            // Buy signal
            double buySignalTickTime = new Minute(series.getTick(trade.getEntry().getIndex()).getEndTime().toDate()).getFirstMillisecond();
            Marker buyMarker = new ValueMarker(buySignalTickTime);
            buyMarker.setPaint(Color.GREEN);
            buyMarker.setLabel("B");
            plot.addDomainMarker(buyMarker);
            // Sell signal
            double sellSignalTickTime = new Minute(series.getTick(trade.getExit().getIndex()).getEndTime().toDate()).getFirstMillisecond();
            Marker sellMarker = new ValueMarker(sellSignalTickTime);
            buyMarker.setPaint(Color.RED);
            sellMarker.setLabel("S");
            plot.addDomainMarker(sellMarker);
        }
    }

    /**
     * Displays a chart in a frame.
     * @param chart the chart to be displayed
     */
    private static void displayChart(JFreeChart chart) {
        // Chart panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(1500, 1000));
        // Application frame
        ApplicationFrame frame = new ApplicationFrame("Ta4j example - Buy and sell signals to chart");
        frame.setContentPane(panel);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception
    {

        // Getting the time series
        //TimeSeries series = CsvTradesLoader.loadBitstampSeries();
        // Building the trading strategy
       // Strategy strategy = MovingMomentumStrategy.buildStrategy(series);

    	//final int marketID = 3, tickTimeS = 900;
    	//final long endTimeS = ((System.currentTimeMillis() - (86400 * 1000 * 600)) / 1000) - (3600 * 0), startTimeS = endTimeS - (3600 * 600);
    	
    	long endTimeMs = System.currentTimeMillis();
    	long startTimeMs = endTimeMs - (1 * 7 * 24 * 60 * 60 * 1000); // W D H M S ms
    	long intervalMs = 5 * 60 * 1000;
    	long periodMs =  5 * 60 * 1000;
    	
    	DBcredentials dbCreds = new DBcredentials("localhost", 3008, "tradingbot", "tradingbot", "tradingbot");
		PoloniexPublic poloEx = new PoloniexPublic();
		MarketID market = new MarketID(ExchangeID.POLONIEX.ordinal(), "BTC", "ETH");
    	//List<recorderv2.Trade> trades = poloEx.getTradeHistory(startTimeMs, endTimeMs, market);
    	//System.out.println(trades.size());
    	List<Candle> cl = poloEx.getCandlestickHistory(startTimeMs, endTimeMs, periodMs, market);
    	
    	//TimeTradeSeries tts = new TimeTradeSeries(startTimeMs, endTimeMs, intervalMs, trades);
    	TimeSeries series = new TimeTradeSeries(startTimeMs, endTimeMs, intervalMs, cl, false).getTimeSeries();
    	
    	System.out.println();
        // Getting the close price of the ticks
        Decimal firstClosePrice = series.getTick(0).getClosePrice();
        System.out.println("First c"
        		+ "lose price: " + firstClosePrice.toDouble());
        // Or within an indicator:
        Indicator closePrice = new ClosePriceIndicator(series);
        // Here is the same close price:
        System.out.println(firstClosePrice.isEqual((Decimal) closePrice.getValue(0))); // equal to firstClosePrice

        // Getting the simple moving average (SMA) of the close price over the last 5 ticks
       // SMAIndicator qshortSma = new SMAIndicator(closePrice, 3);
        /*EMAIndicator shortSma = new EMAIndicator(closePrice, 9);
        // Here is the 5-ticks-SMA value at the 42nd index
        //System.out.println("5-ticks-SMA value at the 42nd index: " + shortSma.getValue(42).toDouble());

        // Getting a longer SMA (e.g. over the 30 last ticks)
        EMAIndicator longSma = new EMAIndicator(closePrice, 3);*/
       
        //EMAIndicator shortSma = new EMAIndicator(closePrice, 18);
        //EMAIndicator longSma = new EMAIndicator(closePrice, 6);
        
        
        Indicator mediumMA = new EMAIndicator(closePrice, 30);
        Indicator shortMA = new EMAIndicator(closePrice, 5);
        //Indicator longMA = new EMAIndicator(closePrice, 6);
        //EMAIndicator veryLongSma = new EMAIndicator(closePrice, 36);
        //SMAIndicator longerSma = new SMAIndicator(closePrice, 12);


        // Ok, now let's building our trading rules!

        // Buying rules
        // We want to buy:
        //  - if the 5-ticks SMA crosses over 30-ticks SMA
        //  - or if the price goes below a defined price (e.g $800.00)
        Rule buyingRule = new CrossedUpIndicatorRule(shortMA, mediumMA);
        //Rule buyingRule = new CrossedUpIndicatorRule(mediumMA, shortMA);
        //.or(new CrossedDownIndicatorRule(closePrice, Decimal.valueOf("0.00000400")));
        
        // Selling rules
        // We want to sell:
        //  - if the 5-ticks SMA crosses under 30-ticks SMA
        //  - or if if the price looses more than 10%
        //  - or if the price earns more than 10%
        Rule sellingRule = new CrossedDownIndicatorRule(shortMA, mediumMA);
        //Rule sellingRule = new CrossedDownIndicatorRule(mediumMA, shortMA);
        
        // Running our juicy trading strategy...
        Strategy strategy = new Strategy(buyingRule, sellingRule);
        TradingRecord tradingRecord = series.run(strategy);
        
        List<Trade> trades = tradingRecord.getTrades();
        for (int t = 0; t < trades.size(); t++)
        {
        	Trade tr = trades.get(t);
        	System.out.println("Trade " + t + ": " + tr.toString());
        }
        
        System.out.println("Number of trades for our strategy: " + tradingRecord.getTradeCount());

        // Analysis

        // Getting the cash flow of the resulting trades
        CashFlow cashFlow = new CashFlow(series, tradingRecord);
        System.out.println("Cash flows: ");
        
        for (int i = 0; i < cashFlow.getSize(); i++)
        {
        	//System.out.println(cashFlow.getValue(i));
        }
        
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
    	
        /**
         * Building chart datasets
         */
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(buildChartTimeSeries(series, new ClosePriceIndicator(series), "Poloniex market " + market));

        dataset.addSeries(buildChartTimeSeries(series, mediumMA, "Medium moving average"));
        dataset.addSeries(buildChartTimeSeries(series, shortMA, "Short moving average"));
        
        /**
         * Creating the chart
         */
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Poloniex market " + market, // title
                "Date", // x-axis label
                "Price", // y-axis label
                dataset, // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
                );
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MM-dd HH:mm"));

        /**
         * Running the strategy and adding the buy and sell signals to plot
         */
        addBuySellSignals(series, strategy, plot);

        /**
         * Displaying the chart
         */
        displayChart(chart);
    }
}
