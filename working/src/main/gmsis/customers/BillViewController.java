/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmsis.customers;

import gmsis.BaseController;
import gmsis.models.Bill;
import gmsis.models.Customer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.Callback;
import gmsis.database.get.Query;
import gmsis.database.tables.UserTable;
import java.text.DecimalFormat;

/**
 *
 * @author ikbalhussainali
 */


public class BillViewController extends BaseController {


    @FXML private Button backButton;
    @FXML private Text title;
    @FXML private TableView<Bill> billView;
    @FXML private TableColumn<Bill, String> status;
    @FXML private TableColumn<Bill, String> billDate;
    @FXML private TableColumn<Bill, String> paidDate;
    @FXML private TableColumn<Bill, String> amount;
    private List<Bill> ndata;
    private ObservableList<Bill> data = FXCollections.observableArrayList();
    private ObservableList<String> strings = FXCollections.observableArrayList();
    private Customer currentCustomer;
    private Bill selectedBill ;
    

    public BillViewController(List<Object> args) { 
        if(args != null && args.size() > 0) {
            if(args.get(0) instanceof Customer) {
                currentCustomer = (Customer) args.get(0);
             }
        }
    }
    
    @FXML
    protected void initialize() throws ParseException {
        title.setText("Bills for " + currentCustomer.getFirstname() + " " + currentCustomer.getSurname());
        load();
    }
    @FXML
    protected void load() throws ParseException {
        
        if(data !=null){data.clear();}
        ndata = currentCustomer.getBills();
        strings.add("Not Paid");
        strings.add("Paid");
        strings.add("Pending");
        
        
        
        for(int i=0; i<ndata.size(); i++){
            
            data.add(ndata.get(i));
        }
        
        
        
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy");
        
        status.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>() {
        
          @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> c) {
                
                if(c.getValue().isPaid()==false){
                    
                    return new SimpleStringProperty(strings.get(0));
                }else{
                
                   return new SimpleStringProperty(strings.get(1));
                
              }
                
            }
           }); 
         
         
         paidDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>() {
        
          @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> c) {
                
                if(c.getValue().getPaidDate()==null){
                    
                 return new SimpleStringProperty(strings.get(2));
                 
                }else{
                    
                     return new SimpleStringProperty(df.format(c.getValue().getPaidDate()));
                }
              }
           }); 
        
         
         
         
       
         
         
          billDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>() {
        
          @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> c) {
            return new SimpleStringProperty(df.format(c.getValue().getBillDate()));
              }
           }); 
         
      
         DecimalFormat df2 = new DecimalFormat(".##");
         amount.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>() {
        
          @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> c) {
            return new SimpleStringProperty(String.format("£%.2f", c.getValue().getTotal()));
              }
           }); 
         
        
         
        
         
         
        billView.setItems(data);
        
    
    }
    
    @FXML protected void settleBill(){
        
         selectedBill = billView.getSelectionModel().getSelectedItem();
        
         if(selectedBill!=null){
        
             if(selectedBill.isPaid()==false){
                selectedBill.setPaid(true);
                selectedBill.setPaidDate(new Date());
                
                
                 try{
             
             getApplication().getDatabase().put()
                .objects(selectedBill)
                .execute();
             
             load();
             
             }catch(ParseException e){
                 //nothinggg
             }
                
             }else{
                 
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Bill has been already settled");

            alert.showAndWait();
            
            
            
             }
            
        
        }else{
             
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select Bill");

            alert.showAndWait();
         }
        
        
        
        
    }
    
    @FXML protected void extraInfo(){
        
        
        selectedBill = billView.getSelectionModel().getSelectedItem();
        
         if(selectedBill!=null){
        
        getApplication().setUI("bookingEditUI",selectedBill.getBooking() );
        
        }else{
             
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select Bill");

            alert.showAndWait();
         }
        
        
        
    }


    public void onBack(ActionEvent actionEvent) {
        getApplication().setUI("customerUI", currentCustomer);
    }
}

