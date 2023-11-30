package P2PFileShare_CC.src.fstp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Fstp {

    public static final int HEADER_SIZE = 28;
    public static final int BUFFER_SIZE = 284; // 256(tamanho máximo) + 28(header)
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

    //TYPES:
    // 1-> BLOCK_REQUEST
    // 2-> BLOCK_SEND
    public Fstp(byte[] data, int type, String clientId, String fileName, int totalBlocks) {
        this.data = data;
        this.header = new byte[HEADER_SIZE];
        setType(type);
        setClientIp(clientId);
        setDataSize(data.length);
        setFileName(fileName);
        setTotalBlocks(totalBlocks);
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
            e.printStackTrace();
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
        int i = 12;
        for (byte b : fileNameBytes) {
            this.header[i++] = b;
        }
    }

    public String getFileName() {
        byte[] fileNameBytes = Arrays.copyOfRange(this.header, 12, 24);
        return new String(fileNameBytes, StandardCharsets.UTF_8).trim();
    }

    public void setDataSize(int dataSize) {
        byte[] chunkIdBytes = ByteBuffer.allocate(4).putInt(dataSize).array();
        int i = 8;
        for (byte b : chunkIdBytes) {
            this.header[i++] = b;
        }
    }

    public int getDataSize() {
        return ByteBuffer.wrap(this.header, 8, 4).getInt();
    }

    public byte[] getData() {
        return Arrays.copyOfRange(this.data, 0, this.getDataSize());
    }

    public byte[] getPacket() {
        byte[] res = new byte[this.header.length + this.data.length];
        System.arraycopy(this.header, 0, res, 0, this.header.length);
        System.arraycopy(this.data, 0, res, this.header.length, this.data.length);
        return res;
    }
    public void setTotalBlocks(int totalBlocks) {
        if (totalBlocks >= 0) {
            byte[] totalBlocksBytes = ByteBuffer.allocate(4).putInt(totalBlocks).array();
            int i = 24;
            for (byte b : totalBlocksBytes) {
                this.header[i++] = b;
            }
        }
    }

    public int getTotalBlocks() {
        if (this.header.length >= 24) {
            return ByteBuffer.wrap(this.header, 24, 4).getInt();
        } else {
            return -1;
        }
    }

    public void printFsChunk() {
        System.out.println("    Type " + this.getType());
        System.out.println("    Client Id " + this.getClientIp());
        System.out.println("    FileName" + this.getFileName());
        System.out.println("    Size " + this.getDataSize());
        System.out.println("    Data " + new String(this.getData()));
    }

    public static String removeLeadingSlash(String input) {
        if (input.startsWith("/")) {
            return input.substring(1);
        }
        return input;
    }
}
