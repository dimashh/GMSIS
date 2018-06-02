package gmsis.database.resolvers;

import gmsis.database.BaseResolver;
import gmsis.database.Database;
import gmsis.database.Lazy;
import gmsis.database.delete.DeleteQuery;
import gmsis.database.get.Query;
import gmsis.database.put.InsertQuery;
import gmsis.database.put.UpdateQuery;
import gmsis.database.tables.BillTable;
import gmsis.database.tables.BookingTable;
import gmsis.models.Bill;
import gmsis.models.Booking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BillResolver extends BaseResolver<Bill> {

    @Override
    public DeleteQuery toDeleteQuery(Database db, Bill bill) {
        return DeleteQuery.builder()
                .table(BillTable.TABLE)
                .where(BillTable.COLUMN_ID + " = ?")
                .whereValues(bill.getId());
    }
    
    @Override
    public InsertQuery toInsertQuery(Database db, Bill bill) {
        return InsertQuery.builder()
                .table(BillTable.TABLE);
    }
    
    @Override
    public UpdateQuery toUpdateQuery(Database db, Bill bill) {
        return UpdateQuery.builder()
                .table(BillTable.TABLE)
                .where(BillTable.COLUMN_ID + " = ?")
                .whereValues(bill.getId());
    }
    
    @Override
    public void setObjectId(Database database, Bill bill, long id) {
        bill.setId((int) id);
    }

    @Override
    public Bill toObject(final Database database, ResultSet results) throws SQLException {
        final int id = results.getInt(BillTable.COLUMN_ID);
        boolean paid = results.getBoolean(BillTable.COLUMN_PAID);
        Timestamp billTs = results.getTimestamp(BillTable.COLUMN_BILL_DATE);
        final Date billDate = billTs != null ? new Date(billTs.getTime()) : null;
        Timestamp paidTs = results.getTimestamp(BillTable.COLUMN_PAID_DATE);
        Date paidDate = paidTs != null ? new Date(paidTs.getTime()) : null;
        double total = results.getDouble(BillTable.COLUMN_TOTAL);

        Lazy<Booking> lazyBooking = new Lazy<>(() -> database.get()
                .objects(Booking.class)
                .withQuery(Query.builder()
                    .table(BookingTable.TABLE)
                    .where(BookingTable.COLUMN_BILL_ID + " = ?")
                    .whereValues(id))
                .execute()
                .getSingle());

        return new Bill(id, lazyBooking, paid, billDate, paidDate, total);
    }
    
    @Override
    public Map<String, Object> toValues(Bill bill) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(BillTable.COLUMN_ID, bill.getId());
        values.put(BillTable.COLUMN_PAID, bill.isPaid());
        values.put(BillTable.COLUMN_BILL_DATE, bill.getBillDate());
        values.put(BillTable.COLUMN_PAID_DATE, bill.getPaidDate());
        values.put(BillTable.COLUMN_TOTAL, bill.getTotal());
        return values;
    }

    @Override
    public String getTableName() {
        return BillTable.TABLE;
    }
    
}
