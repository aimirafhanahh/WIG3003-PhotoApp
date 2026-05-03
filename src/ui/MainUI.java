package ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import multimedia.MediaPlayerUI;
import multimedia.MosaicGenerator;
import multimedia.VideoCreator;
import repository.AnnotationManager;
import repository.ImageModel;

import java.awt.image.BufferedImage;
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
        root.setLeft(createNavigationPanel());
        root.setCenter(createMainContent());
        root.setStyle("-fx-background-color: #fafafa;");
    }

    private HBox createTopBar() {
        Button openFolderButton = new Button("Open Folder");
        openFolderButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15;");
        openFolderButton.setOnAction(e -> openImageFolder());

        Label title = new Label("Photo Repository System");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox topBar = new HBox(15, title, openFolderButton);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        return topBar;
    }

    private VBox createNavigationPanel() {
        Label menuTitle = new Label("MENU");
        menuTitle.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 12px; -fx-font-weight: bold;");

        Button galleryBtn = createNavButton("📂 Gallery");
        Button editingBtn = createNavButton("🎨 Image Editing");
        Button objectBtn = createNavButton("✂️ Object & Transform");
        Button mosaicBtn = createNavButton("🖼️ Mosaic");
        Button videoBtn = createNavButton("🎬 Video Creator");
        Button shareBtn = createNavButton("📤 Share / Export");

        // Module Navigation Actions
        galleryBtn.setOnAction(e -> root.setCenter(createMainContent()));

        editingBtn.setOnAction(e -> showModulePlaceholder("🎨 Image Editing", "Brightness, contrast, grayscale, and border tools."));
        objectBtn.setOnAction(e -> showModulePlaceholder("✂️ Object & Transform", "Resize, rotate, translate, and object extraction tools."));

        // Connect Multimedia Module Buttons
        mosaicBtn.setOnAction(e -> handleMosaicGeneration());
        videoBtn.setOnAction(e -> handleVideoGeneration());

        shareBtn.setOnAction(e -> showModulePlaceholder("📤 Share / Export", "Export and share via Email or WhatsApp."));

        VBox nav = new VBox(12, menuTitle, galleryBtn, editingBtn, objectBtn, mosaicBtn, videoBtn, shareBtn);
        nav.setPadding(new Insets(20));
        nav.setPrefWidth(230);
        nav.setStyle("-fx-background-color: #2c3e50;");

        return nav;
    }

    // --- Core Multimedia Integration Methods ---

    /**
     * Handles Mosaic Generation logic and updates the main preview.
     */
    private void handleMosaicGeneration() {
        if (imageList.isEmpty()) {
            showAlert("Please load images first.");
            return;
        }

        MosaicGenerator generator = new MosaicGenerator();
        // Generate a grid with 3 columns using the current loaded images
        BufferedImage mosaicBI = generator.createGrid(imageList, 3);

        if (mosaicBI != null) {
            // Convert BufferedImage to JavaFX Image for display
            Image fxImage = SwingFXUtils.toFXImage(mosaicBI, null);
            mainImageView.setImage(fxImage);
            fileNameLabel.setText("System-Generated Mosaic Preview");
            root.setCenter(createMainContent()); // Ensure we are in gallery view to see results
        }
    }

    /**
     * Handles Video synthesis and switches view to the Media Player.
     */
    private void handleVideoGeneration() {
        if (imageList.isEmpty()) {
            showAlert("Please load images first.");
            return;
        }

        // 1. Synthesize Video
        VideoCreator creator = new VideoCreator();
        String outputVideo = "output_slideshow.mp4";
        creator.createSlideshow(imageList, outputVideo);

        // 2. Load into Media Player UI
        MediaPlayerUI playerUI = new MediaPlayerUI();
        playerUI.loadVideo(outputVideo);

        // 3. Create a layout for the video player with a back button
        Button backBtn = new Button("Back to Gallery");
        backBtn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white;");
        backBtn.setOnAction(e -> root.setCenter(createMainContent()));

        VBox playerLayout = new VBox(20, playerUI, backBtn);
        playerLayout.setAlignment(Pos.CENTER);
        playerLayout.setPadding(new Insets(20));

        // Switch main view to the player
        root.setCenter(playerLayout);
    }

    // --- Existing UI Logic ---

    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(45);
        String normalStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT; -fx-padding: 10; -fx-background-radius: 8;";
        String hoverStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT; -fx-padding: 10; -fx-background-radius: 8;";
        button.setStyle(normalStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
        return button;
    }

    private BorderPane createMainContent() {
        BorderPane content = new BorderPane();
        Label galleryTitle = new Label("📂 Image Gallery");
        galleryTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10; -fx-text-fill: #2c3e50;");
        content.setTop(galleryTitle);
        content.setLeft(createThumbnailSection());
        content.setCenter(createImagePreviewSection());
        content.setRight(createAnnotationSection());
        return content;
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
        scrollPane.setStyle("-fx-background-color: white;");
        return scrollPane;
    }

    private StackPane createImagePreviewSection() {
        mainImageView = new ImageView();
        mainImageView.setPreserveRatio(true);
        mainImageView.setFitWidth(600);
        mainImageView.setFitHeight(500);
        fileNameLabel = new Label("📷 No image selected");
        fileNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        heartLabel = new Label("♥");
        heartLabel.setStyle("-fx-font-size: 42px; -fx-text-fill: red;");
        heartLabel.setVisible(false);
        StackPane imageStack = new StackPane(mainImageView, heartLabel);
        StackPane.setAlignment(heartLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(heartLabel, new Insets(20));
        VBox centerBox = new VBox(15, fileNameLabel, imageStack);
        centerBox.setPadding(new Insets(20));
        centerBox.setAlignment(Pos.TOP_CENTER);
        StackPane previewPane = new StackPane(centerBox);
        previewPane.setStyle("-fx-background-color: #fafafa;");
        return previewPane;
    }

    private VBox createAnnotationSection() {
        Label annotationLabel = new Label("Image Annotation");
        annotationLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        annotationArea = new TextArea();
        annotationArea.setPromptText("Write notes about this image...");
        annotationArea.setWrapText(true);
        annotationArea.setPrefHeight(250);
        Button saveButton = new Button("Save Annotation");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15;");
        saveButton.setOnAction(e -> saveAnnotation());
        VBox rightBox = new VBox(10, annotationLabel, annotationArea, saveButton);
        rightBox.setPadding(new Insets(15));
        rightBox.setPrefWidth(280);
        rightBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 0 1;");
        return rightBox;
    }

    private void showModulePlaceholder(String titleText, String descriptionText) {
        VBox page = new VBox(20);
        page.setPadding(new Insets(50));
        page.setAlignment(Pos.CENTER);
        page.setStyle("-fx-background-color: #fafafa;");
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label description = new Label(descriptionText);
        description.setStyle("-fx-font-size: 16px; -fx-text-fill: #555555;");
        page.getChildren().addAll(title, description);
        root.setCenter(page);
    }

    private void openImageFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Image Folder");
        File folder = chooser.showDialog(stage);
        if (folder != null) {
            imageList.clear();
            thumbnailPane.getChildren().clear();
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (isImageFile(file)) {
                        ImageModel model = new ImageModel(file.getAbsolutePath());
                        imageList.add(model);
                        addThumbnail(model);
                    }
                }
            }
        }
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }

    private void addThumbnail(ImageModel imageModel) {
        Image image = new Image(new File(imageModel.getFilePath()).toURI().toString());
        ImageView thumbnail = new ImageView(image);
        thumbnail.setFitWidth(100);
        thumbnail.setFitHeight(80);
        thumbnail.setPreserveRatio(true);
        StackPane thumbnailStack = new StackPane(thumbnail);
        thumbnailStack.setStyle("-fx-border-color: #dcdcdc; -fx-padding: 5; -fx-background-color: white;");
        thumbnailStack.setOnMouseClicked(e -> displayImage(imageModel));
        thumbnailPane.getChildren().add(thumbnailStack);
    }

    private void displayImage(ImageModel imageModel) {
        currentImage = imageModel;
        Image image = new Image(new File(imageModel.getFilePath()).toURI().toString());
        mainImageView.setImage(image);
        fileNameLabel.setText(new File(imageModel.getFilePath()).getName());
    }

    private void saveAnnotation() {
        if (currentImage != null) {
            currentImage.setAnnotation(annotationArea.getText());
            annotationManager.saveAnnotation(currentImage.getFilePath(), annotationArea.getText());
            showAlert("Annotation saved successfully.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}