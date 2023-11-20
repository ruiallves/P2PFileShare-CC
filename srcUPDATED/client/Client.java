package P2PFileShare_CC.srcUPDATED.client;

import P2PFileShare_CC.srcUPDATED.files.FileFolder;
import P2PFileShare_CC.srcUPDATED.files.FileInfo;
import P2PFileShare_CC.srcUPDATED.server.Server;

import java.net.Socket;
import java.util.UUID;

public class Client {
    public static String NODE_FOLDER;

    public static void main(String[] args) {

        try{
            Client.NODE_FOLDER = args[1];
            ClientInfo node = new ClientInfo(UUID.randomUUID().toString(),new FileFolder(NODE_FOLDER));

            Socket socket = new Socket(Server.ServerAddress, Server.ServerPort);
            Thread clientThread = new Thread(new ClientHandler(socket,node));
            clientThread.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
