import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class FS_Chunk_Protocol {

    private byte[] data;
    private byte[] header;

    public FS_Chunk_Protocol() {
        this.data = new byte[Constants.DEFAULT_BUFFER_SIZE];
    }

    public FS_Chunk_Protocol(byte[] packet) {
        this.header = Arrays.copyOfRange(packet, 0, Constants.HEADER_SIZE);
        int dataSize = getDataSize();
        this.data = Arrays.copyOfRange(packet, Constants.HEADER_SIZE, Constants.HEADER_SIZE + dataSize);

    }

    public FS_Chunk_Protocol(byte[] data, int type, int chunkId, int serverId) {
        this.data = data;
        this.header = new byte[Constants.HEADER_SIZE];
        setType(type);
        setChunkId(chunkId);
        setServerId(serverId);
        setDataSize(data.length);
    }

    /**
     * Gateway - Server
     * Each integer is a code for the type of packet
     * 0 <- server_connection_open
     * 1 -> server_connection_open_confirmation
     * 2 -> server_connection_close
     * 3 -> data_request
     * 4 <- data_sending
     * 5 -> is_alive_check
     * 6 <- is_alive_confirmation
     * 7 <- file_not_found
     * 8 -> data_request_file_info
     * 9 <- data_sending_file_info
     */
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

    /**
     * Used to know how to correctly assemble the packets
     * uses 4 bytes -> integer
     */
    public void setChunkId(int chunkId) {
        byte[] chunkIdBytes = ByteBuffer.allocate(4).putInt(chunkId).array();
        int i = 4;
        for (byte b : chunkIdBytes) {
            this.header[i++] = b;
        }
    }

    public int getChunkId() {
        return ByteBuffer.wrap(this.header, 4, 4).getInt();
    }

    /**
     * Used to know which FastFileServer sent the packet
     * uses 4 bytes -> integer
     */
    public void setServerId(int chunkId) {
        byte[] chunkIdBytes = ByteBuffer.allocate(4).putInt(chunkId).array();
        int i = 8;
        for (byte b : chunkIdBytes) {
            this.header[i++] = b;
        }
    }

    public int getServerId() {
        return ByteBuffer.wrap(this.header, 8, 4).getInt();
    }

    /**
     * Gives information about the size of the payload
     * uses 4 bytes -> integer
     */
    public void setDataSize(int chunkId) {
        byte[] chunkIdBytes = ByteBuffer.allocate(4).putInt(chunkId).array();
        int i = 12;
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
        return Arrays.copyOfRange(this.header, 0, Constants.HEADER_SIZE);
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
        System.out.println("    Chunk Id " + this.getChunkId());
        System.out.println("    Server Id " + this.getServerId());
        System.out.println("    Size " + this.getDataSize());
        System.out.println("    Data " + new String(this.getData()));
    }

    public void printFsChunkHeader() {
        System.out.println("    Type " + this.getType());
        System.out.println("    Chunk Id " + this.getChunkId());
        System.out.println("    Server Id " + this.getServerId());
        System.out.println("    Size " + this.getDataSize());
    }

}
