package P2PFileShare_CC.src;

import org.javatuples.Pair;

import java.util.*;

public class DataLayer {

    private HashMap<String , NodeInfo > nodes; // key : (ip:port)
    private HashMap<String, List <Pair <String, Integer>>> files; // key : file_name

    public DataLayer()
    {
        nodes = new HashMap<>();
        files = new HashMap<>();
    }

    public boolean RegisterNode(NodeInfo pNodeInfo) {
        try {
            String key = pNodeInfo.getIp() + ":" + pNodeInfo.getPort();
            nodes.put(key, pNodeInfo);
            return true;
        }
        catch (Exception e) {
            return false;
        }

    }

    public boolean UpdateNode(String id, List<Pair<String,Integer>> pFiles) { // (file_name, chunk)
        try {
            for (var entry : files.entrySet()) {
                for (var pair : entry.getValue()) {
                    if (pair.getValue0().matches(id)) {
                        entry.getValue().remove(pair); // funcionamento??
                    }
                }
            }
            
            for (Pair<String,Integer> chunklocation : pFiles){
                List<Pair<String, Integer>> file = files.get(chunklocation.getValue0());
                var mapentry =  files.get(chunklocation.getValue0());
                mapentry.add(new Pair<>(id, chunklocation.getValue1()));
            }
            return true;

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



