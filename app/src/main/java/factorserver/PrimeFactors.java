package factorserver;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for factoring numbers
 * into their prime factors.
 */
public class PrimeFactors implements Factorer {
  /**
   * Gives the prime factors of the number passed in
   * @param n the number to factor
   * @return a List of the factors of n
   * @throw IllegalArgumentException if x is less than 1
   */
  public List<Integer> factorsOf(int n) {
    ArrayList<Integer> answer = new ArrayList<Integer>();
    if (n < 1) {
      throw new IllegalArgumentException("Can't factor" + n);
    }
    int factor = 2;
    while (n > 1) {
      while (n % factor == 0) {
        answer.add(factor);
        n = n / factor;
      }
      factor++;
    }
    return answer;
  }
}
