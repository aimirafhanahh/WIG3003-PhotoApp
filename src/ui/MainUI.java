package ui;

import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
// Ensure you also have this for the conversion logic
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
        root.setLeft(createNavigationPanel());
        root.setCenter(createMainContent());

        root.setStyle("-fx-background-color: #fafafa;");
    }

    private HBox createTopBar() {
        Button openFolderButton = new Button("Open Folder");
        openFolderButton.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 15;"
        );
        openFolderButton.setOnAction(e -> openImageFolder());

        Label title = new Label("Photo Repository System");
        title.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2c3e50;"
        );

        HBox topBar = new HBox(15, title, openFolderButton);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #e0e0e0;" +
                "-fx-border-width: 0 0 1 0;"
        );

        return topBar;
    }

    private VBox createNavigationPanel() {
        Label menuTitle = new Label("MENU");
        menuTitle.setStyle(
                "-fx-text-fill: #bdc3c7;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        Button galleryBtn = createNavButton("📂 Gallery");
        Button editingBtn = createNavButton("🎨 Image Editing");
        Button objectBtn = createNavButton("✂️ Object & Transform");
        Button mosaicBtn = createNavButton("🖼️ Mosaic");
        Button videoBtn = createNavButton("🎬 Video Creator");
        Button shareBtn = createNavButton("📤 Share / Export");

        galleryBtn.setOnAction(e -> root.setCenter(createMainContent()));

        editingBtn.setOnAction(e -> showEditingPage());

        objectBtn.setOnAction(e -> showObjectTransformPage());

        mosaicBtn.setOnAction(e -> showObjectExtractionPage()); // Using Object Extraction here

        videoBtn.setOnAction(e -> showModulePage(
                "🎬 Video Creator",
                "Create video slideshow with text and graphic overlays."
        ));

        shareBtn.setOnAction(e -> showModulePage(
                "📤 Share / Export",
                "Export and share images or videos through Email or WhatsApp."
        ));

        VBox nav = new VBox(12,
                menuTitle,
                galleryBtn,
                editingBtn,
                objectBtn,
                mosaicBtn,
                videoBtn,
                shareBtn
        );

        nav.setPadding(new Insets(20));
        nav.setPrefWidth(230);
        nav.setStyle("-fx-background-color: #2c3e50;");

        return nav;
    }

    private Button createNavButton(String text) {
        Button button = new Button(text);

        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(45);

        String normalStyle =
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-padding: 10;" +
                "-fx-background-radius: 8;";

        String hoverStyle =
                "-fx-background-color: #34495e;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-padding: 10;" +
                "-fx-background-radius: 8;";

        button.setStyle(normalStyle);

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));

        return button;
    }

    private BorderPane createMainContent() {
        BorderPane content = new BorderPane();

        Label galleryTitle = new Label("📂 Image Gallery");
        galleryTitle.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10;" +
                "-fx-text-fill: #2c3e50;"
        );

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
        fileNameLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2c3e50;"
        );

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
        annotationLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2c3e50;"
        );

        annotationArea = new TextArea();
        annotationArea.setPromptText("Write notes about this image...");
        annotationArea.setWrapText(true);
        annotationArea.setPrefHeight(250);

        Button saveButton = new Button("Save Annotation");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setStyle(
                "-fx-background-color: #27ae60;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 15;"
        );

        saveButton.setOnAction(e -> saveAnnotation());

        VBox rightBox = new VBox(10, annotationLabel, annotationArea, saveButton);
        rightBox.setPadding(new Insets(15));
        rightBox.setPrefWidth(280);
        rightBox.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #e0e0e0;" +
                "-fx-border-width: 0 0 0 1;"
        );

        return rightBox;
    }

    private void showModulePage(String titleText, String descriptionText) {
        VBox page = new VBox(20);
        page.setPadding(new Insets(50));
        page.setAlignment(Pos.CENTER);
        page.setStyle("-fx-background-color: #fafafa;");

        Label title = new Label(titleText);
        title.setStyle(
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2c3e50;"
        );

        Label description = new Label(descriptionText);
        description.setWrapText(true);
        description.setMaxWidth(600);
        description.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #555555;"
        );

        Label status = new Label("🚧 Module page ready for integration");
        status.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #7f8c8d;"
        );

        page.getChildren().addAll(title, description, status);

        root.setCenter(page);
    }

    private void showObjectTransformPage() {
    if (currentImage == null) {
        showAlert("Please select an image first!");
        return;
    }

    VBox layout = new VBox(20);
    layout.setPadding(new Insets(30));
    layout.setAlignment(Pos.TOP_CENTER);
    layout.setStyle("-fx-background-color: #F5F5F7;");

    Label title = new Label("Transformations");
    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1D1D1F;");

    ImageView preview = new ImageView(mainImageView.getImage());
    preview.setFitHeight(400);
    preview.setPreserveRatio(true);
    
    StackPane imageFrame = new StackPane(preview);
    imageFrame.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5); -fx-padding: 10;");

    // iOS Style Control Card
    GridPane controls = new GridPane();
    controls.setHgap(20);
    controls.setVgap(15);
    controls.setPadding(new Insets(20));
    controls.setAlignment(Pos.CENTER);
    controls.setStyle("-fx-background-color: white; -fx-background-radius: 15;");

    Slider scaleS = new Slider(50, 200, 100);
    Slider rotateS = new Slider(-180, 180, 0);
    Slider transX = new Slider(-200, 200, 0);
    
    controls.add(new Label("Scale %"), 0, 0);
    controls.add(scaleS, 1, 0);
    controls.add(new Label("Rotate"), 0, 1);
    controls.add(rotateS, 1, 1);
    controls.add(new Label("Move X"), 0, 2);
    controls.add(transX, 1, 2);

    Button applyBtn = new Button("Apply Transforms");
    applyBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 20; -fx-font-weight: bold;");
    
    applyBtn.setOnAction(e -> {
        BufferedImage bimg = fxToBufferedImage(mainImageView.getImage());
        bimg = dip_advanced.Transformations.resize(bimg, scaleS.getValue()/100.0);
        bimg = dip_advanced.Transformations.rotate(bimg, rotateS.getValue());
        bimg = dip_advanced.Transformations.translate(bimg, (int)transX.getValue(), 0);
        preview.setImage(bufferedToFxImage(bimg));
    });

    layout.getChildren().addAll(title, imageFrame, controls, applyBtn);
    root.setCenter(layout);
}

