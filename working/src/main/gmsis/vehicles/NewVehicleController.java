/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmsis.vehicles;

import gmsis.BaseController;
import gmsis.ValidationHelper;
import gmsis.models.Address;
import gmsis.models.Customer;
import gmsis.models.Vehicle;
import gmsis.models.Warranty;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 *
 * @author ec15307
 */
public class NewVehicleController extends BaseController {
    
    ObservableList<String> vehicleTypeList = FXCollections.observableArrayList("Van","Truck", "Car");
    ObservableList<String> vehicleNameList = FXCollections.observableArrayList("Honda Civic 1.6-SE PETROL CAR", "Honda Odyssey 2.2L PETROL VAN",
                                                                               "Audi A4 2.0LI-4 PETROL CAR","Audi A6 3.0L-I6 DIESEL CAR",
                                                                               "BMW X5 3.0L I6 PETROL CAR","BMW 5-Series Turbo-3.0L-I6 DIESEL CAR",
                                                                               "Chevrolet Tahoe 5.3L-V8 PETROL CAR","Chevrolet Camaro 62L-V8 PETROL CAR",
                                                                               "Volvo FH-Series D16C PETROL TRUCK","Volvo FMX D13-Truck PETROL TRUCK",
                                                                               "Ford Transit 2.5L-I4 DIESEL TRUCK","Ford Fiesta 1.6L-I4 DIESEL CAR",
                                                                               "Lexus ES 3.5L-V6 PETROL CAR","Lexus LS 4.6L-V8 PETROL CAR",
                                                                               "Mercedes Vito 3.0L-CDI DIESEL VAN","Mercedes Actros V8-Cylinder DIESEL TRUCK",
                                                                               "Tayota Hiace 16R-I4 PETROL VAN","Toyota Camry 2.5L-I4 DIESEL CAR",
                                                                               "Nissan Sunny 1.5L PETROL CAR", "Nissan Armada 3.5L DIESEL CAR");
    
    ObservableList<String> vehicleFuelList = FXCollections.observableArrayList("PETROL", "DIESEL");  
    ObservableList<String> warrantyList = FXCollections.observableArrayList("Warranty Present", "No Warranty");
    
    @FXML private ComboBox<String> QuickSelect;
    @FXML private ChoiceBox vType;
    @FXML private ChoiceBox vFuel;
    @FXML private ChoiceBox Warranty;
    @FXML private TextField warrName;
    @FXML private TextField warrAddress;
    @FXML private TextField warrTown;
    @FXML private TextField warrCounty;
    @FXML private TextField warrPostcode;
    @FXML private DatePicker warrExpiry;
    @FXML private TextField vModel;
    @FXML private TextField vMake;
    @FXML private TextField vEngine;
    @FXML private TextField vRegistration;
    @FXML private TextField vColour;
    @FXML private DatePicker vServiceDate;
    @FXML private DatePicker MoTdate;
    @FXML private TextField vMileage;

    private boolean status = false;
    
    private Customer currentCustomer = null;
    private Vehicle currentVehicle=null;

    //If an object is passed down to the contructor, and is of instance Customer. It is initialised to currentCustomer variable.
    //If it is of intance Vehicle it is initialised to currentVehicle variable.
    public NewVehicleController(List<Object> args) {
        if(args != null && args.size() > 0) {
            if(args.get(0) instanceof Customer) {
                currentCustomer = (Customer) args.get(0);
            } else if(args.get(0) instanceof Vehicle){
                currentVehicle = (Vehicle) args.get(0);
                currentCustomer = currentVehicle.getCustomer();
            }          
        }
    }
    
    //method to go back to original vehicle screen, takes current Customer as an argument
    @FXML protected void back()
    {
        getApplication().setUI("vehicleUI",currentCustomer);
    }
    
