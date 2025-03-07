package factorserver;

import java.io.PrintWriter;
import java.util.List;

/**
 * This class handles the input and output for the factor
 * program.  Specifically, it reads number from a specified input source
 * until it indicates end of input, and for each number,
 * factors it (using a provided Factorer) and writes the result
 * to the specified output.
 */
public class FactorIO {
  private NumberReader in;
  private PrintWriter out;
  private Factorer factorer;
  /**
   * This is the greeting message that is printed at the start
   * of processing a request. Change this to try out the CI pipeline!
   */
  static String GREETING = "Hello, welcome to Vincen'ts Prime Factor Server!";
  /**
   * This is the prompt that is printed to ask the user for each number
   * to factor.
   */
  static String PROMPT = "Please enter a number:";
  /**
   * This is the message that is printed when the user inputs
   * something that cannot be converted to an integer.
   */
  static String INVALID_NUMBER_MESG = "That is not a valid integer";
  /**
   * This is the message that is printed when the user enters a number
   * that cannot be factored, namely a number that is 0 or negative.
   */
  static String UNFACTORABLE_NUMBER_MESG =
      "That number can't be prime factored (must be  at least 1)";
  /**
   * Constructor for FactorIO that takes in input, output, and factorer.
   * @param in is the input to read numbers from
   * @param out is the output to print message to
   * @param factorer is the object responsible for factoring each number.
   */
  public FactorIO(NumberReader in, PrintWriter out, Factorer factorer) {
    this.in = in;
    this.out = out;
    this.factorer = factorer;
  }
  /**
   * Handles one request, which is defined as
   * printing the greeting, then prompting/reading
   * numbers until end of input, and for each number,
   * either printing the results (if valid) or printing
   * an appropriate error message if not.
   */
  public void handleRequest() {
    // you might think this should be null,
    // but then an invalid number does not reassign it
    // before the loop test.  that is, if the input is
    // "xyz\n2" we would exit the loop after xyz,
    // and not proceess 2.
    Integer i = 0;
    out.println(GREETING);
    do {
      try {
        out.println(PROMPT);
        out.flush();
        i = in.readNumber();
        if (i != null) {
          List<Integer> factors = factorer.factorsOf(i);
          String delim = "";
          for (Integer x : factors) {
            out.print(delim);
            out.print(x);
            delim = " ";
          }
          out.println();
        }
      } catch (NumberFormatException nfe) {
        out.println(INVALID_NUMBER_MESG);
      } catch (IllegalArgumentException iae) {
        out.println(UNFACTORABLE_NUMBER_MESG);
      }
    } while (i != null);
  }
}
