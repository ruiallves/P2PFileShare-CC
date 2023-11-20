package P2PFileShare_CC.src.packet;

import P2PFileShare_CC.src.client.ClientInfo;
import P2PFileShare_CC.src.data.DataLayer;

public class PacketManager {
    private DataLayer dataLayer;

    public PacketManager(){
        this.dataLayer = new DataLayer();
    }

    public DataLayer getDataLayer(){
        return this.dataLayer;
    }

    public void manager(Packet pPacket){
        ClientInfo node = new ClientInfo(pPacket.getContent());


        if(pPacket.getType().equals(Packet.Type.REQUEST)){

            switch (pPacket.getQuery()){

                case Packet.Query.REGISTER:
                    dataLayer.registerNode(node);
                    break;

                case Packet.Query.UPDATE:
                    dataLayer.updateFilesDB(node);
                    break;

                case Packet.Query.GET:
                    break;
            }
        }
        else if(pPacket.getType().equals(Packet.Type.RESPONSE)){

            switch (pPacket.getQuery()){

                case Packet.Query.REGISTER, Packet.Query.UPDATE:
                    System.out.println(pPacket.getContent());
                    break;

            }
        }
    }
}
