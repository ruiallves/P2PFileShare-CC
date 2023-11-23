package P2PFileShare_CC.src.fstp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Fstp {

    public static final int HEADER_SIZE = 12;
    public static final int BUFFER_SIZE  = 268; // 256(tamanho maximo) + 16(header)
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

    public Fstp(byte[] data, int type, String clientId) {
        this.data = data;
        this.header = new byte[HEADER_SIZE];
        setType(type);
        setClientId(clientId);
        setDataSize(data.length);
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

    public void setClientId(String clientId) {
        byte[] clientIdBytes = clientId.getBytes(StandardCharsets.UTF_8);
        int i = 4;
        for (byte b : clientIdBytes) {
            this.header[i++] = b;
        }
    }

    public int getClientId() {
        return ByteBuffer.wrap(this.header, 8, 4).getInt();
    }

    public void setDataSize(int dataSize) {
        byte[] chunkIdBytes = ByteBuffer.allocate(4).putInt(dataSize).array();
        int i = 8;
        for (byte b : chunkIdBytes) {
            this.header[i++] = b;
        }
    }

    public int getDataSize() {
        return ByteBuffer.wrap(this.header, 12, 4).getInt();
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
        System.out.println("    Client Id " + this.getClientId());
        System.out.println("    Size " + this.getDataSize());
        System.out.println("    Data " + new String(this.getData()));
    }

    public void printFsChunkHeader() {
        System.out.println("    Type " + this.getType());
        System.out.println("    Client Id " + this.getClientId());
        System.out.println("    Size " + this.getDataSize());
    }

}
