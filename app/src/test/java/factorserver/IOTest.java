package factorserver;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.stubbing.Answer;
import org.mockito.invocation.InvocationOnMock;
import java.util.ArrayList;
import java.util.Iterator;
public class IOTest {
  static String GREETING = "Hello, welcome to Drew's Prime Factor Server!";
  static String PROMPT = "Please enter a number:";
  static String INVALID_NUMBER_MESG = "That is not a valid integer";
  static String UNFACTORABLE_NUMBER_MESG =
      "That number can't be prime factored (must be  at least 1)";
  private String runOneTest(ArrayList<String> lines) {
    TestPrintWriter tpw = new TestPrintWriter();
    Factorer f = new PrimeFactors();
    // setup mock number reader to give back
    // numbers or exns based on input
    NumberReader nr = mock(NumberReader.class);
    final Iterator<String> lineIt = lines.iterator();
    when(nr.readNumber()).thenAnswer(new Answer<Integer>() {
      public Integer answer(InvocationOnMock iom) {
        if (lineIt.hasNext()) {
          return Integer.parseInt(lineIt.next());
        }
        return null;
      }
    });
    // now make FactorIO object
    FactorIO fio = new FactorIO(nr, tpw.getPrintWriter(), f);
    fio.handleRequest();
    verify(nr, times(lines.size() + 1)).readNumber();
    verifyNoMoreInteractions(nr);
    return tpw.getResult();
  }
  private String makeResString(String[] results) {
    StringBuilder sb = new StringBuilder(GREETING + "\n");
    sb.append(PROMPT + "\n");
    for (String s : results) {
      sb.append(s);
      sb.append("\n");
      sb.append(PROMPT + "\n");
    }
    return sb.toString();
  }
  private void doOneTest(String[] input, String[] outputs) {
    ArrayList<String> param = new ArrayList<String>();
    for (String s : input) {
      param.add(s);
    }
    String expected = makeResString(outputs);
    String actual = runOneTest(param);

    assertEquals(expected, actual);
  }
  @Test
  public void testEmpty() {
    doOneTest(new String[0], new String[0]);
  }
  @Test
  public void testPrimes() {
    String[] primes = new String[] {"2", "7", "101", "4481"};
    String[] temp = new String[1];
    for (String p : primes) {
      temp[0] = p;
      doOneTest(temp, temp);
    }
  }
  @Test
  public void testComposites() {
    doOneTest(new String[] {"12"}, new String[] {"2 2 3"});
    doOneTest(new String[] {"150"}, new String[] {"2 3 5 5"});
  }
  @Test
  public void testErrors() {
    doOneTest(new String[] {"xyz"}, new String[] {INVALID_NUMBER_MESG});
    doOneTest(new String[] {""}, new String[] {INVALID_NUMBER_MESG});
    doOneTest(new String[] {"0"}, new String[] {UNFACTORABLE_NUMBER_MESG});
    doOneTest(new String[] {"-1"}, new String[] {UNFACTORABLE_NUMBER_MESG});
  }
  @Test
  public void testMultipleLines() {
    doOneTest(
        new String[] {"9", "apple", "-20", "42", "99", "0", "fred", "404404"},
        new String[] {"3 3",                    // 9
                      INVALID_NUMBER_MESG,      // apple
                      UNFACTORABLE_NUMBER_MESG, //-20
                      "2 3 7",                  // 42
                      "3 3 11",                 // 99
                      UNFACTORABLE_NUMBER_MESG, // 0
                      INVALID_NUMBER_MESG,      // fred
                      "2 2 7 11 13 101"});      // 404404
  }
}
