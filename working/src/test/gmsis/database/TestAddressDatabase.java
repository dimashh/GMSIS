package gmsis.database;

import gmsis.database.get.Query;
import gmsis.database.resolvers.AddressResolver;
import gmsis.database.tables.AddressTable;
import gmsis.models.Address;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestAddressDatabase {
    
    private static final Address TEST_ADDRESS_1 = new Address(1, "123 Test Street", "London", "Greater London", "E45896");
    private static final Address TEST_ADDRESS_2 = new Address(1, "456 Test Street", "London", "Greater London", "E45896");
    
    private Database database;
    
    public TestAddressDatabase() {
        database = new Database();
        database.init("test.db", null);
        database.registerModel(Address.class, new AddressResolver());
        
        // Load test data
        database.put()
                .objects(TEST_ADDRESS_1)
                .execute();
    }
    
    @Test
    public void testGetAddress() {
        List<Address> results = database.get()
                .objects(Address.class)
                .withQuery(Query.builder()
                    .table(AddressTable.TABLE)
                    .where("id = ?")
                    .whereValues(TEST_ADDRESS_1.getId()))
                .execute()
                .getList();
        
        // Check
        assertEquals(1, results.size());
        assertEquals(TEST_ADDRESS_1, results.get(0));
    }
    
    @Test
    public void testUpdateAddress() {
        int rowsAffected = database.put()
                .objects(TEST_ADDRESS_2)
                .execute();
        
        assertEquals(1, rowsAffected);
        
        // Test
        Address address = database.get()
                .objects(Address.class)
                .withQuery(Query.builder()
                    .table(AddressTable.TABLE)
                    .where("id = ?")
                    .whereValues(TEST_ADDRESS_1.getId()))
                .execute()
                .getSingle();
        
        // Check
        assertEquals(TEST_ADDRESS_2, address);
    }
    
    @Test
    public void testDeleteAddress() {
        // Delete
        int rowsAffected = database.delete()
                .objects(TEST_ADDRESS_2)
                .execute();
        
        assertEquals(1, rowsAffected);
        
        // Check
        List<Address> results = database.get()
                .objects(Address.class)
                .withQuery(Query.builder()
                    .table(AddressTable.TABLE))
                .execute()
                .getList();
        
        // Test empty
        assertEquals(0, results.size());
    }
    
    
    
}
