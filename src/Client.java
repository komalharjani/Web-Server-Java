import java.io.*;
import java.net.Socket;

public class Client {

    private Socket clientSocket;
    private String host;
    private int port;
    private BufferedReader reader;
    private PrintWriter writer;
    private InputStream in;


    /**
     * Constructor for new client (which takes the host request and port)
     * @param port
     */
    public Client(String host, int port){
        this.host = host;
        this.port = port;
        runClient();
    }

    /**
     * Run the client over a port
     */
    private void runClient(){
        try {
            this.clientSocket = new Socket(host, port);
            System.out.println("Client connected on port: " + port + ".");
            System.out.println("To exit, enter a single line containing: 'exit'");
            reader = new BufferedReader(new InputStreamReader(System.in));
            writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = clientSocket.getInputStream();
            listenForInput();

        } catch (Exception e){ // exit cleanly for any Exception
            System.out.println("Ooops on connection on port: " + port + ". " + e.getMessage());
            cleanup(); // execute cleanup method to close connections cleanly
        }
    }

    /**
     * listen for response from server
     * @throws IOException
     * @throws DisconnectedException
     */
    private void listenForInput() throws IOException, DisconnectedException {
        while(true){
            String line = reader.readLine();
            writer.println(line);
            writer.flush();
            if(line.equals("exit")){
                throw new DisconnectedException(" ... user has entered exit command");
            }
        }
    }

    /**
     * Clean Up client side connection - close all streams
     */
    private void cleanup(){
        System.out.println("Client: ... cleaning up and exiting ... " );
        try {
            if(writer != null) writer.close();
            if(reader != null) reader.close();
            if(clientSocket != null) clientSocket.close();
        } catch (IOException ioe){
            System.out.println("Ooops " + ioe.getMessage());
        }
    }
}
