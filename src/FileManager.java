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
            System.err.println("Такого файла не существует.");
        }
    }

    /**
     * Читает из файла
     *
     * @return result строквое представление файла
     */
    String readFromFile() {
        StringBuilder result = new StringBuilder();
        boolean isSuccess;
        do {
            try {
                isSuccess = true;
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                int c;
                while ((c = bufferedReader.read()) != -1) {
                    result.append((char) c);
                }
            } catch (FileNotFoundException e) {
                System.err.println("Отказано в доступе.");
                System.out.println("Введите путь к другому файлу:");
                Scanner in = new Scanner(System.in);
                file = new File(in.nextLine());
                isSuccess = false;
            } catch (IOException e) {
                isSuccess = false;
            }
        } while (!isSuccess);
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

    public void setFile(File file) {
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

