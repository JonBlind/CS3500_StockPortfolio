package src.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to test both the Stock and Portfolio classes.
 */
public class StockPortfolioTest {
  private Share share;
  private Portfolio portfolio;
  private StockRow stockRow;
  private HashMap stockData;


  @Before
  public void setUp() {
    // Mock Stock
    stockRow = new StockRow(100, 120, 80, 90);
    StockRow stockRow2 = new StockRow(90, 120, 80, 100);
    StockRow stockRow3 = new StockRow(95, 120, 80, 90);
    stockData = new HashMap<>();
    stockData.put("2024-06-03", stockRow);
    stockData.put("2024-06-04", stockRow2);
    stockData.put("2024-06-03", stockRow3);
    share = new Share("AAPL", 100, stockData);
    portfolio = new Portfolio("ABC");
  }

  // Testing the StockRow class
  @Test
  public void testGetOpen() {
    assertEquals(100, stockRow.getOpen(), 0.01);
  }

  @Test
  public void testGetHigh() {
    assertEquals(120, stockRow.getHigh(), 0.01);
  }

  @Test
  public void testGetLow() {
    assertEquals(80, stockRow.getLow(), 0.01);
  }

  @Test
  public void testGetClose() {
    assertEquals(90, stockRow.getClose(), 0.01);
  }

  // Testing the Stock class
  @Test
  public void testPurchase() {
    share.purchase(100);
    assertEquals(200, share.getQuantity(), 0.01);
  }

  @Test
  public void testSell() {
    share.purchase(100);
    share.sell(50);
    assertEquals(150, share.getQuantity(), 0.01);
  }

  @Test
  public void testGetData() {
    assertEquals(stockData, share.getData());
  }

  @Test
  public void testGetQuantity() {
    assertEquals(100, share.getQuantity(), 0.01);
  }

  // Testing the Portfolio class
  @Test
  public void testBuyStock() {
    portfolio.buyStock("AAPL", stockData, 100);
    assertEquals(100, portfolio.getStockQuantity("AAPL"), 0.01);
  }

  @Test
  public void testSellStock() {
    portfolio.buyStock("AAPL", stockData, 100);
    portfolio.sellStock("AAPL", 50);
    assertEquals(50, portfolio.getStockQuantity("AAPL"), 0.01);
  }

  @Test
  public void testGetStock() {
    portfolio.buyStock("AAPL", stockData, 100);
    assertEquals(share, portfolio.getShare("AAPL"));
  }

  // New test for rebalancing the portfolio
  @Test
  public void testRebalance() {
    // Set up the initial portfolio
    HashMap<String, StockRow> nflxData = new HashMap<>();
    nflxData.put("2024-06-05", new StockRow(10, 10, 10, 15));
    HashMap<String, StockRow> googlData = new HashMap<>();
    googlData.put("2024-06-05", new StockRow(25, 25, 25, 30));
    HashMap<String, StockRow> msftData = new HashMap<>();
    msftData.put("2024-06-05", new StockRow(10, 10, 10, 10));
    HashMap<String, StockRow> aaplData = new HashMap<>();
    aaplData.put("2024-06-05", new StockRow(50, 50, 50, 30));

    portfolio.buyStock("NFLX", nflxData, 25, "2024-06-01");
    portfolio.buyStock("GOOGL", googlData, 10, "2024-06-01");
    portfolio.buyStock("MSFT", msftData, 25, "2024-06-01");
    portfolio.buyStock("AAPL", aaplData, 5, "2024-06-01");

    Map<String, Double> targetDistribution = new HashMap<>();
    targetDistribution.put("NFLX", 0.25);
    targetDistribution.put("GOOGL", 0.25);
    targetDistribution.put("MSFT", 0.25);
    targetDistribution.put("AAPL", 0.25);

    System.out.println("Before rebalance:");
    for (String symbol : portfolio.getStockNames()) {
      Share share = portfolio.getShare(symbol);
      System.out.println(symbol + ": " + share.getQuantityOnDate("2024-06-05") +
              " shares at $" + share.getPriceOnDate("2024-06-05") +
              " each on 2024-06-05. Total value: $" + share.getValueOnDate("2024-06-05"));
      System.out.println("History: " + share.getHistory());
    }

    // Rebalance the portfolio on a specific date
    portfolio.rebalance("2024-06-05", targetDistribution);

    System.out.println("After rebalance:");

    for (String symbol : portfolio.getStockNames()) {
      Share share = portfolio.getShare(symbol);
      System.out.println(symbol + ": " + share.getQuantityOnDate("2024-06-05") +
              " shares at $" + share.getPriceOnDate("2024-06-05") +
              " each on 2024-06-05. Total value: $" + share.getValueOnDate("2024-06-05"));
      System.out.println("History: " + share.getHistory());
    }

    // Check the rebalanced quantities
    assertEquals(17.917, portfolio.getShare("NFLX").getQuantity(), 0.001);
    assertEquals(8.958, portfolio.getShare("GOOGL").getQuantity(), 0.001);
    assertEquals(26.875, portfolio.getShare("MSFT").getQuantity(), 0.001);
    assertEquals(8.958, portfolio.getShare("AAPL").getQuantity(), 0.001);

    // Check the rebalanced values
    assertEquals(268.755, portfolio.getShare("NFLX").getValueOnDate("2024-06-05"), 0.01);
    assertEquals(268.75, portfolio.getShare("GOOGL").getValueOnDate("2024-06-05"), 0.01);
    assertEquals(268.75, portfolio.getShare("MSFT").getValueOnDate("2024-06-05"), 0.01);
    assertEquals(268.74, portfolio.getShare("AAPL").getValueOnDate("2024-06-05"), 0.01);
  }

