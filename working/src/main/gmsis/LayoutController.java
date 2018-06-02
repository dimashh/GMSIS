package gmsis;

import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.utils.MaterialIconFactory;
import gmsis.models.User;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.*;

/**
 * LayoutController is a special type of Controller that is used for layout.fxml. It is used as the base layout
 * for all other layouts which are loaded within the layout.fxml. Therefore common UI items such as Menu bars, tabs
 * and buttons can be shared between UIs for consistency.
 */
public class LayoutController extends BaseController {

    private static final String NAME_TEMPLATE = "Hi, %s %s";

    @FXML private TextField search;
    @FXML private BorderPane mainLayout;
    @FXML private AnchorPane topBar;
    @FXML private BorderPane innerMainLayout;
    @FXML private Text userName;
    @FXML private Button adminButton;
    @FXML private VBox sidebar;

    private String currentUI;
    private ArrayList<MenuItem> menuItems;
    private Stack<String> history;
    private boolean searchWasCleared = false;

    @FXML
    protected void initialize() {
        history = new Stack<>();

        // Bind managedProperty to visible so that when not visible it is also not part of layout calculations
        topBar.managedProperty().bind(topBar.visibleProperty());
        sidebar.managedProperty().bind(sidebar.visibleProperty());
        adminButton.managedProperty().bind(adminButton.visibleProperty());

        // Bind sidebar width to between 210 and 320, depending onscreen size
        sidebar.prefWidthProperty().bind(Bindings.min(280, Bindings.max(210, mainLayout.widthProperty().multiply(0.2))));

        // Keep top bar constrained to window width
        topBar.prefWidthProperty().bind(mainLayout.widthProperty().subtract(sidebar.widthProperty()));

        search.prefWidthProperty().bind(topBar.widthProperty().multiply(0.5));

        // Setup search listeners
        search.textProperty().addListener(event -> {
            if(searchWasCleared && search.textProperty().get().equals("")) return;

            if(!currentUI.equals("searchUI")) getApplication().setUI("searchUI");
            if(getApplication().getUIController() instanceof SearchController) {
                SearchController searchController = (SearchController) getApplication().getUIController();
                searchController.setSearchText(search.textProperty().get());
            }

            searchWasCleared = false;
        });
        search.setOnAction((event) -> {
            if(!currentUI.equals("searchUI")) getApplication().setUI("searchUI");
            if(getApplication().getUIController() instanceof SearchController) {
                SearchController searchController = (SearchController) getApplication().getUIController();
                searchController.setSearchText(search.textProperty().get());
            }

            searchWasCleared = true;
            search.setText("");
        });

        // Add an event handler to detect when there is a click on the main layout and make it request focus
        // This allows users to deselect fields
        mainLayout.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            private boolean dragging = false;

            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    Log.debug("Not dragging");
                    dragging = false;
                } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                    Log.debug("Dragging");
                    dragging = true;
                } else if (event.getEventType() == MouseEvent.MOUSE_CLICKED && !dragging) {
                    Log.debug("Requesting focus on layout");
                    if(event.getClickCount() == 1) mainLayout.requestFocus();
                }
            }
        });

        // Create the sidebar
        createSidebar();

        Log.info("Layout Initialized");
    }

    // Creates the sidebar
    private void createSidebar() {
        menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Overview", MaterialIcon.TIMELINE, "overviewUI"));
        menuItems.add(new MenuItem("Customers", MaterialIcon.PEOPLE, "customerUI", "addCustomerUI", "billView"));
        menuItems.add(new MenuItem("Vehicles", MaterialIcon.DIRECTIONS_CAR, "vehicleUI", "addNewVehicleUI"));
        menuItems.add(new MenuItem("Bookings", MaterialIcon.INSERT_INVITATION, "bookingCalendarUI", "bookingEditUI", "bookingNewUI"));
//        menuItems.add(new MenuItem("Parts", "partUI"));
//        menuItems.add(new MenuItem("Specialist Repairs", "specialistrepairUI"));

        for(final MenuItem menuItem : menuItems) {
            menuItem.getButton().prefWidthProperty().bind(sidebar.prefWidthProperty());
            menuItem.getButton().setOnAction(event -> getApplication().setUI(menuItem.getDefaultUI()));
            sidebar.getChildren().add(menuItem.getButton());
        }
    }

    /**
     * Called when the inner layout is changed by {@link App#setUI}, this is useful to change the base layout depending
     * on the current visible UI
     * @param nextUI The name of the next UI to be shown
     */
    public void changeTo(String nextUI) {
        if(nextUI.equals(currentUI)) return;

        if(currentUI != null && currentUI.equals("searchUI")) {
            // Changing away from search, clear search in top bar
            search.setText("");
        }

        showUI(nextUI);

        history.push(currentUI);

        currentUI = nextUI;
        Log.debug("Layout changed to {0}", nextUI);
    }

    public void changeBack() {
        if(history.size() == 0) return;

        String prevUI = history.pop();

        showUI(prevUI);

        currentUI = prevUI;
        Log.info("Layout changed back to {0}", prevUI);
    }

    public String getPreviousUI() {
        return history.size() == 0 ? null : history.peek();
    }

    private void showUI(String nextUI) {
        for(MenuItem menuItem : menuItems) {
            if(currentUI != null && menuItem.matchesUI(currentUI)) {
                menuItem.getButton().getStyleClass().remove("selected");
            }
            if(menuItem.matchesUI(nextUI)) {
                menuItem.getButton().getStyleClass().add("selected");
            }
        }

        if(nextUI.equals("login")) {
            // Hide menu bar on login
            sidebar.setVisible(false);
            topBar.setVisible(false);
        } else {
            sidebar.setVisible(true);
            topBar.setVisible(true);
        }

        User loggedInUser = getApplication().getUser();

        if(loggedInUser != null) {
            // Show the logged in users name
            userName.setText(String.format(NAME_TEMPLATE, loggedInUser.getFirstname(), loggedInUser.getSurname()));

            // Show admin button if the user is an admin
            adminButton.setVisible(loggedInUser.isAdministrator());
        } else {
            userName.setText("");
            adminButton.setVisible(false);
        }
    }

    public void onAdminButton(ActionEvent actionEvent) {
        getApplication().setUI("adminUI");
    }

    public void onLogoutButton(ActionEvent actionEvent) {
        getApplication().getAuthentication().logout();
    }

    /**
     * Appends the layout root given into the main layout, this is called by {@link App#setUI} to load the UI into the
     * layout container
     * @param innerUi The layout root to load in
     */
    public void attachLayout(Parent innerUi) {
        innerMainLayout.setCenter(innerUi);
    }

    public static class MenuItem {
        private String name;
        private String defaultUI;
        private List<String> otherUI;
        private Button button;

        public MenuItem(String name, MaterialIcon iconType, String defaultUI, String... other) {
            this.otherUI = Arrays.asList(other);
            this.name = name;
            this.defaultUI = defaultUI;

            this.button = new Button(name.toUpperCase());
            if(iconType != null) {
                Text icon = MaterialIconFactory.get().createIcon(iconType, "20px");
                icon.setStyle(icon.getStyle() + "-fx-fill: white;");
                this.button.setGraphic(icon);
            }
            this.button.getStyleClass().add("menuItem");
            this.button.setId("menuItem-" + defaultUI);
        }

        public boolean matchesUI(String ui) {
            return ui.equals(defaultUI) || otherUI.contains(ui);
        }

        public void addUI(String ui) {
            this.otherUI.add(ui);
        }

        public String getName() {
            return name;
        }

        public String getDefaultUI() {
            return defaultUI;
        }

        public Button getButton() {
            return this.button;
        }
    }
}
