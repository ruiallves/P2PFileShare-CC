package P2PFileShare_CC.src;

import static P2PFileShare_CC.src.Package.Type.RESPONSE;

public class PacketManager {
    private ServerControler svcont;
    private String serverIP;
    private Integer serverPort;
    public PacketManager() {
        svcont = new ServerControler();
        serverIP = "127.0.0.1";
        serverPort = 9090;

    }

    public ServerControler getServerControler(){
        return this.svcont;
    }

    public Package manager(Package pPackage){

        if(pPackage.getType().equals(Package.Type.REQUEST)){
            if(Package.Query.REGISTER.equals(pPackage.getQuery())){
                NodeInfo node = new NodeInfo(pPackage.getContent());
                svcont.register(node.getIp(),node.getPort(),node.getFolderName());
                return new Package(RESPONSE, Package.Query.REGISTER,null,"TRUE");
            }

            else if (Package.Query.UPDATE.equals(pPackage.getQuery())){
                NodeInfo node = new NodeInfo(pPackage.getContent());
                svcont.update(node.getIp(),node.getPort(), pPackage.getContent());
                return new Package(RESPONSE, Package.Query.UPDATE,null,"TRUE");
            }

            else if (Package.Query.GET.equals(pPackage.getQuery())) {
                Package node = new Package(pPackage.getContent());
                var locations = svcont.get(node.getValue());
                return new Package(RESPONSE, Package.Query.REGISTER,pPackage.getValue(),locations);
            }
        }
    return null;
    }

}
