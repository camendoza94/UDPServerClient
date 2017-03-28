/* Source
http://www.developer.com/java/web/socket-programming-udp-clientserver-application.html
*/
package client;

public class StartClient {
   public static void main(String[] args) {
      Client client=new Client();
      client.readyToReceivPacket();
   }
}