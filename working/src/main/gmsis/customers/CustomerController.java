package gmsis.customers;

import gmsis.BaseController;
import gmsis.models.User;
import gmsis.models.Warranty;
import java.util.Date;
import gmsis.database.Lazy;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.stage.Popup;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import gmsis.database.Database;
import gmsis.database.get.Query;
import gmsis.database.tables.CustomerTable;
import gmsis.models.Customer.CustomerType;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import gmsis.models.Customer;
import gmsis.models.Address;
import gmsis.models.Vehicle;
import java.io.IOException;
import java.lang.*;
import org.controlsfx.control.textfield.TextFields;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import gmsis.customers.CustomerModule;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class
 *
 * @author ikbalhussainali
 */


public  class CustomerController extends BaseController {
    
   
    private String date;
    private String time;
    private String reg;
    private String[] split2;
    private ArrayList data=new ArrayList();
    final ObservableList<String> listItems = FXCollections.observableArrayList("SelectToViewDetails");       
    final ObservableList<Customer> tableItmes = FXCollections.observableArrayList();
    
    
    
    
    @FXML private ComboBox<String> comboBox;
    @FXML private TextField phone;
    @FXML private TextField email;
    
    @FXML private TextField pcode;
    @FXML private TextField address;
    @FXML private TextField surname;
    @FXML private TextField county;
    @FXML private TextField town;
    @FXML private TextField type2;
    @FXML private TextField quickSearchName = null;
    @FXML private TextField autoSearch;
    
    @FXML private TextField addName;
    @FXML private TextField addSurname;
    @FXML private TextField addAddress;//to do
    @FXML private TextField addCounty;
    @FXML private TextField addTown;
    @FXML private TextField addPostcode;//to do
    @FXML private TextField addPhone;
    @FXML private TextField addEmail;
    @FXML private TextField addType;
    
    
    
    @FXML private TableView<Customer> customerTable = null;
    @FXML private TableColumn<Customer, String> nameT;
    @FXML private TableColumn<Customer, String> surnameT;
    @FXML private TableColumn<Customer, String> addressT;
    @FXML private TableColumn<Customer, String> pcodeT;
    @FXML private TableColumn<Customer, String> typeT;
    @FXML private TableColumn<Customer, String> countyT;
    @FXML private TableColumn<Customer, String> townT;
    @FXML private TableColumn<Customer, String> mailT;
    @FXML private TableColumn<Customer, String> phoneT;
    
    //@FXML private ListView<String> activeUsers;
    
    private List<Customer> currentCustomer;
    private Customer singleCustomer;
    private Customer singleCusto=null;
    String pattern =  "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
    
    
    public CustomerController(List<Object> args) { 
        if(args != null && args.size() > 0) {
            if(args.get(0) instanceof Customer) {
                singleCusto = (Customer) args.get(0);
             }
        }
    }
     
