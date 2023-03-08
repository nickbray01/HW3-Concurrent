import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class tcpThread extends Thread{
    Socket s;
    Library library;
    ReentrantLock fileLock;
    boolean ended = false;

    public tcpThread(Socket s, Library lib, ReentrantLock lock){
        this.s = s;
        this.library = lib;
        this.fileLock = lock;
    }
        public void start(){
            try {
                while(!ended){
                    Scanner input = new Scanner(s.getInputStream());
                    PrintStream output = new PrintStream(s.getOutputStream());

                    String cmd = input.nextLine();
                    String[] tokens = cmd.split(" ");
                    String retString = "";
                    String oneLineEncodeString = "";


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
                        oneLineEncodeString = "";
                        String[] retArr = retString.split("\n");
                        for (String retToken : retArr){
                            oneLineEncodeString += retToken;
                            oneLineEncodeString += "TCPIS";
                        }
                    } else if (tokens[0].equals("exit")) {
                        fileLock.lock();
                        File outFile = new File("inventory.txt");
                        outFile.delete();
                        outFile.createNewFile();
                        FileWriter myWriter = new FileWriter("inventory.txt");
                        myWriter.write(library.getInventory());
                        myWriter.close();
                        fileLock.unlock();
                        ended = true;
                    }
                    else if (tokens[0].equals("end-from-udp")){
                        retString = "end tcp connection";
                        ended = true;
                    } else {
                        System.out.println("ERROR: No such command");
                    }
                    System.out.println(cmd);
                    System.out.println(retString);

                    if(tokens[0].equals("get-inventory")){
                        output.println(oneLineEncodeString);
                    }
                    else if(tokens[0].equals("end-from-udp")){

                    }
                    else{
                        output.println(retString);
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
