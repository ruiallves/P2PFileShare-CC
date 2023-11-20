package P2PFileShare_CC.srcUPDATED.data;

import P2PFileShare_CC.src.NodeInfo;
import P2PFileShare_CC.srcUPDATED.client.ClientInfo;

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

}
