package src.model;

import java.util.Date;
import java.util.HashMap;

/**
 * A class that represents a portfolio of stocks.
 */
public class Portfolio {

  // Symbol, Share
  HashMap<String, Share> stocks;

  /**
   * Constructor for a new portfolio.
   */
  public Portfolio() {
    stocks = new HashMap<>();
  }

  /**
   * Constructor for a new portfolio.
   *
   * @param portfolio Portfolio to copy.
   */
  public Portfolio(Portfolio portfolio) {
    stocks = new HashMap<>();
    for (String symbol : portfolio.stocks.keySet()) {
      stocks.put(symbol,
              new Share(symbol,
                      portfolio.stocks.get(symbol).getQuantity(),
                      portfolio.stocks.get(symbol).getData()));
    }
  }


  /**
   * Get the quantity of a stock in the portfolio.
   *
   * @param symbol Symbol representing a stock.
   * @return The quantity of the stock in the portfolio.
   */
  public int getStockQuantity(String symbol) {
    if (stocks.containsKey(symbol)) {
      return stocks.get(symbol).getQuantity();
    } else {
      throw new IllegalArgumentException("Stock not found in portfolio");
    }
  }

  /**
   * Buy a stock in the portfolio.
   *
   * @param symbol   Symbol representing a stock.
   * @param stock    Stock data for the stock.
   * @param quantity Quantity of the stock to buy.
   */
  public void buyStock(String symbol, HashMap<String, StockRow> stock, int quantity) {
    System.out.println("Buying " + quantity + " shares of " + symbol);
    System.out.println(this.portfolioAString());
    if (stocks.containsKey(symbol)) {
      System.out.println("Stock already in portfolio");
      stocks.get(symbol).purchase(quantity);
    } else {
      System.out.println("Adding stock to portfolio");
      stocks.put(symbol, new Share(symbol, quantity, stock));
    }
  }

  public void buyStock(String symbol, HashMap<String, StockRow> stock, int quantity, String date) {
    System.out.println("Buying " + quantity + " shares of " + symbol);
    System.out.println(this.portfolioAString());
    if (stocks.containsKey(symbol)) {
      System.out.println("Stock already in portfolio");
      stocks.get(symbol).purchase(quantity, date);
    } else {
      System.out.println("Adding stock to portfolio");
      stocks.put(symbol, new Share(symbol, quantity, stock, date));
    }
  }

  /**
   * Sell a stock in the portfolio.
   *
   * @param symbol   Symbol representing a stock.
   * @param quantity Quantity of the stock to sell.
   */
  public void sellStock(String symbol, int quantity) {
    if (stocks.containsKey(symbol)) {
      stocks.get(symbol).sell(quantity);
      if (stocks.get(symbol).getQuantity() == 0) {
        stocks.remove(symbol);
      }
    } else {
      throw new IllegalArgumentException("Stock not found in portfolio");
    }
  }

  /**
   * Get the value of the portfolio.
   *
   * @return The value of the portfolio.
   */

  public double getPortfolioValue() {
    double total = 0;
    for (String symbol : stocks.keySet()) {
      HashMap<String, StockRow> stock = stocks.get(symbol).getData();
      StockRow lastRow = null;
      // todays date: 
      String date = "2024-06-06";
      lastRow = stock.get(date);
      total += lastRow.getClose() * stocks.get(symbol).getQuantity();
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
    double total = 0;
    for (String symbol : stocks.keySet()) {
      HashMap<String, StockRow> stock = stocks.get(symbol).getData();
      StockRow lastRow = stock.get(date);
      // todays date: 
      total += lastRow.getClose() * stocks.get(symbol).getQuantity();
    }
    return total;
  }

  /**
   * Get the size of this portfolio by counting the size of the HashMap. Essentially, returns
   * the total amount of stocks present.
   *
   * @return int. The total amount of stocks in this portfolio.
   */
  public int getPortfolioSize() {
    return stocks.size();
  }

  /**
   * Get the names of all the stocks.
   *
   * @return the names of all the stocks
   */
  public String[] getStockNames() {
    return stocks.keySet().toArray(new String[0]);
  }

  /**
   * The string representation of the portfolio.
   *
   * @return A string representation of the portfolio.
   * @see Portfolio#portfolioAString()
   */
  public String portfolioAString() {
    String output = "";
    for (String symbol : stocks.keySet()) {
      output += symbol + " " + stocks.get(symbol).getQuantity() + "\n";
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
    return stocks.get(symbol);
  }

}
