import java.net.InetAddress;

import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Client_Requests {

    public class Client_Request{
        private InetAddress clientIP;
        private int clientPort;
        private String filename;
        private Socket socket;

        public Client_Request(InetAddress IP, int port, String filename, Socket socket){
            this.clientIP = IP;
            this.clientPort = port;
            this.filename = filename;
            this.socket = socket;
        }

        public String getFilename(){
            return this.filename;
        }

        public InetAddress getClientIP() {
            return clientIP;
        }

        public int getClientPort() {
            return clientPort;
        }

        public Socket getSocket() {
            return socket;
        }

        @Override
        public String toString() {
            return "Client_Request{" +
                    "clientIP=" + clientIP +
                    ", clientPort=" + clientPort +
                    ", filename='" + filename + '\'' +
                    '}';
        }
    }

    private LinkedBlockingQueue<Client_Request> requestList;

    public Client_Requests(){
        this.requestList = new LinkedBlockingQueue<>(); //thread safe
    }

    public void addRequest(InetAddress IP, int port, String filename, Socket socket){
        Client_Request cr = new Client_Request(IP, port, filename, socket);
        this.requestList.add(cr);
    }

    public boolean isEmpty(){
        return this.requestList.isEmpty();
    }


    public Client_Request getNextRequest(long timeout){
        //this.requestList.take();
        try{
            return this.requestList.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e){
            return null;
        }
    }

    public Client_Request getNextRequest(){
        try{
            return this.requestList.take();
        } catch (InterruptedException e){
            return null;
        }
    }

    @Override
    public String toString() {
        return "Client_Requests{" +
                "requestList=" + requestList +
                '}';
    }
}
