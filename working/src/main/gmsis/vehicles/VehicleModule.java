package gmsis.vehicles;

import gmsis.App;
import gmsis.database.get.Query;
import gmsis.database.tables.CustomerTable;
import gmsis.database.tables.VehicleTable;
import gmsis.models.Customer;
import gmsis.models.Vehicle;
import java.util.List;

public class VehicleModule {

    private App application;
    /**
     *
     * @param instance
     */
    public VehicleModule(App instance) {
        application = instance;     
    }
    

    public List<Vehicle> getVehicles() {
        return application.getDatabase().get()
                .objects(Vehicle.class)
                .withQuery(Query.builder()
                .table(VehicleTable.TABLE))
                .execute()
                .getList();
    }
    
    public void addVehicle(Vehicle newVehicle){
        application.getDatabase().put()
                 .objects(newVehicle)
                 .execute();
    }
    
    public void deleteVehicle(Vehicle dVehicle){
        application.getDatabase().delete()
                 .objects(dVehicle)
                 .execute();
    }
    
    public List<Vehicle> getCustomerVehicles(Customer customer1) {
        return application.getDatabase().get()
                .objects(Vehicle.class)
                .withQuery(Query.builder()
                .table(VehicleTable.TABLE)
                .where("customerId=?")
                .whereValues(customer1.getId()))
                .execute()
                .getList();
    }
    
    public Customer getOwner(Vehicle vehicle) {
        return application.getDatabase().get()
                .objects(Customer.class)
                .withQuery(Query.builder()
                .table(CustomerTable.TABLE)
                .where("id=?")
                .whereValues(vehicle.getCustomerId()))
                .execute()
                .getSingle();
    }
    
}
