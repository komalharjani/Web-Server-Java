public class ClientMain {

    static int port;
    static String directory;

    /**
     * Main method for Client main class - checks arguments and
     * generates new client.
     *
     * @param args
     */
    public static void main(String[] args) {

        directory = args[0];
        port = Integer.parseInt(args[1]);
        if (args.length != 2) {
            //terminate program if args not excepted
            System.out.println("Usage: ClientMain <hostname> <port>");
            System.exit(1);
        }
        //New Client
        Client client = new Client(directory, port);
    }
}


