package utils;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;

public class ShareManager {

    /**
     * Copies the image to the system clipboard and opens the WhatsApp 
     * contact picker so the user can choose who to send it to.
     */
    public void shareToWhatsApp(String path) {
        try {
            // Log the path to ensure the controller passed a valid file location
            System.out.println("DEBUG: Attempting to share file at: " + path);

            // 1. Copy the actual image data to the system clipboard
            copyImageToClipboard(path);

            // 2. Open the WhatsApp Send API
            // This triggers the "Send to..." contact list in the browser
            String message = "Sharing image from PhotoApp";
            String encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            String url = "https://api.whatsapp.com/send?text=" + encodedMsg;

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }

        } catch (Exception e) {
            System.err.println("WhatsApp Share Error: " + e.getMessage());
        }
    }

    /**
     * Reads the file from the path and places the image data on the clipboard.
     */
    public void copyImageToClipboard(String path) throws Exception {
        File imageFile = new File(path);
        
        if (!imageFile.exists()) {
            throw new Exception("File does not exist at: " + path);
        }

        // Read the file as a Java AWT Image
        java.awt.Image image = ImageIO.read(imageFile);
        
        if (image == null) {
            throw new Exception("ImageIO could not decode the file. Check if it's a valid image format.");
        }

        // Wrap the image and set it to the system clipboard
        ImageTransferable transferable = new ImageTransferable(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
    }

    /**
     * Standard Email sharing via the system's default mail client.
     */
 public void shareViaGmail(String subject, String body) {
    try {
        // Construct the Gmail Compose URL
        String url = "https://mail.google.com/mail/?view=cm&fs=1" +
                     "&su=" + URLEncoder.encode(subject, StandardCharsets.UTF_8.toString()) +
                     "&body=" + URLEncoder.encode(body, StandardCharsets.UTF_8.toString());

        if (Desktop.isDesktopSupported()) {
            // This opens Gmail in the user's default web browser (Chrome, Edge, etc.)
            Desktop.getDesktop().browse(new URI(url));
        }
    } catch (Exception e) {
        System.err.println("Gmail Launch Error: " + e.getMessage());
    }
}
}

/**
 * Helper class to translate Java Image objects into a format the Clipboard understands.
 */
class ImageTransferable implements Transferable {
    private java.awt.Image image;

    public ImageTransferable(java.awt.Image image) {
        this.image = image;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(DataFlavor.imageFlavor)) {
            return image;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}