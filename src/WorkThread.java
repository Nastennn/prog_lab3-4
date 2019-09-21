import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import story.Character;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;


public class WorkThread implements Runnable {

    private static final int OFFSET = 69;
    private static final ArrayList<String> commandsNeedData =
            new ArrayList<>(Arrays.asList("add", "remove"));
    private static int counter = 0;

    private ByteBuffer bigBuffer = ByteBuffer.allocate(65536);
    private CollectionManager collectionManager;
    private SocketChannel socketChannel;
    private Function<Character, String> argHandler;
    private int readBytes = 0;
    // количество байт, занятых под метаинформацию
    private int id;
    private String userHash;


    WorkThread(SocketChannel socketChannel, CollectionManager collectionManager) {
        id = ++counter;
        this.socketChannel = socketChannel;
        this.collectionManager = collectionManager;
        System.out.println("Client #" + id + " is connected.");
    }

    @Override
    public void run() {
        while (socketChannel.isConnected()) {
            try {
                byte[] packet = readPackage();

                if (isAuth(packet)) {
                    handleAuthPackage(packet);
                } else if (isCommand(packet)) {
                    if(checkIfUserExistInDB(packet)){
                        this.userHash = getAuthInfo(packet);
                        this.collectionManager.setUserHash(this.userHash);
                        handleCommandPackages(packet);
                    } else{
                        sendMessage("Ошибка авторизации пользователя.");
                    }
                } else {
                    collectDataPackages(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("SocketChannel read failed");
            } catch (ClassNotFoundException e) {
                System.err.println("Невозможно создать объект. Класс не обнаружен.");
            }
        }
    }

    private void handleAuthPackage(byte[] packet) {
        String reply;
        String json = getCommand(packet);
        ArrayList<String> params = new Gson().fromJson(json,
                TypeToken.getParameterized(ArrayList.class, String.class).getType());
        Authorization auth = new Authorization();
        if (params.get(1) != null) {
            reply = auth.loginUser(params.get(0), params.get(1));
        } else {
            reply = auth.registration(params.get(0));
        }
        sendMessage(reply);
    }

    private void handleCommandPackages(byte[] packet) {
        String commandCode = getCommand(packet);
        // если команде не нужны данные
        if (!doesCommandNeedArgument(commandCode)) {
            Function<String, String> simpleHandler = getSimpleHandler(commandCode);
            // выполняем обработчик
            String reply = simpleHandler.apply(commandCode);
            sendMessage(reply);
        } else {
            argHandler = getArgHandler(commandCode);
        }
    }

    private void collectDataPackages(byte[] packet) throws IOException, ClassNotFoundException {
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
            ByteArrayInputStream bais = new ByteArrayInputStream(newData);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Character character = (Character) ois.readObject();
            // собрали данные - выполним обработчик
            String reply = argHandler.apply(character);
            sendMessage(reply);
        }
    }


    /**
     * Читает один пакет из socketChannel
     *
     * @return массив байтов пакета
     * @throws IOException если чтение из socketChannel прошло неудачно
     */
    private byte[] readPackage() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(512);
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
     * Получает информацию о том, авторизационный ли это пакет
     *
     * @param bytes байты пакета
     * @return true, если да; false, если нет
     */
    private boolean isAuth(byte[] bytes) {
        return bytes[4] == 1;
    }

    /**
     * Получает информацию о том, последний ли этот пакет или нет
     *
     * @param bytes байты пакета
     * @return true, если пакет не последний; false, если последний
     */
    private boolean hasMoreFragments(byte[] bytes) {
        return bytes[3] == 1;
    }

    /**
     * Получает количество байт пакета, занятых полезными данными
     *
     * @param bytes байты пакета
     * @return количество байтов
     */
    private int getDataLength(byte[] bytes) {
        int len = bytes[1] * 128 + bytes[2];
        return len;
    }

    String getAuthInfo(byte[] bytes) {
        byte[] authInfo = new byte[64];
        System.arraycopy(bytes, 5, authInfo, 0, 64);
        return new String(authInfo);
    }

    boolean checkIfUserExistInDB(byte[] bytes) {
        DatabaseConnect dbConnect = new DatabaseConnect();
        Reader reader = new Reader(dbConnect.getConnection());

        return reader.checkUser(getAuthInfo(bytes));
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
        String str = new String(cmd);
        return str;
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

    private boolean doesCommandNeedArgument(String code) {
        return commandsNeedData.contains(code);
    }

    /**
     * Возвращает соответсвующий коду команды без аргумента обработчик
     *
     * @param code строка с кодом команды
     * @return функцию-обработчик
     */
    private Function<String, String> getSimpleHandler(String code) {
        switch (code.toLowerCase()) {
            case ("remove_first"):
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
            case ("help"):
                return this::help;
            default:
                return this::commandNotDefined;
        }
    }

    /**
     * Возвращает соответсвующий коду команды с аргументом обработчик
     *
     * @param code строка с кодом команды
     * @return функцию-обработчик
     */
    private Function<Character, String> getArgHandler(String code) {
        switch (code.toLowerCase()) {
            case ("add"):
                return this::add;
            case ("remove"):
                return this::remove;
            default:
                return this::commandNotDefined;
        }
    }

    private String commandNotDefined(Object data) {
        return "Такой команды не существует.";
    }

    private String add(Character character) {
        return collectionManager.add(character);
    }

    private String remove(Character character) {
        return collectionManager.remove(character);
    }

    private String removeFirst(String data) {
        System.out.println("Client #" + id + ": " + data);
        return collectionManager.removeFirst();
    }

    private String info(String data) {
        System.out.println("Client #" + id + ": " + data);
        return collectionManager.info();
    }


    private String show(String data) {
        System.out.println("Client #" + id + ": " + data);
        return collectionManager.show();
    }

    private String save(String data) {
        System.out.println("Client #" + id + ": " + data);
        return collectionManager.save();
    }

    private String reorder(String data) {
        System.out.println("Client #" + id + ": " + data);
        return collectionManager.sort();
    }

    private String load(String data) {
        System.out.println("Client #" + id + ": " + data);
        return collectionManager.load();
    }

    private String help(String data) {
        System.out.println("Client #" + id + ": " + data);
        return collectionManager.getInformation();
    }

    private void sendMessage(String reply) {
        ByteBuffer writeByteBuffer = ByteBuffer.allocate(1000000);
        byte[] b = reply.getBytes();
        writeByteBuffer.put(b);
        writeByteBuffer.flip();
        try {
            socketChannel.write(writeByteBuffer);
        } catch (IOException e) {
            System.out.println("Не удалось отправить сообщение");
        } catch (NullPointerException e) {
            System.out.println("Не обнаружено сообщение для отправки.");
        }
    } // xV8BoyAtSj jared.haygen@sillver.us
}