import java.io.*;
import java.util.Scanner;
import java.util.Vector;

import com.thoughtworks.xstream.XStream;
import story.Character;

/**
 * @author Nastennn
 */
class FileManager {
    private File file;

    FileManager() {
        try {
            file = new File(System.getenv("LAB_5_FILE"));
        } catch (NullPointerException e) {
            System.err.println("Такого файла не существует, либо не введна переменная окружения.");
            file = getFileFromUser();
        }
    }

    private File getFileFromUser() {
        while(true) {
            System.out.println("Введите путь к файлу:");
            Scanner in = new Scanner(System.in);
            file = new File(in.nextLine());
            if (checkFile(file)) {
                return file;
            }
        }
    }

    private boolean checkFile(File file) {
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                System.err.println("Не получилось создать новый файл по переданному пути.");
                return false;
            }
        }
        if (!file.isFile()) {
            System.err.println("Это не файл.");
            return false;
        }
        if (!file.canRead()) {
            System.err.println("Не удается прочитать файл.");
            return false;
        }
        if (!file.canWrite()) {
            System.err.println("Невозможно записать в файл.");
            return false;
        }
        return true;
    }

    /**
     * Читает из файла
     *
     * @return result строквое представление файла
     */
    String readFromFile() {
        StringBuilder result = new StringBuilder();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                int c;
                while ((c = bufferedReader.read()) != -1) {
                    result.append((char) c);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении из файла");
            }
        return result.toString();
    }

    /**
     * Пишет в файл
     *
     * @param string Строка для записи в файл
     * @throws java.io.IOException
     */
    void writeToFile(String string) throws java.io.IOException {
        FileWriter writer = new FileWriter(this.file);
        writer.write(string);
        writer.close();
        System.out.println("Файл сохранен.");
    }

    void setFile(File file) {
        this.file = file;
    }

    /**
     * Преобразует данные файла из формата xml
     *
     * @param xml Строковое представление файла
     * @return Vector
     * @throws com.thoughtworks.xstream.io.StreamException
     */
    Vector<Character> convertFromXML(String xml) throws com.thoughtworks.xstream.io.StreamException {
        Class<?>[] classes = new Class[]{Vector.class, Character.class};
        XStream xStream = new XStream();
        xStream.allowTypes(classes);
        xStream.alias("Characters", Vector.class);
        xStream.alias("Character", Character.class);
        return (Vector<Character>) xStream.fromXML(xml);
    }

    /**
     * Преобразует коллекцию в xml
     *
     * @param characters Коллекция
     * @return XStream
     */
    String convertToXML(Vector<Character> characters) {
        XStream xStream = new XStream();
        xStream.alias("Characters", Vector.class);
        xStream.alias("Character", Character.class);
        return xStream.toXML(characters);
    }
}

