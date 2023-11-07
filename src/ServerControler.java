package P2PFileShare_CC.src;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class ServerControler {

    private DataLayer datalayer;
    public ServerControler (){
        datalayer = new DataLayer();
    }

    public boolean register(String ip, int port,String directory) {
        NodeInfo node = new NodeInfo(ip,port,directory);
        return datalayer.RegisterNode(node);
    }

    public boolean update(String ip, int port, List<Pair<String,Integer>> new_files_chunks){
        var id = datalayer.getNodeID(ip,port);
        return datalayer.UpdateNode(id,new_files_chunks);
    }

    public List<Triplet<String,Integer,Integer>> get(String ip, int port, String file_name) {
        var out = datalayer.getFileLocation(file_name);
        List<Triplet<String,Integer,Integer>> locations = new ArrayList<>();

        for (:
             ) {  //foreach que percorra o out e atraves do id devolva a porta e ip atraves do node info
                  // criar triplo com ip, port, chunk
                  // colocar no locations
        }

        return locations;

    }
