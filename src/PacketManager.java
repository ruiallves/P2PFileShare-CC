package P2PFileShare_CC.src;

public class PacketManager {
    private ServerControler svcont;

    public PacketManager() {
        svcont = new ServerControler();
    }

    public Boolean manager(Package pPackage){

        if(Package.Query.REGISTER.equals(pPackage.getQuery())){
            NodeInfo node = new NodeInfo(pPackage.getContent());
            svcont.register(node.getIp(),node.getPort(),node.getFolderName());
            System.out.println("Node com o ip: " + node.getIp() + " registado com sucesso!");
            return false;
        }

        else if (Package.Query.UPDATE.equals(pPackage.getQuery())){
            NodeInfo node = new NodeInfo(pPackage.getContent());
            svcont.register(node.getIp(),node.getPort(), node.getFolderName());
            System.out.println("Node com o ip: " + node.getIp() + " atualizado com sucesso!");
            return true;
        }

        else if (Package.Query.GET.equals(pPackage.getQuery())){
            Package node = new Package(pPackage.getContent());
            var locations = svcont.get(node.getValue());
            System.out.println("File com nome " + node.getValue() + " encontrando em: \n" + locations);
            return true;
        }
        return false;
    }

}
