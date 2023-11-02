import java.io.*;
import java.net.Socket;

public class HttpGw_ClientSide implements Runnable {
    private Socket socket;
    private Client_Requests clientRequests;


    public HttpGw_ClientSide(Socket socket, Client_Requests clientRequests) {
        this.socket = socket;
        this.clientRequests = clientRequests;
    }

    @Override
    public void run() {
        try {
            // Recebe o pedido HTTPGET e dá parse no nome do ficheiro, e adiciona ao client_requests

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String requestedString;

            if( (requestedString = in.readLine()) !=null ){

                String requestedFile = requestedString.split(" ")[1].replace("/", "");
                System.out.println("File requested: " + requestedFile);
                clientRequests.addRequest(socket.getInetAddress(), socket.getPort(), requestedFile, socket);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
