package jrails;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Model {

    public void save() {
        // When calling save, we need to see which files we already have. If we already have a
        // file for the Model subclass, then we open it. If we don't have one, then we create a
        // new one.

        Integer id = 1;
        String filename = this.getClass().getSimpleName() + ".csv";
        File database = new File(filename);

        if (!database.exists()) {
            try {
                database.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<Integer, String> loaded_database = loadDataBase(database);

    }

    public int id() {
        throw new UnsupportedOperationException();
    }

    public static <T> T find(Class<T> c, int id) {
        throw new UnsupportedOperationException();
    }

    public static <T> List<T> all(Class<T> c) {
        // Returns a List<element type>
        throw new UnsupportedOperationException();
    }

    public void destroy() {
        throw new UnsupportedOperationException();
    }

    public static void reset() {
        throw new UnsupportedOperationException();
    }

    public Map<Integer, String> loadDataBase(File database) {
        Map<Integer, String> loadedData = new HashMap<Integer, String>();
        int lineCount = 1;
        BufferedReader reader;

        // If the file is empty, return an empty map
        if (database.length() == 0) {
            return loadedData;
        }

        // If the file is not empty, store each line in the map with the line number as the key
        // and the contents of the line as the value
        try {
            reader = new BufferedReader(new FileReader(database));
            String line = reader.readLine();

            while (line != null) {
                loadedData.put(lineCount, line);
                lineCount++;
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loadedData;
    }
}
