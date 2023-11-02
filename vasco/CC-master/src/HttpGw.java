import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class HttpGw {


    public static void main(String[] args) throws IllegalMonitorStateException, IOException {
        Client_Requests clientRequests = new Client_Requests();

        // código que inicia uma nova thread para comunicação com o servidor

        HttpGw_ServerSide serverSideThread = new HttpGw_ServerSide(clientRequests); // A ligaçao gateway-servidor fica numa thread à parte
        Thread t = new Thread(serverSideThread);
        t.start();

        // código desta classe, que recebe novos clientes
        ServerSocket serverSocket = new ServerSocket(Constants.GATEWAY_PORT_TCP);
        while (true) {
            Socket socket = serverSocket.accept();

            // cria nova thread HttpGw_ClientSide, que lida com os pedidos do cliente
            HttpGw_ClientSide clientThread = new HttpGw_ClientSide(socket, clientRequests);
            Thread th = new Thread(clientThread);
            th.start();
        }



    }
}
