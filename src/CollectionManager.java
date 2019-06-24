import java.io.File;
import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import story.Character;

/**
 * @author Nastennn
 */
class CollectionManager {
    private Vector<Character> characters;
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
        // characters = this.fileManager.convertFromXML(this.fileManager.readFromFile());
        this.initDate = new Date();
    }

    private boolean checkSource(String source) {
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
    String info() {
        return "Коллекция типа: " + characters.getClass() + "\nВремя инициализации коллекции: " + initDate + "\nКоличество элементов в коллекции: " + characters.size();
    }

    /**
     * Выводит элементы коллекции
     */
    String show() {
        if (characters.isEmpty()) {
            return "Коллекция пуста.";
        } else {
            return new GsonBuilder().setPrettyPrinting().create().toJson(characters);
        }
    }

    /**
     * Добавляет элемент в коллекцию
     *
     * @param jsonString Строка в формате json
     */
    String add(String jsonString) {
        Gson gson = new Gson();
        try {
            Vector<Character> characterVector = gson.fromJson(jsonString, TypeToken.getParameterized(Vector.class, Character.class).getType());
            for (Character ch : characterVector) {
                if (characters.contains(ch)) {
                    return "Такой элемент уже существует.";
                } else {
                    characters.add(ch);
                    return "Элемент добавлен в коллекцию.";
                }
            }
            Collections.sort(characters);
        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            return "Элемент введен неверно.";
        }
        return "И как же это вообще вылезло?";
    }

    String add(Character character) {
        try {
            for (Character ch : characters) {
                if (characters.contains(ch)) {
                    return "Такой элемент уже существует.";
                } else {
                    characters.add(ch);
                    return "Элемент добавлен в коллекцию.";
                }
            }
            Collections.sort(characters);
        } catch (IllegalStateException | NullPointerException e) {
            return "Элемент введен неверно.";
        }
        return "И как же это вообще вылезло?";
    }

    /**
     * Сортирует коллекцию в обратном порядке
     */
    String reorder() {
        if (characters.isEmpty()) {
            return "Коллекция пуста.";
        } else {
            Collections.reverse(characters);
            return "Коллекция отсортирована.";
        }
    }

    /**
     * Удаляет первый элемент коллекции
     */
    String removeFirst() {
        if (characters.isEmpty()) {
            return "Невозможно удалить первый элемент. Коллекция пуста.";
        } else {
            characters.remove(0);
            return "Первый элемент удален.";
        }
    }

    /**
     * Удаляет указанный элемент коллекции
     *
     * @param jsonString Элемент коллекции, указанный в формате json
     */
    String remove(String jsonString) {
        Gson gson = new Gson();
        try {
            Vector<Character> characterVector = gson.fromJson(jsonString, TypeToken.getParameterized(Vector.class, Character.class).getType());
            for (Character ch : characterVector) {
                if (characters.contains(ch)) {
                    characters.remove(ch);
                    return "Элемент удален.";
                } else {
                    return "В коллекции нет такого элемента.";
                }
            }
        } catch (IllegalStateException | JsonSyntaxException e) {
            return "Элемент введен неверно.";
        }
        return "";
    }

    String remove(Character character) {
        try {
            for (Character ch : characters) {
                if (characters.contains(ch)) {
                    characters.remove(ch);
                    return "Элемент удален.";
                } else {
                    return "В коллекции нет такого элемента.";
                }
            }
        } catch (IllegalStateException e) {
            return "Элемент введен неверно.";
        }
        return "";
    }

    /**
     * Перезагружает коллекцию из файла
     */
    String load() {
        try {
            characters = this.fileManager.convertFromXML(this.fileManager.readFromFile());
            return "Файл перезагружен";
        } catch (com.thoughtworks.xstream.io.StreamException e) {
            characters = new Vector<>();
            return "Пустой файл.";
        }
    }

    /**
     * Сохарняет коллекцию
     */
    String save() {
        if (characters.size() != 0) {
            try {
                this.fileManager.writeToFile(this.fileManager.convertToXML(characters));
                return "Коллекция сохранена.";
            } catch (IOException e) {
                return "Не удалось записать в файл.";
            }
        }
        return "";
    }

}