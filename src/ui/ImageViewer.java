package ui;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import repository.AnnotationManager;

import java.io.File;

public class ImageViewer {

    public static VBox createViewer(Stage stage) {

        ImageView imageView = new ImageView();
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);

        TextField annotationField = new TextField();
        annotationField.setPromptText("Enter annotation...");

        Label heartLabel = new Label(""); // ❤️ indicator

        Button loadBtn = new Button("Load Image");
        Button saveBtn = new Button("Save Annotation");

        final String[] currentImagePath = {null};

        // Load image
        loadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                currentImagePath[0] = file.getAbsolutePath();
                imageView.setImage(new Image(file.toURI().toString()));

                // Load existing annotation
                String note = AnnotationManager.getAnnotation(currentImagePath[0]);
                annotationField.setText(note);

                // Show heart if exists
                heartLabel.setText(note.isEmpty() ? "" : "❤️");
            }
        });

        // Save annotation
        saveBtn.setOnAction(e -> {
            if (currentImagePath[0] != null) {
                AnnotationManager.saveAnnotation(currentImagePath[0], annotationField.getText());
                heartLabel.setText("❤️");
            }
        });

        return new VBox(10, loadBtn, imageView, annotationField, saveBtn, heartLabel);
    }
}