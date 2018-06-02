package gmsis.diagrep.components;

import gmsis.diagrep.DiagRepModule;
import gmsis.models.Booking;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SingleDayPane extends Pane {

    private Listener listener;
    private LocalDate dayDate;

    public SingleDayPane(final TimeLinePane timeLinePane, final LocalDate dayDate, Optional<DiagRepModule.Holiday> holiday, List<Booking> bookings, DiagRepModule.OpeningHours openingHours) {

        this.dayDate = dayDate;

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        // Closing time greyed out rectangles
        if(openingHours != null && (!holiday.isPresent() || !holiday.get().isBankHoliday())) {
            // Show opening hours if there are any and there is no holiday or the holiday is not a bank holiday
            Rectangle openingRectangle = new Rectangle();
            openingRectangle.widthProperty().bind(widthProperty());
            double openingTimeInHours = ((double) openingHours.getOpeningTime().getHour()) + ((double) openingHours.getOpeningTime().getMinute() / 60);
            openingRectangle.heightProperty().bind(timeLinePane.getHourHeightProperty().multiply(openingTimeInHours - timeLinePane.getEarliestDisplayHour()));
            openingRectangle.layoutYProperty().set(0);
            openingRectangle.getStyleClass().add("inactive");
            getChildren().add(openingRectangle);

            Rectangle closingRectangle = new Rectangle();
            closingRectangle.widthProperty().bind(widthProperty());
            double closingTimeInHours = ((double) openingHours.getClosingTime().getHour()) + ((double) openingHours.getClosingTime().getMinute() / 60);
            closingRectangle.heightProperty().bind(timeLinePane.getHourHeightProperty().multiply(timeLinePane.getLastDisplayHour() - closingTimeInHours));
            closingRectangle.layoutYProperty().bind(timeLinePane.getHourHeightProperty().multiply(closingTimeInHours - timeLinePane.getEarliestDisplayHour()));
            closingRectangle.getStyleClass().add("inactive");
            getChildren().add(closingRectangle);
        } else {
            // Full day closed, due to no opening hours or holiday
            Rectangle closedRectangle = new Rectangle();
            closedRectangle.widthProperty().bind(widthProperty());
            closedRectangle.heightProperty().bind(timeLinePane.getHourHeightProperty().multiply(timeLinePane.getNumberHoursDisplayed()));
            closedRectangle.layoutYProperty().set(0);
            closedRectangle.getStyleClass().add("inactive");
            getChildren().add(closedRectangle);
        }

        // Highlight current day
        if(dayDate.equals(LocalDate.now())) {
            Rectangle currentRectangle = new Rectangle();
            currentRectangle.widthProperty().bind(widthProperty());
            currentRectangle.heightProperty().bind(timeLinePane.getHourHeightProperty().multiply(timeLinePane.getNumberHoursDisplayed()));
            currentRectangle.layoutYProperty().set(0);
            currentRectangle.getStyleClass().add("current");
            getChildren().add(currentRectangle);
        }

        // Create bookings
        for(final Booking booking : bookings) {
            LocalDateTime bookingDateStart = booking.getDateStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            if(dayDate.equals(bookingDateStart.toLocalDate())) {

                LocalDateTime bookingDateEnd = booking.getDateEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                double numberHoursEnd = ((double) bookingDateEnd.getHour()) + ((double) bookingDateEnd.getMinute() / 60);
                double numberHoursStart = ((double) bookingDateStart.getHour()) + ((double) bookingDateStart.getMinute() / 60);

                final ScrollPane bookingScroll = new ScrollPane();
                bookingScroll.maxWidthProperty().bind(widthProperty());
                bookingScroll.prefWidthProperty().bind(widthProperty());
                bookingScroll.prefHeightProperty().bind(timeLinePane.getHourHeightProperty().multiply(numberHoursEnd - numberHoursStart));
                bookingScroll.layoutYProperty().bind(timeLinePane.getHourHeightProperty().multiply(numberHoursStart-timeLinePane.getEarliestDisplayHour()));
                bookingScroll.getStyleClass().add("booking");

                Color bgColor = Util.getUniqueColorFromString(booking.getMechanic().getFirstname()+booking.getMechanic().getSurname(), 1);
                Color borderColor = bgColor.deriveColor(0, 1.4, 1.1, 1);
                bookingScroll.setStyle("-fx-background-color: rgba(" + bgColor.getRed()*256 + ", " + bgColor.getGreen()*256 + ", " + bgColor.getBlue()*256 + ", 1);" +
                                        "-fx-border-color: rgba(" + borderColor.getRed()*256 + ", " + borderColor.getGreen()*256 + ", " + borderColor.getBlue()*256 + ", 1);");

                final VBox bookingNode = new VBox();
                bookingScroll.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> bookingNode.prefWidthProperty().setValue(newValue.getWidth()));

                Text bookingLabel = new Text();
                bookingLabel.setText(timeFormat.format(
                        booking.getDateStart().toInstant().atZone(ZoneId.systemDefault())) + " - " +
                        timeFormat.format(booking.getDateEnd().toInstant().atZone(ZoneId.systemDefault())) + " " +
                        booking.getType().getName()
                );
                bookingLabel.wrappingWidthProperty().bind(bookingNode.widthProperty());
                bookingNode.getChildren().add(bookingLabel);

                Text bookingInfo = new Text();
                if(booking.getCustomer() != null) {
                    bookingInfo.setText(
                            booking.getVehicle().getRegistration() + " - " + booking.getVehicle().getColour() + " " + booking.getVehicle().getMake() + " " + booking.getVehicle().getModel() + "\n" +
                            booking.getCustomer().getFirstname() + " " + booking.getCustomer().getSurname()
                    );
                }
                bookingInfo.wrappingWidthProperty().bind(bookingNode.widthProperty());
                bookingNode.getChildren().add(bookingInfo);

                bookingScroll.setContent(bookingNode);

                bookingScroll.setOnMouseClicked(event -> {
                    if(event.getButton().equals(MouseButton.PRIMARY)){
                        if(event.getClickCount() == 2){
                            listener.onViewBooking(booking);
                        } else {
                            listener.onSelectedBooking(booking, bookingScroll);
                        }
                        event.consume();
                    }
                });

                getChildren().add(bookingScroll);

            }
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public LocalDate getDayDate() {
        return dayDate;
    }

    public interface Listener {
        void onViewBooking(Booking booking);

        void onSelectedBooking(Booking booking, Node bookingNode);
    }
}
