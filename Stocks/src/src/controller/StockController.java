package src.controller;

import src.helper.DateFormat;
import src.model.Portfolio;
import src.model.Share;
import src.model.StockModel;
import src.model.StockModelInterface;
import src.view.StockView;

import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The StockController class serves as the main controller for the
 * Stock Portfolio Manager application.
 * It mediates between the user interface (view) and the data model, handling user input and
 * updating the view accordingly.
 *
 * <p>Responsibilities:
 * - Initialize the model and view components.
 * - Manage user input and navigate through the application menu.
 * - Handle creation and selection of portfolios.
 * - Execute stock transactions (buying and selling stocks).
 * - Perform portfolio rebalancing based on user-specified distributions.
 * - Provide portfolio analysis tools, such as viewing stock performance and calculating
 *   moving averages.
 * - Graph portfolio performance over a specified date range.
 * - Save and load portfolio data.
 *
 * <p>This class utilizes a Scanner object for reading user input from the console and interacts
 * with the model to perform operations on the portfolio. It also interacts with the view
 * to display information and prompts to the user.
 *
 * <p>Note: The controller expects the dates to be in the "YYYY-MM-DD" format for all
 * date-related operations.
 */
public class StockController implements StockControllerInterface {
  private final StockModelInterface model;
  private final StockView view;
  private final Scanner scan;
  private Portfolio portfolio;
  private boolean exit;
  private HashMap<String, TransactionOption> transactionOptions;


  /**
   * Constructor representing the controller for the Stock Portfolio Manager.
   *
   * @param model the Model state the controller is utilizing.
   * @param view  the text-based display state.
   */
  public StockController(StockModelInterface model, StockView view) {
    this.model = model;
    this.view = view;
    this.scan = new Scanner(System.in);
    this.portfolio = null;
    this.exit = false;

    this.transactionOptions = new HashMap<String, TransactionOption>();
    // buy option parameters hashmap
    HashMap<String, String> buyParameters = new HashMap<>();
    buyParameters.put("Quantity", "Number of shares to Buy");

    transactionOptions.put("Buy",
            new TransactionOption(() -> model.addStockToPortfolio(
                    handleSpecifyStock(), portfolio, getNumberShares()),
                    "Buy", buyParameters));

    // sell option parameters hashmap
    HashMap<String, String> sellParameters = new HashMap<>();
    sellParameters.put("Quantity", "Number of shares to Sell");

    transactionOptions.put("Sell",
            new TransactionOption(() -> model.sellStockFromPortfolio(
                    handleSpecifyStock(), portfolio, getNumberShares()),
                    "Sell", sellParameters));

    // buy from specific date option parameters hashmap
    HashMap<String, String> buyFromDateParameters = new HashMap<>();
    buyFromDateParameters.put("Quantity", "Number of shares to Buy");
    buyFromDateParameters.put("Date", "Date to Buy Stock");

    transactionOptions.put("BuyOnDate",
            new TransactionOption(() -> model.addStockToPortfolio(
                    handleSpecifyStock(), portfolio, getNumberShares(), handleSpecifyDate()),
                    "BuyOnDate", buyFromDateParameters));

    // sell from specific date option parameters hashmap
    HashMap<String, String> sellFromDateParameters = new HashMap<>();
    buyFromDateParameters.put("Quantity", "Number of shares to Sell");
    sellFromDateParameters.put("Date", "Date to Sell Stock");

    transactionOptions.put("SellOnDate",
            new TransactionOption(() -> model.sellStockFromPortfolio(
                    handleSpecifyStock(), portfolio, getNumberShares(), handleSpecifyDate()),
                    "SellOnDate", sellFromDateParameters));


  }

  /**
   * Returns the model associated with this controller.
   *
   * @return the StockModelInterface instance.
   */
  public StockModelInterface getModel() {
    return model;
  }

  /**
   * Reads a single line of user input.
   *
   * @return the user input as a string.
   */
  protected String getUserInput() {
    return scan.next();
  }