private java.awt.Color pickedColor = java.awt.Color.WHITE;

private void showObjectExtractionPage() {
    if (currentImage == null) { showAlert("Select an image!"); return; }

    VBox layout = new VBox(20);
    layout.setPadding(new Insets(30));
    layout.setAlignment(Pos.TOP_CENTER);
    layout.setStyle("-fx-background-color: #F5F5F7;");

    Label title = new Label("Object Extraction");
    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

    ImageView preview = new ImageView(mainImageView.getImage());
    preview.setFitHeight(400);
    preview.setPreserveRatio(true);
    
    Label statusLabel = new Label("Click image to pick color");
    statusLabel.setStyle("-fx-text-fill: #8E8E93;");

    preview.setOnMouseClicked(e -> {
        BufferedImage bimg = fxToBufferedImage(preview.getImage());
        // Simple ratio calculation to find the pixel
        int x = (int)(e.getX() * bimg.getWidth() / preview.getBoundsInLocal().getWidth());
        int y = (int)(e.getY() * bimg.getHeight() / preview.getBoundsInLocal().getHeight());
        if(x >= 0 && y >= 0 && x < bimg.getWidth() && y < bimg.getHeight()){
            pickedColor = new java.awt.Color(bimg.getRGB(x, y), true);
            statusLabel.setText("Picked: " + pickedColor.getRed() + ", " + pickedColor.getGreen() + ", " + pickedColor.getBlue());
        }
    });

    Slider toleranceS = new Slider(0, 150, 60);
    Button extractBtn = new Button("Extract Picked Color");
    extractBtn.setStyle("-fx-background-color: #34C759; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10 20;");

    extractBtn.setOnAction(e -> {
        java.awt.image.BufferedImage bimg = fxToBufferedImage(mainImageView.getImage());
        java.awt.image.BufferedImage result = dip_advanced.ObjectExtractor.extractByColor(bimg, pickedColor, (int)toleranceS.getValue());
        preview.setImage(bufferedToFxImage(result));
    });

    layout.getChildren().addAll(title, new StackPane(preview), statusLabel, new Label("Tolerance"), toleranceS, extractBtn);
    root.setCenter(layout);
}

  private void showEditingPage() {
    if (currentImage == null) {
        showAlert("Please select an image from the Gallery first!");
        return;
    }

    // Main Container with soft background
    VBox layout = new VBox(25);
    layout.setPadding(new Insets(40));
    layout.setAlignment(Pos.TOP_CENTER);
    layout.setStyle("-fx-background-color: #F5F5F7;"); // iOS light gray background

    // Header Area
    Label title = new Label("Edit Photo");
    title.setStyle("-fx-font-family: 'Segoe UI', system-ui; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1D1D1F;");
    
    // The Image View with rounded corners and shadow
    ImageView editPreview = new ImageView(mainImageView.getImage());
    editPreview.setFitHeight(420);
    editPreview.setPreserveRatio(true);
    
    StackPane imageFrame = new StackPane(editPreview);
    imageFrame.setStyle("-fx-background-color: white; -fx-background-radius: 18; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 20, 0, 0, 10); " +
                        "-fx-padding: 10;");
    imageFrame.setMaxWidth(Region.USE_PREF_SIZE);

    // Control Panel (Glassmorphism effect)
    HBox controlPanel = new HBox(30);
    controlPanel.setAlignment(Pos.CENTER);
    controlPanel.setPadding(new Insets(25));
    controlPanel.setMaxWidth(800);
    controlPanel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 20; " +
                          "-fx-border-color: rgba(255, 255, 255, 0.3); -fx-border-width: 1;");

    // Reusable Button Styler
    String btnStyle = "-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                      "-fx-background-radius: 12; -fx-padding: 10 20; -fx-cursor: hand;";
    
    // Grayscale Action
    Button grayBtn = new Button("Mono");
    grayBtn.setStyle(btnStyle);
    grayBtn.setOnAction(e -> {
        java.awt.image.BufferedImage bimg = fxToBufferedImage(editPreview.getImage());
        editPreview.setImage(bufferedToFxImage(dip_basic.Grayscale.apply(bimg)));
    });

    // Border Action
    Button borderBtn = new Button("Frame");
    borderBtn.setStyle(btnStyle.replace("#007AFF", "#34C759")); // iOS Green
    borderBtn.setOnAction(e -> {
        java.awt.image.BufferedImage bimg = fxToBufferedImage(editPreview.getImage());
        editPreview.setImage(bufferedToFxImage(dip_basic.Border.addBorder(bimg, 25)));
    });

    // Brightness Slider Area
    VBox sliderBox = new VBox(8);
    sliderBox.setAlignment(Pos.CENTER);
    Label sliderLabel = new Label("Brightness");
    sliderLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #8E8E93;");
    
    Slider brightSlider = new Slider(-100, 100, 0);
    brightSlider.setPrefWidth(180);
    // Real-time update (Optional: change to setOnMouseReleased for performance)
    brightSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
        java.awt.image.BufferedImage bimg = fxToBufferedImage(mainImageView.getImage()); // Always process from original
        editPreview.setImage(bufferedToFxImage(dip_basic.BrightnessContrast.adjustBrightness(bimg, newVal.intValue())));
    });

    sliderBox.getChildren().addAll(sliderLabel, brightSlider);
    controlPanel.getChildren().addAll(grayBtn, borderBtn, sliderBox);

    // Bottom Navigation
    Button resetBtn = new Button("Reset");
    resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF3B30; -fx-font-weight: bold;");
    resetBtn.setOnAction(e -> editPreview.setImage(mainImageView.getImage()));

    layout.getChildren().addAll(title, imageFrame, controlPanel, resetBtn);
    root.setCenter(layout);
}

    private void openImageFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Image Folder");

        File folder = chooser.showDialog(stage);

        if (folder == null) {
            return;
        }

        imageList.clear();

        if (thumbnailPane != null) {
            thumbnailPane.getChildren().clear();
        }

        File[] files = folder.listFiles();

        if (files == null) {
            showAlert("No files found in this folder.");
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

        if (imageList.isEmpty()) {
            showAlert("No image files found. Please choose a folder with JPG or PNG images.");
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

        thumbnailStack.setStyle(
                "-fx-border-color: #dcdcdc;" +
                "-fx-border-radius: 6;" +
                "-fx-padding: 5;" +
                "-fx-background-color: white;"
        );

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
        if (thumbnailPane == null) {
            return;
        }

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

    private java.awt.image.BufferedImage fxToBufferedImage(Image img) {
    return javafx.embed.swing.SwingFXUtils.fromFXImage(img, null);    }

    private Image bufferedToFxImage(java.awt.image.BufferedImage bimg) {
    return javafx.embed.swing.SwingFXUtils.toFXImage(bimg, null);}
}

