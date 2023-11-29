package P2PFileShare_CC.src.data;

import P2PFileShare_CC.src.client.ClientInfo;
import P2PFileShare_CC.src.files.FileBlock;
import P2PFileShare_CC.src.files.FileFolder;
import P2PFileShare_CC.src.files.FileInfo;
import org.javatuples.Pair;
import java.util.HashMap;
import java.util.List;
import java.net.InetAddress;
import java.util.*;

public class DataLayer{

    private HashMap<String, List<Pair<ClientInfo, FileBlock>>> files;
    private HashMap<String,FileInfo> file;
    private HashMap<String, ClientInfo> nodes;

    public DataLayer()
    {
        nodes = new HashMap<>();
        files = new HashMap<>();
        file = new HashMap<>();
    }

    public void registerNode(ClientInfo node){
        nodes.put(node.getID(),node);
    }

    public void updateFilesDB(ClientInfo node) {
        String path = node.getPath();

        if (path != null) {
            FileFolder fileFolder = new FileFolder(path);

            for (FileInfo fileInfo : fileFolder.getFolder()) {
                String fileName = fileInfo.getFileName();

                if (files.containsKey(fileName)) {
                    List<Pair<ClientInfo, FileBlock>> nodeList = files.get(fileName);
                    addNodeAndBlocks(node, fileInfo, nodeList);
                } else {
                    List<Pair<ClientInfo, FileBlock>> nodeList = new ArrayList<>();
                    addNodeAndBlocks(node, fileInfo, nodeList);
                    files.put(fileName, nodeList);
                }

                file.put(fileName, fileInfo);
            }
        } else {
            System.out.println("O caminho da pasta é nulo para o nó com ID: " + node.getID());
        }
    }


    private void addNodeAndBlocks(ClientInfo node, FileInfo fileInfo, List<Pair<ClientInfo, FileBlock>> nodeList) {
        for (FileBlock block : fileInfo.getBlocks()) {
            Pair<ClientInfo, FileBlock> pair = new Pair<>(node, block);
            nodeList.add(pair);
        }
    }

    public List<InetAddress> getNodesWithFile(String filename) {
        List<InetAddress> nodesWithFile = new ArrayList<>();
        Set<InetAddress> uniqueNodes = new HashSet<>();

        if (files.containsKey(filename)) {
            List<Pair<ClientInfo, FileBlock>> nodeList = files.get(filename);

            for (Pair<ClientInfo, FileBlock> pair : nodeList) {
                InetAddress nodeID = pair.getValue0().getIpClient();
                int blockID = pair.getValue1().getBlockNumber();

                if (uniqueNodes.add(nodeID)) {
                    nodesWithFile.add(nodeID);
                }
            }
        }

        return nodesWithFile;
    }

    public ClientInfo getClientInfoByFileName(String fileName) {
        List<Pair<ClientInfo, FileBlock>> occurrences = files.get(fileName);

        if (occurrences != null && !occurrences.isEmpty()) {
            Pair<ClientInfo, FileBlock> firstOccurrence = occurrences.get(0);
            return firstOccurrence.getValue0();
        }

        return null;
    }




    public void imprimirConteudo() {
        System.out.println("Conteúdo da HashMap 'files':");
        imprimirHashMap(files);

        System.out.println("\nConteúdo da HashMap 'nodes':");
        imprimirHashMap(nodes);

        System.out.println("\nConteúdo da HashMap 'file':");
        imprimirHashMap(file);
    }

    private static void imprimirHashMap(Map<String, ?> hashMap) {
        for (Map.Entry<String, ?> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            System.out.println("Chave: " + key);
            System.out.println("Valor: " + value);
            System.out.println("------");
        }
    }

    public long getFileLength(String key) {
        if (file.containsKey(key)) {
            FileInfo fileInfo = file.get(key);
            return fileInfo.getFileLength();
        } else {
            return -1;
        }
    }

}
