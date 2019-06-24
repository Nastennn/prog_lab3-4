import story.Character;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.function.Function;


public class WorkThread implements Runnable {
    private CollectionManager collectionManager = new CollectionManager();
    private SocketChannel socketChannel;
    private static int counter = 0;
    private ByteBuffer buffer = ByteBuffer.allocate(128);
    private ObjectOutputStream oos;
    private ByteBuffer bigBuffer = ByteBuffer.allocate(65536);
    private Function<String, String> handler;
    private ArrayList<String> commandsNeedData = new ArrayList<>(Arrays.asList("add", "remove"));
    private int readBytes = 0;
    // количество байт, занятых под метаинформацию
    private static final int OFFSET = 3;

    WorkThread(SocketChannel socketChannel) {
        int id = ++counter;
        this.socketChannel = socketChannel;
        System.out.println("Client #" + id + " is connected.");
    }

    @Override
    public void run() {
        String reply;
        while (socketChannel.isConnected()) {
            try {
                byte[] packet = readPackage();

                if (isCommand(packet)) {
                    String commandCode = getCommand(packet);
                    handler = getHandler(commandCode);
                    // если команде не нужны данные
                    if (!commandsNeedData.contains(commandCode)) {
                        if (handler != null) {
                            // выполняем обработчик
                            reply = handler.apply("");
                            sendMessage(reply);
                        }
                    }
                } else {
                    // собираем данные
                    byte[] bytes = getPayload(packet);
                    readBytes += bytes.length;
                    bigBuffer.put(getPayload(packet));
                    // если пришел последний фрагмент
                    if (!hasMoreFragments(packet)) {
                        // переложить из bigBuffer в byte[] data
                        byte[] data = bigBuffer.array();
                        byte[] newData = new byte[readBytes];
                        System.arraycopy(data, 0, newData, 0, readBytes);
                        bigBuffer = ByteBuffer.allocate(65536);
                        readBytes = 0;
                        // собрали данные - выполним обработчик
                        reply = handler.apply(new String(newData, StandardCharsets.UTF_8));
                        sendMessage(reply);
                    }
                }
            } catch (IOException e) {
                System.err.println("SocketChannel read failed");
            }
        }
    }

    /**
     * Читает один пакет из socketChannel
     *
     * @return массив байтов пакета
     * @throws IOException если чтение из socketChannel прошло неудачно
     */
    private byte[] readPackage() throws IOException {
        buffer = ByteBuffer.allocate(128);
        do {
            socketChannel.read(buffer);
        } while (buffer.position() < buffer.limit());
        return buffer.array();
    }

    /**
     * Получает информацию о том, находится в пакете команда или данные
     *
     * @param bytes байты пакета
     * @return true, если команда; false, если данные
     */
    private boolean isCommand(byte[] bytes) {
        return bytes[0] == 1;
    }

    /**
     * Получает информацию о том, последний ли этот пакет или нет
     *
     * @param bytes байты пакета
     * @return true, если пакет не последний; false, если последний
     */
    private boolean hasMoreFragments(byte[] bytes) {
        return bytes[2] == 1;
    }

    /**
     * Получает количество байт пакета, занятых полезными данными
     *
     * @param bytes байты пакета
     * @return количество байтов
     */
    private int getDataLength(byte[] bytes) {
        return bytes[1];
    }

    /**
     * Получает строку с кодом команды из пакета
     *
     * @param bytes байты пакета
     * @return код команды
     */
    private String getCommand(byte[] bytes) {
        int payloadLength = getDataLength(bytes);
        byte[] cmd = new byte[payloadLength];
        System.arraycopy(bytes, OFFSET, cmd, 0, payloadLength);
        return new String(cmd);
    }

    /**
     * Получает данные из пакета, отсекая метаинформацию
     *
     * @param bytes байты пакета
     * @return newBytes данные пакета
     */
    private byte[] getPayload(byte[] bytes) {
        int payloadLength = getDataLength(bytes);
        byte[] newBytes = new byte[payloadLength];
        System.arraycopy(bytes, OFFSET, newBytes, 0, payloadLength);
        return newBytes;
    }


    /**
     * Возвращает соответсвующий коду команды обработчик
     *
     * @param code строка с кодом команды
     * @return функцию-обработчик
     */
    private Function<String, String> getHandler(String code) {
        switch (code.toLowerCase()) {
            case ("add"):
                return this::add;
            case ("remove"):
                return this::remove;
            case ("removeFirst"):
                return this::removeFirst;
            case ("info"):
                return this::info;
            case ("show"):
                return this::show;
            case ("save"):
                return this::save;
            case ("reorder"):
                return this::reorder;
            case ("load"):
                return this::load;
            default:
                return null;
        }
    }

    private String add(String data) {
        return collectionManager.add(data);
    }

    private String remove(String data) {
        collectionManager.remove(data);
        return "success";
    }

    private String removeFirst(String data) {
        collectionManager.removeFirst();
        return "success";
    }

    private String info(String data) {
        System.out.println(collectionManager.info());
        return collectionManager.info();
    }

    private String show(String data) {
        System.out.println(collectionManager.show());
        return collectionManager.show();
    }

    private String save(String data) {
        collectionManager.save();
        return "success";
    }

    private String reorder(String data) {
        collectionManager.reorder();
        return "success";
    }

    private String load(String data) {
        collectionManager.load();
        return "success";
    }

    private void sendMessage(String reply) {
        ByteBuffer writeByteBuffer = ByteBuffer.allocate(2048);
        byte[] b = reply.getBytes();
        writeByteBuffer.put(b);
        writeByteBuffer.flip();
        try {
            socketChannel.write(writeByteBuffer);
        } catch (IOException e) {
            System.out.println("Не удалось отправить сообщение");
        }
    }

    void sendCollection(Vector collection) {
        try {
            oos.writeObject(collection);
        } catch (IOException e) {
            System.out.println("Не удалось отправить ответ клиенту.");
        }
    }


}
