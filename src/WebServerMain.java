
public class WebServerMain {

        static int port;
        static String directory;
        static WebServer server;

    public static void main(String[] args) {
        if(args.length < 2) {
            //exception if args not excepted
            System.out.print("Usage: java WebServerMain <document_root> <port>");
        } else {
            directory = args[0];
            port = Integer.parseInt(args[1]);
            server = new WebServer(directory, port);
        }

    }
}