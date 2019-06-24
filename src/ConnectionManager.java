import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;

class ConnectionManager {
    private static final int PORT = 9876;
    private static final String HOST = "localhost";
    static int count = 0;
    int id;

    ConnectionManager(){
        this.id=++count;
    }

    void startServer() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress localhost = new InetSocketAddress(HOST, PORT);
        serverSocketChannel.bind(localhost);
        try {
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                System.out.println("Connection Accepted: " + socketChannel.getLocalAddress() + "\n");
                Thread thread = new Thread(new WorkThread(socketChannel));
                thread.start();
                System.out.println("Thread #" + id + " started.");

            }
        } catch (IOException e) {
            System.out.println("Press F");
        }
    }
}
