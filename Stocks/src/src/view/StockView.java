package src.view;

import src.controller.TransactionOption;
import src.model.Portfolio;
import src.model.Share;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Class that represents the visual representation of the Stock Portfolio Manager.
 * Contains all methods related to creating visual output.
 */
public class StockView implements StockViewInterface {
  private final PrintStream out;

  /**
   * Constructor for the view of the stock portfolio manager.
   *
   * @param out the output that will be printed.
   */
  public StockView(PrintStream out) {
    this.out = out;
  }

  /**
   * Prints out prompt, welcoming the user :).
   */
  public void printWelcome() {
    out.print("Welcome to the Stock Portfolio Manager!" + System.lineSeparator());
  }

  /**
   * Prints out prompt, telling user to either create a portfolio or quit due to not having a
   * portfolio in store.
   */
  public void printMenuNoPortfolios() {
    out.print("1) Create A Portfolio" + System.lineSeparator());
    out.print("2) Quit" + System.lineSeparator());
  }

  /**
   * Prints out prompt, telling user to choose one of the 6 menu options regarding their
   * current portfolio selection.
   *
   * @param portfolio The current portfolio that is being managed through the menu.
   */
  public void printMenu(Portfolio portfolio) {
    out.println("====================== Menu ======================");
    out.println("Please Select An Option:");
    out.println("You Have " + portfolio.getPortfolioSize() + " Items In Your Portfolio "
            + portfolio.getName());
    out.println("1) Perform Transaction");
    out.println("2) View Present Portfolio Contents");
    out.println("3) Analyze Stocks");
    out.println("4) View Portfolio Value");
    out.println("5) Change/Create Portfolio");
    out.println("6) View Past Portfolio Value");
    out.println("7) Save Portfolio");
    out.println("8) Graph Portfolio");
    out.println("9) Rebalance Portfolio");
    out.println("10) Save and Exit");
    out.println("======================================================");
  }

  /**
   * Prints a status update to the user, saying that the desired portfolios current state is
   * being saved.
   *
   * @param name The name of the portfolio to save.
   */
  public void savePortfolio(String name) {
    out.println("Saving Portfolio: " + name);
  }

  /**
   * Prints an exit status message, telling the user their current state is being saved as they
   * exit.
   */
  public void exitProgram() {
    out.println("Saving and Exiting The Program");
  }

  /**
   * Prints out the graph of their portfolio performance over specified period of time.
   *
   * @param graph The graph to be printed showing the portfolio performance.
   */
  public void printGraph(String graph) {
    out.println(graph);
  }

  /**
   * Prints out possible transaction options to the user.
   *
   * @param transactionOptions possible transaction options the user can choose from, e.g. Sell on
   *                           a specific date, to buy immediately, etc.
   */
  public void printTransactionOptions(HashMap<String, TransactionOption> transactionOptions) {
    out.println("============== Transaction Options =================");
    out.println("Type the [name] of the type of transaction (e.g. Buy or SellOnDate):");
    for (String option : transactionOptions.keySet()) {
      out.print(transactionOptions.get(option));
    }
    out.println("======================================================");
  }

  /**
   * Prints out prompt, telling user to input the quantity of shares they wish to buy or sell.
   */
  public void printSpecifyQuantity() {
    out.println("Please Specify the Quantity of Shares you wish to perform this action on:");
  }

  /**
   * Prints out prompt, telling user to select their desired portfolio.
   *
   * @param portfolios List of portfolios stored in the model to be chosen from.
   */
  public void printPortfolioChanger(String[] portfolios) {
    int start = 1;
    if (portfolios.length == 0) {
      out.println("No Portfolios Found");
      return;
    }
    out.println("============== Portfolio Selector =================");
    out.println("Please Select A Portfolio: [1-" + portfolios.length + "]");
    for (int x = 0; x < portfolios.length; x++) {
      out.println((x + 1) + ") " + portfolios[x]);
    }
    out.println(portfolios.length + 1 + ") Create New Portfolio");
    out.println("======================================================");
  }

  /**
   * Prints out prompt, telling user to name a new portfolio.
   */
  public void printPortfolioMaker() {
    out.println("============== Portfolio Maker =================");
    out.println("Please Name Your New Portfolio");
    out.println("Write 'Quit' To Cancel");
    out.println("======================================================");
  }

  /**
   * Prints out prompt telling user to inpuit Stock Ticker.
   */
  public void printSpecifyStock() {
    out.println("============== Input Desired Stock =================");
    out.println("Type The Desired Stock Ticker Symbol");
    out.println("Write 'Quit' To Cancel");
    out.println("======================================================");
  }

  /**
   * Prints out prompt telling user to input Sell/Buy action with the specific quantity of
   * shares.
   */
  public void printAddOrSellStock() {
    out.println("Type The Action And Then Quantity");
    out.println("Ex: Sell:30 OR Buy:102");
    out.println("Write 'Quit' To Cancel");
  }

  /**
   * Prints out prompt informing the user that their transaction was successful.
   */
  public void printSuccessfulTransaction() {
    out.println("Transaction Successful!");
  }

