package gmsis.diagrep.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.LocalTime;
import java.util.Calendar;

public class TimeLinePane extends Pane {
    
    private final DoubleProperty hourHeightProperty;
    private final DoubleProperty timeBarWidthProperty;
    private final Pane parentPane;
    private int earliestDisplayHour;
    private int lastDisplayHour;

    public TimeLinePane(Pane parentPane, LocalTime earliestOpeningTime, LocalTime latestClosingTime) {
        this.parentPane = parentPane;
        
        hourHeightProperty = new SimpleDoubleProperty(60);
        timeBarWidthProperty = new SimpleDoubleProperty(60);
        earliestDisplayHour = earliestOpeningTime.getHour();
        lastDisplayHour = latestClosingTime.getHour();
        if(lastDisplayHour < 23 && latestClosingTime.getMinute() != 0) lastDisplayHour += 1;

        getStyleClass().add("time-line");
        
        // layout
        layoutXProperty().set(0);
        layoutYProperty().set(0);
        prefWidthProperty().bind(this.parentPane.widthProperty());
        prefHeightProperty().bind(this.parentPane.heightProperty());
        
        setMouseTransparent(true);
        
        drawLines();
    }
    
    public DoubleProperty getHourHeightProperty() {
        return hourHeightProperty;
    }
    
    public DoubleProperty getTimeBarWidthProperty() {
        return timeBarWidthProperty;
    }

    public int getEarliestDisplayHour() {
        return earliestDisplayHour;
    }

    public int getLastDisplayHour() {
        return lastDisplayHour;
    }

    public int getNumberHoursDisplayed() {
        return lastDisplayHour - earliestDisplayHour;
    }

    private void drawLines() {
        for (int hour = earliestDisplayHour; hour <= lastDisplayHour; hour++) {
            int hourCell = hour - earliestDisplayHour;

            // hour line
            Line l = new Line(0, 10, 100, 10);
            l.setId("hourLine" + hour);
            l.getStyleClass().add("hourLine");
            l.startXProperty().bind(timeBarWidthProperty);
            l.startYProperty().bind(hourHeightProperty.multiply(hourCell));
            l.endXProperty().bind(parentPane.widthProperty());
            l.endYProperty().bind(l.startYProperty());
            getChildren().add(l);
            
            // half hour line
            if(hour != lastDisplayHour) { // Don't add extra half hour line after last hour
                l = new Line(0, 10, 100, 10);
                l.setId("hourHalfLine" + hour);
                l.getStyleClass().add("hourHalfLine");
                l.startXProperty().bind(timeBarWidthProperty);
                l.startYProperty().bind(hourHeightProperty.multiply(hourCell + 0.5));
                l.endXProperty().bind(parentPane.widthProperty());
                l.endYProperty().bind(l.startYProperty());
                getChildren().add(l);
            }
            
            // hour text
            final Label t = new Label();

            if(hour < 12) t.setText(hour + ":00 AM");
            else t.setText((hour > 12 ? hour - 12 : hour) + ":00 PM");

            t.setTextAlignment(TextAlignment.RIGHT);
            t.setAlignment(Pos.BOTTOM_RIGHT);
            t.prefWidthProperty().bind(timeBarWidthProperty);

            t.layoutYProperty().bind(hourHeightProperty.multiply(hourCell));
            t.getStyleClass().add("hourText");
            getChildren().add(t);
        }
    }
    
}
