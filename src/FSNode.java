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
            NodeInfo node = new NodeInfo(args[2],Integer.parseInt(args[3]),args[1]);
            Socket socket = new Socket(node.getIp(), node.getPort());

            PacketManager packageManager = new PacketManager();

            Thread tcpThread = new Thread(new TCPThread(socket,node,packageManager));
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
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

                int register_controller = 1;
                while (true) {
                    String reader = consoleReader.readLine();
                    String[] words = reader.split(" ");

                    //VERIFICAR SE O NODE JÁ ESTÁ REGISTADO ATRAVES DO REGISTER_CONTROLLER
                    if(reader.toUpperCase().equals("REGISTER") && register_controller == 0){
                        System.out.println("NODE JÁ REGISTADO!");
                        continue;
                    }

                    if (reader.toUpperCase().equals("REGISTER") && register_controller == 1) {
                        Package register = new Package(Package.Type.REQUEST, Package.Query.REGISTER, "Node", node.toString());
                        out.println(register.toString());
                        register_controller = 0;
                    }

                    else if (reader.toUpperCase().equals("UPDATE") && register_controller == 0) {
                        Package update = new Package(Package.Type.REQUEST, Package.Query.UPDATE, "Node", node.getFolderName());
                        out.println(update.toString());
                    }

                    else if (words[0].toUpperCase().equals("GET") && register_controller == 0) {
                        Package get = new Package(Package.Type.REQUEST, Package.Query.GET, "Node", words[1]);
                        out.println(get.toString());
                    }

                    else {
                        System.out.println("ARGUMENTO INVALIDO."); // %todo -> isto tem de mandar algo e receber algo, caso contrario não conseguimos escrever mais comandos
                    }

                    // Receber mensagens do socket
                    String receivedMessage = in.readLine();
                    Package pPackage = new Package(receivedMessage);

                    packageManager.manager(pPackage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
