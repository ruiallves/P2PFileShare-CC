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
            ServerSocket serverSocket = new ServerSocket(9090); // cria o socket para comunicar com o fstracker
            System.out.println("Servidor ativo na porta " + 9090);

            PacketManager packageManager = new PacketManager();

            while (true) {
                Socket socket = serverSocket.accept(); //aceita a conexão
                Thread thread = new Thread(new TCPThread(socket, packageManager)); //cria uma thread para lidar com ela
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
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // leitor para comunicar com o node
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // escritor para comunicar com o node
                Package pPackage = null;

                while (true) {
                    System.out.println("Waiting for response...");
                    String message = in.readLine(); //lê o que vem do node
                    String[] words = message.split(" "); //split da mensagem para ser usado no QUERY.GET
                    pPackage = new Package(message); //criamos o package com a mensagem

                    if(packageManager.manager(pPackage) && pPackage.getQuery().equals(Package.Query.REGISTER)){
                        pPackage.setType(Package.Type.RESPONSE);
                        pPackage.setContent("FSNode registado com sucesso!");
                        out.println(pPackage.toString());
                    }

                    else if(packageManager.manager(pPackage) && pPackage.getQuery().equals(Package.Query.UPDATE)){
                        pPackage.setType(Package.Type.RESPONSE);
                        pPackage.setContent("FSNode atualizado com sucesso!");
                        out.println(pPackage.toString());
                    }

                    else if(packageManager.manager(pPackage) && pPackage.getQuery().equals(Package.Query.GET)){
                        pPackage.setType(Package.Type.RESPONSE);
                        pPackage.setContent(packageManager.getDataLayer().getNodeForFile(pPackage.getContent()));
                        out.println(pPackage.toString());
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
}