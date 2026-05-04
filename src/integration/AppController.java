package integration;

import repository.ImageModel;
import utils.ShareManager;

public class AppController {
    private ShareManager shareManager = new ShareManager();

   public void handleWhatsAppShare(ImageModel currentImage) {
    if (currentImage != null) {
        // Get the raw path: e.g., "C:\Users\Documents\photo.jpg"
        String path = currentImage.getFilePath();
        
        // Pass ONLY the path to the manager
        shareManager.shareToWhatsApp(path);
    } else {
        System.out.println("No image selected!");
    }
}

    public void handleEmailShare(ImageModel currentImage) {
        if (currentImage != null) {
            String subject = "Shared Media from PhotoApp";
            String body = "You can find the file at: " + currentImage.getFilePath();
            shareManager.shareViaDefaultEmail(subject, body);
        }
    }
}