  /**
   * Reads an integer choice from user input.
   *
   * @return the user's choice as an integer.
   */
  protected int getUserChoice() {
    while (!scan.hasNextInt()) {
      scan.next();
      handleError("Invalid input. Please enter a number.");
    }
    return scan.nextInt();
  }

  /**
   * Reads a validated user choice within a specified range.
   *
   * @param lowerb the lower bound of the valid range.
   * @param upperb the upper bound of the valid range.
   * @return the validated user choice.
   */
  protected int getValidatedUserChoice(int lowerb, int upperb) {
    int choice;
    while (true) {
      choice = getUserChoice();
      if (choice >= lowerb && choice <= upperb) {
        break;
      } else {
        handleError("Invalid Input. Please Try Again.");
      }
    }
    return choice;
  }

  /**
   * This is the overall control method for the Stock Portfolio Software.
   * When the program starts, this is called.
   */
  public void control() {
    this.view.printWelcome();

    while (!exit) {
      if (model.countPortfolios() == 0) {
        view.printMenuNoPortfolios();
        int choice = getValidatedUserChoice(1, 2);

        switch (choice) {
          case 1:
            handleCreatePortfolio();
            break;
          case 2:
            exit = true;
            break;
          default:
            view.displayError("Invalid Input. Please Try Again.");
        }
      } else {
        handleChangePortfolio(true);
      }
    }
    handleExitProgram();
  }

  /**
   * Creates the main menu for a specified portfolio.
   *
   * @param portfolio the portfolio to create the menu for.
   */
  private void createMenu(Portfolio portfolio) {
    view.printMenu(portfolio);
    HashMap<Integer, Runnable> menuOptions = new HashMap<>();
    menuOptions.put(1, () -> handleTransaction());
    menuOptions.put(2, () -> handleViewPortfolioContents());
    menuOptions.put(3, () -> handleAnalyzeStocks());
    menuOptions.put(4, () -> handleViewPortfolioValue());
    menuOptions.put(5, () -> handleChangePortfolio(true));
    menuOptions.put(6, () -> handleViewPortfolioOnSpecificDate());
    menuOptions.put(7, () -> savePortfolio());
    menuOptions.put(8, () -> graphPortfolio());
    menuOptions.put(9, () -> handleRebalance());
    menuOptions.put(10, () -> view.exitProgram());

    while (!exit) {
      int choice = getValidatedUserChoice(1, menuOptions.size());
      if (menuOptions.containsKey(choice)) {
        menuOptions.get(choice).run();
        if (choice == menuOptions.size()) {
          exit = true;
        }
      } else {
        view.displayError("Invalid Input. Please Try Again.");
      }
    }
  }

  /**
   * Saves the current portfolio.
   */
  private void savePortfolio() {
    view.savePortfolio(portfolio.getName());
    model.writePortfolioToFile(portfolio);
    createMenu(portfolio);
  }

  /**
   * Handles an error by displaying the specified message.
   *
   * @param msg the error message to display.
   */
  protected void handleError(String msg) {
    view.displayError(msg);
  }

  /**
   * Graphs the portfolio performance over a specified date range.
   */
  private void graphPortfolio() {
    // get start and end date
    view.printSpecifyStartDate();
    String startDate = handleDate();
    view.printSpecifyEndDate();
    String endDate = handleDate();

    view.printGraph(model.graphPortfolio(portfolio, startDate, endDate));
    createMenu(portfolio);
  }

  /**
   * Handles the creation of a new portfolio.
   */
  protected void handleCreatePortfolio() {
    view.printPortfolioMaker();
    String name = getUserInput();

    if (name.equalsIgnoreCase("Quit")) {

      //If you try quitting at the menu without even making a portfolio
      if (portfolio == null) {
        return;
      } else {
        createMenu(portfolio);
      }

    } else {
      model.addPortfolio(name);
      portfolio = model.getPortfolio(name);
    }
  }

