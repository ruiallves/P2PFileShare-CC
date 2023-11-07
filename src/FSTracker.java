package P2PFileShare_CC.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket; 

public class FSTracker {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9090);
            System.out.println("Servidor ativo em " + "..." + " na porta " + 9090);

            ServerControler serverControler = new ServerControler();

            while (true) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new TCPThread(socket,serverControler));
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class TCPThread implements Runnable {
        private Socket socket;
        private ServerControler serverControler;
        public TCPThread(Socket socket, ServerControler serverControler) {
            this.socket = socket;
            this.serverControler = serverControler;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

                while (true) {
                    //System.out.println("Waiting for response...");
                    String message = in.readLine();
                    //System.out.println("FSTracker received: " + message);
                    Package parsedPackage = new Package(message);

                    if(Package.Query.REGISTER.equals(parsedPackage.getQuery())){
                        NodeInfo node = new NodeInfo(parsedPackage.getContent());
                        serverControler.register(node.getIp(),node.getPort(),node.getFolderName());
                        System.out.println("Node com o ip: " + node.getIp() + " registado com sucesso!");
                    }


                    //Package returnPackage = new Package(Package.Type.RESPONSE, Package.Query.REGISTER, "Node1", "Accepted");
                    //out.println(returnPackage.toString());
                    //System.out.println("FSTracker message reply: " + returnPackage.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
}