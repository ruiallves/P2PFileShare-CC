package P2PFileShare_CC.src;

import org.javatuples.Pair;

import javax.xml.crypto.Data;
import java.util.List;

import static P2PFileShare_CC.src.FileUtil.getFilesInDirectory;
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
                NodeInfo node = new NodeInfo(pPackage.getValue());
                List<String> listaNomes = getFilesInDirectory(pPackage.getContent());
                List<Pair<String, Integer>> files = obterListaDeArquivosNaPasta(pPackage.getContent(), node.getId());
                for(String s : listaNomes) {
                    dataLayer.UpdateNode(s, files);
                }
                return true;
            }

            else if (Package.Query.GET.equals(pPackage.getQuery())) {
                return true;
            }
        }

        else if(pPackage.getType().equals(Package.Type.RESPONSE)){

            if(pPackage.getQuery().equals(Package.Query.GET)){
                System.out.println("Lista de nodes com o ficheiro: " + pPackage.getContent().replace("null",""));
            }
            else{
                System.out.println(pPackage.getContent());
            }
            return true;
        }

        return false;
    }

}
