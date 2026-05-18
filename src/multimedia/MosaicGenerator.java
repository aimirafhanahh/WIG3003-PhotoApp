package multimedia;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class MosaicGenerator {

    // Helper class to store a tile image alongside its pre-calculated average color
    public static class AnalyzedTile {
        public BufferedImage image;
        public Color avgColor;

        public AnalyzedTile(BufferedImage image, Color avgColor) {
            this.image = image;
            this.avgColor = avgColor;
        }
    }

    /**
     * Cache optimization: Analyzes the source files ONCE, turns them into tiny 
     * thumbnails, and immediately releases the heavy raw parent images from RAM.
     */
    public static List<AnalyzedTile> preAnalyzeTiles(List<String> tilePaths, int tileSize) {
        List<AnalyzedTile> analyzedTiles = new ArrayList<>();
        
        for (String path : tilePaths) {
            BufferedImage img = null;
            try {
                File file = new File(path);
                img = ImageIO.read(file);
                
                if (img != null) {
                    // 1. Calculate the color value while the image data is open
                    Color avgColor = getAverageColor(img, img.getWidth(), img.getHeight());
                    
                    // 2. Create a tiny thumbnail version right away to save RAM
                    BufferedImage thumbnail = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = thumbnail.createGraphics();
                    g2d.drawImage(img.getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH), 0, 0, null);
                    g2d.dispose();
                    
                    // 3. Keep only the tiny thumbnail structure in our array list
                    analyzedTiles.add(new AnalyzedTile(thumbnail, avgColor));
                }
            } catch (Exception e) {
                System.err.println("Skipping unreadable tile: " + path);
            } finally {
                // FORCE RELEASING THE LARGE IMAGE BUFFER FROM RAM IMMEDIATELY
                if (img != null) {
                    img.flush();
                    img = null;
                }
            }
        }
        
        // Suggest a garbage collection cleanup pass
        System.gc();
        return analyzedTiles;
    }

    /**
     * Generates a true photomosaic using pre-analyzed tile thumbnail datasets.
     */
    public static BufferedImage createTrueMosaic(File targetFile, List<AnalyzedTile> cachedTiles, int tileSize, int canvasSize) {
        try {
            // 1. Load and scale the target image blueprint map
            BufferedImage rawTarget = ImageIO.read(targetFile);
            if (rawTarget == null || cachedTiles.isEmpty()) return null;
            
            BufferedImage targetImage = new BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D tg = targetImage.createGraphics();
            tg.drawImage(rawTarget.getScaledInstance(canvasSize, canvasSize, Image.SCALE_SMOOTH), 0, 0, null);
            tg.dispose();
            rawTarget.flush(); // Clean original raw blueprint data out of memory

            // 2. Setup final output mosaic grid canvas
            BufferedImage mosaic = new BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = mosaic.createGraphics();

            // 3. Grid Processing: Step through canvas grid blocks
            for (int y = 0; y < canvasSize; y += tileSize) {
                for (int x = 0; x < canvasSize; x += tileSize) {
                    
                    int currentWidth = Math.min(tileSize, canvasSize - x);
                    int currentHeight = Math.min(tileSize, canvasSize - y);

                    // Grab structural color coordinate data matrix maps
                    BufferedImage targetSection = targetImage.getSubimage(x, y, currentWidth, currentHeight);
                    Color targetColor = getAverageColor(targetSection, currentWidth, currentHeight);
                    targetSection.flush(); // Drop this segment map tracking slice right away

                    // Match color matrix values directly
                    AnalyzedTile bestMatch = findBestMatch(targetColor, cachedTiles);

                    // Draw the pre-scaled thumbnail directly onto canvas
                    g.drawImage(bestMatch.image, x, y, null);
                }
            }

            g.dispose();
            targetImage.flush();
            return mosaic;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calculates the average RGB color of a given image region.
     */
    private static Color getAverageColor(BufferedImage img, int width, int height) {
        long sumR = 0, sumG = 0, sumB = 0;
        int totalPixels = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = img.getRGB(x, y);
                sumR += (rgb >> 16) & 0xFF;
                sumG += (rgb >> 8) & 0xFF;
                sumB += rgb & 0xFF;
            }
        }
        return new Color((int) (sumR / totalPixels), (int) (sumG / totalPixels), (int) (sumB / totalPixels));
    }

    /**
     * Finds the tile whose average color has the smallest Euclidean distance to the target color.
     */
    private static AnalyzedTile findBestMatch(Color targetColor, List<AnalyzedTile> tiles) {
        AnalyzedTile bestMatch = tiles.get(0);
        double minDistance = Double.MAX_VALUE;

        for (AnalyzedTile tile : tiles) {
            // 3D Color Distance formula (Euclidean distance in RGB space)
            double rDiff = targetColor.getRed() - tile.avgColor.getRed();
            double gDiff = targetColor.getGreen() - tile.avgColor.getGreen();
            double bDiff = targetColor.getBlue() - tile.avgColor.getBlue();
            
            double distance = Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);

            if (distance < minDistance) {
                minDistance = distance;
                bestMatch = tile;
            }
        }
        return bestMatch;
    }
}