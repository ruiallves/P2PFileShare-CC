package P2PFileShare_CC.src.client;

import P2PFileShare_CC.src.packet.PacketManager;
import P2PFileShare_CC.src.files.FileFolder;
import P2PFileShare_CC.src.server.Server;

import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

public class Client {
    public static String NODE_FOLDER;

    public static void main(String[] args) {

        try{
            Client.NODE_FOLDER = args[1];
            ClientInfo node = new ClientInfo(UUID.randomUUID().toString(),new FileFolder(NODE_FOLDER),NODE_FOLDER,InetAddress.getLocalHost());
            PacketManager packetManager = new PacketManager();

            Socket socket = new Socket(Server.ServerAddress, Server.ServerPort);
            Thread clientThread = new Thread(new ClientHandler(socket,node,packetManager));
            clientThread.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
