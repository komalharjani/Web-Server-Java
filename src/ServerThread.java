
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

public class ServerThread implements Runnable {

    private static Socket socket; //Initialise new Socket
    private static InputStream in;  // get data from client on this input stream
    private static OutputStream out;  //Send data back to the client on this output stream
    BufferedReader reader;         // use buffered reader to read client data
    DataOutputStream print;         //use data out put stream to print back to client
    String[] request;               //Client's request
    static String directory;        //Directory of Server Files


    /**
     * Constructor for a new thread
     * @param socket
     * @param directory
     */
    ServerThread(Socket socket, String directory) {
        ServerThread.socket = socket;
        ServerThread.directory = directory;
        try {
            in = socket.getInputStream();     // get data from client on this input stream
            out = socket.getOutputStream();  // to send data back to the client on this stream
            reader = new BufferedReader(new InputStreamReader(in)); // use buffered reader to read client data
            print = new DataOutputStream(out); //use output stream to print data
        } catch (IOException ioe) {
            System.out.println("Server thread: " + ioe.getMessage());
        }
    }

    public static OutputStream getOut() {
        return out;
    }

    public static void setOut(OutputStream out) {
        ServerThread.out = out;
    }

    /**
     * This method reads all lines of the file through Scanner object and then dumps the contents of the file into a String
     * @param file
     * @return
     */
    private String readFile(File file) {
        String pageContents = "";
        try (Scanner scanner = new Scanner(file);) {
            while (scanner.hasNextLine()) {
                if (scanner.next().equals('\n')) {
                    pageContents += "\n";
                } else {
                    pageContents += scanner.nextLine() + "\n";
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return pageContents;
    }

    /**
     * This method compiles appropriate headers for responses.
     * @param toBeReturned --> true if File exists
     * @param requestedFile
     * @param length --> needed to construct the header using Length method
     * @return
     * @throws IOException
     */
    static String compileHeader(boolean toBeReturned, File requestedFile, int length) throws IOException {
        if (toBeReturned) {
            String response = "HTTP/1.1 200 OK" + "\n"
                    + "Server: Web Server Main" + "\n"
                    + "Content-Type: " + Files.probeContentType(requestedFile.toPath()) + "\n"
                    + "Content-Length: " + Length(requestedFile) + "\n\r\n";
            System.out.print(response);
            return response;
        } else {
            String response = "HTTP/1.1 404 Not Found" + "\n"
                    + "Server: Web Server Main" + "\n"
                    + "Content-Type: " + Files.probeContentType(requestedFile.toPath()) + "\n"
                    + "Content-Length: " + Length(requestedFile) + "\n\r\n";
            System.out.print(response);
            return response;
        }
    }

    /**
     * Compile Header for not implemented method
     * @param requestedFile
     * @param length
     * @return
     * @throws IOException
     */
    static String compileNoMethod(File requestedFile, int length) throws IOException {
        String response = "HTTP/1.1 501 Not Implemented" + "\n"
                + "Server: Web Server Main" + "\n"
                + "Content-Type: " + Files.probeContentType(requestedFile.toPath()) + "\n"
                + "Content-Length: " + Length(requestedFile) + "\n\r\n";
        System.out.print(response);
        return response;
    }

    /**
     * This method compiles the body of the requested file by reading it's bytes and returning them
     * @param requestedFile
     * @return
     * @throws IOException
     */
    static byte[] compileBody(File requestedFile) throws IOException {
        byte[] bytes = Files.readAllBytes(requestedFile.toPath());
        System.out.print(new String(bytes, StandardCharsets.UTF_8));
        return bytes;
    }

    /**
     * This method calculates the length of the file for the compilation of the header
     * @param requestedFile
     * @return
     * @throws IOException
     */
    static int Length(File requestedFile) throws IOException {
        byte[] content = null;
        content = Files.readAllBytes(requestedFile.toPath());
        return content.length;
    }

    /**
     * This method handles the requests of the client, by splitting the arguments of the request and then sending them
     * through appropriate request methods, such as GET and HEAD.
     * @param clientRequest
     * @throws IOException
     */
    private void handleRequest(String clientRequest) throws IOException {
        request = clientRequest.split(" ");
        String method = request[0];
        String path = directory + request[1];
        File requested = new File(path);
        if (requested.exists()) {
            if (method.equals("GET")) {
                readFile(requested);
                print.write(compileHeader(true, requested, Length(requested)).getBytes());
                print.write((compileBody(requested)));
                print.flush();
            } else if (method.equals("HEAD")) {
                print.write(compileHeader(true, requested, Length(requested)).getBytes());
                print.flush();
            } else {
                File notImplemented = new File(directory + "/501.html");
                print.write(compileNoMethod(notImplemented, Length(notImplemented)).getBytes());
                print.flush();
            }
        } else {
            File notFound = new File(directory + "/404.html");
            print.write(compileHeader(false, notFound, Length(notFound)).getBytes());
            print.flush();
        }
    }

    //Overrides run method in Runnable in order to handle several requests
    @Override
    public void run() {
        System.out.println("New Server Thread has been constructed");
        try {
            handleRequest(reader.readLine());
        } catch (Exception e) {
            File notFound = new File(directory + "/404.html");
            try {
                print.write(compileHeader(false, notFound, Length(notFound)).getBytes());
                print.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                compileBody(notFound);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        cleanup();
    }

    //clean up connections and exit cleanly
    private void cleanup() {
        System.out.println("thread: ... cleaning up and exiting ... ");
        try {
            reader.close();
            in.close();
            socket.close();
        } catch (IOException ioe) {
            System.out.println("thread:cleanup " + ioe.getMessage());
        }
    }
}