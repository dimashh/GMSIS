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
import gmsis.models.Vehicle.FuelType;
import gmsis.models.Vehicle.VehicleType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleResolver extends BaseResolver<Vehicle> {

    @Override
    public DeleteQuery toDeleteQuery(Database db, Vehicle vehicle) {
        return DeleteQuery.builder()
                .table(VehicleTable.TABLE)
                .where(VehicleTable.COLUMN_ID + " = ?")
                .whereValues(vehicle.getId());
    }
    
    @Override
    public InsertQuery toInsertQuery(Database db, Vehicle vehicle) {
        return InsertQuery.builder()
                .table(VehicleTable.TABLE);
    }
    
    @Override
    public UpdateQuery toUpdateQuery(Database db, Vehicle vehicle) {
        return UpdateQuery.builder()
                .table(VehicleTable.TABLE)
                .where(VehicleTable.COLUMN_ID + " = ?")
                .whereValues(vehicle.getId());
    }
    
    @Override
    public void setObjectId(Database database, Vehicle vehicle, long id) {
        vehicle.setId((int) id);
    }

    @Override
    public Vehicle toObject(final Database database, ResultSet results) throws SQLException {
        final Integer id = results.getInt(VehicleTable.COLUMN_ID);
        VehicleType type = VehicleType.valueOf(results.getString(VehicleTable.COLUMN_TYPE));
        Integer customerId = results.getInt(VehicleTable.COLUMN_CUSTOMER_ID);
        Integer warrantyId = results.getInt(VehicleTable.COLUMN_WARRANTY_ID);
        if(results.wasNull()) warrantyId = null;
        String registration = results.getString(VehicleTable.COLUMN_REGISTRATION);
        String model = results.getString(VehicleTable.COLUMN_MODEL);
        String make = results.getString(VehicleTable.COLUMN_MAKE);
        String engineSize = results.getString(VehicleTable.COLUMN_ENGINE_SIZE);
        FuelType fuelType = FuelType.valueOf(results.getString(VehicleTable.COLUMN_FUEL_TYPE));
        String colour = results.getString(VehicleTable.COLUMN_COLOUR);
        Timestamp motRenewalDateTs = results.getTimestamp(VehicleTable.COLUMN_MOT_RENEWAL_DATE);
        Date motRenewalDate = motRenewalDateTs != null ? new Date(motRenewalDateTs.getTime()) : null;
        Timestamp lastServiceDateTs = results.getTimestamp(VehicleTable.COLUMN_LAST_SERVICE_DATE);
        Date lastServiceDate = lastServiceDateTs != null ? new Date(lastServiceDateTs.getTime()) : null;
        int currentMileage = results.getInt(VehicleTable.COLUMN_CURRENT_MILEAGE);
        
        // Get Warranty
        Warranty warranty = warrantyId == null ? null : database.get()
                .objects(Warranty.class)
                .withQuery(Query.builder()
                    .table(WarrantyTable.TABLE)
                    .where("id = ?")
                    .whereValues(warrantyId))
                .execute()
                .getSingle();

        Lazy<List<Part>> lazyParts = new Lazy<>(() -> database.get()
                .objects(Part.class)
                .withQuery(Query.builder()
                        .table(PartTable.TABLE)
                        .where("vehicleId = ?")
                        .whereValues(id))
                .execute()
                .getList());

        Lazy<List<Booking>> lazyBookings = new Lazy<>(() -> database.get()
                .objects(Booking.class)
                .withQuery(Query.builder()
                        .table(BookingTable.TABLE)
                        .where("vehicleId = ?")
                        .whereValues(id))
                .execute()
                .getList());

        Lazy<Customer> lazyCustomer = new Lazy<>(() -> database.get()
                .objects(Customer.class)
                .withQuery(Query.builder()
                        .table(CustomerTable.TABLE)
                        .where("id = ?")
                        .whereValues(customerId))
                .execute()
                .getSingle());
        
        return new Vehicle(id, customerId, type, warranty, registration, model,
            make, engineSize, fuelType, colour, motRenewalDate, lastServiceDate, 
                currentMileage, lazyParts, lazyBookings, lazyCustomer);
    }
    
    @Override
    public Map<String, Object> toValues(Vehicle vehicle) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(VehicleTable.COLUMN_ID, vehicle.getId());
        values.put(VehicleTable.COLUMN_TYPE, vehicle.getVehicleType());
        values.put(VehicleTable.COLUMN_CUSTOMER_ID, vehicle.getCustomerId());
        values.put(VehicleTable.COLUMN_WARRANTY_ID, vehicle.getWarranty() == null ? null : vehicle.getWarranty().getId());
        values.put(VehicleTable.COLUMN_REGISTRATION, vehicle.getRegistration());
        values.put(VehicleTable.COLUMN_MODEL, vehicle.getModel());
        values.put(VehicleTable.COLUMN_MAKE, vehicle.getMake());
        values.put(VehicleTable.COLUMN_ENGINE_SIZE, vehicle.getEngineSize());
        values.put(VehicleTable.COLUMN_FUEL_TYPE, vehicle.getFuelType());
        values.put(VehicleTable.COLUMN_COLOUR, vehicle.getColour());
        values.put(VehicleTable.COLUMN_MOT_RENEWAL_DATE, vehicle.getMotRenewalDate());
        values.put(VehicleTable.COLUMN_LAST_SERVICE_DATE, vehicle.getLastServiceDate());
        values.put(VehicleTable.COLUMN_CURRENT_MILEAGE, vehicle.getCurrentMileage());
        return values;
    }
    
    @Override
    public int put(Database database, Vehicle vehicle) {
        int result = 0;
        
        // Save warranty
        result += database.put()
                .objects(vehicle.getWarranty())
                .execute();

        // Save this
        result += super.put(database, vehicle);

        // Save parts
        for(Part p : vehicle.getParts()) p.setVehicleId(vehicle.getId());
        result += database.put()
                .objects(vehicle.getParts())
                .execute();

        // Save bookings
        for(Booking b : vehicle.getBookings()) b.setVehicleId(vehicle.getId());
        result += database.put()
                .objects(vehicle.getBookings())
                .execute();
        
        return result;
    }
    
    @Override
    public int delete(Database database, Vehicle vehicle) {
        int result = 0;
        
        // Delete warranty
        result += database.delete()
                .objects(vehicle.getWarranty())
                .execute();
        
        // delete bookings associated to vehicle
        result += database.delete()
                .objects(vehicle.getBookings())
                .execute();
        
        // delete parts associated to vehicle
        result += database.delete()
                .objects(vehicle.getParts())
                .execute();
        
        // Delete this
        result += super.delete(database, vehicle);
        
        return result;
    }

    @Override
    public String getTableName() {
        return VehicleTable.TABLE;
    }
    
}