  /**
   * Handles rebalancing the portfolio.
   */
  private void handleRebalance() {
    view.printRebalanceWarning();
    view.printSpecifyDate();
    view.printMustBeChronological();
    String date = handleDate();
    if (date == null) {
      return;  // If the user entered "Quit", exit the method
    }

    HashMap<String, Double> desiredDistribution = new HashMap<>();
    for (String stock : portfolio.getStockNames()) {
      view.printSpecifyDistribution(stock);
      double distribution = getUserInputAsDouble();
      desiredDistribution.put(stock, distribution / 100.0);
    }

    double totalDistribution = desiredDistribution.values().stream().
            mapToDouble(Double::doubleValue).sum();
    if (totalDistribution != 1.0) {
      handleError("The total distribution must equal 100%. Please try again.");
      handleRebalance();
      return;
    }

    try {
      portfolio.rebalance(date, desiredDistribution);
      view.printRebalanceSuccess();
    } catch (IllegalArgumentException e) {
      handleError(e.getMessage());
    }
    createMenu(portfolio);
  }

  /**
   * Reads a double input from the user.
   *
   * @return the user's input as a double.
   */
  protected double getUserInputAsDouble() {
    while (!scan.hasNextDouble()) {
      scan.next();
      handleError("Invalid input. Please enter a number.");
    }
    return scan.nextDouble();
  }

  /**
   * Handles viewing the portfolio value at a specific date.
   */
  protected void handleViewPortfolioValue() {
    view.printPortfolioValuePrompt();
    String date = getUserInput();
    view.printPortfolioValueResult(portfolio.getPortfolioValue(date));
    createMenu(portfolio);
  }

  /**
   * Handles viewing the contents of the portfolio.
   */
  private void handleViewPortfolioContents() {
    view.printViewStocks(portfolio);
    createMenu(portfolio);
  }

  /**
   * Handles analyzing stocks in the portfolio.
   */
  private void handleAnalyzeStocks() {
    view.printChooseStockOption();
    int choice = getValidatedUserChoice(1, 4);

    switch (choice) {
      case 1:
        handleViewStockPerformance();
        break;
      case 2:
        handleViewStockMovingAverage();
        break;
      case 3:
        handleViewStockCrossovers();
        break;
      case 4:
        createMenu(portfolio);
        break;
      default:
        view.displayError("Invalid Input. Please Try Again.");
        this.handleAnalyzeStocks();
    }
  }

  /**
   * Handles viewing stock crossovers.
   */
  private void handleViewStockCrossovers() {
    String ticker = handleSpecifyStock();

    if (!ticker.equalsIgnoreCase("Quit")) {
      model.getStock(ticker);

      view.printSpecifyStartDate();
      String startDate = handleDate();

      view.printSpecifyEndDate();
      String endDate = handleDate();

      view.printXValue();
      int x = getValidatedUserChoice(1, 100);

      view.printResultOfCrossOver(model.xDayCrossoverDays(ticker, x, startDate, endDate));
    }
    createMenu(portfolio);
  }

  /**
   * Handles viewing the stock moving average.
   */
  private void handleViewStockMovingAverage() {
    String ticker = handleSpecifyStock();

    if (!ticker.equalsIgnoreCase("Quit")) {
      model.getStock(ticker);
      view.printSpecifyStartDate();
      String date = handleDate();
      Date dateObj = DateFormat.toDate(date);
      view.printXValue();
      int x = getValidatedUserChoice(1, 100);
      view.printResultOfAverage(model.getStockMovingAverage(ticker, dateObj, x));
    }
    createMenu(portfolio);
  }

  // Prompt user for a ticker symbol and a start and end date;
  // use model to display the change/performance of the stock over that period.
  private void handleViewStockPerformance() {
    String ticker = handleSpecifyStock();

    if (!ticker.equalsIgnoreCase("Quit")) {
      model.getStock(ticker);

      view.printSpecifyStartDate();
      String startDate = handleDate();

      view.printSpecifyEndDate();
      String endDate = handleDate();

      view.printStockPerformance(model.getStockChange(ticker, startDate, endDate));
    }
    createMenu(portfolio);

  }


