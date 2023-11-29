package P2PFileShare_CC.src.packet;

import P2PFileShare_CC.src.client.ClientInfo;
import P2PFileShare_CC.src.data.DataLayer;

import java.net.UnknownHostException;

public class PacketManager {
    private DataLayer dataLayer;

    public PacketManager(){
        this.dataLayer = new DataLayer();
    }

    public DataLayer getDataLayer(){
        return this.dataLayer;
    }

    public void manager(Packet pPacket) throws UnknownHostException {

        if(pPacket.getType().equals(Packet.Type.REQUEST)){

            switch (pPacket.getQuery()){

                case Packet.Query.REGISTER:
                    ClientInfo nodeREG = new ClientInfo(pPacket.getContent());
                    dataLayer.registerNode(nodeREG);
                    break;

                case Packet.Query.UPDATE:
                    ClientInfo nodeUPD = new ClientInfo(pPacket.getContent());
                    dataLayer.updateFilesDB(nodeUPD);
                    //dataLayer.imprimirConteudo();
                    break;

                case Packet.Query.GET:
                    pPacket.setContent(dataLayer.getNodesWithFile(pPacket.getContent()).toString());
                    break;

                case Packet.Query.FILE_INFO:
                    pPacket.setContent(String.valueOf(dataLayer.getFileLength(pPacket.getContent())));
                    break;
            }
        }
        else if(pPacket.getType().equals(Packet.Type.RESPONSE) && !pPacket.getQuery().equals(Packet.Query.GET) && !pPacket.getQuery().equals(Packet.Query.FILE_INFO)){
            System.out.println(pPacket.getContent());
        }
    }
}
