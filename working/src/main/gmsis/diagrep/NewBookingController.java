package gmsis.diagrep;

import gmsis.BaseController;
import gmsis.ValidationHelper;
import gmsis.diagrep.components.TimeSpinner;
import gmsis.models.Booking;
import gmsis.models.Customer;
import gmsis.models.User;
import gmsis.models.Vehicle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class NewBookingController extends BaseController implements DisplayBookingController.Callback {

    @FXML private Button backButton;

    @FXML private DisplayBookingController displayBookingController;

    private User mechanic = null;
    private LocalDateTime startDateTime = null;
    private LocalDateTime endDateTime = null;
    private Vehicle selectedVehicle = null;

    public NewBookingController(List<Object> args) {
        if(args != null && args.size() > 0) {
            if(args.get(0) instanceof User) {
                mechanic = (User) args.get(0);
            }
            if(args.get(0) instanceof Vehicle) {
                selectedVehicle = (Vehicle) args.get(0);
            }
            if(args.size() > 2) {
                if(args.get(1) instanceof LocalDateTime && args.get(2) instanceof LocalDateTime) {
                    startDateTime = (LocalDateTime) args.get(1);
                    endDateTime = (LocalDateTime) args.get(2);
                }
            }
        }
    }

    @FXML
    protected void initialize() {
        backButton.setOnAction(event -> getApplication().setUI("bookingCalendarUI"));

        if(startDateTime != null && endDateTime != null) {
            displayBookingController.setDates(startDateTime, endDateTime);
        }

        if(mechanic != null) {
            displayBookingController.setMechanic(mechanic);
        }

        if(selectedVehicle != null) {
            displayBookingController.setVehicle(selectedVehicle);
        }

        displayBookingController.setShowDelete(false);
        displayBookingController.setEditEnabled(true);
        displayBookingController.hideViewButtons();
        displayBookingController.setCallback(this);
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

    @Override
    public void onDeleted(Booking booking) {

    }

    @Override
    public void onSave(Booking booking) {
        // Go back on save
        getApplication().setUI("bookingCalendarUI");
    }
}
