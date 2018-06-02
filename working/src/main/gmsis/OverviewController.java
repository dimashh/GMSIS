package gmsis;

import gmsis.database.get.Query;
import gmsis.database.tables.BillTable;
import gmsis.database.tables.BookingTable;
import gmsis.models.Bill;
import gmsis.models.Booking;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The controller for overviewUI.fxml
 */
public class OverviewController extends BaseController {


    @FXML private Text bookingTitle;
    @FXML private Text title;
    @FXML private VBox todaysBookings;
    @FXML private VBox billsList;

    @FXML
    private void initialize() {
        title.setText("Hello " + getApplication().getUser().getFirstname());
        billsList.getChildren().clear();
        todaysBookings.getChildren().clear();

        if(getApplication().getUser().isMechanic()) bookingTitle.setText("Your bookings today");

        List<Booking> bookings = findBookings();
        if(bookings.isEmpty()) {
            todaysBookings.getChildren().add(createTextItem("No bookings today"));
        } else {
            bookings.forEach(this::addBookingResult);
        }

        List<Bill> bills = findBills().stream().filter(bill -> {
            return bill.getBooking().getVehicle().getWarranty() == null;
        }).collect(Collectors.toList());
        if(bookings.isEmpty()) {
            billsList.getChildren().add(createTextItem("No outstanding bills"));
        } else {
            bills.forEach(this::addBillResult);
        }
    }

    private Node createTextItem(String text) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);
        Text header = new Text(text);
        container.getChildren().add(header);
        HBox.setHgrow(container, Priority.ALWAYS);
        VBox.setVgrow(container, Priority.ALWAYS);
        return container;
    }

    private void addBookingResult(Booking booking) {
        todaysBookings.getChildren().add(ListHelper.createListItem(
                booking.getDateStart().toString() + " - " + booking.getType().getName() + " booking for " + booking.getCustomer().getFirstname() + " " + booking.getCustomer().getSurname(),
                booking.getVehicle().getRegistration() + " - " + booking.getVehicle().getColour() + " " + booking.getVehicle().getMake() + " " + booking.getVehicle().getModel(),
                "View Booking",
                (event -> getApplication().setUI("bookingEditUI", booking))
        ));
    }

    private void addBillResult(Bill bill) {
        billsList.getChildren().add(ListHelper.createListItem(
                String.format("£%.2f", bill.getTotal()) + " - " + bill.getBooking().getCustomer().getFirstname() + " " + bill.getBooking().getCustomer().getSurname(),
                "For " + bill.getBooking().getType().getName() + " Booking on " + bill.getBillDate().toString() + " of Vehicle " + bill.getBooking().getVehicle().getRegistration(),
                "View Bill",
                (event -> getApplication().setUI("bookingEditUI", bill.getBooking()))
        ));
    }

    @FXML
    private void onNewCustomer(ActionEvent actionEvent) {
        getApplication().setUI("addCustomerUI");
    }

    @FXML
    private void onNewVehicle(ActionEvent actionEvent) {
        getApplication().setUI("addNewVehicleUI");
    }

    @FXML
    private void onNewBooking(ActionEvent actionEvent) {
        getApplication().setUI("bookingNewUI");
    }

    private List<Booking> findBookings() {
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
        if(getApplication().getUser().isMechanic()) {
            return getApplication().getDiagRepModule().getBookingsBetweenForMechanic(today, today.plusDays(1), getApplication().getUser());
        } else {
            return getApplication().getDiagRepModule().getBookingsBetween(today, today.plusDays(1));
        }
    }

    private List<Bill> findBills() {
        return getApplication().getDatabase().get()
                .objects(Bill.class)
                .withQuery(Query.builder()
                        .table(BillTable.TABLE)
                        .where(BillTable.COLUMN_PAID + " = ?")
                        .whereValues(false))
                .execute()
                .getList();
    }
}
