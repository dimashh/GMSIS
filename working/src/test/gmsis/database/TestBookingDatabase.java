package gmsis.database;

import gmsis.database.get.Query;
import gmsis.database.resolvers.BillResolver;
import gmsis.database.resolvers.BookingResolver;
import gmsis.database.tables.BookingTable;
import gmsis.models.Bill;
import gmsis.models.Booking;
import gmsis.models.Booking.BookingType;
import java.util.Date;
import java.util.List;

import gmsis.models.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBookingDatabase {
    
    private static final Bill TEST_BILL_1 = new Bill(false, new Date(), null, 0);
    private static final Booking TEST_BOOKING_1 = new Booking(new User("test", "test", "test", User.UserRole.MECHANIC, 5d), BookingType.DIAGNOSIS_REPAIR, TEST_BILL_1, new Date(), new Date());
    private static final Booking TEST_BOOKING_2 = new Booking(new User("test", "test", "test", User.UserRole.MECHANIC, 5d), BookingType.SCHEDULED_MAINTENANCE, TEST_BILL_1, new Date(), new Date());
    
    private Database database;
    
    public TestBookingDatabase() {
        database = new Database();
        database.init("test.db", null);
        database.registerModel(Booking.class, new BookingResolver());
        database.registerModel(Bill.class, new BillResolver());
        
        // Load test data
        database.put()
                .objects(TEST_BOOKING_1)
                .execute();
    }
    
    @Test
    public void testGetBooking() {
        List<Booking> results = database.get()
                .objects(Booking.class)
                .withQuery(Query.builder()
                    .table(BookingTable.TABLE)
                    .where("id = ?")
                    .whereValues(TEST_BOOKING_1.getId()))
                .execute()
                .getList();
        
        // Check
        assertEquals(1, results.size());
        assertEquals(TEST_BOOKING_1, results.get(0));
    }
    
    @Test
    public void testUpdateBooking() {
        int rowsAffected = database.put()
                .objects(TEST_BOOKING_2)
                .execute();
        
        assertEquals(2, rowsAffected);
        
        // Test
        Booking booking = database.get()
                .objects(Booking.class)
                .withQuery(Query.builder()
                    .table(BookingTable.TABLE)
                    .where("id = ?")
                    .whereValues(TEST_BOOKING_1.getId()))
                .execute()
                .getSingle();
        
        // Check
        assertEquals(TEST_BOOKING_2, booking);
    }
    
    @Test
    public void testDeleteBooking() {
        // Delete
        int rowsAffected = database.delete()
                .objects(TEST_BOOKING_2)
                .execute();
        
        assertEquals(2, rowsAffected);
        
        // Check
        List<Booking> results = database.get()
                .objects(Booking.class)
                .withQuery(Query.builder()
                    .table(BookingTable.TABLE))
                .execute()
                .getList();
        
        // Test empty
        assertEquals(0, results.size());
    }
    
    
    
}
