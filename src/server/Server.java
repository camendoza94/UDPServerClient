/* Source
http://www.developer.com/java/web/socket-programming-udp-clientserver-application.html
*/
package server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

class Server {

    private final static String SEPARATOR = ";";
    private DatagramSocket socket;
    private InetAddress lastAddress;
    private int lastPort;
    private int lastWorkID;
    private HashMap<String, int[]> count = new HashMap<>();

    Server(int port) {
        try {
            socket = new DatagramSocket(port);

        } catch (SocketException ex) {
            System.exit(1);
        }
    }

    void readyToReceivePacket() {
        while (true) try {
            byte buffer[] = new byte[128];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String msg = new String(packet.getData());
            long timeSent = Long.parseLong(msg.split(SEPARATOR)[1].trim());
            long timeRecv = System.currentTimeMillis();
            long diff = timeRecv - timeSent;
            int numSeq = Integer.parseInt(msg.split(SEPARATOR)[0].trim());
            // Print statistics if finished with last client
            if (lastAddress != null && (!packet.getAddress().equals(lastAddress) || packet.getPort() != lastPort)) {
                String key = lastAddress.toString().substring(1) + "_" + lastPort;
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(key + ".txt", true)))) {
                    int[] lastData = count.containsKey(key) ? count.get(key) : new int[3];
                    out.println("Objects received: " + lastData[1]);
                    out.println("Objects lost: " + (lastData[2] - lastData[1]));
                    out.println("Mean response time: " + lastData[0] / lastData[1] + " ms");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                lastWorkID = 0;
            }
            String key = packet.getAddress().toString().substring(1) + "_" + packet.getPort();
            int[] currentData = count.containsKey(key) ? count.get(key) : new int[3];
            currentData[0] += diff;
            currentData[1]++;
            if (Integer.parseInt(msg.split(SEPARATOR)[3].trim()) != lastWorkID)
                currentData[2] += Integer.parseInt(msg.split(SEPARATOR)[2].trim());
            // Changes last IP, port and workID to packet values
            count.put(key, currentData);
            lastAddress = packet.getAddress();
            lastPort = packet.getPort();
            lastWorkID = Integer.parseInt(msg.split(SEPARATOR)[3].trim());
            // Show data on server console
            System.out.println("\nHost: " + packet.getAddress() + "\nPort: " + packet.getPort() + "\nLength: "
                    + packet.getLength() + "\nObjectNumber: " + numSeq + "\nElapsed time (ms) : " + diff);
            // Print elapsed time
            try (PrintWriter out = new PrintWriter(
                    new BufferedWriter(new FileWriter(packet.getAddress().toString().substring(1) + "_" + packet.getPort() + ".txt", true)))) {
                out.println(numSeq + ": " + diff + " ms");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}