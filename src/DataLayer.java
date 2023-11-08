package P2PFileShare_CC.src;

import org.javatuples.Pair;

import java.io.File;
import java.util.*;

import static java.awt.SystemColor.info;

public class DataLayer {

    private HashMap<String , NodeInfo > nodes; // key : (ip:port)
    private HashMap<String, List <Pair <String, Integer>>> files; // key : file_name

    public DataLayer()
    {
        nodes = new HashMap<>();
        files = new HashMap<>();
    }

    public HashMap<String, List <Pair <String, Integer>>> getFiles(){
        return this.files;
    }

    public boolean RegisterNode(NodeInfo pNodeInfo) {
        try {
            String key = pNodeInfo.getIp() + ":" + pNodeInfo.getPort();
            nodes.put(key, pNodeInfo);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean UpdateNode(String id, List<Pair<String,Integer>> pFiles) { // (file_name, chunk)
        try {
            for(Map.Entry<String, List<Pair<String, Integer>>> entry : files.entrySet()){
                List<Pair<String, Integer>> value = entry.getValue();

                for (Pair<String, Integer> pair : value) {

                }
            }

        } catch (Exception e) {
            return false;
        }
    }

    public String getNodeID(String ip, int port) {
        try {
            String key = ip + ":" + port;
            NodeInfo node = nodes.get(key);
            return node.getId();
        }
        catch (Exception e) {
            return null;
        }
    }

    public NodeInfo getNodeInfo(String id) {
        try {
            for (Map.Entry<String, NodeInfo> entry : nodes.entrySet()) {
                var node = entry.getValue();
                if (node.getId().matches(id)) {
                    return node;
                }
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    public List<Pair<String,Integer>> getFileLocation (String file_name){
        try{
            List<Pair<String,Integer>> locations = new ArrayList<>();
            locations = files.get(file_name);
            return locations;
        }
        catch (Exception e) {
            return null;
        }
    }

}



