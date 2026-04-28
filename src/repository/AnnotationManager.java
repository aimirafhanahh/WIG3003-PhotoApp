package repository;

import java.io.*;
import java.util.Properties;

public class AnnotationManager {

    private static final String FILE_NAME = "annotations.properties";
    private Properties annotations;

    public AnnotationManager() {
        annotations = new Properties();
        loadAnnotations();
    }

    private void loadAnnotations() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            annotations.load(fis);
        } catch (IOException e) {
            System.out.println("Failed to load annotations: " + e.getMessage());
        }
    }

    public void saveAnnotation(String imagePath, String annotation) {
        annotations.setProperty(imagePath, annotation);

        try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {
            annotations.store(fos, "Image Annotations");
        } catch (IOException e) {
            System.out.println("Failed to save annotation: " + e.getMessage());
        }
    }

    public String getAnnotation(String imagePath) {
        return annotations.getProperty(imagePath, "");
    }

    public boolean hasAnnotation(String imagePath) {
        String note = getAnnotation(imagePath);
        return note != null && !note.trim().isEmpty();
    }
}