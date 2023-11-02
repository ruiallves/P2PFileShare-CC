package P2PFileShare_CC.src;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

public class NodeInfo {

    private HashMap<String, List<Integer>> fileblock;
    private InetAddress serverIP;
    private int serverPort;

    public NodeInfo(HashMap<String, List<Integer>> fileblock, InetAddress serverIp, int serverPort) {
        this.fileblock = fileblock;
        this.serverIP = serverIp;
        this.serverPort = serverPort;
    }

    public List<Integer> getBlocks(String fileName) {
        if (fileblock.containsKey(fileName)) {
            return fileblock.get(fileName);
        }
        return null;
    }

    public InetAddress getServerIP() {
        return serverIP;
    }

    public void setServerIP(InetAddress serverIP) {
        this.serverIP = serverIP;
    }

    public int getPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public HashMap<String, List<Integer>> getFiles() {
        return fileblock;
    }

    public void setFiles(HashMap<String, List<Integer>> files) {
        this.fileblock = files;
    }
}
