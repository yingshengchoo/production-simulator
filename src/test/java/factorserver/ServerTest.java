package factorserver;
import static org.junit.Assert.*;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

public class ServerTest {
  public String sendRequest(String rString) throws IOException {
    Socket s = new Socket("localhost", 1651);
    s.getOutputStream().write(rString.getBytes());
    s.getOutputStream().flush();
    s.shutdownOutput();
    BufferedReader br =
        new BufferedReader(new InputStreamReader(s.getInputStream()));
    StringBuilder sb = new StringBuilder();
    String str = br.readLine();
    while (str != null) {
      System.out.println("Read: " + str);
      sb.append(str);
      sb.append("\n"); // gets stripped off by br.readLine()
      str = br.readLine();
    }
    return sb.toString();
  }

  @Test(timeout = 2500)
  public void testServer() throws IOException, InterruptedException {
    Thread th = new Thread() {
      @Override()
      public void run() {
        try {
          FactorServer.main(new String[0]);
        } catch (Exception e) {
        }
      }
    };
    th.start();
    Thread.sleep(100); // this is a bit of a hack.
    String actual = sendRequest("4\n9\n");
    assertEquals(IOTest.GREETING + "\n" + IOTest.PROMPT + "\n"
                     + "2 2\n" + IOTest.PROMPT + "\n"
                     + "3 3\n" + IOTest.PROMPT + "\n",
                 actual);
    actual = sendRequest("7\n12\n22\n");
    assertEquals(IOTest.GREETING + "\n" + IOTest.PROMPT + "\n"
                     + "7\n" + IOTest.PROMPT + "\n"
                     + "2 2 3\n" + IOTest.PROMPT + "\n"
                     + "2 11\n" + IOTest.PROMPT + "\n",
                 actual);
    th.interrupt();
    th.join();
  }
}
