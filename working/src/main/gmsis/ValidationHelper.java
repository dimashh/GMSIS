package gmsis;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationHelper {

    /**
     * Adds a listener to make sure you can only enter numbers into a TextField
     * @param input
     */
    public static void numbersOnly(TextField input) {
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                input.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * Adds a listener to make sure you can only enter decimals to 2 d.p. into a TextField
     * @param input
     */
    public static void decimalOnly(TextField input) {
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d{0,2}")) {
                input.setText(oldValue);
            }
        });
    }

    /**
     * Adds a listener to make sure you can only enter letters into a TextField
     * @param input
     */
    public static void lettersOnly(TextField input) {
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\D*")) {
                input.setText(newValue.replaceAll("[^\\D]", ""));
            }
        });
    }

    /**
     * Adds a listener to make sure you can only enter uppercase letters
     * @param input
     */
    public static void uppercaseLettersOnly(TextField input) {
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\D*")) {
                String replaced = newValue.replaceAll("[^\\D]", "").toUpperCase();
                if(!replaced.equals(newValue)) input.setText(replaced);
            }
        });
    }

    /**
     * Adds a listener to make sure you can only enter uppercase alphanumeric
     * @param input
     */
    public static void uppercaseAlphaNumericOnly(TextField input) {
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z0-9 ]*")) {
                String replaced = newValue.replaceAll("[^a-zA-Z0-9 ]", "").toUpperCase();
                if(!replaced.equals(newValue)) input.setText(replaced);
            }
        });
    }

    /**
     * Checks if an id is valid
     * @param id
     * @return
     */
    public static ValidationResult isValidId(String id) {
        ValidationResult result = new ValidationResult();
        if(!id.matches("\\d*")) result.addError("Id must be all numbers");
        if(id.length() != 5) result.addError("Id must be 5 digits");
        return result;
    }

    /**
     * Checks if a string is valid text (characters only)
     * @param text
     * @return
     */
    public static ValidationResult isValidText(String text) {
        ValidationResult result = new ValidationResult();
        if(!text.matches("\\D*")) result.addError("Only characters allowed");
        return result;
    }


    public static class ValidationResult {
        private boolean isValid;
        private List<String> errors;

        public ValidationResult(String... args) {
            this.isValid = true;
            this.errors = new ArrayList<>();
            for(String error : args) this.addError(error);
        }

        /**
         * Adds an error and makes the result no longer valid
         * @param error
         */
        public void addError(String error) {
            isValid = false;
            errors.add(error);
        }

        /**
         * Wether the result is valid
         * @return
         */
        public boolean isValid() {
            return isValid;
        }

        /**
         * Show errors in an alert
         */
        public void showErrors() {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(this.toString());

            alert.showAndWait();
        }

        @Override
        public String toString() {
            return errors.stream().map(error -> "- " + error).collect(Collectors.joining("\n"));
        }
    }

}
