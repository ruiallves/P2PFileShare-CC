package P2PFileShare_CC.src.files;

public class FileInfo {


    private String fileName;
    private long fileLength;
    private long chunks;

    public FileInfo(String fileName, long fileLength) {
        this.fileName = fileName;
        this.fileLength = fileLength;
        this.chunks = fileLength / 256;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileLength() {
        return fileLength;
    }
}
