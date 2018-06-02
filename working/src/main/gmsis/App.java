package gmsis;

import gmsis.authentication.Authentication;
import gmsis.customers.CustomerModule;
import gmsis.database.Database;
import gmsis.database.resolvers.*;
import gmsis.diagrep.DiagRepModule;
import gmsis.models.*;
import gmsis.parts.PartsModule;
import gmsis.specialist.SpecialistModule;
import gmsis.vehicles.VehicleModule;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class App extends Application {

    private static boolean DEBUG = false;

    // Modules
    private DiagRepModule diagRepModule;
    private CustomerModule customerModule;
    private PartsModule partsModule;
    private VehicleModule vehicleModule;
    private SpecialistModule specialistModule;
    private Database database;
    private Authentication authentication;
    
    // The UI layout controller
    private BaseController controller;
    // The layout controller
    private LayoutController layoutController;

    // Contains the main layout
    private Pane rootLayout;
    // The main stage
    private Stage stage;

    /**
     * App Constructor
     * This is the main constructor of the App and creates all dependencies necessary for the app.
     */
    public App() {
        if(DEBUG) Log.setLevel(Log.DEBUG);
        else Log.setLevel(Log.NONE);

        initialize();
    }

    /**
     * Called from start() method, initializes app
     */
    private void initialize() {
        database = new Database();
        authentication = new Authentication(this);

        // Register database models
        database.registerModel(Address.class, new AddressResolver());
        database.registerModel(Bill.class, new BillResolver());
        database.registerModel(User.class, new UserResolver());
        database.registerModel(Booking.class, new BookingResolver());
        database.registerModel(Customer.class, new CustomerResolver());
        database.registerModel(Part.class, new PartResolver());
        database.registerModel(Vehicle.class, new VehicleResolver());
        database.registerModel(Warranty.class, new WarrantyResolver());
        database.registerModel(SpecialistRepairCentre.class, new SpecialistRepairCentreResolver());
        database.registerModel(BookingRepair.class, new BookingRepairResolver());

        // Initialize database
        database.init("data.db", new DefaultDatabaseHelper());
        Log.info("Initialised database");

        // Initialize modules.
        // There is no guarantee another module is already created during module
        // contructor, therefore accessing other modules or dependencies should be done in {@link Module#init}
        diagRepModule = new DiagRepModule(this);
        customerModule = new CustomerModule(this);
        specialistModule = new SpecialistModule(this);
        partsModule = new PartsModule(this);
        vehicleModule = new VehicleModule(this);

        Log.info("Initialised modules");
    }

    /**
     * Provides access to the Diagnostic Repair Module
     * @return The diagnostic repair module instance
     */
    public DiagRepModule getDiagRepModule() {
        return this.diagRepModule;
    }

    /**
     * Provides access to the Customer Module
     * @return The customer module instance 
     */
    public CustomerModule getCustomerModule() {
        return this.customerModule;
    }

    /**
     * Provides access to the Parts Module
     * @return The parts module instance
     */
    public PartsModule getPartsModule() {
        return this.partsModule;
    }

    /**
     * Provides access to the Vehicle Module
     * @return The vehicle module instance
     */
    public VehicleModule getVehicleModule() {
        return this.vehicleModule;
    }

    /**
     * Provides access to the Specialist Module
     * @return The specialist module instance
     */
    public SpecialistModule getSpecialistModule() {
        return this.specialistModule;
    }

    /**
     * Provides access to the Authentication system
     * @return Instance of the authentication system
     */
    public Authentication getAuthentication() {
        return this.authentication;
    }
    
   

    /**
     * Provides access to the database
     * @return Instance of the database
     */
    public Database getDatabase() {
        return this.database;
    }

    /**
     * Convenience method for {@link Authentication#getUser} for returning the currently logged in user, or null if
     * the user is not logged in
     * @return The current user or null
     */
    public User getUser() {
        return this.authentication.getUser();
    }

    // Creates a loader for the FXML file and initializes its Controller
    private FXMLLoader createLoader(String uiName, URL location, final List<Object> args) {
        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        fxmlLoader.setControllerFactory(type -> {
            // If you get an error here you probably gave the fxml
            // the wrong controller class name
            Object controller = null;

            // Attempt to find argument constructor
            Constructor argsConstructor = null;
            try {
                argsConstructor = type.getDeclaredConstructor(List.class);
            } catch (NoSuchMethodException | SecurityException ex) {
                // Silent fail
            }

            if(argsConstructor != null) {
                try {
                    controller = argsConstructor.newInstance(args);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Log.error("{0}", ex);
                }
            } else {
                try {
                    controller = type.newInstance();
                } catch (IllegalAccessException | InstantiationException ex) {
                    Log.error("{0}", ex);
                }
            }

            if(controller instanceof BaseController) {
                ((BaseController) controller).setApplication(App.this);
                ((BaseController) controller).setStage(stage);
                ((BaseController) controller).setUiName(uiName);
                ((BaseController) controller).setNamespace(fxmlLoader.getNamespace());
            } else if(controller != null) {
                Log.warn("Controller {0} is not a BaseController, this can cause issues", controller.getClass().getSimpleName());
            }

            return controller;
        });
        
        return fxmlLoader;
    }

    /**
     * Loads the given UI if it exists with optional argument list
     * @param name The ui to load
     * @param args An optional list of arguments
     * @return Returns true if successful
     * @throws IOException
     */
    private boolean loadUI(String name, final List<Object> args) throws IOException {
        if(stage == null || rootLayout == null) return false;
        
        if(controller != null) {
            if(!controller.hide()) return false;
        }
        
        URL location = getClass().getResource("/" + name + ".fxml");
        if(location == null) {
            Log.error("UI with name {0}.fxml not found", name);
            return false;
        }
        FXMLLoader uiLoader = createLoader(name, location, args);

        layoutController.changeTo(name);

        Parent uiRoot = uiLoader.load();
        layoutController.attachLayout(uiRoot);
        
        controller = uiLoader.getController();
        controller.show();

        return true;
    }
    
    /**
     * Changes the UI screen currently visible, it can only show screens other
     * than login when the user is logged in
     * @param name, name of the ui to load as in resources folder e.g. homeUI for loading resources/homeUI.fxml
     * @param args, arguments to give to the new UI's controller
     */
    public void setUI(String name, Object... args) {
        // Only allow setting UI to login if user is not logged in
        if (getUser() == null && !name.equals("login")) {
            Log.error("Cannot change to UI, user not logged in");
            return;
        }

        try {
            loadUI(name, Arrays.asList(args));
        } catch (IOException ex) {
            Log.error("Failed to load GUI screen {0}", name);
            ex.printStackTrace();
        }
    }

    /**
     * Returns the current UI controller
     * @return
     */
    public BaseController getUIController() {
        return controller;
    }

    private void showMainStage() throws Exception {
        URL layoutLocation = getClass().getResource("/layout.fxml");
        FXMLLoader layoutLoader = createLoader("layout", layoutLocation, null);
        rootLayout = layoutLoader.load();

        layoutController = layoutLoader.getController();

        setUI("login");

        layoutController.show();

        Scene scene = new Scene(rootLayout);
        scene.getStylesheets().add("main.css");
        stage.setScene(scene);
        stage.setTitle("GM-SIS");
        stage.setMinWidth(1260);
        stage.setMinHeight(780);
        stage.getIcons().add(new Image("/images/logo.png"));
        stage.show();
    }

    /**
     * Java FX application, entry point
     * @param stage
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        showMainStage();
    }
    
    /**
     * Java FX application, called on exit
     */
    @Override
    public void stop() {
        // Close open database handle
        database.close();
    }

    /**
     * Main app entry point
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        DEBUG = Arrays.asList(args).contains("-debug");
        launch(args);
    }
}
