package P2PFileShare_CC.src.client;

import P2PFileShare_CC.src.files.FileBlock;
import P2PFileShare_CC.src.files.FileFolder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class ClientInfo {

    private String ID;
    private FileFolder fileFolder;
    private String pathFolder;
    private InetAddress ipClient;

    public ClientInfo(String ID,FileFolder fileFolder,String path,InetAddress ipClient){
        this.ID = ID;
        this.fileFolder = fileFolder;
        this.pathFolder = path;
        this.ipClient = ipClient;
    }

    public ClientInfo(String sToClient) throws UnknownHostException {
        String[] parts = sToClient.split(",");
        if(parts.length == 4){
            this.ID = parts[0];
            this.fileFolder = new FileFolder(parts[2]);
            this.pathFolder = parts[2];
            this.ipClient = InetAddress.getByAddress(parseIPv4Address(parts[3]));
        }
    }

    public FileFolder getFileFolder(){
        return this.fileFolder;
    }

    public String getID(){
        return this.ID;
    }

    public String getPath(){
        return this.pathFolder;
    }

    public InetAddress getIpClient(){
        return this.ipClient;
    }

    public String toString(){
        return getID() + "," + getFileFolder() + "," + getPath() + "," + getIpClient();
    }

    private byte[] parseIPv4Address(String ipAddress) {
        String[] parts = ipAddress.replaceAll("[^0-9.]", "").split("\\.");

        byte[] byteAddress = new byte[4];
        for (int i = 0; i < 4; i++) {
            byteAddress[i] = (byte) Integer.parseInt(parts[i]);
        }
        return byteAddress;
    }


}
