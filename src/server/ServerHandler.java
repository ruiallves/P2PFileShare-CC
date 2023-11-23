package P2PFileShare_CC.src.server;
import P2PFileShare_CC.src.client.ClientInfo;
import P2PFileShare_CC.src.packet.Packet;
import P2PFileShare_CC.src.packet.PacketManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private Packet pPacket;
    private PrintWriter out;
    private PacketManager packetManager;

    public ServerHandler(Socket socket, PacketManager packetManager) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.packetManager = packetManager;
    }


    public void run(){

        try{
            Packet pPacket = null;
            String message;

            while((message = in.readLine())!=null){
                String[] words = message.split(" ");
                pPacket = new Packet(message);

                switch(pPacket.getQuery()){

                    case Packet.Query.REGISTER:
                        packetManager.manager(pPacket);
                        pPacket.setType(Packet.Type.RESPONSE);
                        pPacket.setContent("FSNode registado com sucesso!");
                        out.println(pPacket.toString());
                        break;

                    case Packet.Query.UPDATE:
                        packetManager.manager(pPacket);
                        pPacket.setType(Packet.Type.RESPONSE);
                        pPacket.setContent("FSTracker atualizado com sucesso!");
                        out.println(pPacket.toString());
                        break;

                    case Packet.Query.GET:
                        packetManager.manager(pPacket);
                        pPacket.setType(Packet.Type.RESPONSE);
                        out.println(pPacket.toString());
                        break;
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
