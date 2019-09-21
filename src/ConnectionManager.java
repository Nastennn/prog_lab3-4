import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Scanner;

class ConnectionManager {
    // private static final int PORT = 9876;
    private static final String HOST = "localhost";
    private static int count = 0;
    private CollectionManager collectionManager = new CollectionManager();



    void startServer() throws IOException {
        System.out.println("Сервер запущен.");
        int port;
        System.out.println("Введите номер порта:");
        Scanner in = new Scanner(System.in);
        port = in.nextInt();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress localhost = new InetSocketAddress(HOST, port);
        serverSocketChannel.bind(localhost);
        System.out.println("Ждем подключения...");
        DatabaseConnect dbConnect = new DatabaseConnect();
        dbConnect.connect();
        try {
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                System.out.println("Connection Accepted: " + socketChannel.getLocalAddress() + "\n");
                Thread thread = new Thread(new WorkThread(socketChannel, collectionManager));
                thread.start();
                int id = ++count;
                System.out.println("Thread #" + id + " started.");
            }
        } catch (IOException e) {
            System.out.println("Press F");
        }
    }
}
