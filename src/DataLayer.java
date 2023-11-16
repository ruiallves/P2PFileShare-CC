package P2PFileShare_CC.src;

import org.javatuples.Pair;

import java.io.File;
import java.util.*;

import static java.awt.SystemColor.info;

public class DataLayer {

    private HashMap<String , NodeInfo > nodes; // key : (ip:port)
    private HashMap<String, List<Pair<String, Integer>>> files; // key : filename

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

    // C
    public boolean UpdateNode(String filename, List<Pair<String, Integer>> pFiles) {
        try {
            if (files.containsKey(filename)) {
                List<Pair<String, Integer>> existingFiles = files.get(filename);

                for (Pair<String, Integer> pFile : pFiles) {
                    String nodeId = pFile.getValue0();
                    int blockNumber = pFile.getValue1();

                    boolean found = false;
                    for (Pair<String, Integer> existingFile : existingFiles) {
                        if (existingFile.getValue0().equals(nodeId)) {
                            // Atualizar o número do bloco
                            existingFile = new Pair<>(nodeId, blockNumber);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        // Adicionar o novo par
                        existingFiles.add(new Pair<>(nodeId, blockNumber));
                    }
                }

                // Atualizar o HashMap
                files.put(filename, existingFiles);
            } else {
                // Adicionar um novo arquivo ao HashMap
                files.put(filename, pFiles);
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
            String filename = entry.getKey();
            List<Pair<String, Integer>> fileChunks = entry.getValue();

            for (Pair<String, Integer> fileChunk : fileChunks) {
                String nodeId = fileChunk.getValue0();

                System.out.println(fileName);
                System.out.println(filename);

                if (filename.equals(fileName)) {
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

    public void printFilesHashMap() {
        for (Map.Entry<String, List<Pair<String, Integer>>> entry : files.entrySet()) {
            String nodeId = entry.getKey();
            List<Pair<String, Integer>> fileList = entry.getValue();

            System.out.println("FILENAME: " + nodeId);

            for (Pair<String, Integer> file : fileList) {
                String fileName = file.getValue0();
                int chunkNumber = file.getValue1();

                System.out.println("NODES: " + fileName + ", Chunk: " + chunkNumber);
            }

            System.out.println();
        }
    }


}



