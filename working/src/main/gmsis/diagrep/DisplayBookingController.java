package gmsis.diagrep;

import gmsis.BaseController;
import gmsis.ValidationHelper;
import gmsis.database.get.Query;
import gmsis.database.tables.PartTable;
import gmsis.database.tables.SpecialistRepairCentreTable;
import gmsis.diagrep.components.TimeSpinner;
import gmsis.models.*;
import gmsis.models.Booking.BookingType;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;

public class DisplayBookingController extends BaseController {

    @FXML private VBox addRepairItemSection;
    @FXML private VBox spcFields;
    @FXML private CheckBox sendToSpcCheckBox;
    @FXML private ComboBox<SpcChoice> spcComboBox;
    @FXML private TextField spcPriceField;
    @FXML private DatePicker spcDeliveryDate;
    @FXML private DatePicker spcReturnDate;
    @FXML private ComboBox<PartChoice> partComboBox;
    @FXML private Button addItemButton;
    @FXML private ToggleGroup itemRepairToggleGroup;
    @FXML private RadioButton partRepairRadio;
    @FXML private RadioButton newPartRadio;
    @FXML private RadioButton vehicleRadio;
    @FXML private Label mileageLabel;
    @FXML private TextField mileageField;
    @FXML private Text bookingSubHeader;

    @FXML private Button discardButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button completedButton;
    @FXML private Button viewVehicleButton;
    @FXML private Button viewCustomerButton;
    @FXML private ChoiceBox<String> bookingField;
    @FXML private DatePicker dateField;
    @FXML private TimeSpinner timeField;
    @FXML private Spinner<Integer> durationField;
    @FXML private ComboBox<MechanicChoice> mechanicChoiceBox;
    @FXML private TextField customerField;
    @FXML private ChoiceBox<VehicleChoice> vehicleChoiceBox;
    @FXML private VBox secondColumn;
    @FXML private Label mechanicPrice;
    @FXML private Label repairTotalPrice;
    @FXML private Label totalPrice;
    @FXML private TableView<BookingRepair> bookingRepairTable;
    @FXML private TableColumn<BookingRepair, String> partNameCol;
    @FXML private TableColumn<BookingRepair, String> spcCol;
    @FXML private TableColumn<BookingRepair, String> priceCol;

    private Booking selectedBooking = null;
    private Customer selectedCustomer = null;

    private Callback callback = null;
    private boolean isUnsaved = false;

