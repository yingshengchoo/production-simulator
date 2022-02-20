package factorserver;
import java.io.*;

public class TestPrintWriter {
  ByteArrayOutputStream bytes;
  PrintWriter pw;
  public TestPrintWriter() {
    bytes = new ByteArrayOutputStream();
    pw = new PrintWriter(bytes);
  }
  public PrintWriter getPrintWriter() { return pw; }
  public String getResult() {
    pw.flush();
    return new String(bytes.toByteArray());
  }
}
