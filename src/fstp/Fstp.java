package P2PFileShare_CC.src.fstp;

import P2PFileShare_CC.src.packet.PacketManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class Fstp {

    public static final int HEADER_SIZE = 44;
    public static final int BUFFER_SIZE  = 300; // 256(tamanho maximo) + 44(header)
    public static final int PAYLOAD_SIZE = BUFFER_SIZE - HEADER_SIZE;
    private byte[] data;
    private byte[] header;

    public Fstp() {
        this.data = new byte[BUFFER_SIZE];
    }

    public Fstp(byte[] packet) {
        this.header = Arrays.copyOfRange(packet, 0, HEADER_SIZE);
        int dataSize = getDataSize();
        this.data = Arrays.copyOfRange(packet, HEADER_SIZE, HEADER_SIZE + dataSize);

    }

    public Fstp(byte[] data, int type, String clientId, String fileName) {
        this.data = data;
        this.header = new byte[HEADER_SIZE];
        setType(type);
        setClientIp(clientId);
        setDataSize(data.length);
        setFileName(fileName);
    }

    public void setType(int type) {
        byte[] Bytes = ByteBuffer.allocate(4).putInt(type).array();
        int i = 0;
        for (byte b : Bytes) {
            this.header[i++] = b;
        }
    }

    public int getType() {
        return ByteBuffer.wrap(this.header, 0, 4).getInt();
    }

    public void setClientIp(String clientIp) {
        try {
            InetAddress address = InetAddress.getByName(removeLeadingSlash(clientIp));
            byte[] addressBytes = address.getAddress();
            int i = 4;
            for (byte b : addressBytes) {
                this.header[i++] = b;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace(); // Trate a exceção conforme necessário
        }
    }

    public String getClientIp() {
        byte[] addressBytes = Arrays.copyOfRange(this.header, 4, 8);
        try {
            InetAddress address = InetAddress.getByAddress(addressBytes);
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setFileName(String fileName) {
        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        int i = 28;
        for (byte b : fileNameBytes) {
            this.header[i++] = b;
        }
    }

    public String getFileName() {
        byte[] fileNameBytes = Arrays.copyOfRange(this.header, 28, 44);
        return new String(fileNameBytes, StandardCharsets.UTF_8).trim();
    }

    public void setDataSize(int dataSize) {
        byte[] chunkIdBytes = ByteBuffer.allocate(4).putInt(dataSize).array();
        int i = 24;
        for (byte b : chunkIdBytes) {
            this.header[i++] = b;
        }
    }

    public int getDataSize() {
        return ByteBuffer.wrap(this.header, 24, 4).getInt();
    }

    public void setData(byte[] d, int size) {
        if (size >= 0)
            System.arraycopy(d, 0, this.data, 0, size);
    }

    public byte[] getData() {
        return Arrays.copyOfRange(this.data, 0, this.getDataSize());
    }

    public byte[] getHeader() {
        return Arrays.copyOfRange(this.header, 0, HEADER_SIZE);
    }

    public byte[] getPacket() {
        byte[] res = new byte[this.header.length + this.data.length];
        System.arraycopy(this.header, 0, res, 0, this.header.length);
        System.arraycopy(this.data, 0, res, this.header.length, this.data.length);
        return res;
    }

    public int getPacketSize() {
        return this.header.length + this.data.length;
    }

    public void printFsChunk() {
        System.out.println("    Type " + this.getType());
        System.out.println("    Client Id " + this.getClientIp());
        System.out.println("    Size " + this.getDataSize());
        System.out.println("    Data " + new String(this.getData()));
        System.out.println("    FileName" + this.getFileName());
    }

    public static String removeLeadingSlash(String input) {
        if (input.startsWith("/")) {
            return input.substring(1);
        }
        return input;
    }

}
