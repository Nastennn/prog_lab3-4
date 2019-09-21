import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import story.Character;
import story.CharacterComparator;

/**
 * @author Nastennn
 */
class CollectionManager {
    private Vector<Character> characters;
    private Date initDate;
    private FileManager fileManager;
    private  DatabaseConnect connectionDB;
    private String userHash;
    Writer writer;
    Reader reader;

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
                // characters = this.fileManager.convertFromXML(this.fileManager.readFromFile());
            }
            characters = this.fileManager.convertFromXML(this.fileManager.readFromFile());
        }
        this.initDate = new Date();
        connectionDB = new DatabaseConnect();
        writer = new Writer(connectionDB.getConnection());
        reader = new Reader(connectionDB.getConnection());
    }


    void setUserHash(String userHash) {
        this.userHash = userHash;
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
        if (reader.checkIfTableIsEmpty()) {
            return "Коллекция пуста.";
        } else {
            String result = reader.show();
            return result;
        }
    }


    String add(Character character) {
        try {
            if (reader.checkIfCharacterExists(character)) {
                return "Такой элемент уже существует.";
            } else {
                //characters.add(character);
                //sort();
                return writer.addCharacter(character, userHash);
            }
        } catch (IllegalStateException | NullPointerException e) {
            e.printStackTrace();
            return "Элемент введен неверно.";
        }
    }

    /**
     * Сортирует коллекцию в обратном порядке
     */
//    String reorder() {
//        if (characters.isEmpty()) {
//            return "Коллекция пуста.";
//        } else {
//            Collections.reverse(characters);
//            return "Коллекция отсортирована.";
//        }
//    }

    /**
     * Удаляет первый элемент коллекции
     */
    String removeFirst() {
        if (reader.checkIfTableIsEmpty()) {
            return "Невозможно удалить первый элемент. Коллекция пуста.";
        } else {
            //characters.remove(0);
            return reader.deleteFirst(userHash);
        }
    }


    String remove(Character character) {
        try {
                if (reader.checkIfCharacterExists(character)) {
                    return reader.deleteCharacter(character, userHash);
                } else {
                    return "В коллекции нет такого элемента.";
                }
        } catch (IllegalStateException e) {
            return "Элемент введен неверно.";
        }
    }

    /**
     * Перезагружает коллекцию из файла
     */
    String load() {
        try {
            characters = this.fileManager.convertFromXML(this.fileManager.readFromFile());
            sort();
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

     String sort() {
        // characters = characters.stream().sorted(new CharacterComparator()).collect(Collectors.toCollection(Vector::new));
        reader.sort();
        return "Коллекция отсортирована.";
    }

    /**
     * Получение информации о командах
     */
    String getInformation() {
        return "add - Добавляет элемент в коллекцию." + "\nreorder - Сортирует коллекцию в обратном порядке." + "\ninfo - Выводит информацию о коллекции."
        + "\nremove_first - Удаляет первый элемент коллекции." + "\nload - Перезагружает коллекцию из файла." + "\nremove - Удаляет элемент из коллекции."
        + "\nshow - Выводит содержимое коллекции." + "\nsave - сохраняет коллекцию в файл.";
    }

}