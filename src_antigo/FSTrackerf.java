package P2PFileShare_CC.src_antigo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class FSTrackerf {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9090); // Port number for TCP communication

            System.out.println("FSTracker: Waiting for connections...");

            while (true) {
                Socket socket = serverSocket.accept(); // Wait for FSNode to connect
                System.out.println("FSTracker: Connected to FSNode.");

                // Start a new thread to handle the connection with the FSNode
                Thread thread = new Thread(new TCPThread(socket));
                thread.start();
            }
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
                OutputStream output = socket.getOutputStream();

                // Start a thread for receiving messages
                Thread receiveThread = new Thread(new ReceiveThread(input));
                receiveThread.start();

                // Start a thread for sending messages
                Thread sendThread = new Thread(new SendThread(output));
                sendThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ReceiveThread implements Runnable {
        private InputStream input;

        public ReceiveThread(InputStream input) {
            this.input = input;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                while (true) {
                    String message = reader.readLine();
                    System.out.println("FSTracker received: " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class SendThread implements Runnable {
        private OutputStream output;

        public SendThread(OutputStream output) {
            this.output = output;
        }

        @Override
        public void run() {
            try {
                PrintWriter writer = new PrintWriter(output, true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    System.out.print("FSTracker message: ");
                    String message = consoleReader.readLine();
                    writer.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}