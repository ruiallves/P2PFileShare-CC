package P2PFileShare_CC.srcUPDATED.server;
import P2PFileShare_CC.srcUPDATED.packet.Packet;

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

    public ServerHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }


    public void run(){

        try{
            Packet pPacket = null;
            String message;

            while((message = in.readLine())!=null){
                String[] words = message.split(" "); //split da mensagem para ser usado no QUERY.GET
                pPacket = new Packet(message);

                switch(pPacket.getQuery()){

                    case Packet.Query.REGISTER:
                        break;

                    case Packet.Query.UPDATE:
                        break;

                    case Packet.Query.GET:
                        break;
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
