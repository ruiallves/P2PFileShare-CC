package P2PFileShare_CC.src.client;

import P2PFileShare_CC.src.files.FileBlock;
import P2PFileShare_CC.src.files.FileInfo;
import P2PFileShare_CC.src.fstp.Fstp;
import P2PFileShare_CC.src.packet.PacketManager;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class UDPClientHandler implements Runnable{
        private DatagramSocket udpSocket;
        private ClientInfo node;
        private PacketManager packetManager;
        private int port;
        private String fileName;
        private int contador; //USADO EM "sendUDPPacket" PARA GUARDARMOS O NOME DO FICHEIRO

        public UDPClientHandler(ClientInfo node, int port, PacketManager packetManager) throws SocketException {
            this.udpSocket = new DatagramSocket(port);
            this.node = node;
            this.packetManager = packetManager;
            this.port = port;
            this.contador = 1;
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[Fstp.BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                while (true) {
                    udpSocket.receive(packet);
                    Fstp fstpPacket = new Fstp(packet.getData());
                    processUDPPacket(fstpPacket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private void processUDPPacket(Fstp fstpPacket) throws UnknownHostException {
        int type = fstpPacket.getType();
        String nodeIp = removeLeadingSlash(fstpPacket.getClientIp());

        switch (type) {
            case 1:
                String fileName = fstpPacket.getFileName();
                String filePath = node.getPath() + "/" + fileName;
                List<Integer> receivedBlocks = deserializeBlockList(fstpPacket.getData());
                FileInfo file = createFileInfo(fileName, filePath);

                try {
                    for (int blockNumber : receivedBlocks) {
                        assert file != null;
                        FileBlock block = file.getBlocks().get(blockNumber);
                        byte[] blockContent = block.getContent().getBytes();
                        Fstp responsePacket = new Fstp(blockContent, 2, node.getIpClient().toString(), String.valueOf(blockNumber), file.getBlocks().size());
                        sendUDPPacket(responsePacket, InetAddress.getByName(nodeIp), 8888);
                        System.out.println("Enviado Block Number: " + blockNumber + " ao node: " + InetAddress.getByName(nodeIp) + ".");
                    }
                } catch (IOException | IndexOutOfBoundsException e) {
                    System.out.println("Erro ao ler o arquivo ou enviar os blocos: " + e.getMessage());
                }
                break;

            case 2:
                byte[] fileContent = fstpPacket.getData();
                String fileNames = getFileName();
                int blockNumber = Integer.parseInt(fstpPacket.getFileName()); // AQUI VEM O BLOCKNUMBER E NÃO O FILE NAME
                String savePath = node.getPath() + "/" + fileNames;
                int n_blocks = fstpPacket.getTotalBlocks();

                try {
                    storeBlock(fileContent, fileNames, blockNumber);
                    File tempDir = new File(node.getPath() + "/temp_blocks");
                    File[] files = tempDir.listFiles();

                    if (n_blocks == files.length) {
                        assembleFile(fileNames, savePath);
                        System.out.println("Arquivo completo recebido e salvo em: " + savePath);
                    }
                } catch (IOException e) {
                    System.out.println("Erro ao salvar o bloco ou montar o arquivo: " + e.getMessage());
                }
                finally {
                    contador = 1;
                }
                break;

            default:
                System.out.println("Tipo de pacote desconhecido: " + type);
        }
    }

    private void storeBlock(byte[] blockContent, String fileName, int blockNumber) throws IOException {
        String tempBlocksPath = node.getPath() + "/temp_blocks/";
        File tempBlocksDir = new File(tempBlocksPath);

        if (!tempBlocksDir.exists()) {
            tempBlocksDir.mkdirs();
        }

        String blockPath = tempBlocksPath + blockNumber + "_" + fileName;
        writeFileBytes(blockPath, blockContent);
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
                if (contador > 0){
                    this.fileName = fstpPacket.getFileName();
                    contador--;
                }
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

    public static List<Integer> deserializeBlockList(byte[] bytes) {
        List<Integer> blockList = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        while (buffer.hasRemaining()) {
            blockList.add(buffer.getInt());
        }

        return blockList;
    }

    public String getFileName(){
            return this.fileName;
    }

    private void assembleFile(String fileName, String savePath) throws IOException {
        File tempDir = new File(node.getPath() + "/temp_blocks");
        File[] files = tempDir.listFiles();

        List<File> orderedBlocks = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            String blockFileName = i + "_" + fileName;
            File blockFile = new File(tempDir, blockFileName);
            orderedBlocks.add(blockFile);
        }

        orderedBlocks.sort(Comparator.comparing(File::getName));

        File outputFile = new File(savePath);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (File blockFile : orderedBlocks) {
                byte[] blockContent = Files.readAllBytes(blockFile.toPath());
                fos.write(blockContent);
            }
        }

        for (File blockFile : orderedBlocks) {
            blockFile.delete();
        }

        tempDir.delete();
    }

}
