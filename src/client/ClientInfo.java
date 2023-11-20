package P2PFileShare_CC.src.client;

import P2PFileShare_CC.src.files.FileFolder;

public class ClientInfo {

    private String ID;
    private FileFolder fileFolder;

    public ClientInfo(String ID,FileFolder fileFolder){
        this.ID = ID;
        this.fileFolder = fileFolder;
    }

    public ClientInfo(String sToClient){
        String[] parts = sToClient.split(",");
        if(parts.length == 2){
            this.ID = parts[0];
            this.fileFolder = new FileFolder(parts[1]);
        }
    }

    public FileFolder getFileFolder(){
        return this.fileFolder;
    }

    public String getID(){
        return this.ID;
    }

    public String toString(){
        return getID() + "," + getFileFolder();
    }
}
