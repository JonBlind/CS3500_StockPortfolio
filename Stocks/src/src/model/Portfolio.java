package src.model;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import src.helper.DateFormat;

/**
 * A class that represents a portfolio of stocks.
 */
public class Portfolio {

  // Symbol, Share
  private Map<String, Share> shares;
  private String name;
  private ArrayList<String> history;

  /**
   * Constructor for a new portfolio.
   */
  public Portfolio(String name) {
    shares = new HashMap<>();
    this.name = name;
    history = new ArrayList<>();
  }

  /**
   * Constructor for a new portfolio.
   *
   * @param portfolio Portfolio to copy.
   */
  public Portfolio(String name, Portfolio portfolio) {
    this.name = name;
    shares = new HashMap<>();
    shares.putAll(portfolio.shares);
    history = new ArrayList<>(portfolio.history);
  }

  public String getName() {
    return name;
  }

  /**
   * Get the quantity of a stock in the portfolio.
   *
   * @param symbol Symbol representing a stock.
   * @return The quantity of the stock in the portfolio.
   */
  public double getStockQuantity(String symbol) {
    if (shares.containsKey(symbol)) {
      return shares.get(symbol).getQuantity();
    }
    return 0;
  }

  /**
   * Buy a stock in the portfolio.
   *
   * @param symbol   Symbol representing a stock.
   * @param stock    Stock data for the stock.
   * @param quantity Quantity of the stock to buy.
   */
  public void buyStock(String symbol, Map<String, StockRow> stock, int quantity) {
    if (shares.containsKey(symbol)) {
      shares.get(symbol).purchase(quantity);
    } else {
      shares.put(symbol, new Share(symbol, quantity, stock));
    }
    history.add("BUY;" + symbol + ";" + quantity + ";" + DateFormat.toString(new Date()));
  }

  /**
   * Buy a stock given the ticker, portfolio data, quantity of shares, and the date to make the
   * purchase.
   *
   * @param ticker   stock symbol as a String.
   * @param data     The data of the stock to be purchased, key is the ticker symbol as a string
   *                 and the value is the corresponding StockRow.
   * @param quantity the number of shares to purchase.
   * @param date     the date to make this purchase.
   */
  public void buyStock(String ticker, Map<String, StockRow> data, int quantity, String date) {
    if (shares.containsKey(ticker)) {
      shares.get(ticker).purchase(quantity, date);
    } else {
      Share newShare = new Share(ticker, quantity, data);
      shares.put(ticker, newShare);
    }
    shares.get(ticker).addToHistory(date, shares.get(ticker).getQuantity());
    history.add("BUY;" + ticker + ";" + quantity + ";" + date);
  }

  /**
   * Sell a stock in the portfolio.
   *
   * @param symbol   Symbol representing a stock.
   * @param quantity Quantity of the stock to sell.
   */
  public void sellStock(String symbol, int quantity) {
    if (shares.containsKey(symbol)) {
      shares.get(symbol).sell(quantity);
      if (shares.get(symbol).getQuantity() == 0) {
        shares.remove(symbol);
      }
    } else {
      throw new IllegalArgumentException("Stock not found in portfolio");
    }
    history.add("SELL;" + symbol + ";" + quantity + ";" + DateFormat.toString(new Date()));
  }

  /**
   * Sell a stock in the portfolio on a specific date.
   *
   * @param ticker   Symbol representing a stock.
   * @param quantity Quantity of the stock to sell.
   * @param date     date which to perform the sell.
   */
  public void sellStock(String ticker, int quantity, String date) {
    if (shares.containsKey(ticker)) {
      shares.get(ticker).sell(quantity, date);
      shares.get(ticker).addToHistory(date, shares.get(ticker).getQuantity());
      if (shares.get(ticker).getQuantity() == 0) {
        shares.remove(ticker);
      }
    } else {
      throw new IllegalArgumentException("Stock not found in portfolio");
    }
    history.add("SELL;" + ticker + ";" + quantity + ";" + date);
  }

  /**
   * Get the value of the portfolio.
   *
   * @return The value of the portfolio.
   */

  public double getPortfolioValue() {
    double total = 0;
    for (String symbol : shares.keySet()) {
      Map<String, StockRow> stock = shares.get(symbol).getData();
      StockRow lastRow = null;
      // todays date:
      String date = "2024-06-06";
      lastRow = findClosestRecordedDate(stock, date);
      total += lastRow.getClose() * shares.get(symbol).getQuantity();
    }
    return total;
  }

  /**
   * Get the value of the portfolio for a given date.
   *
   * @param date Date to get the value for.
   * @return The value of the portfolio for the given date.
   */
  public double getPortfolioValue(String date) {
    double totalValue = 0;
    for (Share share : shares.values()) {
      double value = share.getValueOnDate(date);
      if (value != 0) {
        totalValue += value;
      }
    }
    return totalValue;
  }

  /**
   * To identify the closest date to the given date, which has a record under the stock, containing
   * information on its price/cost throughout that date.
   *
   * @param stock the stock information/data that we want to scour for pricing information which
   *              is stored based on dates.
   * @param date  the date that we want to find the closest date to. Essentially, date of interest.
   * @return Returns a StockRow containing stock prices for a specific date.
   */
  public StockRow findClosestRecordedDate(Map<String, StockRow> stock, String date) {
    StockRow stockRow = stock.get(date);
    if (stockRow != null) {
      return stockRow;
    }
    Date dateObj = DateFormat.toDate(date);
    String dateString = DateFormat.toString(dateObj);

    while (stockRow == null) {
      dateObj = new Date(dateObj.getTime() - 86400000); // subtract a day
      dateString = DateFormat.toString(dateObj);
      stockRow = stock.get(dateString);
    }
    return stockRow;
  }

