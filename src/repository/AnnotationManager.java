package repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AnnotationManager {

    private static final String FILE = "annotations.json";
    private static Map<String, String> annotations = new HashMap<>();
    private static Gson gson = new Gson();

    static {
        loadFromFile();
    }

    public static void saveAnnotation(String path, String text) {
        annotations.put(path, text);
        saveToFile();
    }

    public static String getAnnotation(String path) {
        return annotations.getOrDefault(path, "");
    }

    private static void saveToFile() {
        try (Writer writer = new FileWriter(FILE)) {
            gson.toJson(annotations, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadFromFile() {
        try (Reader reader = new FileReader(FILE)) {
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            annotations = gson.fromJson(reader, type);
            if (annotations == null) annotations = new HashMap<>();
        } catch (IOException e) {
            annotations = new HashMap<>();
        }
    }
}