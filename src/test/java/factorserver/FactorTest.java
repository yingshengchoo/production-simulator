package factorserver;
import java.util.List;
import java.util.ArrayList;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Collections;

public class FactorTest {
  private void testArrVal(List<Integer> actual, int[] expected) {
    List<Integer> expectedal = new ArrayList<Integer>();
    for (int x : expected) {
      expectedal.add(x);
    }
    Collections.sort(actual);
    Collections.sort(expectedal);
    assertEquals(expectedal, actual);
  }
  @Test
  public void testFactor1() {
    Factorer f = new PrimeFactors();
    testArrVal(f.factorsOf(1), new int[0]);
  }
  @Test
  public void testFactorPrimes() {
    Factorer f = new PrimeFactors();
    int primes[] = {2, 3, 5, 7, 11, 101};
    for (int p : primes) {
      testArrVal(f.factorsOf(p), new int[] {p});
    }
  }
  @Test
  public void testFactorSquares() {
    Factorer f = new PrimeFactors();
    int primes[] = {2, 3, 5, 7, 11, 101};
    for (int p : primes) {
      testArrVal(f.factorsOf(p * p), new int[] {p, p});
    }
  }

  @Test
  public void testFactorsVariousCombos() {
    Factorer f = new PrimeFactors();
    testArrVal(f.factorsOf(2 * 7 * 101 * 31 * 2 * 5),
               new int[] {2, 7, 101, 31, 2, 5});
    testArrVal(f.factorsOf(2 * 2 * 7 * 7 * 2 * 5),
               new int[] {2, 2, 7, 7, 2, 5});
    testArrVal(f.factorsOf(3461 * 4079 * 2 * 3 * 2),
               new int[] {3461, 4079, 2, 3, 2});
  }
  @Test
  public void testProblematicFactors() {
    Factorer f = new PrimeFactors();
    int badnums[] = {0, -1, -4, -7, -9};
    for (int n : badnums) {
      boolean threwIllegalArgExn = false;
      try {
        f.factorsOf(n);
      } catch (IllegalArgumentException ile) {
        threwIllegalArgExn = true;
      }
      assertTrue(threwIllegalArgExn);
    }
  }
}
