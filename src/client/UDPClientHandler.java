package P2PFileShare_CC.src.client;

import P2PFileShare_CC.src.fstp.Fstp;
import P2PFileShare_CC.src.packet.PacketManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPClientHandler implements Runnable{
        private DatagramSocket udpSocket;
        private ClientInfo node;
        private PacketManager packetManager;
        private int port;

        public UDPClientHandler(ClientInfo node, int port, PacketManager packetManager) throws SocketException {
            this.udpSocket = new DatagramSocket(port);
            this.node = node;
            this.packetManager = packetManager;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[Fstp.BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                while (true) {
                    udpSocket.receive(packet);
                    Fstp fstpPacket = new Fstp(packet.getData());
                    fstpPacket.printFsChunk();
                    //processUDPPacket(fstpPacket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //private void processUDPPacket(Fstp fstpPacket) {
        //
        //}

        public void sendUDPPacket(Fstp fstpPacket, InetAddress address, int port) {
            try {
                byte[] packetData = fstpPacket.getPacket();
                DatagramPacket packet = new DatagramPacket(packetData, packetData.length, address, port);
                udpSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