  /**
   * Reads the number of shares to be bought or sold.
   *
   * @return the number of shares as an integer.
   */
  protected int getNumberShares() {
    int shares = 0;
    view.printSpecifyQuantity();
    while (true) {
      String sharesString = getUserInput();
      try {
        shares = Integer.parseInt(sharesString);
        if (shares < 0) {
          throw new NumberFormatException();
        }
        break;
      } catch (NumberFormatException e) {
        view.displayError("Invalid input. Please enter a valid number.");
      }
    }
    return shares;
  }

  /**
   * Handles executing a transaction option.
   */
  void handleTransaction() {
    view.printTransactionOptions(this.transactionOptions);
    String option = getUserInput();

    transactionOptions.get(option).execute();
    view.printSuccessfulTransaction();
    createMenu(portfolio);
  }

  /**
   * Handles changing the current portfolio.
   *
   * @param menu whether to display the menu after changing the portfolio.
   */
  void handleChangePortfolio(boolean menu) {
    while (true) {
      String[] portfolioNames = model.getPortfolioNames();
      view.printPortfolioChanger(portfolioNames);

      int choice = getValidatedUserChoice(1, portfolioNames.length + 1);

      if (choice == portfolioNames.length + 1) {
        handleCreatePortfolio();
        // After creating a new portfolio, continue the loop to update the list and re-prompt

      } else {
        portfolio = model.getPortfolio(portfolioNames[choice - 1]);
        break;
      }
    }

    if (menu) {
      createMenu(portfolio);
    }
  }

  /**
   * Handles specifying the stock ticker.
   *
   * @return the specified stock ticker as a string.
   */
  protected String handleSpecifyStock() {
    view.printSpecifyStock();
    String ticker = getUserInput();

    if (ticker.equalsIgnoreCase("Quit")) {
      createMenu(portfolio);
      return null;
    } else {
      try {
        if (model.isValidTicker(ticker)) {
          return ticker;
        } else {
          handleError("Invalid ticker symbol. " + ticker);
          return handleSpecifyStock();  // Prompt again for valid ticker
        }
      } catch (Exception e) {
        handleError("An error occurred while validating the ticker symbol.");
        createMenu(portfolio);
        return null;
      }
    }
  }

  /**
   * Handles specifying the date.
   *
   * @return the specified date as a string.
   */
  protected String handleSpecifyDate() {
    view.printSpecifyDate();
    return handleDate();
  }

  /**
   * Reads a date from user input.
   *
   * @return the specified date as a string.
   */
  protected String handleDate() {
    String date = getUserInput();
    if (date.equalsIgnoreCase("Quit")) {
      createMenu(portfolio);
      return null;
    } else {
      try {
        if (StockModel.isValidDate(date)) {
          return date;
        } else {
          handleError("Invalid date format. Please enter the date in YYYY-MM-DD format.");
          return handleDate();  // Prompt again
        }
      } catch (Exception e) {
        handleError("An error occurred while validating the date.");
        createMenu(portfolio);
        return null;
      }
    }
  }

  /**
   * Handles the process of exiting a program. Simply displays farewell message.
   */
  protected void handleExitProgram() {
    view.displayFarewell();
  }

  /**
   * Handles the process of obtaining a portfolio state at a specific date.
   */
  private void handleViewPortfolioOnSpecificDate() {
    view.printSpecifyDate();
    String date = handleDate();
    if (date == null) {
      return; // If the user entered "Quit", exit the method
    }

    view.printPortfolioOnDate(date);

    for (String stock : portfolio.getStockNames()) {
      try {
        Share share = portfolio.getShare(stock);
        double quantity = share.getQuantityOnDate(date);
        double price = share.getPriceOnDate(date);
        double value = price * quantity;
        view.printStockDetailsOnDate(stock, quantity, price, value);
        view.printBlankLine();
      } catch (RuntimeException e) {
        handleError(e.getMessage());
      }
    }

    double totalValue = portfolio.getPortfolioValue(date);
    view.printTotalPortfolioValueOnDate(totalValue);
    createMenu(portfolio);
  }
}