    //method to clear the form
    @FXML protected void reset(ActionEvent e)
    {
        QuickSelect.setValue(null);
        vType.setValue(null);
        vFuel.setValue(null);
        Warranty.setValue(null);
        vRegistration.setText("");
        vMake.setText("");
        vModel.setText("");
        vEngine.setText("");
        vColour.setText("");
        vMileage.setText("");
        warrName.setText("");
        warrAddress.setText("");
        warrTown.setText("");
        warrCounty.setText("");
        warrPostcode.setText("");
        warrExpiry.setValue(null);
        vServiceDate.setValue(null);
        MoTdate.setValue(null);
     }
    
    //Displays an error pop-up
    //String argument is passed down to desribe error
    protected void warning(String text){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(text);

            alert.showAndWait();
    }
    
    //Add method
    //Checks if all fields are complete
    //Gets the data and creates a vehicle
    @FXML protected void add(ActionEvent e)
    {
        Vehicle.VehicleType Vtype = null;
         if(vType.getSelectionModel().getSelectedIndex() == -1){ //if a vehicle type is not selected, displays the error 
                warning("Please choose the vehicle type");
                return;
            }
         if(vType.getSelectionModel().getSelectedItem().toString().equals("Van"))
            {
                    Vtype = Vehicle.VehicleType.VAN;
            }
         else if (vType.getSelectionModel().getSelectedItem().toString().equals("Car")){
                    Vtype = Vehicle.VehicleType.CAR;
            }
         
         else if(vType.getSelectionModel().getSelectedItem().toString().equals("Truck")){
                         Vtype = Vehicle.VehicleType.TRUCK;
            }

         Vehicle.FuelType fuelType = null; 
            if(vFuel.getSelectionModel().getSelectedIndex() == -1 ){//if a vehicle fuel type is not selected, displays the error 
                warning("Please choose the fuel type of the vehicle");
                return;
            }
            else if(vFuel.getSelectionModel().getSelectedItem().toString().equals("PETROL"))
                {
                    fuelType = Vehicle.FuelType.PETROL;
                }
            else if(vFuel.getSelectionModel().getSelectedItem().toString().equals("DIESEL"))
                {
                    fuelType = Vehicle.FuelType.DIESEL;
                }
            
         Date serviceDate = null;
            if(vServiceDate.getValue()==null){
                warning("Please select the last service date");//if the last service date is not selected, displays the error
            }
            else{
                LocalDate service = vServiceDate.getValue();
                Instant instant2 = Instant.from(service.atStartOfDay(ZoneId.systemDefault()));
                serviceDate= Date.from(instant2);
                if(!serviceDate.before(new Date())){
                    warning("The selected date for last service is invalid. Enter an older date."); //if last service date is in the future, displays the error
                    return;
                }
            }
            
        Date motDate = null;
            if(MoTdate.getValue()==null){
               warning("Please select the MOT expiration date");//if the MoT renewal date is not selected, displays the error
               return;
            }
            else{
                LocalDate MOT = MoTdate.getValue();
                Instant instant3 = Instant.from(MOT.atStartOfDay(ZoneId.systemDefault()));
                motDate = Date.from(instant3);
                if(!motDate.after(new Date())){
                    warning("The MOT renewal date has passed. Select a future date");//if last MoT renewal date is in the past, displays the error
                    return;
                }
            }
        
        Warranty newWarranty=null;
        Vehicle newVehicle = null;
        
        if(Warranty.getSelectionModel().getSelectedItem().toString().equals("Warranty Present")){
            
            //check if all the warranty details are present if "Warranty Present" option is selected
            if(warrName.getText().equals("") ||
               warrAddress.getText().equals("") ||
               warrTown.getText().equals("") ||
               warrCounty.getText().equals("") ||
               warrPostcode.getText().equals("")){
              
                warning("Please enter all the warranty details");
                return;
            }
            Date date = null;
            if(warrExpiry.getValue()==null){
                warning("Please select the warranty expiry date");//if the warranty expiry date is not selected, displays the error
                return;
            }
            else{
                LocalDate localDate = warrExpiry.getValue();
                Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                date = Date.from(instant);
                if(!date.after(new Date())){
                    warning("The expiration date of the warranty has passed. Select a valid date");//if the warranty expiry date is in the past, displays the error
                    return;
                }
                newWarranty = new Warranty(warrName.getText(), new Address(null,warrAddress.getText(), warrTown.getText(),warrCounty.getText(),warrPostcode.getText()),date);
            }
        }
        //if statement to check no information is entered on "No Warranty selection"
        else if(Warranty.getSelectionModel().getSelectedItem().toString().equals("No Warranty")){
            if(warrName.getText().isEmpty()==false ||
               warrAddress.getText().isEmpty()==false ||
               warrTown.getText().isEmpty()==false||
               warrCounty.getText().isEmpty()==false ||
               warrExpiry.getValue()!=null){
                warning("You have selected 'No warranty option'. Therefore, you can not have information in the warranty fields");
                return;
            }
        }
        
        //Check to see if the vehicle details have been entered
        if(vRegistration.getText().equals("") ||
           vMake.getText().equals("")||
           vModel.getText().equals("") || 
           vEngine.getText().equals("") ||
           vColour.getText().equals("") ||
           vMileage.getText().equals("")){
            
            warning("Please enter all the vehicle details");
            return;
        }

        else if(!vMileage.getText().matches("[0-9]*")){
            warning("Mileage must be numbers");
            return;
        }
        
        //New vehicle created if vehicle type and fuel type are selected
        if(Vtype!=null && fuelType!=null) {
            newVehicle = new Vehicle(Vtype,newWarranty ,
                     vRegistration.getText(), vMake.getText(), vModel.getText(), vEngine.getText(), fuelType,
                     vColour.getText(), serviceDate, motDate, Integer.parseInt(vMileage.getText()));
        }

        //incase edit button is pressed.
        //vehicle to be edited is passed down to the constructor and will be the currentVehicle
        //it checks if the registration of the currentVehicle matches of one present in the register number field
        //data entered gets overwritten and updated in the database
        if(currentVehicle!=null){
            if(currentVehicle.getRegistration().equals(vRegistration.getText())){
                currentVehicle.setVehicleType(Vtype);
                currentVehicle.setWarranty(newWarranty);
                currentVehicle.setRegistration(vRegistration.getText());
                currentVehicle.setMake(vMake.getText());
                currentVehicle.setModel(vModel.getText());
                currentVehicle.setEngineSize(vEngine.getText());
                currentVehicle.setFuelType(fuelType);
                currentVehicle.setColour(vColour.getText());
                currentVehicle.setLastServiceDate(serviceDate);
                currentVehicle.setMotRenewalDate(motDate);
                currentVehicle.setCurrentMileage(Integer.parseInt(vMileage.getText()));
                getApplication().getDatabase().put().objects(currentVehicle).execute();
                status = true;
            }
        }
        //incase edit was not pressed(means add was pressed instead)
        //vehicle is added the currentCustomer which we get from the constructor
       else{
            currentCustomer.addVehicle(newVehicle);
            getApplication().getDatabase().put().objects(currentCustomer).execute();
            status = true;
        }
        //check if the vehicle was added successfully
        if(status==true){
             back();
         }
    }
    
