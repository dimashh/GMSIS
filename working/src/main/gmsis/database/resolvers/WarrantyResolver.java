package gmsis.database.resolvers;

import gmsis.database.BaseResolver;
import gmsis.database.Database;
import gmsis.database.delete.DeleteQuery;
import gmsis.database.get.Query;
import gmsis.database.put.InsertQuery;
import gmsis.database.put.UpdateQuery;
import gmsis.database.tables.AddressTable;
import gmsis.database.tables.BillTable;
import gmsis.database.tables.WarrantyTable;
import gmsis.models.Address;
import gmsis.models.Warranty;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WarrantyResolver extends BaseResolver<Warranty> {

    @Override
    public DeleteQuery toDeleteQuery(Database db, Warranty warranty) {
        return DeleteQuery.builder()
                .table(WarrantyTable.TABLE)
                .where(WarrantyTable.COLUMN_ID + " = ?")
                .whereValues(warranty.getId());
    }
    
    @Override
    public InsertQuery toInsertQuery(Database db, Warranty warranty) {
        return InsertQuery.builder()
                .table(WarrantyTable.TABLE);
    }
    
    @Override
    public UpdateQuery toUpdateQuery(Database db, Warranty warranty) {
        return UpdateQuery.builder()
                .table(WarrantyTable.TABLE)
                .where(WarrantyTable.COLUMN_ID + " = ?")
                .whereValues(warranty.getId());
    }
    
    @Override
    public void setObjectId(Database database, Warranty warranty, long id) {
        warranty.setId((int) id);
    }

    @Override
    public Warranty toObject(Database database, ResultSet results) throws SQLException {
        int id = results.getInt(WarrantyTable.COLUMN_ID);
        String companyName = results.getString(WarrantyTable.COLUMN_COMPANY_NAME);
        int addressId = results.getInt(WarrantyTable.COLUMN_COMPANY_ADDRESS_ID);
        Timestamp expiryDateTs = results.getTimestamp(WarrantyTable.COLUMN_EXPIRY_DATE);
        Date expiryDate = expiryDateTs != null ? new Date(expiryDateTs.getTime()) : null;
        
        // Get Address
        Address address = database.get()
                .objects(Address.class)
                .withQuery(Query.builder()
                    .table(AddressTable.TABLE)
                    .where("id = ?")
                    .whereValues(addressId))
                .execute()
                .getSingle();
        
        return new Warranty(id, companyName, address, expiryDate);
    }
    
    @Override
    public Map<String, Object> toValues(Warranty warranty) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(WarrantyTable.COLUMN_ID, warranty.getId());
        values.put(WarrantyTable.COLUMN_COMPANY_ADDRESS_ID, warranty.getCompanyAddress().getId());
        values.put(WarrantyTable.COLUMN_COMPANY_NAME, warranty.getCompanyName());
        values.put(WarrantyTable.COLUMN_EXPIRY_DATE, warranty.getExpiryDate());
        return values;
    }
    
    @Override
    public int put(Database database, Warranty warranty) {
        int result = 0;
        
        // Save address
        result += database.put()
                .objects(warranty.getCompanyAddress())
                .execute();
        
        // Save this
        result += super.put(database, warranty);
        
        return result;
    }
    
    @Override
    public int delete(Database database, Warranty warranty) {
        int result = 0;
        
        // Delete address
        result += database.delete()
                .objects(warranty.getCompanyAddress())
                .execute();
        
        // Delete this
        result += super.delete(database, warranty);
        
        return result;
    }

    @Override
    public String getTableName() {
        return WarrantyTable.TABLE;
    }
    
}
