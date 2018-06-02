package gmsis.diagrep.components;

import gmsis.diagrep.DiagRepModule;
import gmsis.models.Booking;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

public class MonthPane extends Pane {

    private static final int NUMBER_OF_WEEKS = 6;
    private static final int NUMBER_OF_DAYS_IN_WEEK = 7;

    private DoubleProperty dayWidthProperty;
    private DoubleProperty dayHeightProperty;
    private Listener listener;

    public MonthPane(LocalDate monthStart, List<Booking> bookings, List<DiagRepModule.Holiday> holidays) {

        getStyleClass().add("month-pane");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd");

        LocalDate now = LocalDate.now();
        LocalDate monthPaneStart = monthStart.with(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());

        dayWidthProperty = new SimpleDoubleProperty();
        dayWidthProperty.bind(widthProperty().divide(NUMBER_OF_DAYS_IN_WEEK));
        dayHeightProperty = new SimpleDoubleProperty();
        dayHeightProperty.bind(heightProperty().divide(NUMBER_OF_WEEKS));


        for (int i = 0; i < NUMBER_OF_DAYS_IN_WEEK * NUMBER_OF_WEEKS; i++) {
            LocalDate dayDate = monthPaneStart.plusDays(i);

            Optional<DiagRepModule.Holiday> todaysHoliday = holidays.stream().filter(holiday -> dayDate.equals(holiday.getDate())).findFirst();

            final ScrollPane dayScroll = new ScrollPane();
            dayScroll.prefWidthProperty().bind(dayWidthProperty);
            dayScroll.prefHeightProperty().bind(dayHeightProperty);
            dayScroll.layoutXProperty().bind(dayWidthProperty.multiply(i%NUMBER_OF_DAYS_IN_WEEK));
            dayScroll.layoutYProperty().bind(dayHeightProperty.multiply(Math.floor(i/(NUMBER_OF_WEEKS+1))));

            dayScroll.getStyleClass().add("day");

            if(dayDate.getYear() != monthStart.getYear() || dayDate.getMonth() != monthStart.getMonth()) {
                dayScroll.getStyleClass().add("inactive");
            } else if(dayDate.getYear() == now.getYear() &&
                    dayDate.getDayOfYear() == now.getDayOfYear()) {
                dayScroll.getStyleClass().add("current");
            }

            final VBox dayNode = new VBox();
            Text dayLabel = new Text();
            dayLabel.setText(dateFormat.format(dayDate));
            dayLabel.getStyleClass().add("day-number");
            dayNode.getChildren().add(dayLabel);

            Text daySubLabel = new Text();
            todaysHoliday.ifPresent(holiday -> daySubLabel.setText(holiday.getName()));
            dayNode.getChildren().add(daySubLabel);

            FlowPane dots = new FlowPane(Orientation.VERTICAL);
            for(Booking booking : bookings) {
                if(booking.getDateStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(dayDate)) {
                    Circle circle = new Circle(3);
                    Color color = Util.getUniqueColorFromString(booking.getMechanic().getFirstname()+booking.getMechanic().getSurname(), 1);
                    circle.setFill(color);
                    dots.getChildren().add(circle);
                }
            }
            dots.setPadding(new Insets(5));
            dots.setHgap(4);
            dots.setVgap(4);
            dots.prefWrapLengthProperty().bind(dayNode.widthProperty());
            dayNode.getChildren().add(dots);

            dayScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            dayScroll.setContent(dayNode);
            dayScroll.setOnMouseClicked(event -> {
                if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    if(listener != null) listener.onViewDay(dayDate);
                }
            });
            getChildren().add(dayScroll);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onViewDay(LocalDate date);
    }

}