    @FXML
    protected void initialize() {
        // Vehicle registration
        vehicleChoiceBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            viewCustomerButton.setDisable(false);
            viewVehicleButton.setDisable(false);
            setUnsaved(true);
        }));

        // Customer
        List<Customer> customers = getApplication().getCustomerModule().getCustomer();
        TextFields.bindAutoCompletion(
                customerField,
                param -> customers.stream()
                        .filter(customer -> (customer.getFirstname() + " " + customer.getSurname()).toLowerCase().contains(param.getUserText().toLowerCase()))
                        .collect(Collectors.toList()),
                new StringConverter<Customer>() {
                    @Override
                    public String toString(Customer customer) {
                        return customer.getFirstname() + " " + customer.getSurname();
                    }

                    @Override
                    public Customer fromString(String string) {
                        return customers.stream()
                                .filter(customer -> (customer.getFirstname() + " " + customer.getSurname()).toLowerCase().equals(string.toLowerCase()))
                                .findFirst()
                                .orElse(null);
                    }
                }
        );
        customerField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                Optional<Customer> customerMatch = customers.stream()
                        .filter(customer -> (customer.getFirstname() + " " + customer.getSurname()).toLowerCase().equals(customerField.getText().toLowerCase()))
                        .findFirst();
                if(customerMatch.isPresent()) {
                    // if exists, set selected
                    setCustomer(customerMatch.get());
                } else {
                    // revert to last selected
                    if(selectedCustomer != null) customerField.setText(selectedCustomer.getFirstname() + " " + selectedCustomer.getSurname());
                }
            }
        });

        // Booking type
        for(BookingType type : BookingType.values()) {
            bookingField.getItems().add(type.getName());
        }
        bookingField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setUnsaved(true));

        // Booking start
        dateField.valueProperty().addListener((observable, oldValue, newValue) -> setUnsaved(true));
        timeField.valueProperty().addListener((observable, oldValue, newValue) -> setUnsaved(true));

        // Booking duration
        durationField.setValueFactory(new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int steps) {
                this.setValue(Math.max(this.getValue() - 15 * steps, 0));
            }

            @Override
            public void increment(int steps) {
                this.setValue(this.getValue() + 15 * steps);
            }
        });
        durationField.valueProperty().addListener((observable, oldValue, newValue) -> setUnsaved(true));
        durationField.getValueFactory().setValue(30); // Default

        // Mechanics
        List<User> mechanics = getApplication().getDiagRepModule().getMechanics();
        mechanicChoiceBox.getItems().clear();
        for(User mechanic : mechanics) {
            mechanicChoiceBox.getItems().add(new MechanicChoice(mechanic));
        }
        mechanicChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setUnsaved(true));
        mechanicChoiceBox.setPromptText("Unassigned");

        // Booking Repair table
        bookingRepairTable.setPlaceholder(new Label("No repairs"));
        bookingRepairTable.prefHeightProperty().bind(secondColumn.heightProperty().multiply(0.4));

        partNameCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().getRepairType() == BookingRepair.RepairType.REPAIR_VEHICLE ? "Vehicle" : param.getValue().getPart().getName());
        });
        //partNameCol.prefWidthProperty().bind(bookingRepairTable.widthProperty().multiply(0.24));
        spcCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().isSpc() ? param.getValue().getSpc().getName() : "N/A");
        });
        //spcCol.prefWidthProperty().bind(bookingRepairTable.widthProperty().multiply(0.49));
        priceCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(String.format("£%.2f", param.getValue().getPrice()));
        });
        //priceCol.prefWidthProperty().bind(bookingRepairTable.widthProperty().multiply(0.24));

        // Booking Repair add form
        sendToSpcCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            spcComboBox.setDisable(!newValue);
            spcPriceField.setDisable(!newValue);
            spcDeliveryDate.setDisable(!newValue);
            spcReturnDate.setDisable(!newValue);
        });
        spcComboBox.setDisable(true);
        spcPriceField.setDisable(true);
        spcDeliveryDate.setDisable(true);
        spcReturnDate.setDisable(true);

        spcPriceField.setPromptText("Price");
        ValidationHelper.decimalOnly(spcPriceField);

        partRepairRadio.setUserData("partRepair");
        newPartRadio.setUserData("newPart");
        vehicleRadio.setUserData("vehicle");
        itemRepairToggleGroup = partRepairRadio.getToggleGroup();
        itemRepairToggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue == null) return;

            if(newValue.getUserData().equals("partRepair")) {
                setSpcEnable(true);
                showRepairPartList();
            } else if(newValue.getUserData().equals("newPart")) {
                setSpcEnable(false);
                showNewPartList();
            } else if(newValue.getUserData().equals("vehicle")) {
                hidePartList();
                setSpcEnable(true);
                sendToSpcCheckBox.setSelected(true);
                sendToSpcCheckBox.setDisable(true);
            }
        }));
        itemRepairToggleGroup.selectToggle(newPartRadio);

        partComboBox.setPromptText("Select a part");
        partComboBox.managedProperty().bind(partComboBox.visibleProperty());

        spcComboBox.setPromptText("Select a Specialist");
        spcComboBox.getItems().clear();
        List<SpecialistRepairCentre> spcs = getApplication().getDatabase().get()
                .objects(SpecialistRepairCentre.class)
                .withQuery(Query.builder()
                    .table(SpecialistRepairCentreTable.TABLE)
                )
                .execute()
                .getList();
        spcs.forEach((spc) -> {
            spcComboBox.getItems().add(new SpcChoice(spc));
        });

        // Setup buttons
        viewCustomerButton.setOnAction(event -> getApplication().setUI("customerUI", selectedBooking.getCustomer()));
        viewVehicleButton.setOnAction(event -> getApplication().setUI("vehicleUI", selectedBooking.getVehicle()));

        // Mileage
        mileageField.textProperty().addListener((observable, oldValue, newValue) -> setUnsaved(true));
        ValidationHelper.numbersOnly(mileageField);

        // View
        setEditEnabled(false);
        mileageField.setVisible(false);
        mileageLabel.setVisible(false);
        completedButton.setVisible(false);
        completedButton.managedProperty().bind(completedButton.visibleProperty());
        setUnsaved(false);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * Sets the selected customer, updates vehicle choices to be for that customer
     * @param customer
     */
    public void setCustomer(Customer customer) {
        selectedCustomer = customer;
        customerField.setText(customer.getFirstname() + " " + customer.getSurname());

        vehicleChoiceBox.getItems().clear();
        customer.getVehicles().forEach((vehicle -> {
            VehicleChoice vehicleChoice = new VehicleChoice(vehicle);
            vehicleChoiceBox.getItems().add(vehicleChoice);
        }));
        vehicleChoiceBox.getSelectionModel().clearSelection();
    }

    /**
     * Set the selected vehicle, and it's customer
     * @param vehicle
     */
    public void setVehicle(Vehicle vehicle) {
        // Set the customer and available choice of vehicles
        setCustomer(vehicle.getCustomer());

        // Select the vehicle
        vehicleChoiceBox.getItems().forEach(vehicleChoice -> {
            if(vehicleChoice.getVehicle().equals(vehicle)) vehicleChoiceBox.getSelectionModel().select(vehicleChoice);
        });
    }

    /**
     * Sets the mechanic displaying in the field
     * @param mechanic
     */
    public void setMechanic(User mechanic) {
        for(MechanicChoice c : mechanicChoiceBox.getItems()) {
            if(c.getMechanic().equals(mechanic)) {
                mechanicChoiceBox.getSelectionModel().select(c);
                return;
            }
        }
        mechanicChoiceBox.getSelectionModel().clearSelection();
    }

    public void setDates(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Display booking date
        ZonedDateTime bookingStartDate = startDateTime.atZone(ZoneId.systemDefault());
        dateField.setValue(bookingStartDate.toLocalDate());
        // Display booking time
        timeField.getValueFactory().setValue(bookingStartDate.toLocalTime());

        // Display booking duration
        ZonedDateTime bookingEndDate = endDateTime.atZone(ZoneId.systemDefault());
        durationField.getValueFactory().setValue((int) ChronoUnit.MINUTES.between(bookingStartDate, bookingEndDate));
    }

    public void setShowDelete(boolean showDelete) {
        deleteButton.setDisable(!showDelete);
        deleteButton.setManaged(showDelete);
    }

    public void setBookingRepairs(List<BookingRepair> bookingRepairs) {
        bookingRepairTable.getItems().clear();
        bookingRepairTable.getItems().addAll(bookingRepairs);
    }

    public void showRepairPartList() {
        partComboBox.setVisible(true);
        partComboBox.getItems().clear();

        if(vehicleChoiceBox.getSelectionModel().isEmpty()) return;

        vehicleChoiceBox.getSelectionModel().getSelectedItem().getVehicle().getPartsInstalled().forEach((part -> {
            partComboBox.getItems().add(new PartChoice(part));
        }));
    }

    public void showNewPartList() {
        partComboBox.setVisible(true);
        partComboBox.getItems().clear();
        List<Part> availableParts = getApplication().getDatabase().get()
                .objects(Part.class)
                .withQuery(Query.builder()
                    .table(PartTable.TABLE)
                    .where("dateInstalled IS NULL")
                )
                .execute()
                .getList();
        availableParts.forEach((part -> {
            partComboBox.getItems().add(new PartChoice(part));
        }));
    }

    public void hidePartList() {
        partComboBox.setVisible(false);
    }

    public void setSpcEnable(boolean enable) {
        sendToSpcCheckBox.setDisable(!enable);
        spcFields.setDisable(!enable);
    }

    /**
     * Enables and disables the save changes/discard changes button
     * @param hasUnsavedChanges
     */
    public void setUnsaved(boolean hasUnsavedChanges) {
        isUnsaved = hasUnsavedChanges;
        discardButton.setDisable(!hasUnsavedChanges);
        saveButton.setDisable(!hasUnsavedChanges);
    }

    /**
     * Whether to allow editing in the main form
     * @param enabled
     */
    public void setEditEnabled(boolean enabled) {
        bookingField.setDisable(!enabled);
        dateField.setDisable(!enabled);
        timeField.setDisable(!enabled);
        durationField.setDisable(!enabled);
        mechanicChoiceBox.setDisable(!enabled);
        vehicleChoiceBox.setDisable(!enabled);
        customerField.setDisable(!enabled);
    }

    public void hideViewButtons() {
        viewCustomerButton.setVisible(false);
        viewVehicleButton.setVisible(false);
    }

    private void updateBill() {
        LocalDateTime bookingStartDate = LocalDateTime.of(dateField.getValue(), timeField.getValue());
        int durationInMinutes = durationField.getValueFactory().getValue();
        LocalDateTime bookingEndDate = bookingStartDate.plusMinutes(durationInMinutes);

        // Bill
        double repairTotal = bookingRepairTable.getItems().stream().mapToDouble(BookingRepair::getPrice).sum();
        double mechanicTotalHours = Math.max(ChronoUnit.MINUTES.between(bookingStartDate, bookingEndDate) / 60.0, 0);
        double mechanicTotal = mechanicTotalHours * (mechanicChoiceBox.getSelectionModel().isEmpty() ? 0.0 : mechanicChoiceBox.getSelectionModel().getSelectedItem().getMechanic().getHourlyFee());
        mechanicPrice.setText(String.format("£%.2f", mechanicTotal));
        repairTotalPrice.setText(String.format("£%.2f", repairTotal));
        totalPrice.setText(String.format("£%.2f", mechanicTotal + repairTotal));
    }

    private void displayBookingRepairs(Booking booking) {
        setBookingRepairs(booking.getBookingRepairs());
        updateBill();
    }

    /**
     * Loads a booking into the form fields
     * @param booking
     */
    public void showBooking(Booking booking) {
        selectedBooking = booking;

        // Display booking type
        bookingField.getSelectionModel().select(booking.getType().getName());

        // Show dates
        LocalDateTime startDate = LocalDateTime.ofInstant(booking.getDateStart().toInstant(), ZoneId.systemDefault());
        LocalDateTime endDate = LocalDateTime.ofInstant(booking.getDateEnd().toInstant(), ZoneId.systemDefault());
        setDates(startDate, endDate);

        // Display mechanic
        setMechanic(booking.getMechanic());

        // Display Booking Repair parts
        displayBookingRepairs(booking);

        // Display vehicle
        setVehicle(booking.getVehicle());

        // Enable view customer/vehicle
        viewCustomerButton.setDisable(false);
        viewVehicleButton.setDisable(false);

        setUnsaved(false);

        Date now = new Date();
        if(booking.getDateStart().before(now)) {
            // After booking start
            if(booking.getDateEnd().after(now)) {
                bookingSubHeader.setText("Booking - In progress");
            }
            // Allow editing mileage
            mileageField.setText(Integer.toString(booking.getVehicle().getCurrentMileage()));
            mileageField.setVisible(true);
            mileageLabel.setVisible(true);
            completedButton.setVisible(true);
            saveButton.setVisible(false);
            discardButton.setVisible(false);
            deleteButton.setVisible(false);

            // Disable completion if the bill is already created (means the booking is completed)
            completedButton.setDisable(booking.getBill() != null);
            mileageField.setDisable(booking.getBill() != null);
            addRepairItemSection.setDisable(booking.getBill() != null);


            setEditEnabled(false);
        } else {
            // Before booking start
            mileageField.setVisible(false);
            mileageLabel.setVisible(false);
            completedButton.setVisible(false);
            saveButton.setVisible(true);
            discardButton.setVisible(true);
            deleteButton.setVisible(true);

            setEditEnabled(true);
        }
    }

    private void saveBooking() {
        if(dateField.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Failed to create booking");
            alert.setContentText("- Missing a date value");
            alert.show();
            return;
        }

        // Validate input first
        LocalDateTime bookingStartDate = LocalDateTime.of(dateField.getValue(), timeField.getValue());
        int durationInMinutes = durationField.getValueFactory().getValue();
        LocalDateTime bookingEndDate = bookingStartDate.plusMinutes(durationInMinutes);

        Date startDate = Date.from(bookingStartDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(bookingEndDate.atZone(ZoneId.systemDefault()).toInstant());

        StringBuilder errors = new StringBuilder();
        boolean hasErrors = false;
        if(!dateField.isDisabled() && bookingStartDate.isBefore(LocalDateTime.now())) {
            errors.append("- Booking start date cannot be in the past").append("\n");
            hasErrors = true;
        }

        if(mechanicChoiceBox.getSelectionModel().isEmpty()) {
            errors.append("- Please select a mechanic");
            hasErrors = true;
        }

        if(!timeField.isDisabled() && !getApplication().getDiagRepModule().isValidBooking(
                bookingStartDate,
                bookingEndDate,
                mechanicChoiceBox.getValue() == null ? null : mechanicChoiceBox.getValue().getMechanic(),
                selectedBooking == null ? null : selectedBooking.getId())) {
            errors.append("- The times selected are not available").append("\n");
            hasErrors = true;
        }

        if(vehicleChoiceBox.getSelectionModel().isEmpty()) {
            errors.append("- Please select a customer and vehicle");
            hasErrors = true;
        }

        if(mileageField.getText() != null && !mileageField.getText().isEmpty()) {
            int currentMileage = Integer.parseInt(mileageField.getText());
            if(currentMileage < selectedBooking.getVehicle().getCurrentMileage()) {
                errors.append("- Mileage cannot be less than the previous value");
                hasErrors = true;
            }
        }

        if(hasErrors) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Failed to save changes");
            alert.setContentText(errors.toString());
            alert.show();
            return;
        }

        if(selectedBooking == null) {
            Booking booking = new Booking(
                    mechanicChoiceBox.getValue().getMechanic(),
                    Booking.BookingType.fromName(bookingField.getSelectionModel().getSelectedItem()),
                    null,
                    startDate,
                    endDate
            );

            Vehicle newVehicle = vehicleChoiceBox.getSelectionModel().getSelectedItem().getVehicle();
            newVehicle.addBooking(booking);
            getApplication().getDatabase().put().objects(newVehicle).execute();
        } else {
            // Save mechanic
            if(!mechanicChoiceBox.getSelectionModel().isEmpty()) selectedBooking.setMechanic(mechanicChoiceBox.getSelectionModel().getSelectedItem().getMechanic());
            // Save booking type
            selectedBooking.setType(BookingType.fromName(bookingField.getSelectionModel().getSelectedItem()));
            // Save booking date
            selectedBooking.setDateStart(startDate);
            // Save booking end
            selectedBooking.setDateEnd(endDate);

            // We're in completed mode, create a bill
            if(completedButton.isVisible() && selectedBooking.getBill() == null) {
                double repairTotal = bookingRepairTable.getItems().stream().mapToDouble(BookingRepair::getPrice).sum();
                double mechanicTotalHours = ChronoUnit.MINUTES.between(bookingStartDate, bookingEndDate) / 60.0;
                double mechanicTotal = mechanicTotalHours * selectedBooking.getMechanic().getHourlyFee();
                selectedBooking.setBill(new Bill(false, new Date(), null, repairTotal + mechanicTotal));

                // Save current mileage
                if(mileageField.getText() != null  && !mileageField.getText().isEmpty()) {
                    int currentMileage = Integer.parseInt(mileageField.getText());
                    selectedBooking.getVehicle().setCurrentMileage(currentMileage);
                }

                completedButton.setDisable(true);
                mileageField.setDisable(true);
                addRepairItemSection.setDisable(true);
            }

            if(!selectedBooking.getVehicle().getId().equals(vehicleChoiceBox.getSelectionModel().getSelectedItem().getVehicle().getId())) { // If changed
                // Add to new vehicle, it will update the same booking to belong to a new vehicle
                // No need to delete the booking first
                selectedBooking.getVehicle().removeBooking(selectedBooking);
                Vehicle newVehicle = vehicleChoiceBox.getSelectionModel().getSelectedItem().getVehicle();
                newVehicle.addBooking(selectedBooking);
                getApplication().getDatabase().put().objects(newVehicle).execute();
            } else {
                // Save booking
                getApplication().getDatabase().put().objects(selectedBooking).execute();
            }
        }

        setUnsaved(false);
        if(callback != null) callback.onSave(selectedBooking);
    }

    public void onDiscard(ActionEvent actionEvent) {
        bookingField.getSelectionModel().clearSelection();
        //dateField.
        //timeField.
        //durationField
        mechanicChoiceBox.getSelectionModel().clearSelection();
        vehicleChoiceBox.getSelectionModel().clearSelection();
        customerField.setText("");
        selectedCustomer = null;
    }

    public void onSave(ActionEvent actionEvent) {
        saveBooking();
    }

    public void onDelete(ActionEvent actionEvent) {
        if(callback != null) callback.onDeleted(selectedBooking);
    }

    public void onAddItem(ActionEvent actionEvent) {
        BookingRepair.RepairType repairType = null;
        if(itemRepairToggleGroup.getSelectedToggle().getUserData().equals("vehicle")) {
            repairType = BookingRepair.RepairType.REPAIR_VEHICLE;
        } else if(itemRepairToggleGroup.getSelectedToggle().getUserData().equals("newPart")) {
            repairType = BookingRepair.RepairType.NEW_PART;
        } else if(itemRepairToggleGroup.getSelectedToggle().getUserData().equals("partRepair")) {
            repairType = BookingRepair.RepairType.REPAIR_PART;
        }

        Part part = partComboBox.getSelectionModel().isEmpty() ? null : partComboBox.getSelectionModel().getSelectedItem().getPart();
        SpecialistRepairCentre spc = spcComboBox.getSelectionModel().isEmpty() ? null : spcComboBox.getSelectionModel().getSelectedItem().getSpc();
        Double spcPrice = spcPriceField.getText().isEmpty() ? null : Double.parseDouble(spcPriceField.getText());
        LocalDateTime spcDelivery = spcDeliveryDate.getValue() == null ? null : LocalDateTime.of(spcDeliveryDate.getValue(), LocalTime.MIDNIGHT);
        LocalDateTime spcReturn = spcReturnDate.getValue() == null ? null : LocalDateTime.of(spcReturnDate.getValue(), LocalTime.MIDNIGHT);

        if((repairType == BookingRepair.RepairType.NEW_PART || repairType == BookingRepair.RepairType.REPAIR_PART)) {
            // Check part validity
            if (part == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Failed to add item");
                alert.setContentText("- Please select a part");
                alert.show();
                return;
            }

            for(BookingRepair bookingRepair : selectedBooking.getBookingRepairs()) {
                if(bookingRepair.getPart().getName().equalsIgnoreCase(part.getName())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Failed to add item");
                    alert.setContentText("- Cannot add a part of the same type");
                    alert.show();
                    return;
                }
            }

            if(repairType == BookingRepair.RepairType.REPAIR_PART && sendToSpcCheckBox.isSelected()) {

                if(spc == null || spcPrice == null || spcDelivery == null || spcReturn == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Failed to add item");
                    alert.setContentText("- Please fill in all the spc fields");
                    alert.show();
                    return;
                }

                // Send repair part to spc
                BookingRepair bookingRepair = new BookingRepair(repairType, part, spc, spcPrice,
                        Date.from(spcDelivery.atZone(ZoneId.systemDefault()).toInstant()),
                        Date.from(spcReturn.atZone(ZoneId.systemDefault()).toInstant()));
                selectedBooking.getBookingRepairs().add(bookingRepair);
            } else {
                // Add repair part or new part without spc
                BookingRepair bookingRepair = new BookingRepair(repairType, part, null, null, null, null);
                selectedBooking.getBookingRepairs().add(bookingRepair);
            }

        } else if(repairType == BookingRepair.RepairType.REPAIR_VEHICLE) {
            if(spc == null || spcPrice == null || spcDelivery == null || spcReturn == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Failed to add item");
                alert.setContentText("- Please fill in all the spc fields");
                alert.show();
                return;
            }

            BookingRepair bookingRepair = new BookingRepair(repairType, null, spc, spcPrice,
                    Date.from(spcDelivery.atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(spcReturn.atZone(ZoneId.systemDefault()).toInstant()));
            selectedBooking.getBookingRepairs().add(bookingRepair);
        }

        // Save
        getApplication().getDatabase().put().objects(selectedBooking).execute();

        // update display and set unsaved
        displayBookingRepairs(selectedBooking);
    }

    public void onDeleteItem(ActionEvent actionEvent) {

        if(bookingRepairTable.getSelectionModel().isEmpty()) return;

        selectedBooking.getBookingRepairs().remove(bookingRepairTable.getSelectionModel().getSelectedItem());

        // Delete
        getApplication().getDatabase().delete().objects(bookingRepairTable.getSelectionModel().getSelectedItem()).execute();

        // update display
        displayBookingRepairs(selectedBooking);
    }

    public boolean isUnsaved() {
        return isUnsaved;
    }

    public class MechanicChoice {
        private User mechanic;

        public MechanicChoice(User mechanic) {
            this.mechanic = mechanic;
        }

        @Override
        public String toString() {
            return mechanic.getFirstname();
        }

        public User getMechanic() {
            return mechanic;
        }
    }

    public class VehicleChoice {
        private Vehicle vehicle;

        public VehicleChoice(Vehicle vehicle) {
            this.vehicle = vehicle;
        }

        @Override
        public String toString() {
            return vehicle.getRegistration() + " - " + vehicle.getColour() + " " + vehicle.getMake() + " " + vehicle.getModel();
        }

        public Vehicle getVehicle() {
            return vehicle;
        }
    }

    public class PartChoice {
        private Part part;

        public PartChoice(Part part) {
            this.part = part;
        }

        @Override
        public String toString() {
            return part.getName();
        }

        public Part getPart() {
            return part;
        }
    }

    public class SpcChoice {
        private SpecialistRepairCentre spc;

        public SpcChoice(SpecialistRepairCentre spc) {
            this.spc = spc;
        }

        @Override
        public String toString() {
            return spc.getName();
        }

        public SpecialistRepairCentre getSpc() {
            return spc;
        }
    }

    public interface Callback {
        void onDeleted(Booking booking);

        void onSave(Booking booking);
    }

}
