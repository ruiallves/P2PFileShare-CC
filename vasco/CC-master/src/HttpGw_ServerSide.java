import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.String.valueOf;

public class HttpGw_ServerSide implements Runnable{
    private DatagramSocket socket;

    private byte[] buffer = new byte[Constants.DEFAULT_BUFFER_SIZE];
    private Table table;
    private boolean running;

    private InetAddress lastReceivedIP;
    private int lastReceivedPort;
    private ArrayList<FS_Chunk_Protocol> chunksToProcess;
    private Client_Requests clientRequests;

    private final Lock socketLock = new ReentrantLock();



    public HttpGw_ServerSide(Client_Requests clientRequests)  {
         this.table = new Table();
         this.chunksToProcess = new ArrayList<>();
         this.clientRequests = clientRequests;
    }

    public void run() {
        try {
            this.socket = new DatagramSocket(Constants.GATEWAY_PORT_UDP);
            this.running = true;
            System.out.println("Gateway is running.");

            new Thread(() -> {
                //System.out.println(Thread.currentThread().getName() + " is listening for client requests");
                //Quando um pedido é adicionado ao clientRequests, a thread acorda e trata de pedir o ficheiro e devolvê-lo

                while(this.running){
                    Client_Requests.Client_Request client_request = this.clientRequests.getNextRequest();  //Fica bloqueado até conseguir um request
                    System.out.println("Client request found!");
                    String filename = client_request.getFilename();
                    Socket clientSocket = client_request.getSocket();


                    try{
                        DataOutputStream clientOut = new DataOutputStream(clientSocket.getOutputStream());
                        if(table.getServersWithState(ServerInfo.serverState.READY).size()>0) {    //se tem pelo menos um server disponivel pede o ficheiro
                            // Get the file's data
                            byte [] data = getFile(filename);

                            // Create a new reply with the data
                            if (data!=null){

                                System.out.println("Sending file " + filename + " to client.");
                                String contentLen = "Content-Length: "+data.length +"\r\n";
                                String contentDisposition = "Content-Disposition: attachment; filename=\"" + filename + "\"";
                                clientOut.write("HTTP/1.1 200 OK\r\n".getBytes(StandardCharsets.UTF_8));
                                clientOut.write(contentLen.getBytes(StandardCharsets.UTF_8));
                                clientOut.write(contentDisposition.getBytes(StandardCharsets.UTF_8));
                                clientOut.write("Content-Type: text/plain\r\n\r\n".getBytes(StandardCharsets.UTF_8));
                                clientOut.write(data);
                                clientOut.flush();
                                clientSocket.close();
                            }


                            else{
                                clientOut.write("HTTP/1.1 404 Not Found Error\r\n".getBytes(StandardCharsets.UTF_8));
                                clientOut.write("Content-Length: 0 \r\n".getBytes(StandardCharsets.UTF_8));
                                clientOut.write("Content-Type: text/plain\r\n\r\n".getBytes(StandardCharsets.UTF_8));
                                clientOut.flush();
                                clientSocket.close();
                            }

                        }
                        else{
                            clientOut.write("HTTP/1.1 500 Internal Server Error\r\n".getBytes(StandardCharsets.UTF_8));
                            clientOut.write("Content-Length: 0 \r\n".getBytes(StandardCharsets.UTF_8));
                            clientOut.write("Content-Type: text/plain\r\n\r\n".getBytes(StandardCharsets.UTF_8));
                            clientOut.flush();
                            clientSocket.close();
                        }

                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }


                System.out.println(Thread.currentThread().getName() + " is ending.");
            }).start();


            while(this.running){
                //System.out.println(Thread.currentThread().getName() + " is listening for servers to connect.");
                //System.out.print(" ");

                // Gateway listening, wants servers to connect
                FS_Chunk_Protocol fsChunk = receivePacket(Constants.SERVER_TIMEOUT_MILISECONDS);
                if(fsChunk!=null)
                    processPacket(fsChunk);


                // Processa chunks que tenham sido ignorados, como novos servidores
                processStoredChunks();
                Thread.sleep(100);
                //table.printTable();
            }


            System.out.println("Gateway is shutting down.");
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    // Processes chunks that arrived in the middle of getting file chunks
    public void processStoredChunks(){
        for (FS_Chunk_Protocol fs_chunk_protocol : this.chunksToProcess){
            System.out.println("Processing ignored chunk...");
            processPacket(fs_chunk_protocol);
            this.chunksToProcess.remove(fs_chunk_protocol);
            System.out.println("Processing ignored chunk finished.");
        }
    }


    public byte[] getFile(String filename){
        System.out.println("--------------------------Requesting file--------------------------");
        int serverReady  = table.getServerReady();
        try{
            socketLock.lock(); // Para garantir que esta operaçao é atomica
            if(serverReady>0){  // There needs to be at least 1 server ready

                FS_Chunk_Protocol fsChunk = requestFileInfo(filename);
                if (fsChunk!=null){

                    int numberOfChunksRequested = ByteBuffer.wrap(fsChunk.getData(), 0, 4).getInt();
                    System.out.println("Received file info.");
                    requestFileChunks(filename, numberOfChunksRequested);

                    HashMap<Integer, byte[]> fileData = receiveFile(numberOfChunksRequested);
                    //Check if any chunk is missing

                    int tries = 0;
                    while (fileData.size()!=numberOfChunksRequested && tries<Constants.TRIES_UNTIL_TIMEOUT) {
                        tries++;
                        System.out.println("Trying to get missing chunks. Tentative number " + tries);
                        fileData = checkAndGetMissingChunks(fileData, filename, numberOfChunksRequested);
                    }

                    //String file = transformIntoFile(fileData);
                    //System.out.println(file);
                    if (fileData.size()!=numberOfChunksRequested){
                        System.out.println("Could not get the file, network problems.");
                        return null;
                    }

                    return concatFileData(fileData);

                }
                else
                    System.out.println("Failed to download file.");
            }
            return null;
        } catch (FileNotFoundException e){
            System.out.println("File " + filename + " was not found.");
        } catch (ChunkNotFoundException e){
            System.out.println("Failed to get all the file chunks.");
        } finally {
            socketLock.unlock();
        }
        return null;
    }

    public byte[] concatFileData(HashMap<Integer, byte[]> fileData){
        int dataSize = 0;
        for (Map.Entry<Integer, byte[]> e : fileData.entrySet()) dataSize += e.getValue().length;

        byte[] allBytes = new byte[dataSize];

        ByteBuffer buff = ByteBuffer.wrap(allBytes);
        for(int i = 1; i <= fileData.size() ; i++){
            buff.put(fileData.get(i));
        }
        return buff.array();
    }


    public HashMap<Integer, byte[]> checkAndGetMissingChunks(HashMap<Integer, byte[]> fileData, String filename, int numberOfChunksRequested) throws ChunkNotFoundException{

        HashMap<Integer,byte[]> aux = new HashMap<>();
        for (int i=1; i<=numberOfChunksRequested; i++){  //Creates a map that for each chunkNumber has the chunk's data
            aux.put(i,fileData.get(i));
        }

        for(Map.Entry<Integer, byte[]> e : aux.entrySet()){

            if (e.getValue()==null){ // If this chunk is not present
                //Get the missing file chunk
                System.out.println("Missing chunk " + e.getKey() + ". Trying to receive it..");

                FS_Chunk_Protocol fs_chunk_protocol = getFileChunk(e.getKey(),filename);
                if (fs_chunk_protocol!=null){
                    fileData.put(fs_chunk_protocol.getChunkId(),fs_chunk_protocol.getData());
                }
                else{
                    System.out.println("Failed to get chunk " + e.getKey() + " for file " + filename + ".");
                    throw new ChunkNotFoundException();
                }
            }
        }
        return fileData;
    }


    public FS_Chunk_Protocol getFileChunk(int chunkId, String filename){

        ArrayList<Integer> severs = table.getServers();

        String request = filename + ";" + chunkId + "-0";   //meti aqui 0 no last chunk para o servidor saber que só estou a pedir 1 chunk
        for(Integer s: severs){
            if(checkIfServerAlive(s)){

                for(int i=1; i<=Constants.TRIES_UNTIL_TIMEOUT; i++){

                    sendPacket(request.getBytes(),3,1,s, table.getServerIP(s), table.getServerPort(s));
                    FS_Chunk_Protocol fsChunk = receivePacket(i*1000);
                    fsChunk.printFsChunkHeader();
                    if(fsChunk!=null){
                        //Check the chunk type
                        if(fsChunk.getType()==4)
                            return fsChunk;
                        else
                            this.chunksToProcess.add(fsChunk);
                    }
                    else{
                        if (i==3){
                            table.setServerState(s, ServerInfo.serverState.BLACKLISTED);
                            System.out.println("Failed to reach server " + s);
                        }
                    }
                }
            }
        }
        System.out.println("There was no server ready. Could not get the file chunk.");
        return null;

    }


    public FS_Chunk_Protocol requestFileInfo(String filename) throws FileNotFoundException {

        ArrayList<Integer> severs = table.getServers();
        for(Integer s: severs){
            if(checkIfServerAlive(s)){

                for(int i=1; i<=Constants.TRIES_UNTIL_TIMEOUT; i++){

                    sendPacket(filename.getBytes(),8,1,s, table.getServerIP(s), table.getServerPort(s));
                    FS_Chunk_Protocol fsChunk = receivePacket(i*1000);

                    if(fsChunk!=null){
                        //Check the chunk type
                        if(fsChunk.getType()==9)
                            return fsChunk;
                        if(fsChunk.getType()==7)
                            throw new FileNotFoundException();
                        else
                            this.chunksToProcess.add(fsChunk);
                    }
                    else{
                        if (i==3){
                            table.setServerState(s, ServerInfo.serverState.BLACKLISTED);
                            System.out.println("Failed to reach server " + s);
                        }
                    }
                }
            }
        }
        System.out.println("There was no server ready.");
        return null;
    }


    public void requestFileChunks(String filename, int numberOfChunksRequested){
        //Check which servers are alive
        ArrayList<Integer> severs = table.getServers();
        for(Integer s: severs){
            checkIfServerAlive(s);
        }

        ArrayList<Integer> seversReady = table.getServersWithState(ServerInfo.serverState.READY);
        int chunksPerServer, rest;
        if (seversReady.size() >= numberOfChunksRequested) {
            chunksPerServer = 1;
            rest = 0;
        }
        else{
            chunksPerServer = numberOfChunksRequested / seversReady.size();
            rest = numberOfChunksRequested % seversReady.size();
        }

        int firstChunk =1, lastChunk;
        int requestsSent = 0;

        for(Integer serverId : seversReady){
            if(requestsSent<numberOfChunksRequested){
                lastChunk = firstChunk + chunksPerServer -1;
                // The last server is asked more chunks if the rest is not 0
                if(seversReady.indexOf(serverId) == seversReady.size()-1){
                    lastChunk += rest;
                }


                String arg= ";" + firstChunk + "-" + lastChunk;
                requestsSent += (lastChunk-firstChunk+1);
                String request = filename + arg;
                sendPacket(request.getBytes(),3,1,serverId, table.getServerIP(serverId), table.getServerPort(serverId));
                firstChunk = lastChunk + 1;
            }
            else return;
        }
    }


    public HashMap<Integer, byte[]> receiveFile(int numberOfChunksRequested){
        System.out.println("Receiving file.");
        HashMap<Integer, byte[]> fileData = new HashMap<>();
        int numberOfTimeouts = 0;

        int count=0;
        boolean running = true;

        while(running && numberOfTimeouts < Constants.TRIES_UNTIL_TIMEOUT){
            FS_Chunk_Protocol fsChunkReceived = receivePacket(1000*(numberOfTimeouts+1));

            if (fsChunkReceived != null){
                //Check the chunk type
                if(fsChunkReceived.getType()==4){
                    fileData.put(fsChunkReceived.getChunkId(),fsChunkReceived.getData());
                    count++;
                }
                else
                    this.chunksToProcess.add(fsChunkReceived);
            }
            else{
                numberOfTimeouts++;
            }


            if (count == numberOfChunksRequested)
                running=false;

        }
        System.out.println("Received "+count+" chunks out of " + numberOfChunksRequested + " chunks.");
        return fileData;
    }


    public String transformIntoFile (HashMap<Integer, byte[]> fileData){   //Provavelmente devolver string nao é o mais correto
        String res = "";
        for(byte[] bytes : fileData.values()){
            res += new String(bytes);
        }
        return res;
    }


    public void processPacket(FS_Chunk_Protocol fsChunkReceived){

        switch (fsChunkReceived.getType()) {

            case 0: //Server wants to connect
                InetAddress serverIp = this.lastReceivedIP;
                int serverPort = this.lastReceivedPort;
                System.out.println("A new server has been connected to the network.");
                //Inserting the new connection in the server table
                int serverId = table.addServer(serverIp, serverPort);
                sendPacket(new byte[0], 1, 1, serverId, serverIp, serverPort);
                break;

            case 4: //Receiving File
                /*HashMap<Integer, byte[]> fileData = receiveFile();
                String file = transformIntoFile(fileData);
                System.out.println(file);*/
                break;

            case 6: //IsAlive confirmation
                System.out.println("Server " + fsChunkReceived.getServerId() + " is alive.");
                break;

            case 7: //Server doesn't have file
                String filename = new String(fsChunkReceived.getData());
                System.out.println("File " + filename + " was not found.");
                break;

            case 9: //File info (number of chunks)
                /*int numberOfChunksRequested = ByteBuffer.wrap(fsChunkReceived.getData(), 0, 4).getInt();
                System.out.println("Received file info.");
                requestFileChunks("twoChunks.txt", numberOfChunksRequested);*/
                break;
        }
    }






    public void closeConnectionWithServer(int serverId){
        sendPacket(new byte[0], 2, 1, serverId, table.getServerIP(serverId), table.getServerPort(serverId));
        table.removeServer(serverId);
        System.out.printf("Connection with server " + serverId + " terminated.");
    }


    public boolean checkIfServerAlive (int serverId){
        for(int i=1; i<=Constants.TRIES_UNTIL_TIMEOUT; i++){

            sendPacket(new byte[0], 5, 1, serverId, table.getServerIP(serverId), table.getServerPort(serverId));
            FS_Chunk_Protocol fsChunk = receivePacket(i*1000);

            if(fsChunk!=null){
                processPacket(fsChunk);
                table.setServerState(serverId, ServerInfo.serverState.READY);
                return true;
            }
            else{
                System.out.println("Failed to reach server "+ serverId+". Tentative number "+i+".");
                table.setServerState(serverId, ServerInfo.serverState.BLACKLISTED);
            }
        }
        table.setServerState(serverId, ServerInfo.serverState.BLACKLISTED);
        System.out.println("Server "+serverId+ " timed out.");
        return false;
    }


    public void sendPacket(byte [] payload, int type, int chunkId, int serverId, InetAddress IP, int port){
        try{
            FS_Chunk_Protocol newChunk = new FS_Chunk_Protocol(payload, type, chunkId, serverId);
            DatagramPacket packet = new DatagramPacket(newChunk.getPacket(), newChunk.getPacketSize(), IP, port);
            socket.send(packet);
            System.out.println(">> Sent packet:");
            //newChunk.printFsChunk();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public FS_Chunk_Protocol receivePacket(){
        FS_Chunk_Protocol fsChunk = new FS_Chunk_Protocol();
        try{
            this.socketLock.lock();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            socket.setSoTimeout(0);  //removes any timeout existing
            this.lastReceivedIP = packet.getAddress();
            this.lastReceivedPort = packet.getPort();

            fsChunk = new FS_Chunk_Protocol(this.buffer);

            System.out.println("Packet received.");
            //fsChunk.printFsChunk();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            this.socketLock.unlock();
        }
        return fsChunk;
    }


    public FS_Chunk_Protocol receivePacket(int timeout){
        FS_Chunk_Protocol fsChunk;
        try{
            this.socketLock.lock();
            socket.setSoTimeout(timeout);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            socket.setSoTimeout(0);
            this.lastReceivedIP = packet.getAddress();
            this.lastReceivedPort = packet.getPort();
            fsChunk = new FS_Chunk_Protocol(this.buffer);

            System.out.println("Packet received.");
            //fsChunk.printFsChunk();
        } catch (IOException e){
            //e.printStackTrace();   printei isto para n estar smp a mostrar a exceçao
            return null;
        } finally {
            this.socketLock.unlock();
        }
        return fsChunk;
    }

}
