package utils;

import java.io.File;
import java.nio.file.Files;

public class FileUtils {

    /**
     * Checks if the file at the given path actually exists and is a file.
     * Use this before trying to share or open an image.
     */
    public static boolean exists(String filePath) {
        if (filePath == null || filePath.isEmpty()) return false;
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * Extracts only the file name from a full path.
     * Useful for setting Email subjects or UI labels.
     */
    public static String getFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) return "Unknown File";
        File file = new File(filePath);
        return file.getName();
    }

    /**
     * Checks the file size in Megabytes (MB).
     * Useful because some email providers limit attachments to 25MB.
     */
    public static double getFileSizeInMB(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return 0;
        return (double) file.length() / (1024 * 1024);
    }
}