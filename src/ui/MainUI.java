package ui;

//import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
// Ensure you also have this for the conversion logic
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
//import javafx.scene.paint.Color;
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
    private Button themeToggle; // <--- ADD THIS LINE HERE
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
    // 1. Setup the Open Folder Button
    Button openFolderButton = new Button("Open Folder");
    openFolderButton.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 8 15;"
    );
    openFolderButton.setOnAction(e -> openImageFolder());

    // 2. Setup the Title Label
    Label title = new Label("Photo Repository System");
    title.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2c3e50;"
    );

  // REMOVE "Button" from the start. Just use the variable name.
    // Inside createTopBar()
themeToggle = new Button("☀️"); // Use the class variable
themeToggle.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-cursor: hand;");

themeToggle.setOnAction(e -> {
    if (themeToggle.getText().equals("☀️")) {
        applyDarkMode();
        themeToggle.setText("🌙");
        themeToggle.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-text-fill: white;");
    } else {
        applyLightMode();
        themeToggle.setText("☀️");
        themeToggle.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-text-fill: black;");
    }
});

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox topBar = new HBox(15, title, openFolderButton, spacer, themeToggle);

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

        //mosaicBtn.setOnAction(e -> showObjectExtractionPage()); // Using Object Extraction here

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

    HBox mainLayout = new HBox(0);
    mainLayout.setStyle("-fx-background-color: #000000;"); 
    VBox.setVgrow(mainLayout, Priority.ALWAYS);

    // --- LEFT SIDE: PREVIEW AREA ---
    VBox imageSection = new VBox(20);
    imageSection.setPadding(new Insets(20));
    imageSection.setAlignment(Pos.CENTER);
    HBox.setHgrow(imageSection, Priority.ALWAYS);

    Image sourceImg = mainImageView.getImage();
    ImageView preview = new ImageView(sourceImg);
    preview.setPreserveRatio(true);
    preview.setSmooth(true);
    
    double imgW = sourceImg.getWidth();
    double imgH = sourceImg.getHeight();
    double maxViewW = 900;
    double maxViewH = 650;
    double ratio = Math.min(maxViewW / imgW, maxViewH / imgH);
    
    double canvasWidth = imgW * ratio;
    double canvasHeight = imgH * ratio;

    Pane clipWindow = new Pane(preview); 
    clipWindow.setPrefSize(canvasWidth, canvasHeight);
    clipWindow.setMaxSize(canvasWidth, canvasHeight);
    clipWindow.setStyle("-fx-background-color: #111111; -fx-border-color: #2F3336; -fx-border-width: 1;");
    
    Rectangle clipRegion = new Rectangle(canvasWidth, canvasHeight);
    clipWindow.setClip(clipRegion);

    preview.setFitWidth(canvasWidth);
    preview.setFitHeight(canvasHeight);
    preview.setManaged(false); 

    // --- OBJECT EXTRACTION LOGIC (The "Cursor Pick" Part) ---
    Label selectedColorLabel = new Label("Click image to pick color");
    selectedColorLabel.setStyle("-fx-text-fill: #8E8E93; -fx-font-size: 12px;");

    preview.setOnMouseClicked(e -> {
        // Logic from ObjectExtractor.java: Map mouse click to original image pixels
        java.awt.image.BufferedImage bimg = fxToBufferedImage(preview.getImage());
        int x = (int) (e.getX() * bimg.getWidth() / preview.getFitWidth());
        int y = (int) (e.getY() * bimg.getHeight() / preview.getFitHeight());

        if (x >= 0 && y >= 0 && x < bimg.getWidth() && y < bimg.getHeight()) {
            this.pickedColor = new java.awt.Color(bimg.getRGB(x, y), true);
            selectedColorLabel.setText(String.format("Selected: %d, %d, %d", 
                pickedColor.getRed(), pickedColor.getGreen(), pickedColor.getBlue()));
            selectedColorLabel.setStyle("-fx-text-fill: #34C759;");
        }
    });

    imageSection.getChildren().addAll(
        new Label("Transformation Preview") {{ setStyle("-fx-text-fill: white; -fx-opacity: 0.6;"); }},
        clipWindow
    );

    // --- RIGHT SIDE: TOOLS ---
    VBox controlSideBar = new VBox(15);
    controlSideBar.setPadding(new Insets(30, 25, 30, 25));
    controlSideBar.setPrefWidth(350);
    controlSideBar.setStyle("-fx-background-color: #000000; -fx-border-color: #2F3336; -fx-border-width: 0 0 0 1;");

    Label toolsLabel = new Label("Geometric & Selection");
    toolsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 22px; -fx-text-fill: white;");

    // Sliders
    Label scaleLab = new Label("Scale: 100%");
    Slider scaleS = new Slider(10, 300, 100); 
    Label rotateLab = new Label("Rotation: 0°");
    Slider rotateS = new Slider(-180, 180, 0);
    Label transXLab = new Label("Translation X: 0");
    Slider transX = new Slider(-canvasWidth, canvasWidth, 0);
    Label transYLab = new Label("Translation Y: 0");
    Slider transY = new Slider(-canvasHeight, canvasHeight, 0);

    List.of(scaleLab, rotateLab, transXLab, transYLab).forEach(l -> l.setStyle("-fx-text-fill: #EBEBF5; -fx-font-size: 13px; -fx-opacity: 0.85;"));
    List.of(scaleS, rotateS, transX, transY).forEach(this::setupSliderDesign);

    Button saveBtn = new Button("Save Changes");
    saveBtn.setMaxWidth(Double.MAX_VALUE);
    saveBtn.setDisable(true);
    saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;");

    // Transformation Actions
    Runnable applyRealTime = () -> {
        saveBtn.setDisable(false);
        saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 1.0;");
        double s = scaleS.getValue() / 100.0;
        double r = rotateS.getValue();
        double tx = transX.getValue();
        double ty = -transY.getValue();
        scaleLab.setText(String.format("Scale: %.0f%%", scaleS.getValue()));
        rotateLab.setText(String.format("Rotation: %.0f°", r));
        transXLab.setText(String.format("Translation X: %.0f", tx));
        transYLab.setText(String.format("Translation Y: %.0f", -ty));
        preview.setScaleX(s); preview.setScaleY(s); preview.setRotate(r);
        preview.setTranslateX(tx); preview.setTranslateY(ty);
    };

    scaleS.valueProperty().addListener((o, old, v) -> applyRealTime.run());
    rotateS.valueProperty().addListener((o, old, v) -> applyRealTime.run());
    transX.valueProperty().addListener((o, old, v) -> applyRealTime.run());
    transY.valueProperty().addListener((o, old, v) -> applyRealTime.run());

    // --- EXTRACTION SECTION ---
    Label extractLabel = new Label("Object Extraction");
    extractLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");

    Slider toleranceS = new Slider(0, 150, 60);
    this.setupSliderDesign(toleranceS);

    Button extractBtn = new Button("Extract & Save Object");
    extractBtn.setMaxWidth(Double.MAX_VALUE);
    extractBtn.setStyle("-fx-background-color: #34C759; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12;");
    
    extractBtn.setOnAction(e -> {
        if (this.pickedColor == null) {
            showAlert("Click the image to pick a color first!");
            return;
        }
        try {
            java.awt.image.BufferedImage original = fxToBufferedImage(mainImageView.getImage());
            
            // --- CALLING THE TEAMMATE'S CLASS ---
            java.awt.image.BufferedImage extracted = dip_advanced.ObjectExtractor.extractByColor(
                original, this.pickedColor, (int)toleranceS.getValue()
            );
            
            File outputFile = new File("extracted_" + System.currentTimeMillis() + ".png");
            javax.imageio.ImageIO.write(extracted, "png", outputFile);
            showAlert("Saved: " + outputFile.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });

    // Assemble Sidebar
    controlSideBar.getChildren().addAll(
        toolsLabel, new Separator() {{ setStyle("-fx-background-color: #2F3336;"); }},
        scaleLab, scaleS, rotateLab, rotateS, transXLab, transX, transYLab, transY,
        saveBtn,
        new Separator() {{ setStyle("-fx-background-color: #2F3336;"); }},
        extractLabel, selectedColorLabel, new Label("Tolerance") {{ setStyle("-fx-text-fill: white; -fx-font-size: 11px;"); }},
        toleranceS, extractBtn,
        new Region() {{ VBox.setVgrow(this, Priority.ALWAYS); }}, 
        new Button("Reset All") {{ 
            setStyle("-fx-background-color: transparent; -fx-text-fill: #FF453A; -fx-font-weight: bold;");
            setOnAction(ev -> {
                scaleS.setValue(100); rotateS.setValue(0); transX.setValue(0); transY.setValue(0);
                saveBtn.setDisable(true);
            });
        }}
    );

    mainLayout.getChildren().addAll(imageSection, controlSideBar);
    root.setCenter(mainLayout);
}

private java.awt.Color pickedColor = java.awt.Color.WHITE;

  private boolean isGrayscale = false; // Class-level flag


private void showEditingPage() {
    if (currentImage == null) {
        showAlert("Please select an image from the Gallery first!");
        return;
    }
    
    isGrayscale = false; 

    HBox mainLayout = new HBox(0);
    mainLayout.setStyle("-fx-background-color: #000000;"); 
    VBox.setVgrow(mainLayout, Priority.ALWAYS);

    // --- LEFT SIDE: PREVIEW ---
    VBox imageSection = new VBox(20);
    imageSection.setPadding(new Insets(30));
    imageSection.setAlignment(Pos.CENTER);
    HBox.setHgrow(imageSection, Priority.ALWAYS);

    ImageView editPreview = new ImageView(mainImageView.getImage());
    editPreview.setFitHeight(550);
    editPreview.setPreserveRatio(true);

    StackPane displayStack = new StackPane(editPreview);
    displayStack.setStyle("-fx-border-width: 0; -fx-padding: 0;");
    
    StackPane container = new StackPane(displayStack);
    container.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 12; -fx-padding: 20;");
    
    imageSection.getChildren().addAll(new Label("Edit Photo") {{ 
        setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;"); 
    }}, container);

    // --- RIGHT SIDE: TOOLS ---
    VBox controlSideBar = new VBox(20);
    controlSideBar.setPadding(new Insets(30, 20, 30, 20));
    controlSideBar.setPrefWidth(320);
    controlSideBar.setStyle("-fx-background-color: #000000; -fx-border-color: #2F3336; -fx-border-width: 0 0 0 1;");

    Label toolsLabel = new Label("Adjustment Tools");
    toolsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");

    // --- NEW SAVE CHANGES BUTTON (Starts Inactive) ---
    Button saveBtn = new Button("Save Changes");
    saveBtn.setMaxWidth(Double.MAX_VALUE);
    saveBtn.setDisable(true); // Disable interaction
    saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                     "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;"); // Dimmed look

    // Helper function to "Wake Up" the save button
    Runnable activateSave = () -> {
        saveBtn.setDisable(false);
        saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                         "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 1.0;"); // Fully lit
    };

    // --- MONO FILTER ---
    Button grayBtn = new Button("Apply Mono Filter");
    grayBtn.setMaxWidth(Double.MAX_VALUE);
    grayBtn.setStyle("-fx-background-color: #1C1C1E; -fx-text-fill: white; -fx-border-color: #2F3336; -fx-border-radius: 8; -fx-padding: 12;");
    
    // --- SLIDERS SETUP ---
    Label brightTitle = new Label("Brightness: 0");
    brightTitle.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
    Slider brightSlider = new Slider(-100, 100, 0);
    setupSliderDesign(brightSlider); 

    Label contrastTitle = new Label("Contrast: 0");
    contrastTitle.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
    Slider contrastSlider = new Slider(-100, 100, 0);
    setupSliderDesign(contrastSlider);

    // Shared update logic 
    Runnable updateImage = () -> {
        activateSave.run(); // Light up save button on change
        int bVal = (int) brightSlider.getValue();
        int cVal = (int) contrastSlider.getValue();
        brightTitle.setText("Brightness: " + bVal);
        contrastTitle.setText("Contrast: " + cVal);

        java.awt.image.BufferedImage img = fxToBufferedImage(mainImageView.getImage());
        img = dip_basic.BrightnessContrast.adjustBrightness(img, bVal);
        img = dip_basic.BrightnessContrast.adjustContrast(img, cVal);
        
        if (isGrayscale) {
            img = dip_basic.Grayscale.apply(img);
        }
        editPreview.setImage(bufferedToFxImage(img));
    };

    brightSlider.valueProperty().addListener((obs, old, val) -> updateImage.run());
    contrastSlider.valueProperty().addListener((obs, old, val) -> updateImage.run());

    grayBtn.setOnAction(e -> {
        isGrayscale = true;
        updateImage.run(); 
    });

    // --- BORDER SLIDER ---
    Label borderTitle = new Label("Frame Settings (Color Hue)");
    borderTitle.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
    Slider hueSlider = new Slider(0, 360, 0);
    hueSlider.setStyle("-fx-background-color: linear-gradient(to right, red, orange, yellow, green, cyan, blue, violet, red); -fx-background-radius: 5;");
    hueSlider.valueProperty().addListener((obs, old, val) -> {
        activateSave.run(); // Light up save button on change
        javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.hsb(val.doubleValue(), 1.0, 1.0);
        String hex = String.format("#%02X%02X%02X", (int)(fxColor.getRed()*255), (int)(fxColor.getGreen()*255), (int)(fxColor.getBlue()*255));
        displayStack.setStyle("-fx-border-color: " + hex + "; -fx-border-width: 20;");
    });

    // --- RESET / DISCARD ---
    Button resetBtn = new Button("Discard Changes");
    resetBtn.setMaxWidth(Double.MAX_VALUE);
    resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF3B30; -fx-font-weight: bold;");
    resetBtn.setOnAction(e -> {
        isGrayscale = false;
        brightSlider.setValue(0);
        contrastSlider.setValue(0);
        hueSlider.setValue(0);
        displayStack.setStyle("-fx-border-width: 0;");
        editPreview.setImage(mainImageView.getImage());
        
        // Reset save button to inactive state
        saveBtn.setDisable(true);
        saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                         "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;");
    });

    // Assembly
    controlSideBar.getChildren().addAll(
        toolsLabel, grayBtn, new Separator() {{ setStyle("-fx-background-color: #2F3336;"); }},
        brightTitle, brightSlider, 
        contrastTitle, contrastSlider, new Separator() {{ setStyle("-fx-background-color: #2F3336;"); }},
        borderTitle, hueSlider,
        saveBtn, // Added the professional blue button here
        new Region() {{ VBox.setVgrow(this, Priority.ALWAYS); }}, // Spacer
        resetBtn
    );

    mainLayout.getChildren().addAll(imageSection, controlSideBar);
    root.setCenter(mainLayout);
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

    private void setupSliderDesign(Slider slider) {
    slider.setShowTickMarks(true);
    slider.setShowTickLabels(true);
    slider.setMajorTickUnit(25);
    slider.setMinorTickCount(0);
    slider.setSnapToTicks(false);
    slider.setStyle("-fx-control-inner-background: #333333; -fx-cursor: hand;");}

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

    private void applyDarkMode() {
    String blackBg = "-fx-background-color: #000000;";
    String borderStyle = "-fx-border-color: #2F3336; -fx-border-width: 0 1 0 0;";
    
    // 1. Main Background
    root.setStyle(blackBg);
    
    // 2. Top Bar
    HBox topBar = (HBox) root.getTop();
    topBar.setStyle("-fx-background-color: #000000; -fx-border-color: #2F3336; -fx-border-width: 0 0 1 0;");
    topBar.getChildren().forEach(n -> {
        if (n instanceof Label) n.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 22px;");
    });

    // 3. Navigation (Left)
    VBox nav = (VBox) root.getLeft();
    nav.setStyle(blackBg + borderStyle);

    // 4. Center Gallery (Fixes the big white area in image_5c9d98.png)
    if (root.getCenter() instanceof javafx.scene.layout.Region) {
        javafx.scene.layout.Region center = (javafx.scene.layout.Region) root.getCenter();
        center.setStyle(blackBg);
        
        // This targets the internal scroll pane and viewport to kill the white light
        center.lookupAll(".scroll-pane").forEach(node -> 
            node.setStyle("-fx-background: #000000; -fx-background-color: transparent; -fx-border-color: transparent;")
        );
        center.lookupAll(".viewport").forEach(node -> 
            node.setStyle("-fx-background-color: transparent;")
        );
    }

    // 5. Right Side (Annotation)
    VBox right = (VBox) root.getRight();
    right.setStyle("-fx-background-color: #000000; -fx-border-color: #2F3336; -fx-border-width: 0 0 0 1;");
    annotationArea.setStyle("-fx-control-inner-background: #121212; -fx-text-fill: white; -fx-border-color: #2F3336;");
}

