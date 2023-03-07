import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.locks.ReentrantLock;

public class UDPServer implements Runnable {
    int udpPort;
    int len = 1024;
    private DatagramPacket packet;
    private DatagramSocket socket;
    private Library library;
    private ReentrantLock fileLock;
    
    public UDPServer(DatagramPacket datapacket, DatagramSocket datasocket, Library lib, ReentrantLock lock){
        packet = datapacket;
        socket = datasocket;
        library = lib;
        fileLock = lock;
    }
    
    public void run(){
        DatagramPacket returnpacket;
        try {
        	String packetString = new String(packet.getData(), 0, packet.getLength());
            String[] tokens = packetString.split(" ");
            String retString = "";
            
            
        	
        	if (tokens[0].equals("set-mode")) {
                if(tokens[1].equals("t")){
                    retString = "The communication mode is set to TCP";
                }
                else{
                    retString = "The communication mode is set to UDP";
                }
            } else if (tokens[0].equals("begin-loan")) {
            	String bookname = tokens[2];
            	for(int i = 3; i<tokens.length; i++) {
            		bookname += " " + tokens[i];
            	}
                retString = library.checkoutBook(bookname, tokens[1]);
            } else if (tokens[0].equals("end-loan")) {
                retString = library.endLoan(Integer.valueOf(tokens[1]));
            } else if (tokens[0].equals("get-loans")) {
                retString = library.getLoans(tokens[1]);
            } else if (tokens[0].equals("get-inventory")) {
            	retString = library.getInventory();
            } else if (tokens[0].equals("exit")) {
            	fileLock.lock();
                File output = new File("inventory.txt");
                output.delete();
            	output.createNewFile();
            	FileWriter myWriter = new FileWriter("inventory.txt");
                myWriter.write(library.getInventory());
                myWriter.close();
                fileLock.unlock();
            } else {
                System.out.println("ERROR: No such command");
            }
        	System.out.println(packetString);
        	System.out.println(retString);
        	
        	byte[] buffer = retString.getBytes();
            returnpacket = new DatagramPacket(buffer,
                    buffer.length,
                    packet.getAddress(),
                    packet.getPort());
            socket.send(returnpacket);
        } catch (
                SocketException e) {
            System.err.println(e);
        } catch (
                IOException e) {
            System.err.println(e);
        }
    }
}
