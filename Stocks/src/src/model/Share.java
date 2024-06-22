package src.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import src.helper.DateFormat;

/**
 * Class that represents a share in the Stock Portfolio Manager.
 */
public class Share {

  private String symbol;
  private double quantity;
  private Map<String, StockRow> data;
  private String date;
  private Map<String, Double> history;

  /**
   * Constructor for a new stock.
   *
   * @param symbol   Symbol representing the stock.
   * @param quantity Quantity of the stock.
   * @param data     Stock data for the stock.
   */
  public Share(String symbol, double quantity, Map<String, StockRow> data) {
    this.symbol = symbol;
    this.quantity = quantity;
    this.data = data;
    Date today = new Date();
    this.date = DateFormat.toString(today); // todays date
    this.history = new HashMap<>();
    this.history.put(this.date, this.quantity);
  }

  /**
   * Constructor for a new stock.
   *
   * @param symbol   Symbol representing the stock.
   * @param quantity Quantity of the stock.
   * @param data     Stock data for the stock.
   */
  public Share(String symbol, double quantity, Map<String, StockRow> data, String date) {
    this.symbol = symbol;
    this.quantity = quantity;
    this.data = data;
    this.date = date;
    this.history = new HashMap<>();
    this.history.put(this.date, this.quantity);
  }

  /**
   * Purchase a quantity of the stock.
   *
   * @param quantity Quantity to purchase.
   */

  public void purchase(int quantity) {
    this.quantity += quantity;
    this.date = DateFormat.toString(new Date());
    this.history.put(this.date, this.quantity);
  }


  /**
   * Purchase a quantity of the stock at the given date.
   *
   * @param amount Quantity to purchase.
   * @param date   the date the transaction should take place on.
   */
  public void purchase(int amount, String date) {
    this.quantity += amount;
    history.put(date, this.quantity); // Record the new quantity on the specified date
  }

  /**
   * Sell a quantity of the stock.
   *
   * @param quantity Quantity to sell.
   */
  public void sell(double quantity) {
    if (this.quantity < quantity) {
      throw new IllegalArgumentException("Cannot sell more than you have");
    }
    this.quantity -= quantity;
    this.date = DateFormat.toString(new Date());
    this.history.put(this.date, this.quantity);
  }

  /**
   * Sell a quantity of the stock.
   *
   * @param quantity Quantity to sell.
   */
  public void sell(double quantity, String date) {
    if (this.quantity < quantity) {
      throw new IllegalArgumentException("Cannot sell more than you have");
    }
    this.quantity -= quantity;
    this.history.put(date, this.quantity);
  }

  /**
   * Get the data copy for the stock.
   *
   * @return The data for the stock.
   */
  public HashMap<String, StockRow> getData() {
    // return a copy of the data
    return new HashMap<>(data);
  }

  /**
   * Get the quantity of the stock.
   *
   * @return The quantity of the stock.
   */
  public double getQuantity() {
    return quantity;
  }

  /**
   * Get the closing price of this share's stock on the specified date.
   *
   * @param date the date you want to find the closing price of.
   * @return double, value of a single share at the end of the specified date.
   */
  public double getPriceOnDate(String date) {
    StockRow row = data.get(date);
    if (row != null) {
      return row.getClose();
    }

    // If exact date is not found, find the closest date
    TreeMap<String, StockRow> sortedData = new TreeMap<>(data);
    Map.Entry<String, StockRow> closestEntry = sortedData.floorEntry(date);
    if (closestEntry == null) {
      closestEntry = sortedData.ceilingEntry(date);
    }
    if (closestEntry != null) {
      return closestEntry.getValue().getClose();
    }

    System.out.println("No data found for " + date);
    return 0;
  }

  /**
   * Get the total value of the shares in this stock based on the inputted date.
   *
   * @param date the date to observe the share values.
   * @return the total value of shares owned. Share closing price * share quantity.
   */
  public double getValueOnDate(String date) {
    double price = getPriceOnDate(date);
    if (price == 0) {
      return 0;
    }
    double quantityOnDate = getQuantityOnDate(date);
    return price * quantityOnDate;
  }

  /**
   * Updates the number of shares by adding the given quantity. Quantity could be negative.
   *
   * @param quantity number of shares to add or remove from the current quantity. Action depends
   *                 on the sign of the argument.
   * @param date     the date that which this update takes place.
   */
  public void updateQuantity(double quantity, String date) {
    this.quantity += quantity;
    this.date = date;
    this.history.put(this.date, this.quantity);
  }

  /**
   * Returns the quantity of shares based on the date the portfolio should refer to.
   *
   * @param date the date to check.
   * @return integer representing the quantity of shares we have of this stock at the given
   *         date.
   */
  public double getQuantityOnDate(String date) {
    Date targetDate = DateFormat.toDate(date);
    double closestQuantity = 0.0;
    Date closestDate = null;

    // loop over all the keys in the map
    for (String key : history.keySet()) {
      Date historyDate = DateFormat.toDate(key);
      if (historyDate.equals(targetDate) || historyDate.before(targetDate)) {
        if (closestDate == null || historyDate.after(closestDate)) {
          closestDate = historyDate;
          closestQuantity = history.get(key);
        }
      }
    }

    return closestQuantity;
  }

  /**
   * Returns this Stocks/share's history.
   *
   * @return history in a new HashMap.
   */
  public HashMap<String, Double> getHistory() {
    return new HashMap<>(history);
  }

  /**
   * Used to prevent hard-coding a menu. It maps a selection like a menu.
   *
   * @param map Selections to map into a menu.
   */
  private void printMap(HashMap<String, Integer> map) {
    System.out.println("======================================================");
    System.out.println("Map has " + map.size() + " entries");
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      System.out.println(entry.getKey() + " : " + entry.getValue());
    }
    System.out.println("======================================================");

  }

  /**
   * Quick method to add a transaction update of this stock to the history. Takes the date
   * of the transaction, and the new number of shares.
   *
   * @param date     date the transaction occurred.
   * @param quantity the new quantity of shares after the transaction.
   */
  public void addToHistory(String date, double quantity) {
    history.put(date, quantity);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    src.model.Share stock = (src.model.Share) o;
    return quantity == stock.quantity &&
            symbol.equals(stock.symbol) &&
            data.equals(stock.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(symbol, quantity, data);
  }

  /**
   * Returns a boolean value, does this.date come before the given date.
   *
   * @param date date to see if this date comes before.
   * @return a boolean, does this date come before that date?
   */
  public boolean boughtBefore(Date date) {
    return DateFormat.toDate(this.date).before(date);
  }

  /**
   * Get the latest price of the stock.
   *
   * @return the latest closing price.
   */
  public double getLatestPrice() {
    String latestDate = null;
    for (String date : data.keySet()) {
      if (latestDate == null || date.compareTo(latestDate) > 0) {
        latestDate = date;
      }
    }
    if (latestDate != null) {
      return data.get(latestDate).getClose();
    } else {
      throw new IllegalStateException("No data available for the stock.");
    }
  }
}


