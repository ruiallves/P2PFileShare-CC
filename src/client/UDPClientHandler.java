package P2PFileShare_CC.src.client;

import P2PFileShare_CC.src.fstp.Fstp;
import P2PFileShare_CC.src.packet.Packet;
import P2PFileShare_CC.src.packet.PacketManager;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;

public class UDPClientHandler implements Runnable{
        private DatagramSocket udpSocket;
        private ClientInfo node;
        private PacketManager packetManager;
        private int port;

        public UDPClientHandler(ClientInfo node, int port, PacketManager packetManager) throws SocketException {
            this.udpSocket = new DatagramSocket(port);
            this.node = node;
            this.packetManager = packetManager;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[Fstp.BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                while (true) {
                    udpSocket.receive(packet);
                    Fstp fstpPacket = new Fstp(packet.getData());
                    //fstpPacket.printFsChunk();
                    processUDPPacket(fstpPacket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private void processUDPPacket(Fstp fstpPacket) {
        int type = fstpPacket.getType();
        String nodeIp = removeLeadingSlash(fstpPacket.getClientIp());

        switch (type) {
            case 1:
                String fileName = fstpPacket.getFileName();
                String filePath = node.getPath() + "/" + fileName;

                try {
                    byte[] fileContent = readFileBytes(filePath);

                    Fstp responsePacket = new Fstp(fileContent, 2, node.getIpClient().toString(),fileName);
                    sendUDPPacket(responsePacket, InetAddress.getByName(nodeIp), 8888);
                } catch (IOException e) {
                    System.out.println("Erro ao ler o arquivo: " + e.getMessage());
                }
                break;

            case 2:
                byte[] fileContent = fstpPacket.getData();

                String savedFileName = fstpPacket.getFileName();
                String savePath = node.getPath() + "/" +savedFileName;

                try {
                    writeFileBytes(savePath, fileContent);
                    System.out.println("Arquivo salvo em: " + savePath);
                } catch (IOException e) {
                    System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
                }
                break;


            default:
                System.out.println("Tipo de pacote desconhecido: " + type);
        }
    }

    private byte[] readFileBytes(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }
    private void writeFileBytes(String filePath, byte[] fileContent) throws IOException {
        Path path = Paths.get(filePath);
        Files.write(path, fileContent);
    }
        public void sendUDPPacket(Fstp fstpPacket, InetAddress address, int port) {
            try {
                byte[] packetData = fstpPacket.getPacket();
                DatagramPacket packet = new DatagramPacket(packetData, packetData.length, address, port);
                udpSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public static String removeLeadingSlash(String input) {
        if (input.startsWith("/")) {
            return input.substring(1);
        }
        return input;
    }
}
