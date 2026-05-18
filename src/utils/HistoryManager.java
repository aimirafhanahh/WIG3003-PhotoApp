package utils;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private List<EditAction> history = new ArrayList<>();
    private int currentIndex = -1;

    public void addStep(EditAction action) {
        // If we were in the middle of undoing and did a new action, 
        // remove all "forward" history
        if (currentIndex < history.size() - 1) {
            history = new ArrayList<>(history.subList(0, currentIndex + 1));
        }
        history.add(action);
        currentIndex++;
    }

    public boolean canUndo() { return currentIndex >= 0; }
    public boolean canRedo() { return currentIndex < history.size() - 1; }

    public void undo() { if (canUndo()) currentIndex--; }
    public void redo() { if (canRedo()) currentIndex++; }

    public List<EditAction> getActiveHistory() {
        return history.subList(0, currentIndex + 1);
    }
}