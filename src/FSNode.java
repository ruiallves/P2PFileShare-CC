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
            // Conexão TCP
            Socket socket = new Socket("localhost", Macros.DEFAULT_PORT_TCP);

            Thread tcpThread = new Thread(new TCPThread(socket));
            tcpThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }
    
    static class TCPThread implements Runnable {
        private Socket socket;

        public TCPThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                
                while (true) {
                    // Ler mensagem do console
                    Package test = new Package(Package.Type.REQUEST, Package.Query.REGISTER, "Node1", null);
                    System.out.println("FSNode message (TCP): " + test.toString());
                    out.println(test.toString());

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
