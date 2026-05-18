package utils;

/**
 * Stores the 'instruction' for an edit rather than the whole image.
 */
public class EditAction {
    public enum Type { FILTER, ROTATE, SCALE, TRANSLATE_X, 
        TRANSLATE_Y, BRIGHTNESS, CONTRAST }
    
    private final Type type;
    private final String value; // e.g., "GRAYSCALE", "90", "1.2"

    public EditAction(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() { return type; }
    public String getValue() { return value; }
}