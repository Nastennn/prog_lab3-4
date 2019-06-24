import story.Character;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Vector;


public class WorkThread implements Runnable {
    CollectionManager collectionManager = new CollectionManager();
    SocketChannel socketChannel;
    private static int counter = 0;
    int id;
    ByteBuffer buffer = ByteBuffer.allocate(256);
    ObjectOutputStream oos;
    ObjectInputStream ois;
    CommandExecutor commandExecutor = new CommandExecutor(collectionManager);
    ByteArrayInputStream bais;


    WorkThread(SocketChannel socketChannel) {
        this.id = ++counter;
        this.socketChannel = socketChannel;
        System.out.println("Client #" + id + " is connected.");

    }

    @Override
    public void run() {
        while (socketChannel.isConnected()) {
            getMessage();
    }
    }

     String getInfoAboutCommand() {
         try {
             StringBuilder result = new StringBuilder();
             buffer = ByteBuffer.allocate(256);
             String command;
             do {
                 socketChannel.read(buffer);
             } while (buffer.position() < buffer.limit());
             byte[] byteArray;
             byteArray = buffer.array();
             byte[] cmd = new byte[byteArray[1]];
             if (byteArray[0] == 1) {//isCommand
                 for (int i = 0; i < byteArray[1]; i++) {
                     cmd[i] = byteArray[i + 3];
                 }
                 command = new String(cmd);
                 return command;
             } else if (byteArray[0] == 0) {//isArgument
                 getInfoAboutArgs(byteArray);
                 return null;
             }
         } catch (IOException e) {
             System.out.println("Ошибка чтения.");
             return null;
         }
     }

    Character getInfoAboutArgs(byte[] byteArray){
        try {
            ByteBuffer bigBuffer = ByteBuffer.allocate(65536);
            do {
                socketChannel.read(bigBuffer);
            } while (byteArray[1] == 253);
            buffer.flip();
            byte[] arrayForArgs = new byte[65536];
            for (int i = 0; i < 255; i++) {
                arrayForArgs[i] = byteArray[i];
            }
            for (int i = 256; i < buffer.limit(); i++) {
                arrayForArgs[i] = bigBuffer.array()[i];
            }
            bais = new ByteArrayInputStream(arrayForArgs);
            ois = new ObjectInputStream(bais);
            return (Character) ois.readObject();
        }catch(ClassNotFoundException e){
            System.out.println("Невозможно создать объект, класс не найден.");
            return null;
        } catch (IOException e) {
            System.out.println("Не удалось прочитать из сокет канала.");
            return null;
        }
    }


    void executeCommand(){
        if(getInfoAboutCommand()!=null){
            if (getInfoAboutCommand().equals("add") || getInfoAboutCommand().equals("remove")){
                if (getInfoAboutArgs()!=null){
                    commandExecutor.readCommand(getInfoAboutCommand(), getInfoAboutArgs());
                } else {
                    System.out.println("Для этой команды необходимо ввести аргумент.");
                }
            } else {
                commandExecutor.readCommand(getInfoAboutCommand());
            }
        } else if(getInfoAboutCommand() == null){
            System.out.println("Произошла ошибка, пропробуйте ввести команду заново.");
        }
    }

    void sendMessage(String line){
        ByteBuffer writeByteBuffer = ByteBuffer.allocate(2000);
        byte[] b = line.getBytes();
        writeByteBuffer.put(b);
        writeByteBuffer.flip();
        try {
            socketChannel.write(writeByteBuffer);
        } catch (IOException e) {
            System.out.println("Не удалось отправить сообщение");
        }
    }

    void sendCollection(Vector collection){
        try {
            oos.writeObject(collection);
        } catch (IOException e) {
            System.out.println("Не удалось отправить ответ клиенту.");
        }
    }



}
