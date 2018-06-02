package gmsis.diagrep;

import gmsis.BaseController;
import gmsis.diagrep.components.DayPane;
import gmsis.diagrep.components.HeaderPane;
import gmsis.diagrep.components.MonthPane;
import gmsis.diagrep.components.WeekPane;
import gmsis.models.Booking;
import gmsis.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

public class CalendarController extends BaseController {

    @FXML private Button viewBookingButton;
    @FXML private Label mechanicLabel;
    @FXML private ChoiceBox<MechanicChoice> mechanicChoiceBox;
    @FXML private ToggleButton dayButton;
    @FXML private ToggleButton weekButton;
    @FXML private ToggleButton monthButton;
    @FXML private ToggleGroup calendarViewToggle;
    @FXML private Label currentLabel;
    @FXML private BorderPane calendarBorderPane;

    private LocalDate currentDate;
    private CalendarView currentView = CalendarView.WEEK;

    private Booking selectedBooking = null;
    private Node selectedBookingNode = null;
    private User currentMechanic = null;

    @FXML
    protected void initialize() {
        currentDate = LocalDate.now();

        List<User> mechanics = getApplication().getDiagRepModule().getMechanics();
        mechanicChoiceBox.getItems().clear();
        for(User mechanic : mechanics) {
            mechanicChoiceBox.getItems().add(new MechanicChoice(mechanic));
        }

        mechanicChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentMechanic = newValue.getMechanic();

            refreshView();
        });
        mechanicChoiceBox.getSelectionModel().select(0);

        dayButton.setUserData("dayButton");
        weekButton.setUserData("weekButton");
        monthButton.setUserData("monthButton");
        calendarViewToggle = (ToggleGroup) getNamespace().get("calendarViewToggle");
        calendarViewToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null) return;

            if(newValue.getUserData().equals("dayButton")) {
                currentView = CalendarView.DAY;
            } else if(newValue.getUserData().equals("weekButton")) {
                currentView = CalendarView.WEEK;
            } else if(newValue.getUserData().equals("monthButton")) {
                currentView = CalendarView.MONTH;
            } else {
                return;
            }

            refreshView();
        });
        calendarViewToggle.selectToggle(weekButton);

        viewBookingButton.setDisable(selectedBooking == null);

        mechanicLabel.visibleProperty().bind(mechanicChoiceBox.visibleProperty());
        mechanicLabel.managedProperty().bind(mechanicLabel.visibleProperty());
        mechanicChoiceBox.managedProperty().bind(mechanicChoiceBox.visibleProperty());
    }

    private void refreshView() {
        mechanicChoiceBox.setVisible(false);
        switch (currentView) {
            case DAY:
                loadDayView();
                break;
            case WEEK:
                loadWeekView();
                break;
            case MONTH:
                loadMonthView();
                break;
        }
    }

    private void loadDayView() {
        LocalDate dayStart = currentDate;
        LocalDate dayEnd = currentDate.plusDays(1);

        // Get bookings from database
        final List<Booking> bookings = getApplication().getDiagRepModule().getBookingsBetween(LocalDateTime.of(dayStart, LocalTime.of(0, 0)), LocalDateTime.of(dayEnd, LocalTime.of(0, 0)));
        final List<User> mechanics = getApplication().getDiagRepModule().getMechanics();
        final List<DiagRepModule.Holiday> holidays = getApplication().getDiagRepModule().getHolidaysBetween(currentDate, dayEnd);
        final Optional<DiagRepModule.Holiday> holiday = Optional.ofNullable(holidays.size() > 0 ? holidays.get(0) : null);

        // Create panes
        final ScrollPane weekScrollPane = new ScrollPane();
        final DayPane dayPane = new DayPane(currentDate, bookings, mechanics, getApplication().getDiagRepModule().getEarliestAndLatestOpeningHours(), holiday);
        final HeaderPane headerPane = HeaderPane.createMechanicHeader(dayStart, mechanics, "EEE dd/MM");

        weekScrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> dayPane.prefWidthProperty().setValue(newValue.getWidth()));

        headerPane.headerWidthProperty().bind(dayPane.widthProperty().subtract(dayPane.getTimeLinePane().getTimeBarWidthProperty()));
        headerPane.offsetXProperty().bind(dayPane.getTimeLinePane().getTimeBarWidthProperty());

        dayPane.setListener(new DayPane.Listener() {
            @Override
            public void onSelectedBooking(Booking booking, Node bookingNode) {
                onBookingSelected(booking, bookingNode);
            }

            @Override
            public void onViewBooking(Booking booking) {
                getApplication().setUI("bookingEditUI", booking);
            }

            @Override
            public void onCreateBooking(LocalDateTime startDate, LocalDateTime endDate) {
                getApplication().setUI("bookingNewUI", currentMechanic, startDate, endDate);
            }
        });

        // Attach panes
        weekScrollPane.setContent(dayPane);
        calendarBorderPane.setCenter(weekScrollPane);
        calendarBorderPane.setTop(headerPane);

        mechanicChoiceBox.setVisible(false);

        // Update menu bar
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        currentLabel.setText(df.format(currentDate));
    }

    private void loadWeekView() {
        LocalDate weekStart = currentDate.with(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());
        LocalDate weekEnd = weekStart.plusWeeks(1);

        // Get bookings from database
        final List<Booking> bookings = getApplication().getDiagRepModule().getBookingsBetweenForMechanic(LocalDateTime.of(weekStart, LocalTime.of(0, 0)), LocalDateTime.of(weekEnd, LocalTime.of(0, 0)), currentMechanic);
        final List<DiagRepModule.Holiday> holidays = getApplication().getDiagRepModule().getHolidaysBetween(weekStart, weekEnd);

        // Create panes
        final ScrollPane weekScrollPane = new ScrollPane();
        final WeekPane weekPane = new WeekPane(weekStart, bookings, getApplication().getDiagRepModule().getOpeningHours(), getApplication().getDiagRepModule().getEarliestAndLatestOpeningHours(), holidays);
        final HeaderPane headerPane = HeaderPane.createDateHeader(weekStart, holidays, "EEE dd/MM");

        weekScrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> weekPane.prefWidthProperty().setValue(newValue.getWidth()));

        headerPane.headerWidthProperty().bind(weekPane.widthProperty().subtract(weekPane.getTimeLinePane().getTimeBarWidthProperty()));
        headerPane.offsetXProperty().bind(weekPane.getTimeLinePane().getTimeBarWidthProperty());

        weekPane.setListener(new WeekPane.Listener() {
            @Override
            public void onSelectedBooking(Booking booking, Node bookingNode) {
                onBookingSelected(booking, bookingNode);
            }

            @Override
            public void onViewBooking(Booking booking) {
                getApplication().setUI("bookingEditUI", booking);
            }

            @Override
            public void onViewDay(LocalDate date) {
                currentDate = date;
                calendarViewToggle.selectToggle(dayButton);
            }

            @Override
            public void onCreateBooking(LocalDateTime startDate, LocalDateTime endDate) {
                getApplication().setUI("bookingNewUI", currentMechanic, startDate, endDate);
            }
        });

        // Attach panes
        weekScrollPane.setContent(weekPane);
        calendarBorderPane.setCenter(weekScrollPane);
        calendarBorderPane.setTop(headerPane);

        mechanicChoiceBox.setVisible(true);

        // Update menu bar
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        currentLabel.setText(df.format(weekStart) + " - " + df.format(weekEnd.minusDays(1)));
    }

    private void loadMonthView() {
        LocalDate monthStart = currentDate.withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1);

        // Get bookings from database
        final List<Booking> bookings = getApplication().getDiagRepModule().getBookingsBetween(LocalDateTime.of(monthStart, LocalTime.of(0, 0)), LocalDateTime.of(monthEnd, LocalTime.of(0, 0)));
        final List<DiagRepModule.Holiday> holidays = getApplication().getDiagRepModule().getHolidaysBetween(monthStart, monthEnd);

        // Create panes
        final MonthPane monthPane = new MonthPane(monthStart, bookings, holidays);
        final HeaderPane headerPane = HeaderPane.createDateHeader(monthStart.with(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek()), null, "EEE");

        headerPane.headerWidthProperty().bind(monthPane.widthProperty());

        calendarBorderPane.setCenter(monthPane);
        calendarBorderPane.setTop(headerPane);

        monthPane.setListener(date -> {
            currentDate = date;
            calendarViewToggle.selectToggle(dayButton);
        });

        // Update menu bar
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM yyyy");
        currentLabel.setText(df.format(monthStart));

    }

    private void onBookingSelected(Booking booking, Node bookingNode) {
        if(selectedBookingNode != null) {
            selectedBookingNode.getStyleClass().remove("selected");
        }
        selectedBookingNode = bookingNode;
        if(selectedBookingNode != null) {
            selectedBookingNode.getStyleClass().add("selected");
        }
        selectedBooking = booking;
        viewBookingButton.setDisable(selectedBooking == null);
    }

    public void onNext(ActionEvent actionEvent) {
        switch (currentView) {
            case DAY:
                currentDate = currentDate.plusDays(1);
                break;
            case WEEK:
                currentDate = currentDate.plusWeeks(1);
                break;
            case MONTH:
                currentDate = currentDate.plusMonths(1);
                break;
        }

        refreshView();
    }

    public void onPrevious(ActionEvent actionEvent) {
        switch (currentView) {
            case DAY:
                currentDate = currentDate.minusDays(1);
                break;
            case WEEK:
                currentDate = currentDate.minusWeeks(1);
                break;
            case MONTH:
                currentDate = currentDate.minusMonths(1);
                break;
        }

        refreshView();
    }

    public void onViewBooking(ActionEvent actionEvent) {
        if(selectedBooking != null) {
            getApplication().setUI("bookingEditUI", selectedBooking);
        }
    }

    public void onNewBooking(ActionEvent actionEvent) {
        getApplication().setUI("bookingNewUI", currentMechanic);
    }

    public enum CalendarView {
        DAY, WEEK, MONTH
    }

    public class MechanicChoice {
        private User mechanic;

        public MechanicChoice(User mechanicId) {
            this.mechanic = mechanicId;
        }

        @Override
        public String toString() {
            return mechanic.getFirstname();
        }

        public User getMechanic() {
            return mechanic;
        }
    }
}
