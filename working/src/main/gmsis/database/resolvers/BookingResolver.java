package gmsis.database.resolvers;

import gmsis.database.BaseResolver;
import gmsis.database.Database;
import gmsis.database.Lazy;
import gmsis.database.delete.DeleteQuery;
import gmsis.database.get.Query;
import gmsis.database.put.InsertQuery;
import gmsis.database.put.UpdateQuery;
import gmsis.database.tables.*;
import gmsis.models.*;
import gmsis.models.Booking.BookingType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingResolver extends BaseResolver<Booking> {

    @Override
    public DeleteQuery toDeleteQuery(Database db, Booking booking) {
        return DeleteQuery.builder()
                .table(BookingTable.TABLE)
                .where(BookingTable.COLUMN_ID + " = ?")
                .whereValues(booking.getId());
    }
    
    @Override
    public InsertQuery toInsertQuery(Database db, Booking booking) {
        return InsertQuery.builder()
                .table(BookingTable.TABLE);
    }
    
    @Override
    public UpdateQuery toUpdateQuery(Database db, Booking booking) {
        return UpdateQuery.builder()
                .table(BookingTable.TABLE)
                .where(BookingTable.COLUMN_ID + " = ?")
                .whereValues(booking.getId());
    }
    
    @Override
    public void setObjectId(Database database, Booking booking, long id) {
        booking.setId((int) id);
    }

    @Override
    public Booking toObject(final Database database, ResultSet results) throws SQLException {
        int id = results.getInt(BookingTable.COLUMN_ID);
        final int vehicleId = results.getInt(BookingTable.COLUMN_VEHICLE_ID);
        final int mechanicId = results.getInt(BookingTable.COLUMN_MECHANIC_ID);
        int billId = results.getInt(BookingTable.COLUMN_BILL_ID);
        BookingType type = BookingType.valueOf(results.getString(BookingTable.COLUMN_TYPE));
        Timestamp dateStartTs = results.getTimestamp(BookingTable.COLUMN_DATE_START);
        Date dateStart = dateStartTs != null ? new Date(dateStartTs.getTime()) : null;
        Timestamp dateEndTs = results.getTimestamp(BookingTable.COLUMN_DATE_END);
        Date dateEnd = dateEndTs != null ? new Date(dateEndTs.getTime()) : null;
        
        // Get Bill
        Bill bill = database.get()
                .objects(Bill.class)
                .withQuery(Query.builder()
                    .table(BillTable.TABLE)
                    .where("id = ?")
                    .whereValues(billId))
                .execute()
                .getSingle();

        Lazy<User> lazyMechanic = new Lazy<>(() -> database.get()
                .objects(User.class)
                .withQuery(Query.builder()
                    .table(UserTable.TABLE)
                    .where("id = ?")
                    .whereValues(mechanicId))
                .execute()
                .getSingle());

        final Lazy<Vehicle> lazyVehicle = new Lazy<>(() -> database.get()
                .objects(Vehicle.class)
                .withQuery(Query.builder()
                        .table(VehicleTable.TABLE)
                        .where("id = ?")
                        .whereValues(vehicleId))
                .execute()
                .getSingle());

        Lazy<Customer> lazyCustomer = new Lazy<>(() -> {
            Vehicle loadedVehicle = lazyVehicle.get();
            if(loadedVehicle == null) return null;

            return database.get()
                    .objects(Customer.class)
                    .withQuery(Query.builder()
                            .table(CustomerTable.TABLE)
                            .where("id = ?")
                            .whereValues(loadedVehicle.getCustomerId()))
                    .execute()
                    .getSingle();
        });

        final Lazy<List<BookingRepair>> lazyBookingRepair = new Lazy<>(() -> database.get()
                .objects(BookingRepair.class)
                .withQuery(Query.builder()
                        .table(BookingRepairTable.TABLE)
                        .where(BookingRepairTable.COLUMN_BOOKING_ID + " = ?")
                        .whereValues(id))
                .execute()
                .getList());
        
        return new Booking(id, vehicleId, type, bill, dateStart, dateEnd, lazyBookingRepair, lazyMechanic, lazyCustomer, lazyVehicle);
    }
    
    @Override
    public Map<String, Object> toValues(Booking booking) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(BookingTable.COLUMN_ID, booking.getId());
        values.put(BookingTable.COLUMN_VEHICLE_ID, booking.getVehicleId());
        values.put(BookingTable.COLUMN_MECHANIC_ID, booking.getMechanic() == null ? null : booking.getMechanic().getId());
        values.put(BookingTable.COLUMN_BILL_ID, booking.getBill() == null ? null : booking.getBill().getId());
        values.put(BookingTable.COLUMN_TYPE, booking.getType());
        values.put(BookingTable.COLUMN_DATE_START, new Timestamp(booking.getDateStart().getTime()));
        values.put(BookingTable.COLUMN_DATE_END, new Timestamp(booking.getDateEnd().getTime()));
        return values;
    }
    
    @Override
    public int put(Database database, Booking booking) {
        int result = 0;
        
        // Save bill
        result += database.put()
                .objects(booking.getBill())
                .execute();

        // Save this
        result += super.put(database, booking);

        booking.getBookingRepairs().forEach(bookingRepair -> {
            bookingRepair.setBookingId(booking.getId());
        });
        result += database.put()
                .objects(booking.getBookingRepairs())
                .execute();
        
        return result;
    }
    
    @Override
    public int delete(Database database, Booking booking) {
        int result = 0;
        
        // Delete bill
        result += database.delete()
                .objects(booking.getBill())
                .execute();
        
        // Delete this
        result += super.delete(database, booking);

        result += database.delete()
                .objects(booking.getBookingRepairs())
                .execute();
        
        return result;
    }

    @Override
    public String getTableName() {
        return BookingTable.TABLE;
    }
    
}
