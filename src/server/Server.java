package P2PFileShare_CC.src.server;

import P2PFileShare_CC.src.packet.PacketManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int ServerPort = 9090;

    public static void main(String[] args) throws IOException {
        String ServerAddress = args[1];

        try {
            Socket socket;
            ServerSocket serverSocket = new ServerSocket(ServerPort);
            System.out.println("Servidor ativo em " + ServerAddress + " porta " + ServerPort);
            PacketManager packetManager = new PacketManager();

            while ((socket = serverSocket.accept()) != null) {
                new Thread(new ServerHandler(socket, packetManager)).start();
            }

            serverSocket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
