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
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                
                while (true) {
                    // Ler mensagem do console
                    System.out.print("FSNode message (TCP): ");
                    String message = consoleReader.readLine();
                    writer.println(message);

                    // Receber mensagens do socket
                    String receivedMessage = reader.readLine();
                    System.out.println("FSNode received (TCP): " + receivedMessage);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
