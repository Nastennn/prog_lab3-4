import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author Nastennn
 */
class CommandExecutor {
    private String[] Commands = {"add", "reorder", "info", "removeFirst", "load", "remove", "show", "help", "save"};
    private ArrayList<String> jsonCommands = new ArrayList<>(Arrays.asList("add", "remove"));
    private Scanner in = new Scanner(System.in);
    private CollectionManager collectionManager;

    CommandExecutor(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Чтение и парсинг команды
     *
     * @return int executeStatus
     */
    int readCommand() {
        StringBuilder str = new StringBuilder();
        int executeStatus = 0;
        System.out.println("Введите команду:");
        try {
            String nextLine;
            nextLine = in.nextLine();
            str.append(nextLine);
            if (jsonCommands.contains(nextLine.split(" ", 2)[0])){
                while (in.hasNextLine()){
                    nextLine = in.nextLine();
                    if (nextLine.equals("")){
                        break;
                    }
                    str.append(nextLine);
                }
            }

            str = new StringBuilder(str.toString());
            if (!str.toString().equals("")) {
                String[] words = str.toString().split(" ", 2);
                String cmd = words[0];
                String arg;
                if (words.length > 1) {
                    arg = words[1];
                    arg = arg.trim();
                    if (!arg.startsWith("[")) {
                        arg = "[" + arg;
                    }
                    if (!arg.endsWith("]")){
                        arg = arg + "]";
                    }

                    executeStatus = execute(cmd, arg);
                } else if (jsonCommands.contains(cmd)){
                    System.err.println("Этой команде нужно передать аругмент");
                } else {
                    executeStatus = execute(cmd);
                }
            }
        } catch (NoSuchElementException e) {
            System.err.println("EOF");
            executeStatus = -1;
        }

        return executeStatus;
    }

    /**
     * Выполнение команды
     *
     * @param cmd Команда для выполнения
     * @param arg Передаваемый аргумент
     * @return int Для дальнейшего выполнения
     */
    private int execute(String cmd, String arg) {
        switch (cmd) {
            case ("add"): {
                collectionManager.add(arg);
                break;
            }
            case ("remove"): {
                collectionManager.remove(arg);
                break;
            }
            default: {
                System.out.println("Такой команды не существует.");
                readCommand();
            }
        }
        return 0;
    }

    private int execute(String cmd) {
        switch (cmd) {
            case ("reorder"): {
                collectionManager.reorder();
                break;
            }
            case ("info"): {
                collectionManager.info();
                break;
            }
            case ("removeFirst"): {
                collectionManager.removeFirst();
                break;
            }
            case ("load"): {
                collectionManager.load();
                break;
            }
            case ("show"): {
                collectionManager.show();
                break;
            }
            case ("help"): {
                getInformation();
                break;
            }
            case ("save"): {
                collectionManager.save();
                break;
            }
            case ("exit"): {
                return -1;
            }
            default: {
                System.out.println("Такой команды не существует.");
                readCommand();
            }
        }
        return 0;
    }

    /**
     * Получение информации о командах
     */
    private void getInformation() {
        for (String command : Commands) {
            switch (command) {
                case ("add"): {
                    System.out.println("add - Добавляет элемент в коллекцию.");
                    break;
                }
                case ("reorder"): {
                    System.out.println("reorder - Сортирует коллекцию в обратном порядке.");
                    break;
                }
                case ("info"): {
                    System.out.println("info - Выводит информацию о коллекции.");
                    break;
                }
                case ("removeFirst"): {
                    System.out.println("removeFirst - Удаляет первый элемент коллекции.");
                    break;
                }
                case ("load"): {
                    System.out.println("load - Перезагружает коллекцию из файла.");
                    break;
                }
                case ("remove"): {
                    System.out.println("remove - Удаляет элемент из коллекции.");
                    break;
                }
                case ("show"): {
                    System.out.println("show - Выводит содержимое коллекции.");
                    break;
                }
                case ("save"): {
                    System.out.println("save - сохраняет коллекцию в файл.");
                    break;
                }
            }

        }


    }
}
