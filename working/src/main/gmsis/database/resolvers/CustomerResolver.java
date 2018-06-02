package gmsis.database.resolvers;

import gmsis.database.BaseResolver;
import gmsis.database.Database;
import gmsis.database.Lazy;
import gmsis.database.delete.DeleteQuery;
import gmsis.database.get.Query;
import gmsis.database.put.InsertQuery;
import gmsis.database.put.UpdateQuery;
import gmsis.database.tables.AddressTable;
import gmsis.database.tables.CustomerTable;
import gmsis.database.tables.VehicleTable;
import gmsis.models.Address;
import gmsis.models.Customer;
import gmsis.models.Customer.CustomerType;
import gmsis.models.Vehicle;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerResolver extends BaseResolver<Customer> {

    @Override
    public DeleteQuery toDeleteQuery(Database db, Customer customer) {
        return DeleteQuery.builder()
                .table(CustomerTable.TABLE)
                .where(CustomerTable.COLUMN_ID + " = ?")
                .whereValues(customer.getId());
    }
    
    @Override
    public InsertQuery toInsertQuery(Database db, Customer customer) {
        return InsertQuery.builder()
                .table(CustomerTable.TABLE);
    }
    
    @Override
    public UpdateQuery toUpdateQuery(Database db, Customer customer) {
        return UpdateQuery.builder()
                .table(CustomerTable.TABLE)
                .where(CustomerTable.COLUMN_ID + " = ?")
                .whereValues(customer.getId());
    }
    
    @Override
    public void setObjectId(Database database, Customer customer, long id) {
        customer.setId((int) id);
    }

    @Override
    public Customer toObject(final Database database, ResultSet results) throws SQLException {
        final Integer id = results.getInt(CustomerTable.COLUMN_ID);
        CustomerType type = CustomerType.valueOf(results.getString(CustomerTable.COLUMN_TYPE));
        Integer addressId = results.getInt(CustomerTable.COLUMN_ADDRESS_ID);
        String firstname = results.getString(CustomerTable.COLUMN_FIRSTNAME);
        String surname = results.getString(CustomerTable.COLUMN_SURNAME);
        String phoneNumber = results.getString(CustomerTable.COLUMN_PHONE_NUMBER);
        String email = results.getString(CustomerTable.COLUMN_EMAIL);
        
        Address address = database.get()
                .objects(Address.class)
                .withQuery(Query.builder()
                    .table(AddressTable.TABLE)
                    .where("id = ?")
                    .whereValues(addressId))
                .execute()
                .getSingle();

        Lazy<List<Vehicle>> lazyVehicles = new Lazy<>(() -> database.get()
                .objects(Vehicle.class)
                .withQuery(Query.builder()
                        .table(VehicleTable.TABLE)
                        .where("customerId = ?")
                        .whereValues(id))
                .execute()
                .getList());
        
        return new Customer(id, type, firstname, surname, phoneNumber, email, 
                address, lazyVehicles);
    }
    
    @Override
    public Map<String, Object> toValues(Customer customer) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(CustomerTable.COLUMN_ID, customer.getId());
        values.put(CustomerTable.COLUMN_TYPE, customer.getType());
        values.put(CustomerTable.COLUMN_FIRSTNAME, customer.getFirstname());
        values.put(CustomerTable.COLUMN_SURNAME, customer.getSurname());
        values.put(CustomerTable.COLUMN_ADDRESS_ID, customer.getAddress().getId());
        values.put(CustomerTable.COLUMN_PHONE_NUMBER, customer.getPhoneNumber());
        values.put(CustomerTable.COLUMN_EMAIL, customer.getEmail());
        return values;
    }
    
    @Override
    public int put(Database database, Customer customer) {
        int result = 0;

        // Save address
        result += database.put()
                .objects(customer.getAddress())
                .execute();

        // Save this
        result += super.put(database, customer);
        
        // Save vehicles
        // Update customer id for vehicles
        for(Vehicle v : customer.getVehicles()) v.setCustomerId(customer.getId());
        result += database.put()
                .objects(customer.getVehicles())
                .execute();
        
        return result;
    }
    
    @Override
    public int delete(Database database, Customer customer) {
        int result = 0;
        
        // Delete address
        result += database.delete()
                .objects(customer.getAddress())
                .execute();

        // Delete vehicles
        result += database.delete()
                .objects(customer.getVehicles())
                .execute();

        // Delete this
        result += super.delete(database, customer);
        
        return result;
    }

    @Override
    public String getTableName() {
        return CustomerTable.TABLE;
    }
    
}
