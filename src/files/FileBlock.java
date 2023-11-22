package P2PFileShare_CC.src.files;

public class FileBlock {
    private int blockNumber;
    private long blockLength;
    private String content;

    public FileBlock(int blockNumber, String content,long blockLength) {
        this.blockNumber = blockNumber;
        this.content = content;
        this.blockLength = blockLength;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public String getContent() {
        return content;
    }

    public long getBlockLength(){
        return blockLength;
    }

    @Override
    public String toString() {
        return "Block " + blockNumber + ": " + content;
    }
}
