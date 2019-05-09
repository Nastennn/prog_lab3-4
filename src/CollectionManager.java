import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import story.Character;

/**
 * @author Nastennn
 */
class CollectionManager {
    private Vector<Character> characters = new Vector<>();
    private Date initDate;
    private FileManager fileManager;

    CollectionManager() {
        this.fileManager = new FileManager();
        if (fileManager.readFromFile().equals("")) {
            System.out.println("Файл пуст.");
            characters = new Vector<>();
        } else {
            while (!checkSource(fileManager.readFromFile())) {
                System.err.println("Из файла невозможно создать коллекцию. Данные в файле некорректны.");
                System.out.println("Введите путь к другому файлу:");
                Scanner in = new Scanner(System.in);
                this.fileManager.setFile(new File(in.nextLine()));
                characters = this.fileManager.convertFromXML(this.fileManager.readFromFile());
            }
        }
        this.initDate = new Date();
    }

    boolean checkSource(String source) {
        try {
            fileManager.convertFromXML(source);
            return true;
        } catch (com.thoughtworks.xstream.io.StreamException e) {
            System.err.println("Данные в файле некорректны.");
            return false;
        }
    }

    /**
     * Выводит информацию о коллекции
     */
    void info() {
        System.out.println("Коллекция типа: " + characters.getClass());
        System.out.println("Время инициализации коллекции: " + initDate);
        System.out.println("Количество элементов в коллекции: " + characters.size());
    }

    /**
     * Выводит элементы коллекции
     */
    void show() {
        if (characters.isEmpty()) {
            System.out.println("Коллекция пуста.");
        } else {
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(characters));
        }
    }

    /**
     * Добавляет элемент в коллекцию
     *
     * @param jsonString Строка в формате json
     */
    void add(String jsonString) {
        Gson gson = new Gson();
        try {
            Character ch = gson.fromJson(jsonString, Character.class);
            if (characters.contains(ch)) {
                System.out.println("Такой элемент уже существует.");
            } else if (jsonString.equals("")) {
                System.err.println("Необходимо ввести объект.");
            } else {
                characters.add(ch);
                System.out.println("Элемент добавлен в коллекцию.");
            }
            Collections.sort(characters);
        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            System.err.println("Элемент введен неверно.");
        }
    }

    /**
     * Сортирует коллекцию в обратном порядке
     */
    void reorder() {
        if (characters.isEmpty()) {
            System.out.println("Коллекция пуста.");
        } else {
            Collections.reverse(characters);
            System.out.println("Коллекция отсортирована.");
        }
    }

    /**
     * Удаляет первый элемент коллекции
     */
    void removeFirst() {
        if (characters.isEmpty()) {
            System.out.println("Невозможно удалить первый элемент. Коллекция пуста.");
        } else {
            characters.remove(0);
            System.out.println("Первый элемент удален.");
        }
    }

    /**
     * Удаляет указанный элемент коллекции
     *
     * @param string Элемент коллекции, указанный в формате json
     */
    void remove(String string) {
        Gson gson = new Gson();
        try {
            Character ch = gson.fromJson(string, Character.class);
            if (characters.contains(ch)) {
                characters.remove(ch);
            } else {
                System.out.println("В коллекции нет такого элемента.");
            }
            System.out.println("Элемент удален.");
        } catch (IllegalStateException | JsonSyntaxException e) {
            System.out.println("Элемент введен неверно.");
        }
    }

    /**
     * Перезагружает коллекцию из файла
     */
    void load() {
        try {
            characters = this.fileManager.convertFromXML(this.fileManager.readFromFile());
        } catch (com.thoughtworks.xstream.io.StreamException e) {
            System.err.println("Пустой файл.");
            characters = new Vector<>();
        }
    }

    /**
     * Сохарняет коллекцию
     */
    void save() {
        if (characters.size() != 0) {
            try {
                this.fileManager.writeToFile(this.fileManager.convertToXML(characters));
            } catch (IOException e) {
                System.err.println("Не удалось записать в файл.");
            }
        }

    }

}