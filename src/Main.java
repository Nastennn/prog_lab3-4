/**
 * @author Nastennn
 */

public class Main {
    private static volatile boolean keepRunning = true;

    public static void main(String[] args) {
        CollectionManager collectionManager = new CollectionManager();
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                keepRunning = false;
                collectionManager.save();
                mainThread.join();
            } catch (InterruptedException e) {
                System.err.println("Случилась беда :(");
            }
        }));
        CommandExecutor commandExecutor = new CommandExecutor(collectionManager);
        int readStatus;
        do {
            readStatus = commandExecutor.readCommand();

        } while (readStatus != -1 && keepRunning);
        collectionManager.save();
    }
}
