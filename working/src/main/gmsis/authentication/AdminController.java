package gmsis.authentication;

import gmsis.BaseController;
import gmsis.ValidationHelper;
import gmsis.database.get.Query;
import gmsis.models.User;
import gmsis.database.tables.UserTable;

import java.util.List;
import java.util.Objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;


/**
 * The controller for AdminUI.fxml
 */
public class AdminController extends BaseController {

    @FXML private TextField txtID;
    @FXML private TextField txtName;
    @FXML private TextField txtSurname;
    @FXML private TextField txtPassword;
    @FXML private TextField txtWage;
    @FXML private Label wl;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> ID;
    @FXML private TableColumn<User, String> FirstName;
    @FXML private TableColumn<User, String> Surname;
    @FXML private TableColumn<User, String> Password;
    @FXML private TableColumn<User, String> Role;
    @FXML private TableColumn<User, String> Wage;
    @FXML private ComboBox<String> comboBox;

    private List<User> users;

    @FXML
    protected void initialize() {
        if (userTable != null) {
            // Initialize user table
            refresh();

            ID.setCellValueFactory(c -> new SimpleStringProperty(String.format("%05d", c.getValue().getId())));
            ID.setCellFactory(TextFieldTableCell.forTableColumn());
            ID.setOnEditCommit(t -> {
                ValidationHelper.ValidationResult result = ValidationHelper.isValidId(t.getNewValue());
                if(!result.isValid()) {
                    result.showErrors();
                    t.getTableView().getColumns().get(0).setVisible(false);
                    t.getTableView().getColumns().get(0).setVisible(true);
                    return;
                }

                User user = (t.getTableView().getItems().get(t.getTablePosition().getRow()));
                user.setId(Integer.parseInt(t.getNewValue()));
                saveUser(user);
            });


            FirstName.setCellValueFactory(new PropertyValueFactory<>("Firstname"));
            FirstName.setCellFactory(TextFieldTableCell.forTableColumn());
            FirstName.setOnEditCommit(t -> {
                ValidationHelper.ValidationResult result = ValidationHelper.isValidText(t.getNewValue());
                if(!result.isValid()) {
                    result.showErrors();
                    t.getTableView().getColumns().get(0).setVisible(false);
                    t.getTableView().getColumns().get(0).setVisible(true);
                    return;
                }

                User user = (t.getTableView().getItems().get(t.getTablePosition().getRow()));
                user.setFirstname(t.getNewValue());
                saveUser(user);
            });

            Surname.setCellValueFactory(new PropertyValueFactory<>("Surname"));
            Surname.setCellFactory(TextFieldTableCell.forTableColumn());
            Surname.setOnEditCommit(t -> {
                ValidationHelper.ValidationResult result = ValidationHelper.isValidText(t.getNewValue());
                if(!result.isValid()) {
                    result.showErrors();
                    t.getTableView().getColumns().get(0).setVisible(false);
                    t.getTableView().getColumns().get(0).setVisible(true);
                    return;
                }

                User user = (t.getTableView().getItems().get(t.getTablePosition().getRow()));
                user.setSurname(t.getNewValue());
                saveUser(user);
            });

            Password.setCellValueFactory(new PropertyValueFactory<>("Password"));
            Password.setCellFactory(TextFieldTableCell.forTableColumn());
            Password.setOnEditCommit(t -> {
                User user = (t.getTableView().getItems().get(t.getTablePosition().getRow()));
                user.setPassword(t.getNewValue());
                saveUser(user);
            });

            Wage.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHourlyFee() == null ? "N/A" : Double.toString(c.getValue().getHourlyFee())));
            Wage.setCellFactory(TextFieldTableCell.<User>forTableColumn());
            Wage.setOnEditCommit(t -> {
                User user = (t.getTableView().getItems().get(t.getTablePosition().getRow()));

                if (user.getUserRole() == User.UserRole.ADMINISTRATOR || user.getUserRole() == User.UserRole.USER) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Not Allowed");
                    alert.showAndWait();
                    t.getTableView().getColumns().get(0).setVisible(false);
                    t.getTableView().getColumns().get(0).setVisible(true);

                   

                    return;
                }

                if(!t.getNewValue().matches("\\d+\\.\\d+")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("- Must be a decimal number\n");
                    alert.showAndWait();
                    t.getTableView().getColumns().get(0).setVisible(false);
                    t.getTableView().getColumns().get(0).setVisible(true);

                   

                    return;
                }

                user.setHourlyFee(Double.parseDouble(t.getNewValue()));
                saveUser(user);
            });


            Role.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUserRole().getName()));
            Role.setCellFactory(TextFieldTableCell.forTableColumn());
            Role.setOnEditCommit(t -> {
                User user = (t.getTableView().getItems().get(t.getTablePosition().getRow()));
                User.UserRole newRole = User.UserRole.fromName(t.getNewValue());

                if(newRole == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("- Must be a valid role (Administrator, User, Mechanic)\n");
                    alert.showAndWait();       
                    t.getTableView().getColumns().get(0).setVisible(false);
                    t.getTableView().getColumns().get(0).setVisible(true);

                    alert.showAndWait();

                    return;
                }

                user.setRole(newRole);
                saveUser(user);
            });
        } else {
            // Initiaise Add User
            txtWage.setVisible(false);
            wl.setVisible(false);

            // Add user roles to combobox
            for(User.UserRole role : User.UserRole.values()) {
                comboBox.getItems().add(role.getName());
            }
            comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals("Mechanic")) {
                    txtWage.setVisible(true);
                    wl.setVisible(true);
                } else {
                    txtWage.setVisible(false);
                    wl.setVisible(false);
                }
            });

            // Validate inputs
            ValidationHelper.numbersOnly(txtID);
            ValidationHelper.numbersOnly(txtWage);
            ValidationHelper.lettersOnly(txtName);
            ValidationHelper.lettersOnly(txtSurname);
        }
    }

    private void refresh() {
        userTable.getItems().clear();

        users = getUsers();
        users.forEach((user) -> userTable.getItems().add(user));
    }


    @FXML
    protected void onDelete(ActionEvent event) {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if(selectedUser != null && Objects.equals(getApplication().getAuthentication().getUser().getId(), selectedUser.getId())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Cannot delete yourself");

            alert.showAndWait();
            return;
        }

        if (selectedUser != null) {
            deleteUser(selectedUser);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user");

            alert.showAndWait();
        }
        
        
        refresh();
    }

    @FXML
    protected void onAdd(ActionEvent event) {
        getApplication().setUI("addUser");
    }

    @FXML
    protected void onCancel(ActionEvent event) {
        getApplication().setUI("adminUI");
    }

    @FXML
    protected void addUser(ActionEvent event) {
        if (txtID.getText().equals("")
                || txtName.getText().equals("")
                || txtPassword.getText().equals("")
                || (txtWage.isVisible() && txtWage.getText().equals(""))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all the fields");

            alert.showAndWait();
            return;
        }

        if (txtID.getText().length() != 5) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("ID can only have 5 digits");

            alert.showAndWait();
            return;
        }

        User.UserRole roleType = User.UserRole.fromName(comboBox.getSelectionModel().getSelectedItem());

        if (roleType == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Enter a valid role (Administrator, User, Mechanic)");

            alert.showAndWait();

            return;
        }

        int id = Integer.parseInt(txtID.getText());

        boolean userExists = !getApplication().getDatabase()
                .get()
                .objects(User.class)
                .withQuery(Query.builder()
                    .table(UserTable.TABLE)
                    .where(UserTable.COLUMN_ID + " = ?")
                    .whereValues(id))
                .execute()
                .getList()
                .isEmpty();

        if(userExists) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("The user ID already exists");

            alert.showAndWait();

            return;
        }

        Double wage = txtWage.isVisible() ? Double.parseDouble(txtWage.getText()) : null;

        User newUser = new User(id, txtName.getText(), txtSurname.getText(), txtPassword.getText(), roleType, wage);
        saveUser(newUser);
        getApplication().setUI("adminUI");
    }


    private List<User> getUsers() {
        return getApplication().getDatabase().get()
                .objects(User.class)
                .withQuery(Query.builder()
                        .table(UserTable.TABLE)
                )
                .execute()
                .getList();
    }

    private void saveUser(User user) {
        getApplication().getDatabase().put()
                .objects(user)
                .execute();
    }

    private void deleteUser(User user) {
        getApplication().getDatabase().delete()
                .objects(user)
                .execute();
    }


}
