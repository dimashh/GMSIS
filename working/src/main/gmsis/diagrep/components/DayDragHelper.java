package gmsis.diagrep.components;

import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class DayDragHelper {

    private final DragCallback dragCallback;
    private Rectangle dragRectangle;
    private boolean isDragging = false;
    private double startDragYPosition = 0;
    private double startDragXPosition = 0;
    private int dragColNum = 0;

    public DayDragHelper(Pane pane, DragCallback dragCallback, DoubleProperty xOffset, DoubleProperty width, DoubleProperty minHeight) {
        // Drag to create booking
        dragRectangle = new Rectangle();
        dragRectangle.widthProperty().bind(width);
        dragRectangle.getStyleClass().add("select-area");

        this.dragCallback = dragCallback;

        pane.setOnMouseDragged(event -> {
            double nextY = minHeight.get() * Math.round(event.getY() / minHeight.get());

            if(!isDragging) { // First time
                startDragYPosition = nextY;
                dragColNum = (int) ((event.getX() - xOffset.get()) / width.get());
                startDragXPosition = (dragColNum * width.get()) + xOffset.get();

                dragRectangle.setLayoutX(startDragXPosition);
                dragRectangle.getStyleClass().add("dragging");

                if(!pane.getChildren().contains(dragRectangle)) pane.getChildren().add(dragRectangle);

                isDragging = true;
            }

            // Update drag
            if(nextY < startDragYPosition) {
                dragRectangle.setLayoutY(nextY);
                dragRectangle.setHeight(startDragYPosition - nextY);
            } else {
                dragRectangle.setLayoutY(startDragYPosition);
                dragRectangle.setHeight(nextY - startDragYPosition);
            }
        });

        pane.setOnMouseReleased(event -> {
            if(isDragging) { // Deal with end drag
                dragRectangle.getStyleClass().remove("dragging");
                isDragging = false;

                double startY = dragRectangle.getY();
                double endY = dragRectangle.getY() + dragRectangle.getHeight();

                if(startY == endY) return;

                this.dragCallback.onRangeSelected(dragColNum, startY, endY);
            } else {
                pane.getChildren().remove(dragRectangle);
            }
        });
    }

    public interface DragCallback {
        void onRangeSelected(int colNum, double startY, double endY);

        // void isValid(int colNum, double startY, double endY);
    }

}
