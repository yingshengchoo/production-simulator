package factorserver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayInputStream;
import static org.mockito.Mockito.*;
import java.io.IOException;
import org.mockito.stubbing.Answer;
import org.mockito.invocation.InvocationOnMock;

import java.util.Collections;

public class NumberInputStreamReaderUnitTest {
  private InputStream makeTestInputStream(ArrayList<Integer> inp) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(baos);
    for (Integer i : inp) {
      pw.println(i);
    }
    pw.flush();
    return new ByteArrayInputStream(baos.toByteArray());
  }
  @Test
  public void testOneNum() {
    ArrayList<Integer> data = new ArrayList<Integer>();
    data.add(4);
    NumberInputStreamReader nis =
        new NumberInputStreamReader(makeTestInputStream(data));
    Integer expected = 4;
    assertEquals(expected, nis.readNumber());
    assertEquals(null, nis.readNumber());
  }
  @Test
  public void testAFewNums() {
    ArrayList<Integer> data = new ArrayList<Integer>();
    data.add(4);
    data.add(42);
    data.add(-12);
    data.add(0);
    NumberInputStreamReader nis =
        new NumberInputStreamReader(makeTestInputStream(data));
    Integer expected = 4;
    assertEquals(expected, nis.readNumber());
    expected = 42;
    assertEquals(expected, nis.readNumber());
    expected = -12;
    assertEquals(expected, nis.readNumber());
    expected = 0;
    assertEquals(expected, nis.readNumber());
    assertEquals(null, nis.readNumber());
  }
  @Test
  public void testIoError() {
    // we don't really know or care which read method gets
    // called under the hood, so we just make our InputStream
    // throw IOException for any method that gets called.
    InputStream is = mock(InputStream.class, new Answer<Object>() {
      @Override
      public Object answer(InvocationOnMock inv) throws IOException {
        throw new IOException("Test io exception");
      }
    });
    NumberInputStreamReader nis = new NumberInputStreamReader(is);
    Integer x = nis.readNumber();
    assertEquals(null, x);
  }
}
