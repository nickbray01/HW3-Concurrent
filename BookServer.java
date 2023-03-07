import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class BookServer {
    public static void main(String[] args) {
        int tcpPort;
        int udpPort;
        if (args.length != 1) {
            System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
            System.exit(-1);
        }
        String fileName = args[0];
        tcpPort = 7000;
        udpPort = 8000;

        // parse the inventory file
        HashMap<String, Integer> inventory = new HashMap<String, Integer>();
        ArrayList<String> books = new ArrayList<String>();
        
        String invFile = args[0];
        Scanner sc;
		try {
			sc = new Scanner(new FileReader(invFile));
			while (sc.hasNextLine()) {
	        	String row = sc.nextLine();
	        	int endIndex = row.substring(1).indexOf("\"") + 2;
	        	String book = row.substring(0,endIndex);
	        	String num = row.substring(endIndex+1);
	            inventory.put(book, Integer.valueOf(num));
	            books.add(book);
	        }
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
        
        Library library = new Library(inventory, books);
        
        int len = 1024;
        ReentrantLock fileLock = new ReentrantLock();
        Thread tcpServer = new Thread(new TCPServer(tcpPort, library, fileLock));
        tcpServer.start();
        DatagramPacket datapacket, returnpacket;
        try {
            DatagramSocket datasocket = new DatagramSocket(udpPort);
            byte[] buf = new byte[len];
            while (true) {
                datapacket = new DatagramPacket(buf, buf.length);
                datasocket.receive(datapacket);
                
                Thread udpThread = new Thread(new UDPServer(datapacket, datasocket, library, fileLock));
                udpThread.start();
                /*
                returnpacket = new DatagramPacket(
                        datapacket.getData(),
                        datapacket.getLength(),
                        datapacket.getAddress(),
                        datapacket.getPort());
                datasocket.send(returnpacket);
                */
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
