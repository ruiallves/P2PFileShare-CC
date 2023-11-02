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

            System.out.println("Servidor ativo em " + "..." + " na porta " + Macros.DEFAULT_PORT_TCP);

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
                BufferedReader readerIn = new BufferedReader(new InputStreamReader(input));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

                while (true) {
                    String message = readerIn.readLine();
                    System.out.println("FSTracker received: " + message);

                    System.out.print("FSTracker message: ");
                    String response = "fafe";
                    writer.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
}