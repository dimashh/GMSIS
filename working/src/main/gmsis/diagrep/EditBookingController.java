package gmsis.diagrep;

import gmsis.BaseController;
import gmsis.ValidationHelper;
import gmsis.diagrep.components.TimeSpinner;
import gmsis.models.*;
import gmsis.models.Booking.BookingType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.controlsfx.control.textfield.TextFields;

public class EditBookingController extends BaseController implements DisplayBookingController.Callback {

    @FXML private Text title;
    @FXML private Button backButton;

    @FXML private Button newButton;
    @FXML private TextField filterField;
    @FXML private ListView<BookingListView> bookingList;

    @FXML private DisplayBookingController displayBookingController;

    private ObservableList<BookingListView> bookingListData = FXCollections.observableArrayList();

    private Integer selectedBookingId = null;
    private BookingListView selectedBooking = null;

    private Vehicle vehicle = null;
    private Customer customer = null;

    public EditBookingController(List<Object> args) {
        if(args != null && args.size() > 0) {
            if(args.get(0) instanceof Booking) {
                Booking booking = (Booking) args.get(0);
                selectedBookingId = booking.getId();
                vehicle = booking.getVehicle();
                customer = booking.getCustomer();
            } else if(args.get(0) instanceof Customer) {
                customer = (Customer) args.get(0);
            } else if(args.get(0) instanceof Vehicle) {
                vehicle = (Vehicle) args.get(0);
                customer = vehicle.getCustomer();
            }
        }
    }

    @FXML
    protected void initialize() {
        title.setText("Bookings for " + customer.getFirstname() + " " + customer.getSurname());

        backButton.setOnAction(event -> getApplication().setUI("bookingCalendarUI"));
        newButton.setOnAction(event -> getApplication().setUI("bookingNewUI", vehicle));

        displayBookingController.setCallback(this);

        setupBookingList();

        if(vehicle != null) filterField.setText(vehicle.getRegistration());

        displayBookingController.setUnsaved(false);
    }

    @Override
    public boolean hide() {
        if(displayBookingController.isUnsaved()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Unsaved changes");
            alert.setContentText("You have unsaved changes. Are you sure you to leave?");
            Optional<ButtonType> result = alert.showAndWait();

            return result.isPresent() && result.get() == ButtonType.OK;

        }

        return true;
    }

    // Load list of bookings current customer
    private void setupBookingList() {
        List<Booking> bookings = customer.getVehicles().stream()
                .flatMap(vehicle -> vehicle.getBookings().stream())
                .collect(Collectors.toList());

        bookings.forEach(booking -> {
            BookingListView view = new BookingListView(booking);
            if(booking.getId().equals(selectedBookingId)) selectedBooking = view;
            bookingListData.add(view);
        });

        // Pass data list through filter
        Comparator<BookingListView> comparator = Comparator.comparing(o -> o.getBooking().getDateStart());
        bookingListData.sort(comparator.reversed());
        FilteredList<BookingListView> filteredBookingListData = new FilteredList<>(bookingListData, bookingListView -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredBookingListData.setPredicate(bookingListView -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) return true;

                String filterString = newValue.trim().toLowerCase();

                return filterString.isEmpty() || bookingListView.getBooking().getVehicle().getRegistration().toLowerCase().contains(filterString);

            });
        });
        bookingList.setItems(filteredBookingListData);
        bookingList.setPlaceholder(new Label("No bookings found"));

        bookingList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                selectedBooking = newValue;
                displayBookingController.showBooking(newValue.getBooking());
            }
        });
        bookingList.getSelectionModel().select(selectedBooking);
    }

    @Override
    public void onDeleted(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to delete this booking?");
        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(deleteButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == deleteButton){
            getApplication().getDiagRepModule().deleteBooking(selectedBooking.getBooking());
            bookingListData.remove(selectedBooking);
        }
    }

    @Override
    public void onSave(Booking newBooking) {
    }

    public class BookingListView {
        private final DateFormat format = new SimpleDateFormat("dd/MM/YYYY");

        private Booking booking;

        public BookingListView(Booking booking) {
            this.booking = booking;
        }

        @Override
        public String toString() {
            return format.format(booking.getDateStart()) + " - " + booking.getVehicle().getRegistration() + " - " + booking.getType().getName();
        }

        public int getId() { return booking.getId(); }

        public Booking getBooking() {
            return booking;
        }
    }
    
}
