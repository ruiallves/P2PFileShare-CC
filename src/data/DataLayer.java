package P2PFileShare_CC.src.data;

import P2PFileShare_CC.src.client.ClientInfo;
import P2PFileShare_CC.src.files.FileFolder;
import P2PFileShare_CC.src.files.FileInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataLayer{

    private HashMap<String, List<ClientInfo>> files;
    private HashMap<String, ClientInfo> nodes;

    public DataLayer()
    {
        nodes = new HashMap<>();
        files = new HashMap<>();
    }

    public void registerNode(ClientInfo node){
        nodes.put(node.getID(),node);
    }

    public void updateFilesDB(ClientInfo node) {
        FileFolder fileFolder = node.getFileFolder();

            for (FileInfo fileInfo : fileFolder.getFolder()) {
                String fileName = fileInfo.getFileName();

                if (files.containsKey(fileName)) {
                    List<ClientInfo> nodeList = files.get(fileName);
                    nodeList.add(node);
                } else {
                    List<ClientInfo> nodeList = new ArrayList<>();
                    nodeList.add(node);
                    files.put(fileName, nodeList);
                }
            }
    }

}
