package src.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;

/**
 * Testing class for the StockModel class.
 */
public class StockModelTest {
  StockModel model;

  @Before
  public void setUp() throws Exception {
    // create a model instance 
    model = new StockModel();
  }

  @Test
  public void testCountPortfolios() {
    assertEquals(0, model.countPortfolios(), 0.01);
    model.addPortfolio("ABC");
    assertEquals(1, model.countPortfolios(), 0.01);
  }

  @Test
  public void addPortfolio() {
    model.addPortfolio("ABC");
    assertEquals(1, model.countPortfolios(), 0.01);

  }

  @Test
  public void testGetPortfolioNames() {
    assertEquals(0, model.countPortfolios(), 0.01);
    model.addPortfolio("ABC");
    model.addPortfolio("DEF");
    assertEquals(2, model.getPortfolioNames().length);
  }

  @Test
  public void testGetPortfolio() {
    model.addPortfolio("ABC");
    Portfolio portfolio = model.getPortfolio("ABC");
    assertEquals(new Portfolio("ABC").portfolioAString(), portfolio.portfolioAString());
  }

  @Test
  public void testAddStockToPortfolio() {
    model.addPortfolio("ABC");
    Portfolio portfolio = model.getPortfolio("ABC");

    model.addStockToPortfolio("AAPL", portfolio, 100);
    assertEquals(100, portfolio.getStockQuantity("AAPL"), 0.01);
  }

  @Test
  public void testGetStockChange() {
    model.addPortfolio("ABC");
    model.addStockToPortfolio("AAPL", model.getPortfolio("ABC"), 100);
    assertEquals(0.449, model.getStockChange("AAPL", "2024-06-03", "2024-06-06"), 0.01);
  }

  @Test
  public void testXDayCrossoverDays() {
    model.addPortfolio("ABC");
    model.addStockToPortfolio("AAPL", model.getPortfolio("ABC"), 100);
    assertEquals(1, model.xDayCrossoverDays("AAPL", 1, "2024-06-03", "2024-06-06").size());
  }


  @Test
  public void testGetStockMovingAverage() {
    model.addPortfolio("ABC");
    model.addStockToPortfolio("AAPL", model.getPortfolio("ABC"), 100);
    assertEquals(205.22, model.getStockMovingAverage("AAPL", new Date(), 20), 0.01);
  }


  @Test
  public void testGetStock() {
    model.addPortfolio("ABC");
    model.addStockToPortfolio("AAPL", model.getPortfolio("ABC"), 100);
    assertEquals(194.03, model.getStock("AAPL").get("2024-06-03").getClose(), 0.01);
  }

  @Test
  public void testGetPortfolioValue() {
    model.addPortfolio("ABC");
    model.getStock("AAPL");
    model.addStockToPortfolio("AAPL", model.getPortfolio("ABC"), 100);
    assertEquals(0, model.getPortfolioValue("2024-06-03", model.getPortfolio("ABC")), 0.01);
  }

  @Test
  public void testLoadLocalStock() {
    model.addPortfolio("ABC");
    model.addStockToPortfolio("AAPL", model.getPortfolio("ABC"), 100);
    assertEquals(194.03, model.getStock("AAPL").get("2024-06-03").getClose(), 0.01);
  }

  @Test
  public void testFindClosestRecordedDate() {
    HashMap<String, StockRow> stock = new HashMap<>();
    stock.put("2023-06-01", new StockRow(100.0, 110.0, 95.0, 105.0));
    stock.put("2023-06-02", new StockRow(106.0, 115.0, 101.0, 111.0));
    stock.put("2023-06-05", new StockRow(112.0, 120.0, 108.0, 118.0));

    StockRow result = model.findClosestRecordedDate(stock, "2023-06-03");
    assertNotNull(result);
    assertEquals(111.0, result.getClose(), 0.001);
  }
}