package dip_basic;

import java.awt.image.BufferedImage;

public class BrightnessContrast {

    public static BufferedImage adjustBrightness(BufferedImage img, int value) {
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int rgb = img.getRGB(x, y);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                r = clamp(r + value);
                g = clamp(g + value);
                b = clamp(b + value);

                int newRgb = (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRgb);
            }
        }

        return result;
    }

    public static BufferedImage adjustContrast(BufferedImage img, int value) {
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        double factor = (259.0 * (value + 255)) / (255 * (259 - value));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int rgb = img.getRGB(x, y);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                r = clamp((int)(factor * (r - 128) + 128));
                g = clamp((int)(factor * (g - 128) + 128));
                b = clamp((int)(factor * (b - 128) + 128));

                int newRgb = (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRgb);
            }
        }

        return result;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}