    @FXML
    protected void initialize() {
        if(customerTable != null) {refreshData();
        }else{
            
          
            
            addName.textProperty().addListener(new ChangeListener<String>() { 
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\D*")) { 
                    addName.setText(newValue.replaceAll("[^\\D]", "")); 
                }
            }
        });
            
            
             addSurname.textProperty().addListener(new ChangeListener<String>() { 
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\D*")) { 
                    addSurname.setText(newValue.replaceAll("[^\\D]", "")); 
                }
            }
        });
             
              addCounty.textProperty().addListener(new ChangeListener<String>() { 
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\D*")) { 
                    addCounty.setText(newValue.replaceAll("[^\\D]", "")); 
                }
            }
        });
              
               addTown.textProperty().addListener(new ChangeListener<String>() { 
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\D*")) { 
                    addTown.setText(newValue.replaceAll("[^\\D]", "")); 
                }
            }
        });
               
                addPhone.textProperty().addListener(new ChangeListener<String>() { 
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) { 
                    addPhone.setText(newValue.replaceAll("[^\\d]", "")); 
                }
            }
        });
            
            
        }
        
        if(quickSearchName != null) {
            quickSearchName.textProperty().addListener(new ChangeListener<String>() {

                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    refreshData();
                }
            });
        }
        
        
        
        
    }
    
   
   
   private void refreshData() {
       
      
       /*
       Deletes values in ListItmes(ListView), tableItmes(TableView), and data (AutoCompleteTextField) as updated data is retrivied
       from database
       */
       
       listItems.removeAll(listItems);
       tableItmes.removeAll(tableItmes);
       data.clear();
      
       currentCustomer= getApplication().getCustomerModule().getCustomer(); //gets Customer Objects (List)
       
       customerTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Customer>() {

           @Override
           public void changed(ObservableValue<? extends Customer> observable, Customer oldValue, Customer newValue) {
               singleCustomer = newValue;
           }
       });
       
       
       /*
        -- Adds Customer objects' name and surnname to ListView
        -- Adds Customer objects to tableView
        -- Adds Customer objects' name and surnname to data used for autocomplete Textfield
       */
       
          String filter = quickSearchName.getText().trim().toLowerCase();
          for(int i=0; i<currentCustomer.size(); i++){
              String fullName = currentCustomer.get(i).getFirstname()+"  "+currentCustomer.get(i).getSurname();
              if(filter.isEmpty() || fullName.toLowerCase().contains(filter)) {
                listItems.add(fullName);
                
                if(singleCusto==null){
                    
                   tableItmes.add(currentCustomer.get(i));
                   data.add(currentCustomer.get(i).getFirstname()+"  "+currentCustomer.get(i).getSurname());
                   
                }else{
                    
                    tableItmes.add(singleCusto);
                    data.add(singleCusto.getFirstname()+"  "+singleCusto.getSurname());
                    break;
                }
                
              }
       }
          
       /* 
       
          -- Sest Cell field to the Customer/Address object (using models Getter methods)
          -- Sets Cell to a editable textField
          -- Sets onCommit function (Press Enter) to new value entered
          || New value is set using the setter functions 
          || To save new values the saveButton must be clicked otherwise values will be unvhanged into the database
       */
          
        nameT.setCellValueFactory(new PropertyValueFactory<Customer, String>("firstname"));
        nameT.setCellFactory(TextFieldTableCell.<Customer>forTableColumn());
         nameT.setOnEditCommit(new EventHandler<CellEditEvent<Customer, String>>() {

           @Override
           public void handle(CellEditEvent<Customer, String> t) { 
               
               if(t.getNewValue().matches("\\D*")){
               ( t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setFirstname(t.getNewValue());
                 
               }else{
            
                   
                   Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Only Characters allowed");

            alert.showAndWait();
            
            t.getTableView().getColumns().get(0).setVisible(false);
            t.getTableView().getColumns().get(0).setVisible(true);
               }
           }
             
         });
        
        
//       
        
        surnameT.setCellValueFactory(new PropertyValueFactory<Customer, String>("surname")); 
       surnameT.setCellFactory(TextFieldTableCell.<Customer>forTableColumn());
        surnameT.setOnEditCommit(new EventHandler<CellEditEvent<Customer, String>>() {

           @Override
           public void handle(CellEditEvent<Customer, String> t) {
                if(t.getNewValue().matches("\\D*")){
               ( t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setSurname(t.getNewValue());
                
                }else{
                    
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Only Characters allowed");

            alert.showAndWait();
            
            t.getTableView().getColumns().get(0).setVisible(false);
            t.getTableView().getColumns().get(0).setVisible(true);
                    
                }
           }
             
         });
        
        
        
      
        mailT.setCellValueFactory(new PropertyValueFactory<Customer, String>("email"));
        mailT.setCellFactory(TextFieldTableCell.<Customer>forTableColumn());
         mailT.setOnEditCommit(new EventHandler<CellEditEvent<Customer, String>>() {

           @Override
           public void handle(CellEditEvent<Customer, String> t) {
               if(t.getNewValue().matches(pattern)){
               ( t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setEmail(t.getNewValue());
                 
               }else{
                   
                    Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Enter Valid Email");

            alert.showAndWait();
            
            t.getTableView().getColumns().get(0).setVisible(false);
            t.getTableView().getColumns().get(0).setVisible(true);
                   
               }
               
               
           }
             
         });
        
       
        phoneT.setCellValueFactory(new PropertyValueFactory<Customer, String>("phoneNumber"));
        
        
        phoneT.setCellFactory(TextFieldTableCell.<Customer>forTableColumn());
         phoneT.setOnEditCommit(new EventHandler<CellEditEvent<Customer, String>>() {

           @Override
           public void handle(CellEditEvent<Customer, String> t) {
               
               if(t.getNewValue().matches("\\d*")){
               ( t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setPhoneNumber(t.getNewValue());
               }else{
                   
             Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Only numbers allowed");

            alert.showAndWait();
            
            t.getTableView().getColumns().get(0).setVisible(false);
            t.getTableView().getColumns().get(0).setVisible(true);
                   
               }
           }
             
         });
        
        
        typeT.setCellValueFactory(new Callback<CellDataFeatures<Customer, String>, ObservableValue<String>>() {
        
          @Override
            public ObservableValue<String> call(CellDataFeatures<Customer, String> c) {
            return new SimpleStringProperty(c.getValue().getType().getName());
              }
           }); 
         typeT.setCellFactory(TextFieldTableCell.<Customer>forTableColumn());
         typeT.setOnEditCommit(new EventHandler<CellEditEvent<Customer, String>>() {

           @Override
           public void handle(CellEditEvent<Customer, String> t) {
               
               if(t.getNewValue().matches("\\D*")){
                   
                   if(t.getNewValue().toLowerCase().equals("business") ||
                           t.getNewValue().toLowerCase().equals("individual")){
               ( t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setType(CustomerType.fromName(t.getNewValue()));
                 
                   }else{
                       
             Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Enter Valid Role");

            alert.showAndWait();
            
            t.getTableView().getColumns().get(0).setVisible(false);
            t.getTableView().getColumns().get(0).setVisible(true);
                       
                   }
                 
               }else{
                   
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Only characters allowed");

            alert.showAndWait();
            
            t.getTableView().getColumns().get(0).setVisible(false);
            t.getTableView().getColumns().get(0).setVisible(true);
                   
                   
                   
               }
           }
             
         });
        
        addressT.setCellValueFactory(new Callback<CellDataFeatures<Customer, String>, ObservableValue<String>>() {
        
          @Override
            public ObservableValue<String> call(CellDataFeatures<Customer, String> c) {
            return new SimpleStringProperty(c.getValue().getAddress().getAddressLine());                
              }
           }); 
        
      addressT.setCellFactory(TextFieldTableCell.<Customer>forTableColumn());
         addressT.setOnEditCommit(new EventHandler<CellEditEvent<Customer, String>>() {

           @Override
           public void handle(CellEditEvent<Customer, String> t) {
               ( t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                       ).getAddress().setAddressLine(t.getNewValue());
           }
             
         });
        
       countyT.setCellValueFactory(new Callback<CellDataFeatures<Customer, String>, ObservableValue<String>>() {
        
          @Override
            public ObservableValue<String> call(CellDataFeatures<Customer, String> c) {
            return new SimpleStringProperty(c.getValue().getAddress().getCounty());                
              }
           }); 
       
         countyT.setCellFactory(TextFieldTableCell.<Customer>forTableColumn());
         countyT.setOnEditCommit(new EventHandler<CellEditEvent<Customer, String>>() {

           @Override
           public void handle(CellEditEvent<Customer, String> t) {
               if(t.getNewValue().matches("\\D*")){
               ( t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).getAddress().setCounty(t.getNewValue());
               }else{
                   
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Only characters allowed");

            alert.showAndWait();
            
            t.getTableView().getColumns().get(0).setVisible(false);
            t.getTableView().getColumns().get(0).setVisible(true);
               }
           }
             
         });
       
        pcodeT.setCellValueFactory(new Callback<CellDataFeatures<Customer, String>, ObservableValue<String>>() {
        
          @Override
            public ObservableValue<String> call(CellDataFeatures<Customer, String> c) {
            return new SimpleStringProperty(c.getValue().getAddress().getPostcode());                
              }
           });
        
        pcodeT.setCellFactory(TextFieldTableCell.<Customer>forTableColumn());
         pcodeT.setOnEditCommit(new EventHandler<CellEditEvent<Customer, String>>() {

           @Override
           public void handle(CellEditEvent<Customer, String> t) {
               ( t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).getAddress().setPostcode(t.getNewValue());
           }
             
         });
        
//       
        
        
         townT.setCellValueFactory(new Callback<CellDataFeatures<Customer, String>, ObservableValue<String>>() {
        
          @Override
            public ObservableValue<String> call(CellDataFeatures<Customer, String> c) {
            return new SimpleStringProperty(c.getValue().getAddress().getTown());                
              }
           }); 
         townT.setCellFactory(TextFieldTableCell.<Customer>forTableColumn());
         townT.setOnEditCommit(new EventHandler<CellEditEvent<Customer, String>>() {

           @Override
           public void handle(CellEditEvent<Customer, String> t) {
               
               if(t.getNewValue().matches("\\D*")){
               ( t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).getAddress().setTown(t.getNewValue());
               }else{
                   
                   Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Only characters allowed");

            alert.showAndWait();
            
            t.getTableView().getColumns().get(0).setVisible(false);
            t.getTableView().getColumns().get(0).setVisible(true);
               }
           }
             
         });
        
        
        /*
         Sets Items in ListView and TableView
         */
        
       customerTable.setItems(tableItmes);      
       //activeUsers.setItems(listItems);
       
       
       
      TextFields.bindAutoCompletion(quickSearchName,data);//Insersts Data for Auto Complete TextField
        
       
       
   }
   
    @FXML protected void deleteCustomer(ActionEvent event) {
        
        singleCustomer=customerTable.getSelectionModel().getSelectedItem();//Selected Customer objects from table
        
        if(singleCustomer==null){
            
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select customer");

            alert.showAndWait();
            
        }else{
        
            /*
            Confirmation Dialog
            */
               
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Delete customer "+ singleCustomer.getFirstname()+" "+ singleCustomer.getSurname()+" ?");

        Optional<ButtonType> result = alert.showAndWait();
       
        
                if (result.get() == ButtonType.OK){
    
                    getApplication().getCustomerModule().deleteCustomer(singleCustomer);//dELETE
                    singleCustomer=null;
                 }
                
       /*refreshes table     */        
                
       listItems.removeAll(listItems);
       tableItmes.removeAll(tableItmes);
       data.clear();
       currentCustomer= getApplication().getCustomerModule().getCustomer(); //gets Customer Objects (List)
       for(int i=0; i<currentCustomer.size(); i++){
       
                listItems.add(currentCustomer.get(i).getFirstname()+"  "+currentCustomer.get(i).getSurname());
                tableItmes.add(currentCustomer.get(i));
                data.add(currentCustomer.get(i).getFirstname()+"  "+currentCustomer.get(i).getSurname());
       } 
       customerTable.setItems(tableItmes);    
                
              
        }
        
        refreshData();

      }
   
    
    @FXML protected void addCustomer(ActionEvent event) {
        
         //try {
             
//        Window ownerWindow = ((Node) event.getTarget()).getScene().getWindow();     
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/addCustomerUI.fxml"));
//                Parent root1 = (Parent) fxmlLoader.load();
//                Stage stage = new Stage();
//                stage.initModality(Modality.APPLICATION_MODAL);
//                stage.initOwner(ownerWindow);
//                stage.initStyle(StageStyle.UNDECORATED);
//                stage.setScene(new Scene(root1));  
//                stage.show();
//                stage.wait();
//        } catch(Exception e) {
//           e.printStackTrace();
//          }
             getApplication().setUI("addCustomerUI");
        
        
    }
    
    @FXML protected void addCustomer2(ActionEvent event) {
        
        
        if(addName.getText().equals("") || 
          addSurname.getText().equals("") || 
          addAddress.getText().equals("") || 
          addCounty.getText().equals("") ||
          addTown.getText().equals("") ||
          addPostcode.getText().equals("") ||
          addEmail.getText().equals("") ||
          addPhone.getText().equals("") ||
          addType.getText().equals("") ){
            
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please Fill in all the boxes");

            alert.showAndWait();
            
        }else if (singleCustomer!=null ){
            
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please Clear Fields before adding new Customer");
            alert.showAndWait();
        }else{
        
         Customer.CustomerType typeee = null;
         boolean check=false;
        
        if(addType.getText().toLowerCase().equals("business")){
                        typeee =  Customer.CustomerType.BUSINESS;
                        check=true;
                    }else if(addType.getText().toLowerCase().equals("individual")){
                         typeee =  Customer.CustomerType.INDIVIDUAL;
                         check=true;
                    }else{
                        Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid type");
            alert.showAndWait();
                    }
        
        
        
        if(!addEmail.getText().matches(pattern)){
            
            check=false;
             Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid email");
            alert.showAndWait();
            
        }
        
        if(check){
        
        Address newAddress= new Address(null,addAddress.getText(),addCounty.getText(),addTown.getText(),addPostcode.getText()); 
        Customer newCustomer = new Customer(typeee,addName.getText(),addSurname.getText(),addPhone.getText(),addEmail.getText(),newAddress);        
        getApplication().getCustomerModule().addCustomer(newCustomer);//Inserts them into database
        
        getApplication().setUI("customerUI");
        }
       
       /*refreshes table     */        
                
//       listItems.removeAll(listItems);
//       tableItmes.removeAll(tableItmes);
//       data.clear();
//       currentCustomer= getApplication().getCustomerModule().getCustomer(); //gets Customer Objects (List)
//       for(int i=0; i<currentCustomer.size(); i++){
//       
//                listItems.add(currentCustomer.get(i).getFirstname()+"  "+currentCustomer.get(i).getSurname());
//                tableItmes.add(currentCustomer.get(i));
//                data.add(currentCustomer.get(i).getFirstname()+"  "+currentCustomer.get(i).getSurname());
//       } 
//       customerTable.setItems(tableItmes);    
//       
        }
    }
    
    
    
    @FXML protected void editCustomer(ActionEvent event) {
          
         /*
        
         Update values in the database using ListView rather than TableView
        
        singleCustomer.setFirstname(name.getText());
        singleCustomer.setSurname(surname.getText());
        singleCustomer.getAddress().setAddressLine(address.getText());
        singleCustomer.getAddress().setCounty(county.getText());
        singleCustomer.getAddress().setTown(town.getText());
        singleCustomer.getAddress().setPostcode(pcode.getText());
        singleCustomer.setEmail(email.getText());
        singleCustomer.setPhoneNumber(phone.getText());
        
         Customer.CustomerType typee;
        
        if(type2.getText().equals("business")){
                       singleCustomer.setType(Customer.CustomerType.BUSINESS);
                    }else{
                        singleCustomer.setType(Customer.CustomerType.INDIVIDUAL);
                    }
        
        */
        
        /*
        Updates values in the database using TableView Cells Values
        Saves all objects at once//Still to do
        */
        
        
        /*
        Saves all customers
        */
        
        for(int i=0; i<tableItmes.size(); i++){
        
        customerTable.getSelectionModel().select(i);
        singleCustomer=customerTable.getSelectionModel().getSelectedItem();
        getApplication().getCustomerModule().addCustomer(singleCustomer);
        
        }
        
        customerTable.getSelectionModel().clearSelection();
        
        
      }
    
    
    
    @FXML protected void seeVehicles(ActionEvent event) {
       
        if(singleCustomer!=null){
             
            getApplication().setUI("vehicleUI", singleCustomer);
            
         }else{
             
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select customer");

            alert.showAndWait();
         }
        
        
      }
    
    @FXML protected void seeBookings(ActionEvent event) {
        
         if(singleCustomer!=null){
             
            getApplication().setUI("bookingEditUI", singleCustomer);
            
         }else{
             
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select customer");

            alert.showAndWait();
         }
        
      }
    
    @FXML
    protected void close() {
        getApplication().setUI("customerUI");
    }
    
    
    @FXML
    protected void viewBills() {
        
         if(singleCustomer!=null){
        
        getApplication().setUI("billView",singleCustomer );
        
        }else{
             
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select customer");
            alert.showAndWait();
         }
    }
    
   
   
    
}
