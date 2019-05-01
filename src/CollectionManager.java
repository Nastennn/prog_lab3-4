import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import story.Character;

class CollectionManager {
    private Vector<Character> characters;
    private Date initDate;
    private FileManager fileManager;

    CollectionManager() {
        this.fileManager = new FileManager();
        characters = fileManager.convertFromXML(fileManager.readFromFile());
        this.initDate = new Date();
    }

    void info() {
        System.out.println("Коллекция типа: " + characters.getClass());
        System.out.println("Время инициализации коллекции: " + initDate);
        System.out.println("Количество элементов в коллекции: " + characters.size());
    }

    void show() {
        if (characters.isEmpty()) {
            System.out.println("Коллекция пуста.");
        } else {
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(characters));
        }
    }

    void add(String jsonString) {
        Gson gson = new Gson();
        try {
            Character ch = gson.fromJson(jsonString, Character.class);
            if (characters.contains(ch)) {
                System.out.println("Такой элемент уже существует.");
            } else {
                characters.add(ch);
            }
            Collections.sort(characters);
            save();
        } catch (IllegalStateException | JsonSyntaxException e) {
            System.out.println("Элемент введен неверно.");
        }
    }

    void reorder() {
        if (characters.isEmpty()) {
            System.out.println("Коллекция пуста.");
        } else {
            Collections.reverse(characters);
            save();
        }

    }

    void removeFirst() {
        if (characters.isEmpty()) {
            System.out.println("Невозможно удалить первый элемент. Коллекция пуста.");
        } else {
            characters.remove(0);
            save();
        }
    }

    void remove(String string) {
        Gson gson = new Gson();
        try {
            Character ch = gson.fromJson(string, Character.class);
            if (characters.contains(ch)) {
                characters.remove(ch);
            } else {
                System.out.println("В коллекции нет такого элемента.");
            }
            save();
        } catch (IllegalStateException | JsonSyntaxException e) {
            System.out.println("Элемент введен неверно.");
        }
    }

    void load() {
        FileManager fileManager = new FileManager();
        characters = fileManager.convertFromXML(fileManager.readFromFile());
    }

    void save() {
        try {
            fileManager.writeToFile(fileManager.convertToXML(characters));
            System.out.println("Файл сохранен.");
        } catch (IOException e) {
            System.err.println("Не удалось записать в файл.");
        }
    }

}