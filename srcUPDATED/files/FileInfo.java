package P2PFileShare_CC.srcUPDATED.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileInfo {

    private String fileName;
    private long fileLength;

    public FileInfo(String fileName, long fileLength) {
        this.fileName = fileName;
        this.fileLength = fileLength;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileLength() {
        return fileLength;
    }
}
