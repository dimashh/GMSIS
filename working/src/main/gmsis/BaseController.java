package gmsis;

import javafx.collections.ObservableMap;
import javafx.stage.Stage;

/**
 * This is the BaseController that all controllers should extend, it is used as the base class by the {@link App},
 * it is used to automatically inject references to the application and stage when instantiated by {@link App#setUI}
 */
public abstract class BaseController {

    // Instance of the main Application
    private App application;
    // The stage the UI is loaded in
    private Stage stage;
    private ObservableMap<String, Object> namespace;
    private String uiName;

    /**
     * Internal use only, it sets the application instance for use in the controller
     * @param instance
     */
    public void setApplication(App instance) {
        this.application = instance;
    }

    /**
     * Returns the reference to the application for access to other dependencies and modules
     * @return The application instance
     */
    public App getApplication() {
        return application;
    }

    /**
     * Internal use only, it sets the stage for use in the controller
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUiName(String uiName) {
        this.uiName = uiName;
    }

    public String getUiName() {
        return uiName;
    }

    /**
     * This is an optional method which may be overriden, it is called when this UI is going to be hidden
     */
    public boolean hide() {
        return true; // return false to cancel event
    }

    /**
     * This is an optional method which may be overriden, it is called when this UI is shown
     */
    public void show() {
        // Empty, to be overriden
    }

    public void setNamespace(ObservableMap<String, Object> namespace) {
        this.namespace = namespace;
    }

    public ObservableMap<String, Object> getNamespace() {
        return namespace;
    }
}
