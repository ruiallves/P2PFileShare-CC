package P2PFileShare_CC.src;

import org.javatuples.Pair;

import java.io.File;
import java.util.*;

import static java.awt.SystemColor.info;

public class DataLayer {

    private HashMap<String , NodeInfo > nodes; // key : (ip:port)
    private HashMap<String, List<Pair<String, Integer>>> files; // key : node_id

    public DataLayer()
    {
        nodes = new HashMap<>();
        files = new HashMap<>();
    }

    private HashMap<String, List<Pair<String, Integer>>> getFiles(){
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

    public boolean UpdateNode(String id, List<Pair<String, Integer>> pFiles) {
        try {
            if (files.containsKey(id)) {
                List<Pair<String, Integer>> existingFiles = files.get(id);

                for (Pair<String, Integer> pFile : pFiles) {
                    if (!existingFiles.contains(pFile)) {
                        existingFiles.add(pFile);
                    }
                }
            } else {
                files.put(id, new ArrayList<>(pFiles));
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

    public String getNodeForFile(String fileName) {
        List<String> nodesWithFile = new ArrayList<>();

        for (Map.Entry<String, List<Pair<String, Integer>>> entry : files.entrySet()) {
            String nodeId = entry.getKey();
            List<Pair<String, Integer>> fileChunks = entry.getValue();

            for (Pair<String, Integer> fileChunk : fileChunks) {
                String file = fileChunk.getValue0();
                if (file.equals(fileName)) {
                    nodesWithFile.add(nodeId);
                    break;
                }
            }
        }

        if(!nodesWithFile.isEmpty()){
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < nodesWithFile.size(); i++) {
                result.append(nodesWithFile.get(i));
                if (i < nodesWithFile.size() - 1) {
                    result.append(", ");
                }
            }
            return result.toString();
        }

    return null;
    }



    public List<String> getFileNamesForId(String nodeId) {
        List<String> fileNames = new ArrayList<>();

        for (Map.Entry<String, List<Pair<String, Integer>>> entry : files.entrySet()) {
            List<Pair<String, Integer>> pairs = entry.getValue();
            for (Pair<String, Integer> pair : pairs) {
                if (pair.getValue0().equals(nodeId)) {
                    fileNames.add(entry.getKey());
                    break;
                }
            }
        }

        return fileNames;
    }

    public List<Pair<String,Integer>> getFileLocation (String file_name){
        try{
            List<Pair<String,Integer>> locations = new ArrayList<>();
            //locations = files.get(file_name);
            return locations;
        }
        catch (Exception e) {
            return null;
        }
    }

    public void printFilesInfo() {
        for (Map.Entry<String, List<Pair<String, Integer>>> entry : files.entrySet()) {
            String nodeId = entry.getKey();
            List<Pair<String, Integer>> fileChunks = entry.getValue();

            System.out.println("Node ID: " + nodeId);
            System.out.println("Files and Chunks:");

            for (Pair<String, Integer> fileChunk : fileChunks) {
                String fileName = fileChunk.getValue0();
                int chunkNumber = fileChunk.getValue1();

                System.out.println("  File Name: " + fileName);
                System.out.println("  Chunk Number: " + chunkNumber);
            }
        }
    }

}