private void applyLightMode() {
    // Restore the standard light background
    root.setStyle("-fx-background-color: #fafafa;");
    
    HBox topBar = (HBox) root.getTop();
    topBar.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
    
    // Update the button icon visibility
    if (themeToggle != null) {
        themeToggle.setText("☀️");
        themeToggle.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-text-fill: black;");
    }
}

// --- PUT THIS AT THE BOTTOM OF YOUR CLASS ---
private java.awt.image.BufferedImage extractObjectBySimilarity(java.awt.image.BufferedImage source, javafx.scene.paint.Color targetFxColor) {
    int width = source.getWidth();
    int height = source.getHeight();
    java.awt.image.BufferedImage output = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);

    // Convert JavaFX Color to float HSV for better lighting handling
    float[] targetHSV = new float[3];
    java.awt.Color.RGBtoHSB(
        (int)(targetFxColor.getRed()*255), 
        (int)(targetFxColor.getGreen()*255), 
        (int)(targetFxColor.getBlue()*255), 
        targetHSV
    );

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            java.awt.Color pixelColor = new java.awt.Color(source.getRGB(x, y), true);
            float[] pixelHSV = new float[3];
            java.awt.Color.RGBtoHSB(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), pixelHSV);

            // Calculate Hue Distance (Circular distance handles shadows/highlights better)
            double hueDiff = Math.abs(pixelHSV[0] - targetHSV[0]);
            if (hueDiff > 0.5) hueDiff = 1.0 - hueDiff;

            // Euclidean distance focused on Hue rather than just RGB brightness
            double distance = Math.sqrt(Math.pow(hueDiff * 2.0, 2) + Math.pow(pixelHSV[1] - targetHSV[1], 2));

            // 0.15 is a standard similarity threshold for realistic photos
            if (distance < 0.15) {
                output.setRGB(x, y, source.getRGB(x, y)); 
            } else {
                output.setRGB(x, y, 0x00000000); // Make background transparent
            }
        }
    }
    return output;
}
}

