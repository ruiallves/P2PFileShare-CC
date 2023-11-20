package P2PFileShare_CC.src.data;

import P2PFileShare_CC.src.client.ClientInfo;
import P2PFileShare_CC.src.files.FileFolder;
import P2PFileShare_CC.src.files.FileInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String path = node.getPath();

        if (path != null) {
            FileFolder fileFolder = new FileFolder(path);

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
        } else {
            System.out.println("O caminho da pasta é nulo para o nó com ID: " + node.getID());
        }
    }

    public List<String> getNodesWithFile(String filename) {
        List<String> nodesWithFile = new ArrayList<>();

        if (files.containsKey(filename)) {
            List<ClientInfo> nodeList = files.get(filename);

            for (ClientInfo node : nodeList) {
                nodesWithFile.add(node.getID());
            }
        }

        return nodesWithFile;
    }

    public void imprimirConteudo() {
        System.out.println("Conteúdo da HashMap 'files':");
        imprimirHashMap(files);

        System.out.println("\nConteúdo da HashMap 'nodes':");
        imprimirHashMap(nodes);
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

}
