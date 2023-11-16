package P2PFileShare_CC.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class FSNode {
    public static void main(String[] args) {
        try {
            NodeInfo node = new NodeInfo(args[  2],Integer.parseInt(args[3]),args[1]); //preenchemos o nodeInfo com o argumentos
            Socket socket = new Socket(node.getIp(), node.getPort()); // criação do socket para comunicarmos com o fstracker

            PacketManager packageManager = new PacketManager();

            Thread tcpThread = new Thread(new TCPThread(socket,node,packageManager)); //criação da thread para lidar com a comunicação
            tcpThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class TCPThread implements Runnable {
        private Socket socket;
        private NodeInfo node;
        private PacketManager packageManager;

        public TCPThread(Socket socket, NodeInfo node, PacketManager packageManager) {
            this.socket = socket;
            this.node = node;
            this.packageManager = packageManager;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //leitor que serve para ler o que vem do outro lado
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //escritor para o fstracker
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in)); //leitor que serve para ler do terminal

                int register_controller = 1; //int que controla sse o node ja foi registado
                while (true) {
                    String reader = consoleReader.readLine(); // lê o que está no terminal e coloca no reader
                    String[] words = reader.split(" "); // divide o reader em partes para se usar no GET

                    //VERIFICAR SE O NODE JÁ ESTÁ REGISTADO ATRAVES DO REGISTER_CONTROLLER
                    if(reader.toUpperCase().equals("REGISTER") && register_controller == 0){
                        System.out.println("NODE JÁ REGISTADO!");
                        continue;
                    }

                    // Envia um pedido de registro ao FSTracker
                    if (reader.toUpperCase().equals("REGISTER") && register_controller == 1) {
                        Package register = new Package(Package.Type.REQUEST, Package.Query.REGISTER, node.toString(), node.toString());
                        out.println(register.toString());
                        register_controller = 0;
                    }

                    // Envia um pedido de atualização ao FSTracker
                    else if (reader.toUpperCase().equals("UPDATE") && register_controller == 0) {
                        Package update = new Package(Package.Type.REQUEST, Package.Query.UPDATE, node.toString() , node.getFolderName());
                        out.println(update.toString());
                    }

                    // Envia um pedido GET ao FSTracker
                    else if (words[0].toUpperCase().equals("GET") && register_controller == 0) {
                        Package get = new Package(Package.Type.REQUEST, Package.Query.GET, node.toString(), words[1]);
                        out.println(get.toString());
                    }

                    // Mensagem de argumento inválido
                    else {
                        System.out.println("    ARGUMENTO INVALIDO.");
                        continue;
                    }


                    String receivedMessage = in.readLine(); // Aguarda a resposta do FSTracker
                    Package pPackage = new Package(receivedMessage);   // Cria um pacote com base na mensagem recebida

                    packageManager.manager(pPackage);  // Chama o gerenciador de pacotes para processar a mensagem
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}