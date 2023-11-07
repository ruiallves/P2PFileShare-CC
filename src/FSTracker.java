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
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

                while (true) {
                    System.out.println("Waiting for response...");
                    String message = in.readLine();
                    System.out.println("FSTracker received: " + message);

                    Package parsedPackage = new Package(message);

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