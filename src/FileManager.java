import java.io.*;
import java.util.Vector;

import com.thoughtworks.xstream.XStream;
import story.Character;

class FileManager {
    private File file;

    FileManager() {
        try {
            file = new File(System.getenv("LAB_5_FILE"));
        } catch (NullPointerException e) {
            System.err.println(e);
        }
    }

    String readFromFile() {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            int c;
            while ((c = bufferedReader.read()) != -1) {
                result.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    void writeToFile(String string) throws java.io.IOException {
        FileWriter writer = new FileWriter(this.file);
        writer.write(string);
        writer.close();
    }

    Vector<Character> convertFromXML(String xml) {
        Class<?>[] classes = new Class[]{Vector.class, Character.class};
        XStream xStream = new XStream();
        xStream.allowTypes(classes);
        xStream.alias("Characters", Vector.class);
        xStream.alias("Character", Character.class);
        return (Vector<Character>) xStream.fromXML(xml);
    }

    String convertToXML(Vector<Character> characters) {
        XStream xStream = new XStream();
        xStream.alias("Characters", Vector.class);
        xStream.alias("Character", Character.class);
        return xStream.toXML(characters);
    }
}

