package gmsis.customers;


import gmsis.database.get.Query;
import gmsis.database.get.GetBuilder;
import gmsis.database.get.GetResult;
import gmsis.database.tables.CustomerTable;
import gmsis.models.Customer;
import gmsis.App;
import java.util.List;

public class CustomerModule {

    final private App application;
    
    /**
     *
     * @param instance
     */
    public CustomerModule(App instance) {
        
       application=instance;
    }
    
   
    public List<Customer> getCustomer(){
        
        
       return application.getDatabase().get()
                 .objects(Customer.class)
                 .withQuery(Query.builder()
                    .table(CustomerTable.TABLE)
                    // .where("firstname = ?")
                    //.whereValues(id)
                    )
                 .execute()
                 .getList();
        
        //
        
        
    }
    
    
    public void addCustomer(Customer newCustomer){
        
                application.getDatabase().put()
                 .objects(newCustomer)
                 .execute();
        
        
        
    }
    
    public void deleteCustomer(Customer dCustomer){
        
                application.getDatabase().delete()
                 .objects(dCustomer)
                 .execute();
        
        
        
    }
    
    

}
