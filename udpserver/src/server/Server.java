/* Source
http://www.developer.com/java/web/socket-programming-udp-clientserver-application.html
*/
package server;

import java.awt.Dimension;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Server extends JFrame {

   private final JTextArea msgArea = new JTextArea();
   private DatagramSocket socket;

   public Server(int port) {
      super("Message Server");
      super.add(new JScrollPane(msgArea));
      super.setSize(new Dimension(450, 350));
      super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      super.setVisible(true);
      msgArea.setEditable(false);

      try {
         socket = new DatagramSocket(12345);

      } catch (SocketException ex) {
         System.exit(1);
      }
   }

   public void readyToReceivPacket() {
      while (true) {
         try {
            byte buffer[] = new byte[128];
            DatagramPacket packet =
               new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String msg = new String(packet.getData());
            System.out.println(msg);
            long timeSent = Long.parseLong(msg.split(";")[1].trim());
            long timeRecv = System.currentTimeMillis();
            long diff = timeRecv - timeSent;
            showMsg("\nHost: " + packet.getAddress()
                    + "\nPort: " + packet.getPort()
                    + "\nLength: " + packet.getLength()
                    + "\nData: "
                    + msg
                    +"\nElapsed time (ms) : " + diff);
            sendPacket(packet);
            try(PrintStream ps = new PrintStream("ElapsedTimes.txt")) 
            { 
                System.out.println("Printing to file");
                ps.println(diff);
                System.out.println("Finished printing");
            }
            catch (IOException ex){
                System.out.println("Couldn't print to file");
                showMsg(ex.getMessage());
            }
         } catch (IOException ex) {
            showMsg(ex.getMessage());
         }
      }
   }

   public void sendPacket(DatagramPacket packetReceived) {
      showMsg("\nEcho to client...");
      try {
         DatagramPacket packet =
            new DatagramPacket(packetReceived.getData(),
            packetReceived.getLength(),
            packetReceived.getAddress(),
            packetReceived.getPort());
         socket.send(packet);
         showMsg("\nMessage sent");
      } catch (IOException ex) {

      }
   }

   public void showMsg(final String msg) {
      SwingUtilities.invokeLater(() -> {
         msgArea.append(msg);
      });
   }

}