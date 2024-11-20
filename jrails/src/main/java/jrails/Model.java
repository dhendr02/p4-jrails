package jrails;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class Model {

    private Integer id = 0;
    private static Map<Integer, Map<String, Object>> allModels = new LinkedHashMap<>();
    private static final String dbFileName = "Model_DB.csv";

    public int id() {
        return id;
    }

    private void setId(int newId) {
        this.id = newId;
    }

    public void save() {
        // Validate field types and collect model data
        Map<String, Object> modelData = new LinkedHashMap<>();
        modelData.put("Model_Type", this.getClass().getName());

        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Class<?> type = field.getType();

                // Validate field type
                if (!(type == String.class || type == int.class || type == boolean.class)) {
                    throw new IllegalArgumentException(
                            "Unsupported field type: " + type.getName() +
                                    " in field: " + field.getName() +
                                    ". Only String, int, and boolean are supported."
                    );
                }

                // Collect field value
                field.setAccessible(true);
                try {
                    Object value = field.get(this);
                    modelData.put(field.getName(), value == null ? "NULL" : value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error accessing field: " + field.getName(), e);
                }
            }
        }

        // Assign a unique ID if the object is new
        if (this.id() == 0) {
            this.setId(generateUniqueId());
        } else {
            // Validate that the model with the given ID exists before updating
            if (!allModels.containsKey(this.id())) {
                throw new IllegalArgumentException(
                        "Model with ID " + this.id() + " does not exist in the database."
                );
            }
        }

        // Update in-memory data
        allModels.put(this.id(), modelData);

        // Persist changes to the database
        storeAllModelsInDB();
    }


    public static <T> T find(Class<T> c, int id) {
        readAndStoreData(); // Ensure data is loaded into memory
        Map<String, Object> modelData = allModels.get(id);
        if (modelData == null || !modelData.get("Model_Type").equals(c.getName())) {
            return null;
        }

        try {
            T model = c.getDeclaredConstructor().newInstance();
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    field.setAccessible(true);
                    Object value = modelData.get(field.getName());
                    if (value != null && !value.equals("NULL")) {
                        if (field.getType() == int.class) {
                            field.setInt(model, Integer.parseInt(value.toString()));
                        } else if (field.getType() == boolean.class) {
                            field.setBoolean(model, Boolean.parseBoolean(value.toString()));
                        } else {
                            field.set(model, value.toString());
                        }
                    }
                }
            }
            return model;
        } catch (Exception e) {
            throw new RuntimeException("Error instantiating model", e);
        }
    }

    public static <T> List<T> all(Class<T> c) {
        System.out.println("[DEBUG] allModels before iteration: " + allModels);
        readAndStoreData(); // Ensure data is loaded into memory
        System.out.println("[DEBUG] allModels after read: " + allModels);

        List<T> result = new ArrayList<>();
        Map<Integer, Map<String, Object>> snapshot = new LinkedHashMap<>(allModels); // Create a snapshot

        for (Map.Entry<Integer, Map<String, Object>> entry : snapshot.entrySet()) {
            if (entry.getValue().get("Model_Type").equals(c.getName())) {
                T model = find(c, entry.getKey());
                if (model != null) {
                    result.add(model);
                }
            }
        }
        System.out.println("[DEBUG] Resulting models: " + result);
        return result;
    }


    public void destroy() {
        readAndStoreData(); // Ensure data is loaded into memory
        if (!allModels.containsKey(this.id())) {
            throw new IllegalArgumentException("Model with ID " + this.id() + " does not exist.");
        }

        allModels.remove(this.id()); // Directly remove the entry
        storeAllModelsInDB(); // Persist changes
    }


    public static void reset() {
        allModels.clear(); // Clear in-memory data
        try (PrintWriter writer = new PrintWriter(new FileWriter(dbFileName))) {
            writer.print(""); // Clear the file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readAndStoreData() {
        if (allModels.isEmpty()) { // Only read if in-memory data is empty
            try (BufferedReader reader = new BufferedReader(new FileReader(dbFileName))) {
                String headerLine = reader.readLine(); // Read the header
                if (headerLine == null) return; // Empty file

                String line;
                while ((line = reader.readLine()) != null) {
                    List<String> parts = parseCsvLine(line);
                    if (parts.size() < 2) {
                        System.err.println("[ERROR] Malformed line: " + line);
                        continue;
                    }
                    int id = Integer.parseInt(parts.get(0));
                    String modelType = parts.get(1);

                    Map<String, Object> modelData = new LinkedHashMap<>();
                    modelData.put("Model_Type", modelType);

                    for (int i = 2; i < parts.size(); i++) {
                        String[] keyValue = parts.get(i).split("=", 2);
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim();
                            modelData.put(key, value.equals("NULL") ? null : value);
                        } else {
                            System.err.println("[ERROR] Malformed key-value pair: " + parts.get(i));
                        }
                    }
                    allModels.put(id, modelData);
                }
            } catch (IOException e) {
                // File may not exist yet; that's okay
            }
        }
    }



    private static void storeAllModelsInDB() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFileName))) {
            String header = "id,Model_Type";
            if (!allModels.isEmpty()) {
                Map.Entry<Integer, Map<String, Object>> firstEntry = allModels.entrySet().iterator().next();
                String modelType = (String) firstEntry.getValue().get("Model_Type");
                try {
                    Class<?> modelClass = Class.forName(modelType);
                    header = generateHeaderRow(modelClass);
                } catch (ClassNotFoundException e) {
                    System.err.println("[ERROR] Unable to find model class: " + modelType);
                }
            }
            writer.write(header);
            writer.newLine();

            for (Map.Entry<Integer, Map<String, Object>> entry : allModels.entrySet()) {
                int id = entry.getKey();
                Map<String, Object> data = entry.getValue();
                StringBuilder row = new StringBuilder();
                row.append(id).append(",").append(data.get("Model_Type"));
                for (Map.Entry<String, Object> field : data.entrySet()) {
                    if (!field.getKey().equals("Model_Type")) {
                        String value = field.getValue() == null ? "NULL" : escapeCsvValue(field.getValue().toString());
                        row.append(",").append(field.getKey()).append("=").append(value);
                    }
                }
                writer.write(row.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int generateUniqueId() {
        return allModels.isEmpty() ? 1 : Collections.max(allModels.keySet()) + 1;
    }


    private static String generateHeaderRow(Class<?> modelClass) {
        StringBuilder header = new StringBuilder("id,Model_Type");
        for (Field field : modelClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                header.append(",").append(field.getName());
            }
        }
        return header.toString();
    }

    private static String escapeCsvValue(String value) {
        if (value.contains(",") || value.contains("\"")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else if (c == '"') {
                inQuotes = !inQuotes; // Toggle quote state
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim()); // Add the last field
        return result;
    }




}
