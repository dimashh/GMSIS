package gmsis.database.resolvers;

import gmsis.database.BaseResolver;
import gmsis.database.Database;
import gmsis.database.delete.DeleteQuery;
import gmsis.database.put.InsertQuery;
import gmsis.database.put.UpdateQuery;
import gmsis.database.tables.AddressTable;
import gmsis.models.Address;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AddressResolver extends BaseResolver<Address> {

    @Override
    public DeleteQuery toDeleteQuery(Database db, Address address) {
        return DeleteQuery.builder()
                .table(AddressTable.TABLE)
                .where(AddressTable.COLUMN_ID + " = ?")
                .whereValues(address.getId());
    }
    
    @Override
    public InsertQuery toInsertQuery(Database db, Address address) {
        return InsertQuery.builder()
                .table(AddressTable.TABLE);
    }
    
    @Override
    public UpdateQuery toUpdateQuery(Database db, Address address) {
        return UpdateQuery.builder()
                .table(AddressTable.TABLE)
                .where(AddressTable.COLUMN_ID + " = ?")
                .whereValues(address.getId());
    }
    
    @Override
    public void setObjectId(Database database, Address address, long id) {
        address.setId((int) id);
    }

    @Override
    public Address toObject(Database db, ResultSet results) throws SQLException {
        Integer id = results.getInt(AddressTable.COLUMN_ID);
        String addressLine = results.getString(AddressTable.COLUMN_ADDRESS_LINE);
        String town = results.getString(AddressTable.COLUMN_TOWN);
        String county = results.getString(AddressTable.COLUMN_COUNTY);
        String postcode = results.getString(AddressTable.COLUMN_POSTCODE);

        return new Address(id, addressLine, town, county, postcode);
    }
    
    @Override
    public Map<String, Object> toValues(Address address) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(AddressTable.COLUMN_ID, address.getId());
        values.put(AddressTable.COLUMN_ADDRESS_LINE, address.getAddressLine());
        values.put(AddressTable.COLUMN_TOWN, address.getTown());
        values.put(AddressTable.COLUMN_COUNTY, address.getCounty());
        values.put(AddressTable.COLUMN_POSTCODE, address.getPostcode());
        return values;
    }

    @Override
    public String getTableName() {
        return AddressTable.TABLE;
    }
    
}
