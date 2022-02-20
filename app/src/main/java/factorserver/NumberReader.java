package factorserver;
/**
 * This is the interface for classes that behave
 * as streams of integers.  That is, each call to readNumber
 * returns the next integer in the stream, or null to indicate
 * end of file.
 */
public interface NumberReader {
  /**
   * Reads an integer, and returns it.
   * @return the number that is read, or null on end of file
   * @throw NumberFormatException if what is read cannot be converted to a
   * number
   */
  public Integer readNumber();
}
