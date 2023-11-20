package P2PFileShare_CC.src.client;

import P2PFileShare_CC.src.Package;
import P2PFileShare_CC.src.packet.Packet;
import P2PFileShare_CC.src.packet.PacketManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;
    private ClientInfo node;
    private PacketManager packetManager;
    public ClientHandler(Socket socket, ClientInfo node,PacketManager packetManager) {
        this.socket = socket;
        this.node = node;
        this.packetManager = packetManager;
    }

    @Override
    public void run(){

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //leitor que serve para ler o que vem do outro lado
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //escritor para o fstracker
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in)); //leitor que serve para ler do terminal

            int register_controller = 1;
            while (true) {
                String reader = consoleReader.readLine();
                String[] words = reader.split(" ");

               if(reader.toUpperCase().equals("REGISTER") && register_controller == 0){
                    System.out.println("NODE JÁ REGISTADO!");
                    continue;
                }

                if (reader.toUpperCase().equals("REGISTER") && register_controller == 1) {
                    Packet register = new Packet(Packet.Type.REQUEST, Packet.Query.REGISTER, node.toString(), node.toString());
                    out.println(register.toString());
                    register_controller = 0;
                }

               else if (reader.toUpperCase().equals("UPDATE") && register_controller == 0) {
                    Packet update = new Packet(Packet.Type.REQUEST, Packet.Query.UPDATE, node.toString() , node.toString());
                    out.println(update.toString());
                }

               else if (words[0].toUpperCase().equals("GET") && register_controller == 0) {
                    Packet get = new Packet(Packet.Type.REQUEST, Packet.Query.GET, node.toString(), words[1]);
                    out.println(get.toString());
                }

                else {
                    System.out.println("ARGUMENTO INVALIDO.");
                    continue;
                }


                String receivedMessage = in.readLine();
                Packet pPacket = new Packet(receivedMessage);
                packetManager.manager(pPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
