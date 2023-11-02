import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FastFileServer{

    private DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[Constants.DEFAULT_BUFFER_SIZE];
    private int serverId=1;
    private InetAddress GatewayIP;
    private int GatewayPort;


    public FastFileServer(InetAddress GatewayIP, int GatewayPort)  {
        this.GatewayIP = GatewayIP;
        this.GatewayPort = GatewayPort;
    }

    public void run() {
        try{
            running = false;
            this.socket = new DatagramSocket();
            socket.connect(this.GatewayIP, this.GatewayPort);  //Not sure if this is necessary
            System.out.println("Server is running!");


            running = openConnection(); //Starts the connection with the gateway

            while (running) {
                System.out.println("--------------------------------");
                FS_Chunk_Protocol fsChunk = receivePacket();

                processPacket(fsChunk);

                System.out.println("--------------------------------");
            }
            socket.close();
            System.out.println("Server is shutting down.");
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public boolean openConnectionWithTimeout(){

        for(int i=1; i<=Constants.TRIES_UNTIL_TIMEOUT; i++) {

            // Sends signal to start the connection
            sendPacket(new byte[0], 0, 1, this.serverId);

            // Awaits until the connection is confirmed
            FS_Chunk_Protocol fsChunk = receivePacket(i*1000);
            if( fsChunk!=null){
                processPacket(fsChunk);
                return true;
            }

        }
        System.out.println("Failed to reach HTTPGateway.");
        return false;
    }


    public boolean openConnection(){

        // Sends signal to start the connection
        sendPacket(new byte[0], 0, 1, this.serverId);

        // Awaits until the connection is confirmed
        FS_Chunk_Protocol fsChunk = receivePacket();
        if( fsChunk!=null){
            processPacket(fsChunk);
            return true;
        }
        else
            return false;
    }


    public void sendPacket(byte [] payload, int type, int chunkId, int serverId){
        try{
            FS_Chunk_Protocol newChunk = new FS_Chunk_Protocol(payload, type, chunkId, serverId);
            DatagramPacket packet = new DatagramPacket(newChunk.getPacket(), newChunk.getPacketSize(), this.GatewayIP, this.GatewayPort);
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
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            fsChunk = new FS_Chunk_Protocol(this.buffer);

            System.out.println("Packet received.");
            fsChunk.printFsChunk();
        } catch (IOException e){
            e.printStackTrace();
        }
        return fsChunk;
    }

    public FS_Chunk_Protocol receivePacket(int timeout){
        FS_Chunk_Protocol fsChunk;
        try{
            socket.setSoTimeout(timeout);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            socket.setSoTimeout(0);
            fsChunk = new FS_Chunk_Protocol(this.buffer);

            System.out.println("Packet received.");
            fsChunk.printFsChunk();
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return fsChunk;
    }


    public void processPacket(FS_Chunk_Protocol fsChunk){
        System.out.println("Processing packet.");
        String filename;
        switch (fsChunk.getType()){

            case 1: //Server connection confirmed
                running = fsChunk.getType()==1;
                if(running) System.out.println("Connection opened successfully.");
                this.serverId = fsChunk.getServerId(); //guarda o server ID que lhe foi atribuido
                break;

            case 2: //Server connection closed
                running = false;
                System.out.println("Connection with the server was terminated. Shutting off.");
                break;

            case 3: //Requesting a file
                ArrayList<String> args = parseRequest(new String(fsChunk.getData()));
                int firstChunk = Integer.parseInt(args.get(1));
                int lastChunk = Integer.parseInt(args.get(2));
                if(lastChunk==0)
                    sendFileChunks(args.get(0), firstChunk, firstChunk);
                if (firstChunk<=lastChunk)
                    sendFileChunks(args.get(0), firstChunk, lastChunk);
                break;

            case 5: //Is alive check
                // Sends a Im alive signal
                sendPacket(new byte[0], 6, 1, this.serverId);
                break;

            case 8: //Requesting file info
                filename = new String(fsChunk.getData());
                if(checkIfFileExists(filename)){
                    try{
                        long filesize = Files.size(Paths.get("..\\CC\\ServerFiles\\" + filename));
                        int numberOfChunksNecessary = getNumberOfChunksNecessary(filesize);
                        byte[] payload = ByteBuffer.allocate(4).putInt(numberOfChunksNecessary).array();

                        sendPacket(payload, 9, 1, this.serverId);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else{ // Sends a file not found signal
                    byte[] filenameBytes = filename.getBytes();
                    sendPacket(filenameBytes, 7, 1, this.serverId);
                }
                break;

        }
    }


    public int getNumberOfChunksNecessary(long filesize){
        int numberOfChunksNecessary = (int) (filesize / Constants.PAYLOAD_SIZE);
        if((filesize % Constants.PAYLOAD_SIZE)>0) numberOfChunksNecessary++;
        return numberOfChunksNecessary;
    }


    public ArrayList<String> parseRequest(String request){
        ArrayList<String> res = new ArrayList<>();
        String[] aux1 = request.split(";");
        String[] aux2 = aux1[1].split("-");
        res.add(aux1[0]);
        res.add(aux2[0]);
        res.add(aux2[1]);
        return res;
    }


    public void sendFileChunks(String filename, int firstChunk, int lastChunk){
        try{
            System.out.println("Sending file: " + filename + ",  chunks: " + firstChunk+"-"+lastChunk+".");
            long filesize = Files.size(Paths.get(Constants.SERVER_FILES_PATH + filename));
            int numberOfChunksNecessary = getNumberOfChunksNecessary(filesize);

            byte[] fileToSend = Files.readAllBytes(Paths.get(Constants.SERVER_FILES_PATH + filename));
            ByteBuffer bb = ByteBuffer.wrap(fileToSend);


            for (int i=1; i<=numberOfChunksNecessary; i++){
                int chunkSize = i < numberOfChunksNecessary ? Constants.PAYLOAD_SIZE : (int) (filesize % Constants.PAYLOAD_SIZE);
                byte[] chunkData = new byte[chunkSize];
                bb.get(chunkData, 0, chunkSize);

                if(i>= firstChunk && i<= lastChunk){

                    sendPacket(chunkData, 4, i, this.serverId);
                    System.out.println("Chunk number " + i + " sent.");
                    
                }

            }

        } catch (IOException | BufferUnderflowException e){
            System.out.println("An error has occurred while sending the file.");
            e.printStackTrace();
        }
    }


    public boolean checkIfFileExists(String filename){
        File f = new File(Constants.SERVER_FILES_PATH + filename);
        return f.exists();
    }







}