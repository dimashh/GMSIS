package gmsis;

import gmsis.models.Booking;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ListHelper {

    public static Node createListItem(String headerText, String subheaderText, String actionButtonText, EventHandler<ActionEvent> buttonEventHandler) {
        HBox container = new HBox();
        container.getStyleClass().add("list-item");

        VBox content = new VBox();
        Text header = new Text(headerText);
        Text subHeader = new Text(subheaderText);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(header, subHeader);

        if(actionButtonText != null && !actionButtonText.isEmpty()) {
            Button action = new Button(actionButtonText);
            action.setOnAction(buttonEventHandler);
            action.setAlignment(Pos.CENTER_RIGHT);
            container.getChildren().addAll(content, action);
        }

        HBox.setHgrow(content, Priority.ALWAYS);
        return container;
    }

    public static Node createListItem(String headerText, String subheaderText) {
        return createListItem(headerText, subheaderText, null, null);
    }

}
