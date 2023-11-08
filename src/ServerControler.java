package P2PFileShare_CC.src;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;

public class ServerControler {

    private DataLayer datalayer;

    public ServerControler() {
        datalayer = new DataLayer();
    }

    public DataLayer getDatalayer() {
        return datalayer;
    }

    public boolean register(String ip, int port, String directory) {
        NodeInfo node = new NodeInfo(ip, port, directory);
        return datalayer.RegisterNode(node);
    }

    public boolean update(String ip, int port, String new_files_chunks) {
        var id = datalayer.getNodeID(ip, port);
        List<Pair<String, Integer>> new_files = parseUpdateContent(new_files_chunks);
        return datalayer.UpdateNode(id, new_files);
    }

    public String get(String file_name) {
        var out = datalayer.getFileLocation(file_name);
        List<Triplet<String, Integer, Integer>> locations = new ArrayList<>();

        for (var entry : out) {
            var info = datalayer.getNodeInfo(entry.getValue0());
            var ipf = info.getIp();
            var portf = info.getPort();
            Triplet<String, Integer, Integer> output = new Triplet<>(ipf, portf, entry.getValue1());
            locations.add(output);
        }
        return fileLocationToString(locations);
    }

    private List<Pair<String, Integer>> parseUpdateContent (String new_files){
        var lines = new_files.split("\n");
        var result = new ArrayList<Pair<String,Integer>>();
        for ( var line : lines){
            var line_splited = line.split("/");
            var file_name = line_splited[0];
            var chunks = line_splited[1].split(",");
            for (var chunk: chunks){
                var pair = new Pair<>(file_name,Integer.parseInt(chunk));
                result.add(pair);
            }

        }
    return  result;
    }

    private String fileLocationToString(List<Triplet<String,Integer,Integer>> ip_port_chunk){
        StringBuilder result= new StringBuilder();
        for( var entry: ip_port_chunk){
            var line = entry.getValue0() + "/" + entry.getValue1()+ "/" + entry.getValue2()+"\n";
            result.append(line);
        }
        return result.toString();
    }


}