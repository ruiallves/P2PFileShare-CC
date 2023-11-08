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

            PacketManager packageManager = new PacketManager();

            while (true) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new TCPThread(socket, packageManager));
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class TCPThread implements Runnable {
        private Socket socket;
        private Package pPackage;
        private PacketManager packageManager;
        public TCPThread(Socket socket, PacketManager packageManager) {
            this.socket = socket;
            this.packageManager = packageManager;

        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                Package pPackage = null;

                DataLayer data = new DataLayer();

                while (true) {
                    System.out.println("Waiting for response...");
                    String message = in.readLine();
                    pPackage = new Package(message);
                    //System.out.println("FSTracker received: " + message);

                    if(packageManager.manager(pPackage) && pPackage.getQuery().equals(Package.Query.REGISTER)){
                        //estas linhas comentadas são testes
                        //NodeInfo node = new NodeInfo(pPackage.getContent());
                        //System.out.println(packageManager.getServerControler().getDatalayer().getNodeInfo(node.getId()));;
                        //data.RegisterNode(new NodeInfo(pPackage.getContent()));
                        //System.out.println(data.getNodeInfo(node.getId()));

                        pPackage.setType(Package.Type.RESPONSE);
                        pPackage.setContent("FSNode registado com sucesso!");
                        out.println(pPackage.toString());
                    }

                    else if(packageManager.manager(pPackage) && pPackage.getQuery().equals(Package.Query.UPDATE)){
                        pPackage.setType(Package.Type.RESPONSE);
                        //pPackage.setContent("FSNode registado com sucesso!");
                        out.println(pPackage.toString());
                    }

                    else if(packageManager.manager(pPackage) && pPackage.getQuery().equals(Package.Query.GET)){
                        //pPackage.setType(Package.Type.RESPONSE);
                        //pPackage.setContent("FSNode registado com sucesso!");
                        out.println(pPackage.toString());
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
}