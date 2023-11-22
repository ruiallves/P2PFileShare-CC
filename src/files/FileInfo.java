package P2PFileShare_CC.src.files;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileInfo {
    private String fileName;
    private long fileLength;
    private List<FileBlock> blocks;

    public FileInfo(String fileName, long fileLength, String filePath) {
        this.fileName = fileName;
        this.fileLength = fileLength;
        this.blocks = divideFileIntoBlocks(filePath);
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public List<FileBlock> getBlocks() {
        return blocks;
    }

    private List<FileBlock> divideFileIntoBlocks(String filePath) {
        List<FileBlock> dividedBlocks = new ArrayList<>();
        long blockSize = 256;
        long chunks = (long) Math.ceil((double) fileLength / blockSize);

        Path file = Paths.get(filePath);

        try {
            byte[] fileData = java.nio.file.Files.readAllBytes(file);

            for (int i = 0; i < chunks; i++) {
                int offset = (int) (i * blockSize);
                int length = (int) Math.min(blockSize, fileLength - offset);

                byte[] blockContent = new byte[length];
                System.arraycopy(fileData, offset, blockContent, 0, length);

                String blockContentString = new String(blockContent);

                FileBlock block = new FileBlock(i, blockContentString,length);
                dividedBlocks.add(block);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dividedBlocks;
    }

    public void printFileInfo() {
        System.out.println("FileName: " + fileName);
        System.out.println("FileLength: " + fileLength);
        System.out.println("Number of Blocks: " + blocks.size());

        for (FileBlock block : blocks) {
            System.out.println("Block ID: " + block.getBlockNumber());
            System.out.println("Block Length: " + block.getBlockLength());
            System.out.println("Content: " + block.getContent());
            System.out.println("\n");
        }
    }
}
