import java.io.IOException;
import java.net.*;
import java.util.concurrent.locks.ReentrantLock;

public class TCPServer implements Runnable{
    int tcpPort;
    Library library;
    ReentrantLock fileLock;

    public TCPServer(int port, Library lib, ReentrantLock lock){
        this.tcpPort = port;
        this.library = lib;
        this.fileLock = lock;
    }

    public void run(){
        try {
            ServerSocket listener = new ServerSocket(tcpPort);
            Socket s;
            while((s = listener.accept()) != null){
                Thread t = new tcpThread(s, library, fileLock);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
