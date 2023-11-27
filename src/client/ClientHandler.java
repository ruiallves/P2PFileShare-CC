package P2PFileShare_CC.src.client;

import P2PFileShare_CC.src.files.FileInfo;
import P2PFileShare_CC.src.fstp.Fstp;
import P2PFileShare_CC.src.packet.Packet;
import P2PFileShare_CC.src.packet.PacketManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ClientInfo node;
    private PacketManager packetManager;
    private UDPClientHandler udpClientHandler;

    public ClientHandler(Socket socket, ClientInfo node, PacketManager packetManager, UDPClientHandler udpClientHandler) {
        this.socket = socket;
        this.node = node;
        this.packetManager = packetManager;
        this.udpClientHandler = udpClientHandler;
    }

    @Override
    public void run() {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //leitor que serve para ler o que vem do outro lado
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //escritor para o fstracker
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in)); //leitor que serve para ler do terminal

            int register_controller = 1;
            while (true) {
                String reader = consoleReader.readLine();
                String[] words = reader.split(" ");

                if (reader.toUpperCase().equals("REGISTER") && register_controller == 0) {
                    System.out.println("NODE JÁ REGISTADO!");
                    continue;
                }

                if (reader.toUpperCase().equals("REGISTER")) {
                    Packet register = new Packet(Packet.Type.REQUEST, Packet.Query.REGISTER, node.toString());
                    out.println(register.toString());
                    register_controller = 0;
                } else if (reader.toUpperCase().equals("UPDATE") && register_controller == 0) {
                    Packet update = new Packet(Packet.Type.REQUEST, Packet.Query.UPDATE, node.toString());
                    out.println(update.toString());
                } else if (words[0].toUpperCase().equals("GET") && register_controller == 0) {
                    Packet get = new Packet(Packet.Type.REQUEST, Packet.Query.GET, words[1]);
                    out.println(get.toString());
                } else {
                    System.out.println("ARGUMENTO INVALIDO.");
                    continue;
                }

                String receivedMessage = in.readLine();
                Packet pPacket = new Packet(receivedMessage);
                packetManager.manager(pPacket);

                if (pPacket.getType().equals(Packet.Type.RESPONSE) && pPacket.getQuery().equals(Packet.Query.GET)) {
                    String[] ips = handleGetResponse(pPacket);

                    FileInfo file = createFileInfo(words[1],node.getPath() + "/" + words[1]); //
                    assert file != null;
                    int n_blocks = (int) Math.ceil((double) file.getFileLength() / 256);
                    int n_nodes = ips.length;

                    int blocksPerNode = n_blocks / n_nodes;
                    int remainder = n_blocks % n_nodes;

                    Map<String, List<Integer>> blockDistributionMap = new HashMap<>();

                    int blockCounter = 0;
                    for (int i = 0; i < n_nodes; i++) {
                        int blocksAssigned = blocksPerNode + (i < remainder ? 1 : 0);

                        List<Integer> nodeBlocks = new ArrayList<>();
                        for (int j = 0; j < blocksAssigned; j++) {
                            nodeBlocks.add(blockCounter++);
                        }

                        blockDistributionMap.put(ips[i], nodeBlocks);
                    }

                    while(n_nodes > 0){
                        for(String ip : ips){
                            //if(!ip.equals(node.getIpClient().toString().substring(1))){
                                Fstp packet = new Fstp(serializeBlockList(blockDistributionMap.get(ip)),1, node.getIpClient().toString(), words[1]);
                                udpClientHandler.sendUDPPacket(packet, InetAddress.getByName(ip),8888);
                            //}
                        }
                        n_nodes--;
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] handleGetResponse(Packet pPacket) {
        String content = pPacket.getContent().replaceAll("\\s+", ""); // Remove espaços em branco
        String[] ips = content.split(",");

        for (int i = 0; i < ips.length; i++) {
            ips[i] = ips[i].replaceAll("\\[|\\]", "");

            if (ips[i].startsWith("/")) {
                ips[i] = ips[i].substring(1);
            }
        }

        return ips;
    }

    public static FileInfo createFileInfo(String fileName, String filePath) {
        Path file = Paths.get(filePath);

        if (Files.exists(file)) {
            try {
                long fileLength = Files.size(file);
                return new FileInfo(fileName, fileLength, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("O arquivo não foi encontrado no caminho especificado.");
        }

        return null;
    }

    public static byte[] serializeBlockList(List<Integer> blockList) {
        ByteBuffer buffer = ByteBuffer.allocate(blockList.size() * 4);
        for (int block : blockList) {
            buffer.putInt(block);
        }
        return buffer.array();
    }
}