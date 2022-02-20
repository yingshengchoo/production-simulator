package factorserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
/**
 * This class wraps an input stream with the NumberReader
 * interface, i.e., the ability to call readNumber and
 * get back an integer.
 * @see java.io.InputStream
 */
public class NumberInputStreamReader implements NumberReader {
  BufferedReader lineReader;
  /**
   * Constructs a NumberInputStreamReader which reads
   * from the specified underlying InputStream.
   * @param is is the InputStream to read from.
   */
  public NumberInputStreamReader(InputStream is) {
    lineReader = new BufferedReader(new InputStreamReader(is));
  }
  /**
   * Read one number from the input stream.  This reads
   * a textual representation of an Integer (e.g. "42")
   * and returns the Integer that corresponds to it (e.g., 42).
   * On EOF or an IOException on the underlying stream,
   * this method returns null.
   * @return the number read, or null if EOF or an IOException are encountered
   * @throws NumberFormatException if what is read from the stream
   * is not a valid number (as defined by Integer.parseInt).
   */
  public Integer readNumber() {
    try {
      String line = lineReader.readLine();
      if (line == null) {
        return null;
      }
      return Integer.parseInt(line.trim());
    } catch (IOException ioe) {
      return null;
    }
  }
}
