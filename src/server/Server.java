/* Source
http://www.developer.com/java/web/socket-programming-udp-clientserver-application.html
*/
package server;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Server extends JFrame {

	public final static String SEPARATOR = ";";
	private final JTextArea msgArea = new JTextArea();
	private DatagramSocket socket;
	private int lastSeq = 0;
	private int totalObjects = 1;
	private int totalTime;
	private int objectsReceived;
	private int objectsLost;

	public Server(int port) {
		super("Message Server");
		super.add(new JScrollPane(msgArea));
		super.setSize(new Dimension(450, 350));
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setVisible(true);
		msgArea.setEditable(false);

		try {
			socket = new DatagramSocket(port);

		} catch (SocketException ex) {
			System.exit(1);
		}
	}

	public void readyToReceivPacket() {
		while (true) {
			try {
				byte buffer[] = new byte[128];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				String msg = new String(packet.getData());
				long timeSent = Long.parseLong(msg.split(SEPARATOR)[1].trim());
				long timeRecv = System.currentTimeMillis();
				long diff = timeRecv - timeSent;
				totalTime += diff;
				objectsReceived++;
				int numSeq = Integer.parseInt(msg.split(SEPARATOR)[0].trim());
				// Print lost objects
				if (numSeq != lastSeq + 1 && lastSeq != totalObjects) {
					//try (PrintWriter out = new PrintWriter(
							//new BufferedWriter(new FileWriter(packet.getPort() + "LostObjects.txt", true)))) {
						if (numSeq < lastSeq) {
							objectsLost += numSeq;
							//out.println("At least " + numSeq + " object(s) lost at " + timeRecv);
						} else {
							objectsLost += numSeq - lastSeq - 1;
							//out.println("At least " + (numSeq - lastSeq - 1) + " object(s) lost at " + timeRecv);
						}
					//} catch (IOException e) {
					//	System.err.println(e);
					//}
				}
				totalObjects = Integer.parseInt(msg.split(SEPARATOR)[2].trim());
				lastSeq = numSeq;
				// Show statistics on server GUI
				showMsg("\nHost: " + packet.getAddress() + "\nPort: " + packet.getPort() + "\nLength: "
						+ packet.getLength() + "\nObjectNumber: " + numSeq + "\nElapsed time (ms) : " + diff
						+ "\nMean response time (ms) : " + totalTime / objectsReceived + "\nObjects received : "
						+ objectsReceived + "\nObjects lost : " + objectsLost);
				//sendPacket(packet);
				// Print elapsed time
				try (PrintWriter out = new PrintWriter(
						new BufferedWriter(new FileWriter(packet.getPort() + ".txt", true)))) {
					out.println(numSeq + ": " + diff);
				} catch (IOException e) {
					System.err.println(e);
				}
			} catch (IOException ex) {
				showMsg(ex.getMessage());
			}
		}
	}

	public void sendPacket(DatagramPacket packetReceived) {
		showMsg("\nEcho to client...");
		try {
			DatagramPacket packet = new DatagramPacket(packetReceived.getData(), packetReceived.getLength(),
					packetReceived.getAddress(), packetReceived.getPort());
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