  @Test
  public void testRebalanceUnequalDistribution() {
    // Set up the initial portfolio
    HashMap<String, StockRow> nflxData = new HashMap<>();
    nflxData.put("2024-06-03", new StockRow(10, 10, 10, 15));
    HashMap<String, StockRow> googlData = new HashMap<>();
    googlData.put("2024-06-03", new StockRow(25, 25, 25, 30));
    HashMap<String, StockRow> msftData = new HashMap<>();
    msftData.put("2024-06-03", new StockRow(10, 10, 10, 10));
    HashMap<String, StockRow> aaplData = new HashMap<>();
    aaplData.put("2024-06-03", new StockRow(50, 50, 50, 30));

    portfolio.buyStock("NFLX", nflxData, 25, "2024-06-03");
    portfolio.buyStock("GOOGL", googlData, 10, "2024-06-03");
    portfolio.buyStock("MSFT", msftData, 25, "2024-06-03");
    portfolio.buyStock("AAPL", aaplData, 5, "2024-06-03");

    Map<String, Double> targetDistribution = new HashMap<>();
    targetDistribution.put("NFLX", 0.40);
    targetDistribution.put("GOOGL", 0.20);
    targetDistribution.put("MSFT", 0.20);
    targetDistribution.put("AAPL", 0.20);

    System.out.println("Before rebalance:");
    for (String symbol : portfolio.getStockNames()) {
      Share share = portfolio.getShare(symbol);
      System.out.println(symbol + ": " + share.getQuantity() + " shares at $" +
              share.getPriceOnDate("2024-06-03") + " each on 2024-06-03. Total value: $" +
              share.getValueOnDate("2024-06-03"));
    }

    // Rebalance the portfolio on a specific date
    portfolio.rebalance("2024-06-03", targetDistribution);

    System.out.println("After rebalance:");
    for (String symbol : portfolio.getStockNames()) {
      Share share = portfolio.getShare(symbol);
      System.out.println(symbol + ": " + share.getQuantity() + " shares at $" +
              share.getPriceOnDate("2024-06-03") + " each on 2024-06-03. Total value: $" +
              share.getValueOnDate("2024-06-03"));
    }

    // Check the rebalanced quantities
    assertEquals(28.667, portfolio.getShare("NFLX").getQuantity(), 0.01);
    assertEquals(7.167, portfolio.getShare("GOOGL").getQuantity(), 0.01);
    assertEquals(21.5, portfolio.getShare("MSFT").getQuantity(), 0.01);
    assertEquals(7.167, portfolio.getShare("AAPL").getQuantity(), 0.01);

    // Check the rebalanced values
    assertEquals(430, portfolio.getShare("NFLX").getValueOnDate("2024-06-03"), 0.01);
    assertEquals(215, portfolio.getShare("GOOGL").getValueOnDate("2024-06-03"), 0.01);
    assertEquals(215, portfolio.getShare("MSFT").getValueOnDate("2024-06-03"), 0.01);
    assertEquals(215, portfolio.getShare("AAPL").getValueOnDate("2024-06-03"), 0.01);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRebalanceInvalidDate() {
    // Set up the initial portfolio
    HashMap<String, StockRow> nflxData = new HashMap<>();
    nflxData.put("2024-06-01", new StockRow(10, 10, 10, 15));
    HashMap<String, StockRow> googlData = new HashMap<>();
    googlData.put("2024-06-01", new StockRow(25, 25, 25, 30));
    HashMap<String, StockRow> msftData = new HashMap<>();
    msftData.put("2024-06-01", new StockRow(10, 10, 10, 10));
    HashMap<String, StockRow> aaplData = new HashMap<>();
    aaplData.put("2024-06-01", new StockRow(50, 50, 50, 30));

    portfolio.buyStock("NFLX", nflxData, 25, "2024-06-01");
    portfolio.buyStock("GOOGL", googlData, 10, "2024-06-01");
    portfolio.buyStock("MSFT", msftData, 25, "2024-06-01");
    portfolio.buyStock("AAPL", aaplData, 5, "2024-06-01");

    Map<String, Double> targetDistribution = new HashMap<>();
    targetDistribution.put("NFLX", 0.40);
    targetDistribution.put("GOOGL", 0.20);
    targetDistribution.put("MSFT", 0.20);
    targetDistribution.put("AAPL", 0.20);

    System.out.println("Before rebalance:");
    for (String symbol : portfolio.getStockNames()) {
      Share share = portfolio.getShare(symbol);
      System.out.println(symbol + ": " + share.getQuantity() + " shares at $" +
              share.getPriceOnDate("2024-06-01") + " each on 2024-06-01. Total value: $" +
              share.getValueOnDate("2024-06-01"));
    }

    // Attempt to rebalance the portfolio on an invalid date (before the most recent purchase)
    portfolio.rebalance("2024-05-01", targetDistribution);
  }
}
