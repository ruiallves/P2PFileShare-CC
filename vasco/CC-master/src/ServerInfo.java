import java.net.InetAddress;

public class ServerInfo {

    enum serverState {
        READY, RUNNING, BLACKLISTED
    }

    private InetAddress serverIp;
    private int serverPort;
    private serverState serverState;

    public ServerInfo(InetAddress ip, int port, serverState state){
        this.serverIp = ip;
        this.serverPort = port;
        this.serverState = state;
    }

    public InetAddress getServerIp() {
        return serverIp;
    }

    public void setServerIp(InetAddress serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public ServerInfo.serverState getServerState() {
        return serverState;
    }

    public void setServerState(ServerInfo.serverState serverState) {
        this.serverState = serverState;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "serverIp=" + serverIp +
                ", serverPort=" + serverPort +
                ", serverState=" + serverState +
                '}';
    }
}
