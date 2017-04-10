/* Source
http://www.developer.com/java/web/socket-programming-udp-clientserver-application.html
*/
package server;

public class StartServer {

	public static void main(String[] args) {
		Server server;
		if (args.length > 0) {
			server = new Server(Integer.parseInt(args[0]));
		} else {
			server = new Server(1234);
		}
		server.readyToReceivePacket();
	}
}