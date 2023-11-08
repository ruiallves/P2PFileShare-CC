package P2PFileShare_CC.src;

import java.util.UUID;

public class NodeInfo {
    private String id;
    private String ip;
    private int port;
    private String folderName;

    public  NodeInfo(String ip, int port, String folderName) {
        this.id = UUID.randomUUID().toString();
        this.ip = ip;
        this.port = port;
        this.folderName = folderName;
    }

    public  NodeInfo(String id,String ip, int port, String folderName) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.folderName = folderName;
    }


    public NodeInfo(String sToNodeInfo){
        String[] parts = sToNodeInfo.split(",");
        if (parts.length == 4) {
            this.id = parts[0];
            this.ip = parts[1];
            this.port = Integer.parseInt(parts[2]);
            this.folderName = parts[3];
        }
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

    @Override
    public String toString() {
        return getId() + "," + getIp() + "," + port + "," + getFolderName();
    }

}
