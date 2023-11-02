package P2PFileShare_CC.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket; 

public class FSTracker {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(Macros.DEFAULT_PORT_TCP);

            System.out.println("Servidor ativo em " + Macros.DEFAULT_SERVER_IP + " na porta " + Macros.DEFAULT_PORT_TCP);

            while (true) {
                Socket socket = serverSocket.accept();
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

                Thread receiveThread = new Thread(new ReceiveThread(input));
                receiveThread.start();

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
                    // tratar a mensagem recebida!
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
            PrintWriter writer = new PrintWriter(output, true);
            while (true) {
                writer.println();
                // imprementar logistica do output.
            }
        }
    }
}