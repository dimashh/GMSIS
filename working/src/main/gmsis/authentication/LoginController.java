package gmsis.authentication;

import gmsis.BaseController;
import gmsis.Log;
import gmsis.ValidationHelper;
import gmsis.models.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class LoginController extends BaseController {

    @FXML
    private Text text;
    @FXML
    private TextField ID;
    @FXML
    private PasswordField Password;

    @FXML
    protected void initialize() {
        ValidationHelper.numbersOnly(ID);

        Platform.runLater(() -> ID.requestFocus());

    }

    @FXML
    protected void loginAction(ActionEvent event) throws IOException {
        String id = ID.getText().trim();
        String password = Password.getText().trim();

        if (!id.isEmpty() && !password.isEmpty()) {
            Integer parsedId;
            try {
                parsedId = Integer.parseInt(id);
            } catch(NumberFormatException e) {
                // Not a number, should not happen
                return;
            }

            User user = getApplication().getAuthentication().login(parsedId, password);

            if (user != null) {
                getApplication().setUI("overviewUI");
                Log.info("User {0} logged in", user.getFirstname());
                return;
            }
        }

        // Show incorrect text
        text.setVisible(true);
        // and clear password
        Password.setText("");
    }

    @FXML
    protected void resetAction(ActionEvent event) {
        Password.setText("");
        ID.setText("");
        text.setVisible(false);
    }

}
