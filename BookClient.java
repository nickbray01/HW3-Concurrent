import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.io.*;
import java.util.*;

public class BookClient {
    public static void main(String[] args) {
        String hostAddress;
        int tcpPort;
        int udpPort;
        int clientId;

        if (args.length != 2) {
            System.out.println("ERROR: Provide 2 arguments: command-file, clientId");
            System.out.println("\t(1) command-file: file with commands to the server");
            System.out.println("\t(2) clientId: an integer between 1..9");
            System.exit(-1);
        }

        String commandFile = args[0];
        clientId = Integer.parseInt(args[1]);
        hostAddress = "localhost";
        tcpPort = 7000;// hardcoded -- must match the server's tcp port
        udpPort = 8000;// hardcoded -- must match the server's udp port

        try {
            Scanner sc = new Scanner(new FileReader(commandFile));

            DatagramSocket datasocket = new DatagramSocket();
            DatagramPacket sPacket, rPacket;

            while (sc.hasNextLine()) {
                String cmd = sc.nextLine();
                String[] tokens = cmd.split(" ");

                byte[] buffer = cmd.getBytes();
                byte[] rbuffer = new byte[cmd.length()];
                sPacket = new DatagramPacket(buffer,
                        buffer.length,
                        InetAddress.getByName(hostAddress),
                        udpPort);
                datasocket.send(sPacket);
                rPacket = new DatagramPacket(rbuffer, rbuffer.length);
                datasocket.receive(rPacket);
                String retstring = new String(rPacket.getData(), 0,
                        rPacket.getLength());
                System.out.println("Received from Server:" + retstring);

                if (tokens[0].equals("set-mode")) {
                    // TODO: set the mode of communication for sending commands to the server
                } else if (tokens[0].equals("begin-loan")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                } else if (tokens[0].equals("end-loan")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                } else if (tokens[0].equals("get-loans")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                } else if (tokens[0].equals("get-inventory")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                } else if (tokens[0].equals("exit")) {
                    // TODO: send appropriate command to the server
                } else {
                    System.out.println("ERROR: No such command");
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
