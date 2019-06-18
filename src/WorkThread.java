import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;


public class WorkThread implements Runnable {
    CollectionManager collectionManager = new CollectionManager();
    SocketChannel socketChannel;
    private SelectionKey key;
    Selector selector;
    private static int counter = 0;
    int id;
    ByteBuffer buffer = ByteBuffer.allocate(4096);


    WorkThread(SocketChannel socketChannel) {
        this.id = ++counter;
        this.socketChannel = socketChannel;
        System.out.println("Hello there. It's client #" + id);

    }

    @Override
    public void run() {
        StringBuilder result = new StringBuilder();
        while (true) {
            try {
                buffer = ByteBuffer.allocate(4096);
                int bytesRead = socketChannel.read(buffer);

                if (bytesRead < 1 && result.length() != 0) {
                    System.out.println("Client #" + id + ": " + result);
                    String message = "Сервер получил ваше сообщение \n";
                    ByteBuffer writeByteBuffer = ByteBuffer.allocate(2000);
                    byte[] b = message.getBytes();
                    writeByteBuffer.put(b);
                    writeByteBuffer.flip();
                    socketChannel.write(writeByteBuffer);
                    result = new StringBuilder();
                } else if (bytesRead > 0) {
                    buffer.flip();
                    byte[] b = new byte[buffer.remaining()];
                    buffer.get(b);
                    result.append(new String(b, StandardCharsets.UTF_8).trim());
                }

            } catch (IOException e) {
                System.out.println("Не удалось получить сообщение.");
            }
        }
    }

    Byte sendMessage(String cmd){
        switch(cmd){
            case("add"):{

            }
        }
    }


}