    //Sets vehicles details according the vehicle selected from QuickSelect list
    @FXML protected void handleSelect(ActionEvent e)
    {
         String line = QuickSelect.getValue();
         String[] splitted;
         splitted = line.split("\\s+");
         vModel.setText(splitted[0]);
         vMake.setText(splitted[1]);
         vEngine.setText(splitted[2]);
         if (splitted[3].equals("PETROL"))
         {
             vFuel.getSelectionModel().selectFirst();
         }
         else if(splitted[3].equals("DIESEL"))
         {
             vFuel.getSelectionModel().selectLast();
         }
         if(splitted[4].equals("CAR")){
             vType.getSelectionModel().select(2);
         }
         else if(splitted[4].equals("VAN")){
             vType.getSelectionModel().select(0);
         }
         else if(splitted[4].equals("TRUCK")){
             vType.getSelectionModel().select(1);
         }
             
    }
        
    //sets the items of the lists
    //in case a vehicle object is passed down(meaning edit button was pressed)
    //sets the vehicle detail fields with data from the specific vehicle object 
    @FXML protected void initialize()
    {
         vType.setItems(vehicleTypeList);
         QuickSelect.setItems(vehicleNameList);
         vFuel.setItems(vehicleFuelList);
         Warranty.setItems(warrantyList);
         
         ValidationHelper.uppercaseAlphaNumericOnly(vRegistration);
         ValidationHelper.uppercaseAlphaNumericOnly(vMake);
         ValidationHelper.uppercaseAlphaNumericOnly(vModel);
         ValidationHelper.uppercaseAlphaNumericOnly(vEngine);
         ValidationHelper.lettersOnly(vColour);
         ValidationHelper.numbersOnly(vMileage);
         ValidationHelper.lettersOnly(warrName);
         ValidationHelper.uppercaseAlphaNumericOnly(warrAddress);
         ValidationHelper.lettersOnly(warrTown);
         ValidationHelper.lettersOnly(warrCounty);
         ValidationHelper.uppercaseAlphaNumericOnly(vMake);
         
         if(currentVehicle != null){
             if(currentVehicle.getVehicleType().toString().equals("CAR"))
             {
                 vType.getSelectionModel().select(2);
             }
             else if(currentVehicle.getVehicleType().toString().equals("TRUCK"))
             {
                 vType.getSelectionModel().select(1);
             }
             else if(currentVehicle.getVehicleType().toString().equals("VAN")){
                 vType.getSelectionModel().select(0);
             }
             
             if(currentVehicle.hasWarranty()==true){
                 Warranty.getSelectionModel().selectFirst();
                 warrName.setText(currentVehicle.getWarranty().getCompanyName());
                 warrAddress.setText(currentVehicle.getWarranty().getCompanyAddress().getAddressLine());
                 warrTown.setText(currentVehicle.getWarranty().getCompanyAddress().getTown());
                 warrCounty.setText(currentVehicle.getWarranty().getCompanyAddress().getCounty());
                 warrPostcode.setText(currentVehicle.getWarranty().getCompanyAddress().getPostcode());
                 Date expiry = currentVehicle.getWarranty().getExpiryDate();
                 LocalDate expiryDate = expiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                 warrExpiry.setValue(expiryDate);
             }
             else if (currentVehicle.hasWarranty()==false)
             {
                 Warranty.getSelectionModel().selectLast();
                 warrName.setText("");
                 warrAddress.setText("");
                 warrTown.setText("");
                 warrCounty.setText("");
             }
             
             if(currentVehicle.getFuelType().toString().equals("PETROL"))
             {
                 vFuel.getSelectionModel().selectFirst();
             }
             else if(currentVehicle.getFuelType().toString().equals("DIESEL"))
             {
                 vFuel.getSelectionModel().selectLast();
             }
            vRegistration.setText(currentVehicle.getRegistration());
            vModel.setText(currentVehicle.getModel());
            vMake.setText(currentVehicle.getMake());
            vEngine.setText(currentVehicle.getEngineSize());
            vColour.setText(currentVehicle.getColour());
            vMileage.setText(String.valueOf(currentVehicle.getCurrentMileage()));
            if(currentVehicle.getLastServiceDate()!=null ){
            
                Date service = currentVehicle.getLastServiceDate();
                LocalDate serviceDate = service.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                vServiceDate.setValue(serviceDate);
            }
            if(currentVehicle.getMotRenewalDate()!=null){
                Date mot = currentVehicle.getMotRenewalDate();
                LocalDate motDate = mot.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                MoTdate.setValue(motDate);
            }
            else{
                
                 vServiceDate.setValue(null);
                 MoTdate.setValue(null);
            }
            
         }
         
  
    }
}

