package factorserver;
import java.util.List;

/**
 * The interface for objects that can factor
 * an integer into a list of its prime factors
 */
public interface Factorer {
  /**
   * Gives the prime factors of the number passed in
   * @param x the number to factor
   * @return a List of the factors of x.
   * @throw IllegalArgumentException if x is less than 1
   */
  public List<Integer> factorsOf(int x);
}
