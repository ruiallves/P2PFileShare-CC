package P2PFileShare_CC.src.client;

import P2PFileShare_CC.src.client.ClientInfo;
import P2PFileShare_CC.src.files.FileFolder;
import P2PFileShare_CC.src.packet.PacketManager;
import P2PFileShare_CC.src.server.Server;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Client {
    public static String NODE_FOLDER;
    public static final int SERVER_PORT = 9090; // Porta do servidor TCP
    public static final int CLIENT_PORT = 8888; // Porta do cliente UDP

    public static void main(String[] args) {
        try {
            Client.NODE_FOLDER = args[1];

            UUID uuid = UUID.randomUUID();
            byte[] uuidBytes = convertUUIDTo4Bytes(uuid);
            String nodeID = new String(uuidBytes, "UTF-8");

            ClientInfo node = new ClientInfo(nodeID, new FileFolder(Client.NODE_FOLDER), Client.NODE_FOLDER, InetAddress.getLocalHost());
            PacketManager packetManager = new PacketManager();
            UDPClientHandler udpClientHandler = new UDPClientHandler(node, CLIENT_PORT, packetManager);

            Socket socket = new Socket(Server.ServerAddress, Server.ServerPort);
            Thread clientThread = new Thread(new ClientHandler(socket, node, packetManager, udpClientHandler));
            Thread udpThread = new Thread(udpClientHandler);

            clientThread.start();
            udpThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] convertUUIDTo4Bytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4]);
        byteBuffer.putInt((int) (uuid.getMostSignificantBits() & 0xFFFFFFFFL));
        return byteBuffer.array();
    }
}
