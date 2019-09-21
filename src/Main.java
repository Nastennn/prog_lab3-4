import java.io.IOException;

/**
 * @author Nastennn
 */

public class Main {
    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Программа завершила работу.")));
        ConnectionManager connectionManager = new ConnectionManager();
        connectionManager.startServer();
    }
}
