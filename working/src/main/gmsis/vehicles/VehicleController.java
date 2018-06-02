package gmsis.vehicles;

import gmsis.BaseController;
import gmsis.models.Bill;
import gmsis.models.Booking;
import gmsis.models.Customer;
import gmsis.models.Vehicle;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;


/**
 *
 * @author ec15307
 */
public class VehicleController extends BaseController {
    
    @FXML private TableView<Vehicle> vehicleInfo; 
    @FXML private TableColumn<Vehicle, String > totalCost;
    @FXML private TableColumn<Vehicle, String> futBooking;
    @FXML private TableColumn<Vehicle, String> vehicleCustomer;
    
    

    @FXML private ListView<VehicleView> vehicleListView;
    private Customer currentCustomer=null;
    private Vehicle currentVehicle=null;
    private Vehicle passedVehicle = null;
    
    private List<Vehicle> availableVehicles;
    
    private ObservableList<Vehicle> Vec = FXCollections.observableArrayList();
    private ObservableList<Vehicle> resultList = FXCollections.observableArrayList();
    
    // if an object is passed down to the contructor, and is of instance Customer. It is initialised to currentCustomer variable. 
    public VehicleController(List<Object> args) { 
        if(args != null && args.size() > 0) {
            if(args.get(0) instanceof Customer) {
                currentCustomer = (Customer) args.get(0);//Customer that is currently being managed
             }
            else if(args.get(0) instanceof Vehicle){
                passedVehicle = (Vehicle) args.get(0);
                currentCustomer = passedVehicle.getCustomer();
            }
        }
    }
    
    //redirects user to appriate page
    @FXML protected void addVehicle(ActionEvent event){
        //if no instance of Customer found, redirects to customer page so customer can be selected
        if(currentCustomer==null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("To add a vehicle you need to select a customer. Please select a customer and press add/see vehicles button");

            alert.showAndWait();
            getApplication().setUI("customerUI",currentCustomer);
        }
        //if currentCustomer already holds customer instance, user redirected to add/edit page with selected customer instance.
        else{
            getApplication().setUI("addNewVehicleUI",currentCustomer);
        }
    }
    
    //redirects user to edit page, passing down the selected vehicle instance
    @FXML protected void editVehicle(ActionEvent e){
        Vehicle editVehicle = selected();
        if(editVehicle!=null){ 
            getApplication().setUI("addNewVehicleUI", editVehicle); //redirects only if there is vehicle chosen
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("To edit a vehicle, please select one from the list.");

            alert.showAndWait();
        }   
    }
    
    //returns a vehicle selected from the list
    @FXML protected Vehicle selected(){
        if(vehicleListView.getSelectionModel().getSelectedIndex()!= -1){    
            currentVehicle = vehicleListView.getSelectionModel().getSelectedItem().getVehicle();
        }
        return currentVehicle;
    }
    
    //delete method, once vehicle is deleted the original list is refreshed
    @FXML protected void deleteVehicle(ActionEvent e){
        Vehicle toDelete = selected();
        
        if(toDelete!=null){
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setTitle("Confirmation");
            alert1.setHeaderText(null);
            alert1.setContentText("Delete vehicle "+ toDelete.getRegistration()+" "+ toDelete.getMake()+" ?"); // confirmation pop-up

            Optional<ButtonType> result = alert1.showAndWait();
                    if (result.get() == ButtonType.OK){
                        getApplication().getVehicleModule().deleteVehicle(toDelete);
                    }

            availableVehicles.clear();
           availableVehicles = getAvailableVehicles();
                   
            for(int i = 0; i<availableVehicles.size(); i++){
                    Vec.add(availableVehicles.get(i));}
            
            vehicleListView.getItems().clear();
           for(Vehicle v : availableVehicles) {
               vehicleListView.getItems().add(new VehicleView(v));
           }
           for ( int i = 0; i<vehicleInfo.getItems().size(); i++) {
                vehicleInfo.getItems().clear();
            }

        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("To delete a vehicle, please select one from the list.");

            alert.showAndWait();
        }  
    }
    
