package P2PFileShare_CC.src.client;
import P2PFileShare_CC.src.files.FileFolder;
import P2PFileShare_CC.src.packet.PacketManager;
import P2PFileShare_CC.src.server.Server;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

public class Client {
    public static String NODE_FOLDER;
    public static final int CLIENT_PORT = 8888; // Porta do cliente UDP

    public static void main(String[] args) {
        try {
            Client.NODE_FOLDER = args[1];

            ClientInfo node = new ClientInfo(UUID.randomUUID().toString(), new FileFolder(Client.NODE_FOLDER), Client.NODE_FOLDER, InetAddress.getByName(args[3]));
            PacketManager packetManager = new PacketManager();
            UDPClientHandler udpClientHandler = new UDPClientHandler(node, CLIENT_PORT, packetManager);

            Socket socket = new Socket(args[2], Server.ServerPort);
            Thread clientThread = new Thread(new ClientHandler(socket, node, packetManager, udpClientHandler));
            Thread udpThread = new Thread(udpClientHandler);

            clientThread.start();
            udpThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
