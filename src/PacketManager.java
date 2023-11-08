package P2PFileShare_CC.src;

import org.javatuples.Pair;

import javax.xml.crypto.Data;
import java.util.List;

import static P2PFileShare_CC.src.FileUtil.obterListaDeArquivosNaPasta;

public class PacketManager {
    //private ServerControler svcont;

    //public PacketManager() {
        //svcont = new ServerControler();
    //}

    //public ServerControler getServerControler(){
        //return this.svcont;
    //}

    private DataLayer dataLayer;

    public PacketManager(){
        dataLayer = new DataLayer();
    }

    public DataLayer getDataLayer(){
        return this.dataLayer;
    }

    public Boolean manager(Package pPackage){

        if(pPackage.getType().equals(Package.Type.REQUEST)){
            if(Package.Query.REGISTER.equals(pPackage.getQuery())){
                NodeInfo node = new NodeInfo(pPackage.getContent());
                dataLayer.RegisterNode(node);
                return true;
            }

            else if (Package.Query.UPDATE.equals(pPackage.getQuery())){
                NodeInfo node = new NodeInfo(pPackage.getContent());
                String filename = pPackage.getContent();
                //System.out.println(filename);
                List<Pair<String, Integer>> fileChunks = obterListaDeArquivosNaPasta(filename);
                dataLayer.UpdateNode(node.getId(),fileChunks);
                return true;
            }

            else if (Package.Query.GET.equals(pPackage.getQuery())) {
                Package node = new Package(pPackage.getContent());
                //var locations = svcont.get(node.getValue());
                return true;
            }
        }

        if(pPackage.getType().equals(Package.Type.RESPONSE)){
            if(Package.Query.REGISTER.equals(pPackage.getQuery())){
                System.out.println(pPackage.getContent());
                return true;
            }

            else if (Package.Query.UPDATE.equals(pPackage.getQuery())){
                NodeInfo node = new NodeInfo(pPackage.getContent());
                //svcont.update(node.getIp(),node.getPort(), node.getFolderName());
                return true;
            }

            else if (Package.Query.GET.equals(pPackage.getQuery())) {
                Package node = new Package(pPackage.getContent());
                //var locations = svcont.get(node.getValue());
                return true;
            }
        }

        return false;
    }

}
