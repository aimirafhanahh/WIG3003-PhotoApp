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
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import multimedia.MosaicGenerator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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

        editingBtn.setOnAction(e -> showModulePage(
                "🎨 Image Editing",
                "Brightness, contrast, grayscale, and border tools will be connected here."
        ));

        objectBtn.setOnAction(e -> showModulePage(
                "✂️ Object & Transform",
                "Resize, rotate, translate, and object extraction tools will be connected here."
        ));

        mosaicBtn.setOnAction(e -> showMosaicPage());

        videoBtn.setOnAction(e -> showVideoPage());

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

private void showMosaicPage() {
    VBox layout = new VBox(15);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);

    Button generateBtn = new Button("Generate Mosaic");

    ImageView mosaicView = new ImageView();
    mosaicView.setFitWidth(600);
    mosaicView.setPreserveRatio(true);

    generateBtn.setOnAction(e -> {
        try {
            List<BufferedImage> bufferedImages = new ArrayList<>();

            for (ImageModel img : imageList) {
                BufferedImage bi = javax.imageio.ImageIO.read(new File(img.getFilePath()));
                bufferedImages.add(bi);
            }

            BufferedImage mosaic = MosaicGenerator.createMosaic(bufferedImages, 4, 100);

            Image fxImage = javafx.embed.swing.SwingFXUtils.toFXImage(mosaic, null);
            mosaicView.setImage(fxImage);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });

    layout.getChildren().addAll(generateBtn, mosaicView);
    root.setCenter(layout);
}

private void showVideoPage() {
    VBox layout = new VBox(15);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);
    layout.setStyle("-fx-background-color: #fafafa;");

    Label title = new Label("🎬 Video Slideshow Creator");
    title.setStyle(
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2c3e50;"
    );

    ImageView slideshowView = new ImageView();
    slideshowView.setFitWidth(650);
    slideshowView.setFitHeight(430);
    slideshowView.setPreserveRatio(true);

    Label overlayText = new Label("My Photo Story");
    overlayText.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-background-color: rgba(0,0,0,0.5);" +
            "-fx-padding: 10;" +
            "-fx-background-radius: 8;"
    );

    StackPane videoFrame = new StackPane(slideshowView, overlayText);
    StackPane.setAlignment(overlayText, Pos.BOTTOM_CENTER);
    StackPane.setMargin(overlayText, new Insets(20));
    videoFrame.setPrefSize(700, 460);
    videoFrame.setStyle(
            "-fx-background-color: #222222;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 15;"
    );

    TextField overlayInput = new TextField("My Photo Story");
    overlayInput.setPromptText("Enter text overlay...");
    overlayInput.setMaxWidth(400);

    overlayInput.textProperty().addListener((obs, oldValue, newValue) -> {
        overlayText.setText(newValue);
    });

    final int[] currentIndex = {0};
    final Timeline[] timeline = new Timeline[1];

    Button playBtn = new Button("▶ Play");
    Button pauseBtn = new Button("⏸ Pause");
    Button restartBtn = new Button("🔁 Restart");
    Button prevBtn = new Button("⬅ Previous");
    Button nextBtn = new Button("Next ➡");

    playBtn.setOnAction(e -> {
        if (imageList.isEmpty()) {
            showAlert("Please open an image folder first.");
            return;
        }

        if (timeline[0] == null) {
            timeline[0] = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                Image img = new Image(new File(imageList.get(currentIndex[0]).getFilePath()).toURI().toString());
                slideshowView.setImage(img);

                currentIndex[0]++;

                if (currentIndex[0] >= imageList.size()) {
                    currentIndex[0] = 0;
                }
            }));

            timeline[0].setCycleCount(Timeline.INDEFINITE);
        }

        timeline[0].play();
    });

    pauseBtn.setOnAction(e -> {
        if (timeline[0] != null) {
            timeline[0].pause();
        }
    });

    restartBtn.setOnAction(e -> {
        if (imageList.isEmpty()) {
            showAlert("Please open an image folder first.");
            return;
        }

        currentIndex[0] = 0;
        Image img = new Image(new File(imageList.get(currentIndex[0]).getFilePath()).toURI().toString());
        slideshowView.setImage(img);

        if (timeline[0] != null) {
            timeline[0].playFromStart();
        }
    });

    prevBtn.setOnAction(e -> {
        if (imageList.isEmpty()) {
            showAlert("Please open an image folder first.");
            return;
        }

        currentIndex[0]--;

        if (currentIndex[0] < 0) {
            currentIndex[0] = imageList.size() - 1;
        }

        Image img = new Image(new File(imageList.get(currentIndex[0]).getFilePath()).toURI().toString());
        slideshowView.setImage(img);
    });

    nextBtn.setOnAction(e -> {
        if (imageList.isEmpty()) {
            showAlert("Please open an image folder first.");
            return;
        }

        currentIndex[0]++;

        if (currentIndex[0] >= imageList.size()) {
            currentIndex[0] = 0;
        }

        Image img = new Image(new File(imageList.get(currentIndex[0]).getFilePath()).toURI().toString());
        slideshowView.setImage(img);
    });

    HBox controls = new HBox(10, prevBtn, playBtn, pauseBtn, restartBtn, nextBtn);
    controls.setAlignment(Pos.CENTER);

    VBox overlayBox = new VBox(8, new Label("Text Overlay:"), overlayInput);
    overlayBox.setAlignment(Pos.CENTER);

    layout.getChildren().addAll(title, videoFrame, overlayBox, controls);

    root.setCenter(layout);
}
}
