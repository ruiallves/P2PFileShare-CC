import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerRunner {

    public static void main(String[] args) {

        try{
            InetAddress serverIP = InetAddress.getByName(args[0]);
            int serverPort = Integer.parseInt(args[1]);
            FastFileServer fastFileServer = new FastFileServer(serverIP, serverPort);
            fastFileServer.run();

        } catch (UnknownHostException e){
            e.printStackTrace();
            System.out.println("Invalid IP.");
        }


    }
}
