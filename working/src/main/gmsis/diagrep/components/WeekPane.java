package gmsis.diagrep.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import gmsis.diagrep.DiagRepModule;
import gmsis.models.Booking;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class WeekPane extends Pane implements DayDragHelper.DragCallback {

    private static final int NUM_DAYS = 7;

    private List<SingleDayPane> days;
    private TimeLinePane timeLinePane;
    private Listener listener;

    private DoubleProperty dayWidthProperty;
    private DoubleProperty minBookingIntervalProperty;

    public WeekPane(final LocalDate weekStart, List<Booking> bookings, Map<Integer,
            DiagRepModule.OpeningHours> openingHours, DiagRepModule.OpeningHours earliestAndLatestOpeningHours,
                    List<DiagRepModule.Holiday> holidays) {

        getStyleClass().add("week-pane");

        timeLinePane = new TimeLinePane(this, earliestAndLatestOpeningHours.getOpeningTime(), earliestAndLatestOpeningHours.getClosingTime());
        getChildren().add(timeLinePane);

        minBookingIntervalProperty = new SimpleDoubleProperty();
        minBookingIntervalProperty.bind(getTimeLinePane().getHourHeightProperty().multiply(0.25));
        dayWidthProperty = new SimpleDoubleProperty();
        dayWidthProperty.bind(widthProperty().subtract(timeLinePane.getTimeBarWidthProperty()).divide(NUM_DAYS));

        // Bind height to number of hours shown
        prefHeightProperty().bind(timeLinePane.getHourHeightProperty().multiply(timeLinePane.getNumberHoursDisplayed()));

        // Create day panes
        days = new ArrayList<>();
        for(int i = 0; i < NUM_DAYS; i++) {
            LocalDate dayDate = weekStart.plusDays(i);

            Optional<DiagRepModule.Holiday> dayHoliday = holidays.stream().filter(holiday -> dayDate.equals(holiday.getDate())).findFirst();

            SingleDayPane singleDayPane = new SingleDayPane(getTimeLinePane(), dayDate, dayHoliday, bookings, openingHours.get(i));

            singleDayPane.prefWidthProperty().bind(getDayWidthProperty());
            singleDayPane.prefHeightProperty().bind(timeLinePane.getHourHeightProperty().multiply(timeLinePane.getNumberHoursDisplayed()));
            singleDayPane.layoutXProperty().bind(getDayWidthProperty().multiply(i).add(timeLinePane.getTimeBarWidthProperty()));
            singleDayPane.getStyleClass().add("day");

            singleDayPane.setListener(new SingleDayPane.Listener() {
                @Override
                public void onViewBooking(Booking booking) {
                    listener.onViewBooking(booking);
                }

                @Override
                public void onSelectedBooking(Booking booking, Node bookingNode) {
                    listener.onSelectedBooking(booking, bookingNode);
                }
            });

            days.add(singleDayPane);
        }
        
        getChildren().addAll(days);

        new DayDragHelper(this, this, timeLinePane.getTimeBarWidthProperty(), getDayWidthProperty(), getMinBookingIntervalProperty());

        this.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2) {
                int dragColNum = (int) ((event.getX() - timeLinePane.getTimeBarWidthProperty().get()) / getDayWidthProperty().get());
                listener.onViewDay(weekStart.plusDays(dragColNum));
            } else {
                listener.onSelectedBooking(null, null);
            }
        });
    }

    public DoubleProperty getMinBookingIntervalProperty() {
        return minBookingIntervalProperty;
    }

    public DoubleProperty getDayWidthProperty() {
        return dayWidthProperty;
    }

    public TimeLinePane getTimeLinePane() {
        return timeLinePane;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onRangeSelected(int colNum, double startY, double endY) {
        double startInHours = getTimeLinePane().getEarliestDisplayHour() + (startY / getTimeLinePane().getHourHeightProperty().get());
        double endInHours = getTimeLinePane().getEarliestDisplayHour() + (endY / getTimeLinePane().getHourHeightProperty().get());

        LocalDate dayDate = days.get(colNum).getDayDate();

        LocalDateTime startDate = LocalDateTime.of(
                dayDate,
                LocalTime.of((int) Math.floor(startInHours), (int) (startInHours - Math.floor(startInHours)) * 60)
        );

        LocalDateTime endDate = LocalDateTime.of(
                dayDate,
                LocalTime.of((int) Math.floor(endInHours), (int) (endInHours - Math.floor(endInHours)) * 60)
        );

        listener.onCreateBooking(startDate, endDate);
    }

    public interface Listener {
        void onSelectedBooking(Booking booking, Node bookingNode);

        void onViewBooking(Booking booking);

        void onViewDay(LocalDate date);

        void onCreateBooking(LocalDateTime startDateTime, LocalDateTime endDateTime);
    }
}
