/* Source
http://www.developer.com/java/web/socket-programming-udp-clientserver-application.html
 */
package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client extends JFrame {

	private DatagramSocket socket;
	public final static String SEPARATOR = ";";
	private static final String DEFAULT_SERVER_IP = "localhost";
	private static final int DEFAULT_PORT = 1234;
	private static final int DEFAULT_OBJECTS_NUMBER = 5;
	private JButton uploadFileButton = new JButton("Send objects");
	private JPanel mainPanel = new JPanel();
	private final JLabel lblServerIp = new JLabel("Server IP: ");
	private final JLabel lblServerPort = new JLabel("Server PORT: ");
	private final JLabel lblObjectsNumber = new JLabel("No. of objects:");
	private final JTextField txtFieldIP = new JTextField();
	private final JTextField txtFieldPort = new JTextField();
	private final JTextField txtFieldObjectsNumber = new JTextField();

	public Client() {
		super("UDP Client");
		mainPanel.setLayout(null);
		lblServerIp.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblServerIp.setBounds(24, 27, 143, 56);
		mainPanel.add(lblServerIp);

		lblServerPort.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblServerPort.setBounds(24, 69, 190, 56);
		mainPanel.add(lblServerPort);

		lblObjectsNumber.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblObjectsNumber.setBounds(24, 111, 255, 56);
		mainPanel.add(lblObjectsNumber);

		txtFieldIP.setBounds(280, 46, 228, 22);
		txtFieldIP.setColumns(10);
		mainPanel.add(txtFieldIP);

		txtFieldPort.setColumns(10);
		txtFieldPort.setBounds(413, 88, 95, 22);
		mainPanel.add(txtFieldPort);

		txtFieldObjectsNumber.setColumns(10);
		txtFieldObjectsNumber.setBounds(413, 124, 95, 22);
		mainPanel.add(txtFieldObjectsNumber);

		uploadFileButton.setBounds(170, 180, 200, 50);
		mainPanel.add(uploadFileButton);
		uploadFileButton.addActionListener((ActionEvent evt) -> {
			try {
				int numObjects = Integer.parseInt(txtFieldObjectsNumber.getText());
				for (int i = 1; i <= numObjects; i++) {
					String msg = i + SEPARATOR + System.currentTimeMillis() + SEPARATOR + numObjects;
					byte buff[] = msg.getBytes();
					DatagramPacket packetSend = new DatagramPacket(buff, buff.length,
							InetAddress.getByName(txtFieldIP.getText()), Integer.parseInt(txtFieldPort.getText()));
					socket.send(packetSend);
				}
			} catch (IOException ex) {
				System.exit(1);
			}
		});

		txtFieldIP.setText(DEFAULT_SERVER_IP);
		txtFieldPort.setText(Integer.toString(DEFAULT_PORT));
		txtFieldObjectsNumber.setText(Integer.toString(DEFAULT_OBJECTS_NUMBER));

		super.add(mainPanel, BorderLayout.CENTER);

		super.setSize(new Dimension(540, 300));
		super.setVisible(true);
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			socket = new DatagramSocket();
		} catch (SocketException ex) {
			System.exit(1);
		}
	}
}
