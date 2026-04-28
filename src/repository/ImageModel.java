package repository;

public class ImageModel {
    private String filePath;
    private String annotation;
    private boolean favorite;

    public ImageModel(String filePath) {
        this.filePath = filePath;
        this.annotation = "";
        this.favorite = false;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public boolean hasAnnotation() {
        return annotation != null && !annotation.trim().isEmpty();
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}