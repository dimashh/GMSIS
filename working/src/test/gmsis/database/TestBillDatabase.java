package gmsis.database;

import gmsis.database.get.Query;
import gmsis.database.resolvers.BillResolver;
import gmsis.database.tables.BillTable;
import gmsis.models.Bill;
import java.util.Date;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBillDatabase {
    
    private static final Bill TEST_BILL_1 = new Bill(false, new Date(), null, 60);
    private static final Bill TEST_BILL_2 = new Bill(true, new Date(), new Date(), 60);
    
    private Database database;
    
    public TestBillDatabase() {
        database = new Database();
        database.init("test.db", null);
        database.registerModel(Bill.class, new BillResolver());
        
        // Load test data
        database.put()
                .objects(TEST_BILL_1)
                .execute();
    }
    
    @Test
    public void testGetBill() {
        List<Bill> results = database.get()
                .objects(Bill.class)
                .withQuery(Query.builder()
                    .table(BillTable.TABLE)
                    .where("id = ?")
                    .whereValues(TEST_BILL_1.getId()))
                .execute()
                .getList();
        
        // Check
        assertEquals(1, results.size());
        assertEquals(TEST_BILL_1, results.get(0));
    }
    
    @Test
    public void testUpdateBill() {
        int rowsAffected = database.put()
                .objects(TEST_BILL_2)
                .execute();
        
        assertEquals(1, rowsAffected);
        
        // Test
        Bill bill = database.get()
                .objects(Bill.class)
                .withQuery(Query.builder()
                    .table(BillTable.TABLE)
                    .where("id = ?")
                    .whereValues(TEST_BILL_1.getId()))
                .execute()
                .getSingle();
        
        // Check
        assertEquals(TEST_BILL_2, bill);
    }
    
    @Test
    public void testDeleteBill() {
        // Delete
        int rowsAffected = database.delete()
                .objects(TEST_BILL_2)
                .execute();
        
        assertEquals(1, rowsAffected);
        
        // Check
        List<Bill> results = database.get()
                .objects(Bill.class)
                .withQuery(Query.builder()
                    .table(BillTable.TABLE))
                .execute()
                .getList();
        
        // Test empty
        assertEquals(0, results.size());
    }
    
    
    
}
