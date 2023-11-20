package P2PFileShare_CC.srcUPDATED.client;

import P2PFileShare_CC.srcUPDATED.files.FileFolder;
import P2PFileShare_CC.srcUPDATED.files.FileInfo;

import java.util.List;
import java.util.UUID;

public class ClientInfo {

    private String ID;
    private FileFolder fileFolder;

    public ClientInfo(String ID,FileFolder fileFolder){
        this.ID = ID;
        this.fileFolder = fileFolder;
    }

}
