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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookingRepairResolver extends BaseResolver<BookingRepair> {

    @Override
    public DeleteQuery toDeleteQuery(Database database, BookingRepair bookingRepair) {
        return DeleteQuery.builder()
                .table(BookingRepairTable.TABLE)
                .where(BookingRepairTable.COLUMN_ID + " = ?")
                .whereValues(bookingRepair.getId());
    }

    @Override
    public InsertQuery toInsertQuery(Database database, BookingRepair bookingRepair) {
        return InsertQuery.builder()
                .table(BookingRepairTable.TABLE);
    }

    @Override
    public UpdateQuery toUpdateQuery(Database database, BookingRepair bookingRepair) {
        return UpdateQuery.builder()
                .table(BookingRepairTable.TABLE)
                .where(BookingRepairTable.COLUMN_ID + " = ?")
                .whereValues(bookingRepair.getId());
    }

    @Override
    public void setObjectId(Database database, BookingRepair bookingRepair, long id) {
        bookingRepair.setId((int) id);
    }

    @Override
    public BookingRepair toObject(Database database, ResultSet results) throws SQLException {
        Integer id = results.getInt(BookingRepairTable.COLUMN_ID);
        Integer bookingId = results.getInt(BookingRepairTable.COLUMN_BOOKING_ID);
        Integer partId = results.getInt(BookingRepairTable.COLUMN_PART_ID);
        if(results.wasNull()) partId = null;
        BookingRepair.RepairType repairType = BookingRepair.RepairType.valueOf(results.getString(BookingRepairTable.COLUMN_REPAIR_TYPE));
        Double spcPrice = results.getDouble(BookingRepairTable.COLUMN_SPC_PRICE);
        Integer spcId = results.getInt(BookingRepairTable.COLUMN_SPC_ID);
        if(results.wasNull()) spcId = null;
        Timestamp dateDeliveryTs = results.getTimestamp(BookingRepairTable.COLUMN_SPC_DELIVERY_DATE);
        Date dateDelivery = dateDeliveryTs != null ? new Date(dateDeliveryTs.getTime()) : null;
        Timestamp dateReturnTs = results.getTimestamp(BookingRepairTable.COLUMN_SPC_RETURN_DATE);
        Date dateReturn = dateReturnTs != null ? new Date(dateReturnTs.getTime()) : null;

        Lazy<Booking> lazyBooking = new Lazy<>(() -> database.get()
                .objects(Booking.class)
                .withQuery(Query.builder()
                        .table(BookingTable.TABLE)
                        .where(BookingTable.COLUMN_ID + " = ?")
                        .whereValues(bookingId))
                .execute()
                .getSingle());

        Part part = partId == null ? null :
            database.get()
                    .objects(Part.class)
                    .withQuery(Query.builder()
                            .table(PartTable.TABLE)
                            .where(PartTable.COLUMN_ID + " = ?")
                            .whereValues(partId))
                    .execute()
                    .getSingle();

        SpecialistRepairCentre spc = spcId == null ? null :
            database.get()
                    .objects(SpecialistRepairCentre.class)
                    .withQuery(Query.builder()
                            .table(SpecialistRepairCentreTable.TABLE)
                            .where(SpecialistRepairCentreTable.COLUMN_ID + " = ?")
                            .whereValues(spcId))
                    .execute()
                    .getSingle();

        return new BookingRepair(id, bookingId, part, spc, repairType, spcPrice, dateDelivery, dateReturn, lazyBooking);
    }

    @Override
    public Map<String, Object> toValues(BookingRepair bookingRepair) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(BookingRepairTable.COLUMN_ID, bookingRepair.getId());
        values.put(BookingRepairTable.COLUMN_BOOKING_ID, bookingRepair.getBookingId());
        values.put(BookingRepairTable.COLUMN_PART_ID, bookingRepair.getPart() != null ? bookingRepair.getPart().getId() : null);
        values.put(BookingRepairTable.COLUMN_REPAIR_TYPE, bookingRepair.getRepairType());
        values.put(BookingRepairTable.COLUMN_SPC_PRICE, bookingRepair.getSpcPrice());
        values.put(BookingRepairTable.COLUMN_SPC_ID, bookingRepair.getSpc() != null ? bookingRepair.getSpc().getId() : null);
        values.put(BookingRepairTable.COLUMN_SPC_DELIVERY_DATE, bookingRepair.getSpcDeliveryDate());
        values.put(BookingRepairTable.COLUMN_SPC_RETURN_DATE, bookingRepair.getSpcReturnDate());
        return values;
    }

    @Override
    public String getTableName() {
        return BookingRepairTable.TABLE;
    }

}
