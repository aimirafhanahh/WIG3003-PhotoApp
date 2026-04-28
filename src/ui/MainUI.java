package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import repository.AnnotationManager;
import repository.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainUI {

    private BorderPane root;
    private FlowPane thumbnailPane;
    private ImageView mainImageView;
    private TextArea annotationArea;
    private Label heartLabel;
    private Label fileNameLabel;

    private Stage stage;
    private AnnotationManager annotationManager;
    private ImageModel currentImage;

    private List<ImageModel> imageList;

    public MainUI(Stage stage) {
        this.stage = stage;
        this.annotationManager = new AnnotationManager();
        this.imageList = new ArrayList<>();

        createUI();
    }

    public Parent getRoot() {
        return root;
    }

    private void createUI() {
        root = new BorderPane();

        root.setTop(createTopBar());
        root.setLeft(createThumbnailSection());
        root.setCenter(createImagePreviewSection());
        root.setRight(createAnnotationSection());
    }

    private HBox createTopBar() {
        Button openFolderButton = new Button("Open Folder");

        openFolderButton.setOnAction(e -> openImageFolder());

        Label title = new Label("Photo Repository Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox topBar = new HBox(15, title, openFolderButton);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #f2f2f2;");

        return topBar;
    }

    private ScrollPane createThumbnailSection() {
        thumbnailPane = new FlowPane();
        thumbnailPane.setPadding(new Insets(10));
        thumbnailPane.setHgap(10);
        thumbnailPane.setVgap(10);
        thumbnailPane.setPrefWrapLength(220);

        ScrollPane scrollPane = new ScrollPane(thumbnailPane);
        scrollPane.setPrefWidth(250);
        scrollPane.setFitToWidth(true);

        return scrollPane;
    }

    private StackPane createImagePreviewSection() {
        mainImageView = new ImageView();
        mainImageView.setPreserveRatio(true);
        mainImageView.setFitWidth(600);
        mainImageView.setFitHeight(500);

        fileNameLabel = new Label("No image selected");
        fileNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        heartLabel = new Label("♥");
        heartLabel.setStyle("-fx-font-size: 42px; -fx-text-fill: red;");
        heartLabel.setVisible(false);

        StackPane imageStack = new StackPane(mainImageView, heartLabel);
        StackPane.setAlignment(heartLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(heartLabel, new Insets(20));

        VBox centerBox = new VBox(15, fileNameLabel, imageStack);
        centerBox.setPadding(new Insets(20));
        centerBox.setAlignment(Pos.TOP_CENTER);

        return new StackPane(centerBox);
    }

    private VBox createAnnotationSection() {
        Label annotationLabel = new Label("Image Annotation");
        annotationLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        annotationArea = new TextArea();
        annotationArea.setPromptText("Write notes about this image...");
        annotationArea.setWrapText(true);
        annotationArea.setPrefHeight(250);

        Button saveButton = new Button("Save Annotation");
        saveButton.setMaxWidth(Double.MAX_VALUE);

        saveButton.setOnAction(e -> saveAnnotation());

        VBox rightBox = new VBox(10, annotationLabel, annotationArea, saveButton);
        rightBox.setPadding(new Insets(15));
        rightBox.setPrefWidth(280);

        return rightBox;
    }

    private void openImageFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Image Folder");

        File folder = chooser.showDialog(stage);

        if (folder == null) {
            return;
        }

        imageList.clear();
        thumbnailPane.getChildren().clear();

        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (isImageFile(file)) {
                ImageModel imageModel = new ImageModel(file.getAbsolutePath());
                imageModel.setAnnotation(annotationManager.getAnnotation(file.getAbsolutePath()));

                imageList.add(imageModel);
                addThumbnail(imageModel);
            }
        }
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();

        return name.endsWith(".jpg")
                || name.endsWith(".jpeg")
                || name.endsWith(".png")
                || name.endsWith(".bmp")
                || name.endsWith(".gif");
    }

    private void addThumbnail(ImageModel imageModel) {
        Image image = new Image(new File(imageModel.getFilePath()).toURI().toString());

        ImageView thumbnail = new ImageView(image);
        thumbnail.setFitWidth(100);
        thumbnail.setFitHeight(80);
        thumbnail.setPreserveRatio(true);

        Label heart = new Label("♥");
        heart.setStyle("-fx-text-fill: red; -fx-font-size: 20px;");
        heart.setVisible(annotationManager.hasAnnotation(imageModel.getFilePath()));

        StackPane thumbnailStack = new StackPane(thumbnail, heart);
        StackPane.setAlignment(heart, Pos.TOP_RIGHT);

        thumbnailStack.setStyle("-fx-border-color: lightgray; -fx-padding: 5;");
        thumbnailStack.setOnMouseClicked(e -> displayImage(imageModel));

        thumbnailPane.getChildren().add(thumbnailStack);
    }

    private void displayImage(ImageModel imageModel) {
        currentImage = imageModel;

        Image image = new Image(new File(imageModel.getFilePath()).toURI().toString());
        mainImageView.setImage(image);

        File file = new File(imageModel.getFilePath());
        fileNameLabel.setText(file.getName());

        String annotation = annotationManager.getAnnotation(imageModel.getFilePath());
        annotationArea.setText(annotation);

        heartLabel.setVisible(annotationManager.hasAnnotation(imageModel.getFilePath()));
    }

    private void saveAnnotation() {
        if (currentImage == null) {
            showAlert("Please select an image first.");
            return;
        }

        String annotation = annotationArea.getText();

        currentImage.setAnnotation(annotation);
        annotationManager.saveAnnotation(currentImage.getFilePath(), annotation);

        heartLabel.setVisible(currentImage.hasAnnotation());

        refreshThumbnails();

        showAlert("Annotation saved successfully.");
    }

    private void refreshThumbnails() {
        thumbnailPane.getChildren().clear();

        for (ImageModel imageModel : imageList) {
            imageModel.setAnnotation(annotationManager.getAnnotation(imageModel.getFilePath()));
            addThumbnail(imageModel);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}