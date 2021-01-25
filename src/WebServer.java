import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {

    private ServerSocket serverSocket; // listen for client connection requests on this server socket

    /**
     * Constructor for WebServer
     * @param directory - filepath
     * @param port - port which serves files
     */
    public WebServer(String directory, int port){
        //Create new Thread Pool
        ExecutorService threadPool;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started ... listening on port " + port + " ...");
            //new threadpool with maximum of 10 threads
            threadPool = Executors.newFixedThreadPool(10);
            //Infinite Loop to listen for requests on port
            while(true){
                Socket webServer = serverSocket.accept();
                System.out.println("Server got new connection request from " + webServer.getInetAddress());
                Runnable st = new ServerThread(webServer, directory);
                threadPool.execute(st);
            }
        } catch (IOException ioe){
            System.out.println("Ooops " + ioe.getMessage());
        }
    }
}