       //redirects to booking page with selected vehicle instance
    @FXML protected void goBooking(ActionEvent e){
        Vehicle bookingVehicle = selected();
        if(bookingVehicle != null){
            getApplication().setUI("bookingEditUI", bookingVehicle);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("To make a booking for a vehicle, please select one from the list.");

            alert.showAndWait();
        }    
    }
    
    //return a list with avalilable vehicles 
    protected List<Vehicle> getAvailableVehicles(){
        if(currentCustomer==null){
            availableVehicles = getApplication().getVehicleModule().getVehicles();
       }
       //Gets available vehicles specific to customer
       else{
            availableVehicles=getApplication().getVehicleModule().getCustomerVehicles(currentCustomer);
       }
        return availableVehicles;
    }
     
    //Initalisation
    @FXML protected void initialize()
    { 
           availableVehicles = getAvailableVehicles();
           vehicleListView.getItems().clear();
           vehicleListView.setPlaceholder(new Label("No vehicles available"));
           
           for(int i = 0; i<availableVehicles.size(); i++){
            Vec.add(availableVehicles.get(i));
        }
           for(Vehicle v : availableVehicles) {
               vehicleListView.getItems().add(new VehicleView(v));
           }
           vehicleInfo.setItems(Vec);
           vehicleInfo.setPlaceholder(new Label("Please select a vehicle"));
           //Displays the information of the selected vehicle from the listview in the tableview
           vehicleListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<VehicleView>() {
               @Override
               public void changed(ObservableValue observable, VehicleView oldVlaue, VehicleView newValue){
                   
                   Vec.clear();
                   Vehicle chosen = selected();
                   Vec.add(chosen); 
               }
           });
                      
           //sets information for vehicle bookings
           //every row corresponds to the order of the vehicleListView listview
           totalCost.setCellValueFactory(new Callback<CellDataFeatures<Vehicle,String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(CellDataFeatures<Vehicle, String> param) {
                        Booking futbooking = param.getValue().getNextBooking();
                        if(futbooking!=null){
                            Bill total = futbooking.getBill();
                            if(total!=null){
                                return new SimpleStringProperty(String.format("£%.2f", total.getTotal()));
                            }
                            else{
                                return new SimpleStringProperty("No bill cost");
                            }
                         }
                        else{
                            return new SimpleStringProperty("No bill");
                        }
                    }
                });
           
           //sets information for vehicle's next booking date
           futBooking.setCellValueFactory(new Callback<CellDataFeatures<Vehicle,String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(CellDataFeatures<Vehicle, String> param) {
                        Booking futbooking = param.getValue().getNextBooking();
                        if(futbooking!=null){
                        return new SimpleStringProperty(param.getValue().getNextBooking().getDateStart().toString());
                        }
                        else{
                            return new SimpleStringProperty("No booking made");
                        }
                    }
                });
           
         //sets information for vehicle's customer name  
         vehicleCustomer.setCellValueFactory(new Callback<CellDataFeatures<Vehicle, String>, ObservableValue<String>>(){
             @Override
             public ObservableValue<String> call(CellDataFeatures<Vehicle, String> param){
                 return new SimpleStringProperty(param.getValue().getCustomer().getFirstname());
             }
         });
         
    }     
    
   //wrapper for vehicle
    //keeps track of vehicles in the listview
   public class VehicleView{
       Vehicle vehicle;
       
       public VehicleView(Vehicle vehicle){
           this.vehicle=vehicle;
       }
       
       public Vehicle getVehicle(){
           return this.vehicle;
       }
       
       @Override
       public String toString(){
           return vehicle.getRegistration()+"  "+vehicle.getMake()+"  "+vehicle.getModel();
       }
       
   }
     
}
    
    
    
   
    

   
    

