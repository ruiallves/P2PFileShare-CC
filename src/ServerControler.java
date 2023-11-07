package P2PFileShare_CC.src;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

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

    public boolean update(String ip, int port, List<Pair<String, Integer>> new_files_chunks) {
        var id = datalayer.getNodeID(ip, port);
        return datalayer.UpdateNode(id, new_files_chunks);
    }

    public List<Triplet<String, Integer, Integer>> get(String ip, int port, String file_name) {
        var out = datalayer.getFileLocation(file_name);
        List<Triplet<String, Integer, Integer>> locations = new ArrayList<>();

        for (var entry : out) {
            var info = datalayer.getNodeInfo(entry.getValue0());
            var ipf = info.getIp();
            var portf = info.getPort();
            Triplet<String, Integer, Integer> output = new Triplet<>(ipf, portf, entry.getValue1());
            locations.add(output);
        }
        return locations;
    }
}