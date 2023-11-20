package P2PFileShare_CC.srcUPDATED.client;

import P2PFileShare_CC.src.Package;
import P2PFileShare_CC.srcUPDATED.packet.Packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;
    private ClientInfo node;
    public ClientHandler(Socket socket, ClientInfo node) {
        this.socket = socket;
        this.node = node;
    }

    @Override
    public void run(){

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //leitor que serve para ler o que vem do outro lado
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //escritor para o fstracker
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in)); //leitor que serve para ler do terminal

            int register_controller = 1; //int que controla sse o node ja foi registado
            while (true) {
                String reader = consoleReader.readLine(); // lê o que está no terminal e coloca no reader
                String[] words = reader.split(" "); // divide o reader em partes para se usar no GET

               if(reader.toUpperCase().equals("REGISTER") && register_controller == 0){
                    System.out.println("NODE JÁ REGISTADO!");
                    continue;
                }

                if (reader.toUpperCase().equals("REGISTER") && register_controller == 1) {
                    Package register = new Package(Packet.Type.REQUEST, Package.Query.REGISTER, node.toString(), node.toString());
                    out.println(register.toString());
                    register_controller = 0;
                }

               else if (reader.toUpperCase().equals("UPDATE") && register_controller == 0) {
                    Package update = new Package(Package.Type.REQUEST, Package.Query.UPDATE, node.toString() , node.getFolderName());
                    out.println(update.toString());
                }

               else if (words[0].toUpperCase().equals("GET") && register_controller == 0) {
                    Package get = new Package(Package.Type.REQUEST, Package.Query.GET, node.toString(), words[1]);
                    out.println(get.toString());
                }

                else {
                    System.out.println("ARGUMENTO INVALIDO.");
                    continue;
                }


                String receivedMessage = in.readLine(); // Aguarda a resposta do FSTracker
                Package pPackage = new Package(receivedMessage);   // Cria um pacote com base na mensagem recebida

                //packageManager.manager(pPackage);  // Chama o gerenciador de pacotes para processar a mensagem
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
