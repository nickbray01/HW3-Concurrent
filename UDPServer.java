import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer implements Runnable {
    int udpPort;
    int len = 1024;
    public UDPServer(int udpPort){
        this.udpPort = udpPort;
    }
    public void run(){
        DatagramPacket datapacket, returnpacket;
        try {
            DatagramSocket datasocket = new DatagramSocket(udpPort);
            byte[] buf = new byte[len];
            while (true) {
                datapacket = new DatagramPacket(buf, buf.length);
                datasocket.receive(datapacket);
                String packetString = new String(datapacket.getData(), 0, datapacket.getLength());
                System.out.println(packetString);
                returnpacket = new DatagramPacket(
                        datapacket.getData(),
                        datapacket.getLength(),
                        datapacket.getAddress(),
                        datapacket.getPort());
                datasocket.send(returnpacket);
            }
        } catch (
                SocketException e) {
            System.err.println(e);
        } catch (
                IOException e) {
            System.err.println(e);
        }
    }
}
