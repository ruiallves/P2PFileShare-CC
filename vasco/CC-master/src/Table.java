import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Table {

    private ConcurrentHashMap<Integer, ServerInfo> servers;  // id server -> serverInfo
    private int nextServerId;

    public Table(){
        this.servers = new ConcurrentHashMap<>();
        this.nextServerId = 1;
    }



    public int addServer(InetAddress ip, int port){
        ServerInfo serverInfo = new ServerInfo(ip, port, ServerInfo.serverState.READY);
        servers.put(this.nextServerId, serverInfo);
        this.nextServerId++;
        return this.nextServerId-1;
    }


    public InetAddress getServerIP(int serverId){ return servers.get(serverId).getServerIp(); }


    public int getServerPort(int serverId){
        return servers.get(serverId).getServerPort();
    }


    public void setServerState(int serverId, ServerInfo.serverState state){
        servers.get(serverId).setServerState(state);
    }


    public ArrayList<Integer> getServersWithState(ServerInfo.serverState state){
        ArrayList<Integer> res = new ArrayList();
        for(Map.Entry<Integer, ServerInfo> e: servers.entrySet()){
            if(e.getValue().getServerState().equals(state))
                res.add(e.getKey());
        }
        return res;
    }

    public int getServerReady(){
        for(Map.Entry<Integer, ServerInfo> e: servers.entrySet()){
            if(e.getValue().getServerState().equals(ServerInfo.serverState.READY))
                return e.getKey();
        }
        return -1;
    }

    public ArrayList<Integer> getServers(){
        ArrayList<Integer> res = new ArrayList();
        for(Map.Entry<Integer, ServerInfo> e: servers.entrySet()){
            res.add(e.getKey());
        }
        return res;
    }


    public void printTable(){
        System.out.println("Gateway's Table: ");
        for(Map.Entry<Integer, ServerInfo> e: servers.entrySet()){
            System.out.println("Server " + e.getKey() +":\n" +e.getValue().toString());
        }
    }


    public void removeServer(int serverId){
        this.servers.remove(serverId);
    }
}
