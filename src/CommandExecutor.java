import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author Nastennn
 */
//class CommandExecutor {
//    private String[] Commands = {"add", "reorder", "info", "removeFirst", "load", "remove", "show", "help", "save"};
//    private ArrayList<String> jsonCommands = new ArrayList<>(Arrays.asList("add", "remove"));
//    private Scanner in = new Scanner(System.in);
//    private CollectionManager collectionManager;
//
//    CommandExecutor(CollectionManager collectionManager) {
//        this.collectionManager = collectionManager;
//    }

    /**
     * Чтение и парсинг команды
     *
     * @return int executeStatus
     */
//    int readCommand(String message) {
 //       StringBuilder str = new StringBuilder();
//        int executeStatus = 0;
        //System.out.println("Введите команду:");
//        try {
//            String nextLine;
//            nextLine = in.nextLine();
//            str.append(nextLine);
//            if (jsonCommands.contains(cmd.split(" ", 2)[0])){
//                while (in.hasNextLine()){
//                    nextLine = in.nextLine();
//                    if (nextLine.equals("")){
//                        break;
//                    }
//                    str.append(nextLine);
//                }
//            }

           // str = cmd;
//            if (!message.equals("")) {
//                String[] words = message.split(" ", 2);
//                String cmd = words[0];
//                String arg;
//                if (words.length > 1) {
//                    arg = words[1];
//                    arg = arg.trim();
//                    if (!arg.startsWith("[")) {
//                        arg = "[" + arg;
//                    }
//                    if (!arg.endsWith("]")){
//                        arg = arg + "]";
//                    }
//
//                    executeStatus = execute(cmd, arg);
//                } else if (jsonCommands.contains(cmd)){
//                    System.err.println("Этой команде нужно передать аругмент");
//                } else {
//                    executeStatus = execute(cmd);
//                }
//            }
//        } catch (NoSuchElementException e) {
//            System.err.println("EOF");
//            executeStatus = -1;
//        }
//
//        return executeStatus;
//    }
//
//    /**
//     * Выполнение команды
//     *
//     * @param cmd Команда для выполнения
//     * @param arg Передаваемый аргумент
//     * @return int Для дальнейшего выполнения
//     */
//    private int execute(String cmd, String arg) {
//        switch (cmd) {
//            case ("add"): {
//                collectionManager.add(arg);
//                break;
//            }
//            case ("remove"): {
//                collectionManager.remove(arg);
//                break;
//            }
//            default: {
//                System.out.println("Такой команды не существует.");
//                //readCommand();
//            }
//        }
//        return 0;
//    }

//    private int execute(String cmd) {
//        switch (cmd) {
//            case ("reorder"): {
//                collectionManager.reorder();
//                break;
//            }
//            case ("info"): {
//                collectionManager.info();
//                break;
//            }
//            case ("removeFirst"): {
//                collectionManager.removeFirst();
//                break;
//            }
//            case ("load"): {
//                collectionManager.load();
//                break;
//            }
//            case ("show"): {
//                collectionManager.show();
//                break;
//            }
//            case ("help"): {
//                getInformation();
//                break;
//            }
//            case ("save"): {
//                collectionManager.save();
//                break;
//            }
//            case ("exit"): {
//                return -1;
//            }
//            default: {
//                System.out.println("Такой команды не существует 1.");
//                //readCommand();
//            }
//        }
//        return 0;
//    }


//}
