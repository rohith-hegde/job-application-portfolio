# Coin Trading Bot

![](media/cryptocurrency-trading-bot-stock.jpg)

### A program that automatically trades crypto coins to make a profit

In 2016, I was very interested in crypto currencies like Bitcoin, Ethereum and Monero. I also just learned about Wall Street and high-frequency trading. I wanted to create my own trading bot and possibly make a profit and become rich!

Unfortunately, I abandoned the project after a few months. I learned about the efficient market hypothesis. It's impossible to consistently make a profit trading because assets are always priced correctly. My robot wouldn't be able to compete with professionals that have low-latency connections and advanced algorithms.

Anyway, here's what I achieved so far:

### Exchange connections

My trading bot supports the Poloniex and Bitfinex exchange websites. It uses the REST APIs of these exchanges to get information on coin prices, volumes, order books and other technical data. It can also perform actions such as making trades and canceling orders.

### Technical analysis

The robot supports indicators like price crosses, simple moving averages (SMAs), exponential MAs, trendlines and volatility indexes. If the coin's price crosses these indicators, the robot can make a decision or recommendation to buy or sell the coin. I used the 'TA4j' Java library to help with the bot's decision processing.

### Trader alerts & notifications

When the robot makes a recommendation to buy or sell, it can send a text message or e-mail alert to the trader. Currently, human verification is needed to prevent potential mistakes by the bot.

The program uses the Google Chrome API to open a web browser window. It visits the exchange website and takes a screenshot of the chart. The image of the chart and the decision the robot made is included in the e-mail.

Here's an example of an e-mail alert:
![](media/example_email_1.png)