  /**
   * Get the size of this portfolio by counting the size of the HashMap. Essentially, returns
   * the total amount of shares present.
   *
   * @return int. The total amount of shares in this portfolio.
   */
  public int getPortfolioSize() {
    return shares.size();
  }

  /**
   * Get the names of all the shares.
   *
   * @return the names of all the shares
   */
  public String[] getStockNames() {
    return shares.keySet().toArray(new String[0]);
  }

  /**
   * The string representation of the portfolio.
   *
   * @return A string representation of the portfolio.
   * @see src.model.Portfolio#portfolioAString()
   */
  public String portfolioAString() {
    String output = "";
    for (String symbol : shares.keySet()) {
      output += symbol + " " + shares.get(symbol).getQuantity() + "\n";
    }
    return output;
  }

  /**
   * Returns the Shares given the symbol.
   *
   * @param symbol Symbol representing a stock.
   * @return Stock with the corresponding inputted symbol.
   */
  public Share getShare(String symbol) {
    return shares.get(symbol);
  }

  /**
   * This saves the history of a portfolio upon exiting. This is done by writing the contents
   * of the history into a file designated for the portfolio.
   * @param filename filename associated with the portfolio to save the history to.
   */
  public void writeHistoryToFile(String filename) {
    try {
      FileWriter fw = new FileWriter(filename);
      for (String line : history) {
        fw.write(line + "\n");
      }
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Obtains the last date which a transaction occurred on this portfolio.
   *
   * @return Date, the date the last transaction occurred, chronologically.
   */
  public Date getLastTransactionDate() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date lastDate;
    try {
      lastDate = dateFormat.parse("1970-01-01");
    } catch (ParseException e) {
      throw new RuntimeException("Error parsing initial date", e);
    }

    for (String entry : history) {
      String[] parts = entry.split(";");
      if (parts.length != 4) {
        continue;
      }

      String dateStr = parts[3];
      Date date;
      try {
        date = dateFormat.parse(dateStr);
      } catch (ParseException e) {
        continue;
      }

      if (date.after(lastDate)) {
        lastDate = date;

      }
    }

    return lastDate;
  }

  /**
   * rebalances the current portfolio to fit the desired value distribution.
   *
   * @param date                the date the portfolio should be rebalanced from.
   * @param desiredDistribution the desired distribution the contents of the portfolio should be
   *                            after balancing.
   */
  public void rebalance(String date, Map<String, Double> desiredDistribution) {
    System.out.println("Starting rebalance for date: " + date);

    Date transactionDate = DateFormat.toDate(date);
    Date lastTransactionDate = getLastTransactionDate();

    if (transactionDate.before(lastTransactionDate)) {
      throw new IllegalArgumentException("Transaction date must be after the last" +
              " transaction date. Last transaction date was: "
              + DateFormat.toString(lastTransactionDate));
    }

    double portfolioTotalValue = getPortfolioValue(date);
    System.out.println("Portfolio total value on " + date + ": " + portfolioTotalValue);

    Map<String, BigDecimal> targetValues = new HashMap<>();
    Map<String, BigDecimal> buySellShares = new HashMap<>();

    for (String symbol : shares.keySet()) {
      BigDecimal targetValue = BigDecimal.valueOf(portfolioTotalValue)
              .multiply(BigDecimal.valueOf(desiredDistribution
                      .getOrDefault(symbol, 0.0)));
      targetValues.put(symbol, targetValue);
      System.out.println("Target value for " + symbol + ": " + targetValue);
    }

    for (String symbol : shares.keySet()) {
      Share share = shares.get(symbol);
      BigDecimal currentValue = BigDecimal.valueOf(share.getValueOnDate(date));
      BigDecimal targetValue = targetValues.get(symbol);
      BigDecimal difference = targetValue.subtract(currentValue);
      BigDecimal priceOnDate = BigDecimal.valueOf(share.getPriceOnDate(date));
      BigDecimal sharesToTrade = difference.divide(priceOnDate, 3, RoundingMode.HALF_UP);

      if (BigDecimal.valueOf(share.getQuantityOnDate(date)).add(sharesToTrade)
              .compareTo(BigDecimal.ZERO) < 0) {
        sharesToTrade = BigDecimal.valueOf(-share.getQuantityOnDate(date));
      }

      buySellShares.put(symbol, sharesToTrade);

      System.out.println("For " + symbol + ": currentValue=" + currentValue + ", targetValue=" + targetValue +
              ", difference=" + difference + ", priceOnDate=" + priceOnDate +
              ", sharesToTrade=" + sharesToTrade);
    }

    for (String symbol : buySellShares.keySet()) {
      BigDecimal sharesToTrade = buySellShares.get(symbol);
      shares.get(symbol).updateQuantity(sharesToTrade.doubleValue(), date);

      String actionType = sharesToTrade.compareTo(BigDecimal.ZERO) > 0 ? "BUY" : "SELL";
      history.add(actionType + ";" + symbol + ";" + sharesToTrade
              .setScale(3, RoundingMode.HALF_UP) + ";" + date);
      shares.get(symbol).addToHistory(date, shares.get(symbol).getQuantity());

      System.out.println(actionType + " " + sharesToTrade.abs()
              .setScale(3, RoundingMode.HALF_UP) + " shares of " + symbol + " on " + date);
    }

    System.out.println("Rebalance completed for date: " + date);
  }

  public Map<String, Share> getShares() {
    return shares;
  }
}



