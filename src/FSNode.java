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

            Thread tcpThread = new Thread(new TCPThread(socket,node));
            tcpThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }
    
    static class TCPThread implements Runnable {
        private Socket socket;
        private NodeInfo node;


        public TCPThread(Socket socket, NodeInfo node) {
            this.socket = socket;
            this.node = node;
        }
        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                
                while (true) {
                    // Ler mensagem do console
                    //Package test = new Package(Package.Type.REQUEST, Package.Query.REGISTER, "Node1", node.toString());
                    //System.out.println("FSNode message (TCP): " + test.toString());
                    //out.println(test.toString());

                    String reader = consoleReader.readLine();

                    if(reader.toUpperCase().equals("REGISTER")){
                        Package register = new Package(Package.Type.REQUEST, Package.Query.REGISTER, "Node1", node.toString()); // ESCREVER APENAS "REGISTER" NO TERMINAL PARA REGISTAR O NODO
                        out.println(register.toString());
                    }

                    // Receber mensagens do socket
                    String receivedMessage = in.readLine();
                    System.out.println("FSNode received (TCP): " + receivedMessage);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
