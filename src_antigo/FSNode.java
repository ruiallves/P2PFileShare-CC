package P2PFileShare_CC.src_antigo;

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
            Socket socket = new Socket("localhost", 9090);

            // Start a thread for receiving TCP messages
            Thread receiveTCPThread = new Thread(new ReceiveTCPThread(socket));
            receiveTCPThread.start();

            // Start a thread for sending TCP messages
            Thread sendTCPThread = new Thread(new SendTCPThread(socket));
            sendTCPThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ReceiveTCPThread implements Runnable {
        private Socket socket;

        public ReceiveTCPThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                while (true) {
                    String message = reader.readLine();
                    System.out.println("FSNode received (TCP): " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class SendTCPThread implements Runnable {
        private Socket socket;

        public SendTCPThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    System.out.print("FSNode message (TCP): ");
                    String message = consoleReader.readLine();
                    writer.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
