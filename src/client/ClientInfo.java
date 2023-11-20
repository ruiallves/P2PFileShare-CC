package P2PFileShare_CC.src.client;

import P2PFileShare_CC.src.files.FileFolder;

public class ClientInfo {

    private String ID;
    private FileFolder fileFolder;
    private String path;

    public ClientInfo(String ID,FileFolder fileFolder,String path){
        this.ID = ID;
        this.fileFolder = fileFolder;
        this.path = path;
    }

    public ClientInfo(String sToClient){
        String[] parts = sToClient.split(",");
        if(parts.length == 3){
            this.ID = parts[0];
            this.fileFolder = new FileFolder(parts[2]);
            this.path = parts[2];
        }
    }

    public FileFolder getFileFolder(){
        return this.fileFolder;
    }

    public String getID(){
        return this.ID;
    }

    public String getPath(){
        return this.path;
    }

    public String toString(){
        return getID() + "," + getFileFolder() + "," + getPath();
    }
}
