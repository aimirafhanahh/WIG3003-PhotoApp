package integration;

import repository.ImageModel;
import utils.ShareManager;
import java.io.File;


// public class AppController {
//     private ShareManager shareManager = new ShareManager();

//    public void handleWhatsAppShare(ImageModel currentImage) {
//     if (currentImage != null) {
//         // Get the raw path: e.g., "C:\Users\Documents\photo.jpg"
//         String path = currentImage.getFilePath();
        
//         // Pass ONLY the path to the manager
//         shareManager.shareToWhatsApp(path);
//     } else {
//         System.out.println("No image selected!");
//     }
// }

//     public void handleEmailShare(ImageModel currentImage) {
//         if (currentImage != null) {
//             String subject = "Shared Media from PhotoApp";
//             String body = "You can find the file at: " + currentImage.getFilePath();
//             shareManager.shareViaDefaultEmail(subject, body);
//         }
//     }
// }

public class AppController {
    private ShareManager shareManager = new ShareManager();

    // Now accepts a File directly (the latestSavedFile)
    public void handleWhatsAppShare(File file) {
        if (file != null) {
            // WhatsApp Web only supports pasting images via clipboard
            // We pass the absolute path of the saved file
            shareManager.shareToWhatsApp(file.getAbsolutePath());
        } else {
            System.out.println("No saved file available to share!");
        }
    }

public void handleEmailShare(File file) {
    if (file != null) {
        // 1. If it's an image, copy it to clipboard so user can Ctrl+V
        if (file.getName().toLowerCase().endsWith(".png") || 
            file.getName().toLowerCase().endsWith(".jpg")) {
            try {
                shareManager.copyImageToClipboard(file.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Clipboard error: " + e.getMessage());
            }
        }

        // 2. Prepare the Gmail content
        String subject = "Media Export from PhotoApp";
        String body = "Check out my latest creation! \n\n" + 
                       "File Path: " + file.getAbsolutePath() + 
                       "\n(press Ctrl+V to paste it here!)";
        
        shareManager.shareViaGmail(subject, body);
    }
}
}