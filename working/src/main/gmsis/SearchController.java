package gmsis;

import gmsis.database.get.Query;
import gmsis.database.tables.CustomerTable;
import gmsis.database.tables.VehicleTable;
import gmsis.models.Booking;
import gmsis.models.Customer;
import gmsis.models.Vehicle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchController extends BaseController {

    @FXML private Text title;
    @FXML private VBox results;
    @FXML private ScrollPane resultsScrollPane;

    private String searchText;
    private Timeline fetchTimeout;

    public SearchController() {
        fetchTimeout = new Timeline(new KeyFrame(
                Duration.millis(400),
                (event) -> fetchResults()
        ));
    }

    @FXML
    public void initialize() {
        showLoading();
        resultsScrollPane.setFitToHeight(true);
        resultsScrollPane.setFitToWidth(true);
    }

    /**
     * Displays a customer result
     * @param customer
     */
    private void addCustomerResult(Customer customer) {
        results.getChildren().add(ListHelper.createListItem(
                customer.getFirstname() + " " + customer.getSurname(),
                customer.getEmail(),
                "View Customer",
                (event -> getApplication().setUI("customerUI", customer))
        ));
    }

    /**
     * Displays a vehicle result
     * @param vehicle
     */
    private void addVehicleResult(Vehicle vehicle) {
        StringBuilder details = new StringBuilder();
        if(vehicle.getLastServiceDate() != null) details.append("Last Service - ").append(vehicle.getLastServiceDate()).append(" ");
        details.append(vehicle.getEngineSize()).append(" / ")
                .append(vehicle.getColour()).append(" / ")
                .append(vehicle.getFuelType().toString()).append(" / ")
                .append(vehicle.getCurrentMileage()).append("Mi");

        results.getChildren().add(ListHelper.createListItem(
                vehicle.getRegistration() + " " + vehicle.getMake() + " " + vehicle.getModel(),
                details.toString(),
                "View Vehicle",
                (event -> getApplication().setUI("vehicleUI", vehicle))
        ));
    }

    /**
     * Displays a booking result
     * @param booking
     */
    private void addBookingResult(Booking booking) {
        results.getChildren().add(ListHelper.createListItem(
                booking.getDateStart().toString() + " - " + booking.getType().getName() + " booking for " + booking.getCustomer().getFirstname() + " " + booking.getCustomer().getSurname(),
                booking.getVehicle().getRegistration() + " - " + booking.getVehicle().getColour() + " " + booking.getVehicle().getMake() + " " + booking.getVehicle().getModel(),
                "View Booking",
                (event -> getApplication().setUI("bookingEditUI", booking))
        ));
    }

    private void showText(String text) {
        results.getChildren().clear();

        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);

        container.getChildren().add(new Text(text));

        results.getChildren().add(container);
    }

    private void showLoading() {
        showText("Loading...");
    }

    public void fetchResults() {
        if(searchText.isEmpty()) {
            showText("Search for any customer, vehicle or booking. E.g. John Smith Ford Focus");
            return;
        }

        // Fetch customers and vehicles that match
        List<Vehicle> originalVehicles = findVehicles();
        List<Customer> originalCustomers = findCustomers();

        List<Customer> customers = Stream.concat(
                originalCustomers.stream(),
                originalVehicles.stream().map(Vehicle::getCustomer)
        ).distinct().collect(Collectors.toList());
        List<Vehicle> vehicles = Stream.concat(
                originalVehicles.stream(),
                originalCustomers.stream().flatMap(customer -> customer.getVehicles().stream())
        ).distinct().collect(Collectors.toList());

        // Get all bookings for matching vehicles
        List<Booking> bookings = vehicles.stream().flatMap(vehicle -> vehicle.getBookings().stream()).collect(Collectors.toList());

        // Clear current results
        results.getChildren().clear();

        // Display the new set of results
        customers.forEach(this::addCustomerResult);
        vehicles.forEach(this::addVehicleResult);
        bookings.forEach(this::addBookingResult);

        if(customers.isEmpty() && vehicles.isEmpty() && bookings.isEmpty()) {
            showText("No results found");
        }
    }

    /**
     * Set the current search text, this is called by {@link LayoutController} when the search bar text
     * changes.
     * @param searchText
     */
    public void setSearchText(String searchText) {
        fetchTimeout.stop();

        this.searchText = searchText;
        title.setText("Search results for \"" + searchText + "\"");

        // Fetch timeout causes a small artificial delay in presenting results to avoid spamming results on every keypress
        // It waits for a 300ms pause in typing
        fetchTimeout.play();
    }


    /**
     * Finds all customers that match the current searchText
     * Splits the search text by words then checks for matches in certain fields
     * @return
     */
    private List<Customer> findCustomers() {
        StringBuilder whereQuery = new StringBuilder();
        List<Object> args = new ArrayList<>();
        String[] words = searchText.trim().toUpperCase().split("\\s+");
        String delim = "";
        for(String word : words) {
            String arg = "%" + word + "%";
            whereQuery.append(delim).append(CustomerTable.COLUMN_FIRSTNAME + " LIKE ? OR " + CustomerTable.COLUMN_SURNAME + " LIKE ?");
            args.add(arg);
            args.add(arg);
            delim = " OR ";
        }

        return getApplication().getDatabase().get()
                .objects(Customer.class)
                .withQuery(Query.builder()
                    .table(CustomerTable.TABLE)
                    .where(whereQuery.toString())
                    .whereValues(args))
                .execute()
                .getList();
    }

    /**
     * Finds all vehicles that match the current searchText
     * Splits the search text by words then checks for matches in certain fields
     * @return
     */
    private List<Vehicle> findVehicles() {
        StringBuilder whereQuery = new StringBuilder();
        List<Object> args = new ArrayList<>();
        String[] words = searchText.trim().toUpperCase().split("\\s+");
        String delim = "";
        for(String word : words) {
            String arg = "%" + word + "%";
            whereQuery.append(delim).append(VehicleTable.COLUMN_REGISTRATION + " LIKE ? OR " +
                    "\"" + VehicleTable.COLUMN_COLOUR + "\" || ' ' || \"" + VehicleTable.COLUMN_MAKE + "\" || ' ' || \"" + VehicleTable.COLUMN_MODEL + "\" LIKE ? OR " +
                    VehicleTable.COLUMN_TYPE + " LIKE ?");
            args.add(arg);
            args.add(arg);
            args.add(arg);
            delim = " OR ";
        }

        return getApplication().getDatabase().get()
                .objects(Vehicle.class)
                .withQuery(Query.builder()
                        .table(VehicleTable.TABLE)
                        .where(whereQuery.toString())
                        .whereValues(args))
                .execute()
                .getList();
    }
}