  /**
   * Prints out all stocks inside a portfolio in a numbered list, prompting the user to select one.
   *
   * @param portfolio Portfolio whose stocks will be examined.
   */
  public void printViewStocks(Portfolio portfolio) {
    String[] names = portfolio.getStockNames();
    Arrays.sort(names); // Sort the stock names alphabetically
    out.println("============== Portfolio Contents =================");
    for (String stockName : names) {
      Share stock = portfolio.getShare(stockName);
      if (stock != null) {
        double quantity = stock.getQuantity();
        out.println(stockName + ": " + quantity);
      } else {
        out.println(stockName + ": Stock not found");
      }
    }
    out.println("======================================================");
  }

  /**
   * Prints out options to choose while observing a specific stock.
   */
  public void printChooseStockOption() {
    out.println("============== Choose A Stock Analysis Option =================");
    out.println("1) View Stock Performance Over Specified Period");
    out.println("2) View Stock X-Day Moving Average");
    out.println("3) View Stock X-Day Crossovers");
    out.println("4) Back");
    out.println("======================================================");
  }

  public void printStockPerformance(Double change) {
    out.println("The Stock Change Was: " + change);
  }

  /**
   * Prints out message, telling user to input a start date.
   */
  public void printSpecifyStartDate() {
    out.println("Please Specify Start Date YYYY-MM-DD");
  }

  /**
   * Prints out message, telling user to input an end date.
   */
  public void printSpecifyEndDate() {
    out.println("Please Specify End Date YYYY-MM-DD");
  }

  /**
   * Prints out message, telling user to input a date.
   */
  public void printSpecifyDate() {
    out.println("Please Specify The Date YYYY-MM-DD You wish to perform this action on");
  }

  /**
   * Prints message asking user to specify their distribution for rebalancing.
   *
   * @param stock Stock to ender their desired percentage value takeup.
   */
  public void printSpecifyDistribution(String stock) {
    out.println("Please Specify What % Distribution You Wish For This Stock To Have: " + stock);
  }

  /**
   * Prints message saying that rebalance was successful.
   */
  public void printRebalanceSuccess() {
    out.println("Successfully Rebalanced Portfolio");
  }

  /**
   * Prints out message, telling user to input an x-value to observe X-Day information.
   */
  public void printPortfolioValueResult(Double value) {
    out.println("The Portfolio Value Was " + value);
  }

  /**
   * Prints message telling user to make sure they don't rebalance before the last transaction.
   */
  public void printMustBeChronological() {
    out.println("You Cannot Rebalance Before The Last Chronological Transaction!");
  }

  public void printRebalanceWarning() {
    out.println("Rebalancing Will Buy/Sell Stocks Up To 3 Decimal Places!");
  }

  /**
   * Prints out to ask user to input date.
   */
  public void printPortfolioValuePrompt() {
    out.println("Please Specify the date you wish to view the value of the portfolio YYYY-MM-DD");
  }

  /**
   * Prints out message, telling user to input an x-value to observe X-Day information.
   */
  public void printXValue() {
    out.println("Please Input the X Value of days you wish to observe");
  }

  /**
   * Prints out a message showing the result of the X-Day Moving Average.
   *
   * @param num The result.
   */
  public void printResultOfAverage(double num) {
    out.println("The Moving Average Is: " + num);
  }

  /**
   * Prints out a message showing the result of the X-Day crossovers.
   *
   * @param days The resulting days.
   */
  public void printResultOfCrossOver(ArrayList<String> days) {
    out.println("The Crossover Days Are The Following: ");
    for (String day : days) {
      out.println(day);
    }
  }

  /**
   * Print the heading for portfolio details.
   *
   * @param date date that the user wishes to view the portfolio state.
   */
  public void printPortfolioOnDate(String date) {
    System.out.println("Portfolio details for date: " + date);
  }

  /**
   * Print portfolio details containing stocks, their quantity, their price, and the overall value.
   *
   * @param stock    The stock ticker.
   * @param quantity The number of shares owned of this stock.
   * @param price    the price of a single share.
   * @param value    the value of all shares in the stock.
   */
  public void printStockDetailsOnDate(String stock, double quantity, double price, double value) {
    System.out.println("Stock: " + stock);
    System.out.println("Quantity: " + quantity);
    System.out.println("Price Per Share: $" + price);
    System.out.println("Value: $" + value);
  }

  /**
   * Print portfolio details containing the entire portfolio value at a specified date.
   *
   * @param totalValue the total value that the portfolio amounts to.
   */
  public void printTotalPortfolioValueOnDate(double totalValue) {
    System.out.println("Total Portfolio Value: $" + totalValue);
  }

  /**
   * Displays an error message.
   *
   * @param error The error message to display.
   */
  public void displayError(String error) {
    out.println("Error: " + error);
  }

  /**
   * Simply prints an empty line for separation to make it easier on the eyes.
   */
  public void printBlankLine() {
    System.out.println();
  }

  /**
   * Displays a farewell message.
   */
  public void displayFarewell() {
    out.println("Thank You For Using The Stock Portfolio Manager!");
  }


}
