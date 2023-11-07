package P2PFileShare_CC.src;

import java.util.UUID;

public class NodeInfo {
    private String id;
    private String ip;
    private int port;
    private String folderName;

    public void NodeInfo(String ip, int port, String folderName) {
        this.id = UUID.randomUUID().toString();
        this.ip = ip;
        this.port = port;
        this.folderName = folderName;
    }

    public String getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getFolderName() {
        return folderName;
    }
}
