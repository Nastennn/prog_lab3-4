import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author Nastennn
 */
public class CommandExecutor {
    public String[] Commands = {"add", "reorder", "info", "removeFirst", "load", "remove", "show", "help"};
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
            do {
                str.append(in.nextLine());
            } while ((str.length() - str.toString().replaceAll("\\{", "").length()) >
                    (str.length() - str.toString().replaceAll("}", "").length()));
            str = new StringBuilder(str.toString().trim());
            if (!str.toString().equals("")) {
                String[] words = str.toString().split(" ", 2);
                String cmd = words[0];
                String arg = null;
                if (words.length > 1) {
                    arg = words[1];
                    String[] args = arg.split("}");
                    for (int i = 0; i < args.length; i++) {
                        args[i] += "}";

                    }

                    if (args.length > 1) {
                        for (String s : args) {
                            arg = s.trim();
                            executeStatus = execute(cmd, arg);
                        }
                    } else {
                        executeStatus = execute(cmd, arg);
                    }
                } else {
                    executeStatus = execute(cmd, null);
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
            case ("remove"): {
                collectionManager.remove(arg);
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
            }

        }


    }
}
