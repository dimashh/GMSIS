package gmsis.database.resolvers;

import gmsis.database.BaseResolver;
import gmsis.database.Database;
import gmsis.database.delete.DeleteQuery;
import gmsis.database.get.Query;
import gmsis.database.put.InsertQuery;
import gmsis.database.put.UpdateQuery;
import gmsis.database.tables.AddressTable;
import gmsis.database.tables.PartTable;
import gmsis.database.tables.SpecialistRepairCentreTable;
import gmsis.models.Address;
import gmsis.models.SpecialistRepairCentre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SpecialistRepairCentreResolver extends BaseResolver<SpecialistRepairCentre> {

    @Override
    public DeleteQuery toDeleteQuery(Database database, SpecialistRepairCentre spc) {
        return DeleteQuery.builder()
                .table(SpecialistRepairCentreTable.TABLE)
                .where(SpecialistRepairCentreTable.COLUMN_ID + " = ?")
                .whereValues(spc.getId());
    }

    @Override
    public InsertQuery toInsertQuery(Database database, SpecialistRepairCentre spc) {
        return InsertQuery.builder()
                .table(SpecialistRepairCentreTable.TABLE);
    }

    @Override
    public UpdateQuery toUpdateQuery(Database database, SpecialistRepairCentre spc) {
        return UpdateQuery.builder()
                .table(SpecialistRepairCentreTable.TABLE)
                .where(SpecialistRepairCentreTable.COLUMN_ID + " = ?")
                .whereValues(spc.getId());
    }

    @Override
    public void setObjectId(Database database, SpecialistRepairCentre spc, long id) {
        spc.setId((int) id);
    }

    @Override
    public SpecialistRepairCentre toObject(Database database, ResultSet results) throws SQLException {
        Integer id = results.getInt(SpecialistRepairCentreTable.COLUMN_ID);
        String name = results.getString(SpecialistRepairCentreTable.COLUMN_NAME);
        Integer addressId = results.getInt(SpecialistRepairCentreTable.COLUMN_ADDRESS_ID);
        String phoneNumber = results.getString(SpecialistRepairCentreTable.COLUMN_PHONE_NUMBER);
        String email = results.getString(SpecialistRepairCentreTable.COLUMN_EMAIL);

        Address address = database.get()
                .objects(Address.class)
                .withQuery(Query.builder()
                        .table(AddressTable.TABLE)
                        .where("id = ?")
                        .whereValues(addressId))
                .execute()
                .getSingle();

        return new SpecialistRepairCentre(id, name, address, phoneNumber, email);
    }

    @Override
    public Map<String, Object> toValues(SpecialistRepairCentre spc) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(SpecialistRepairCentreTable.COLUMN_ID, spc.getId());
        values.put(SpecialistRepairCentreTable.COLUMN_NAME, spc.getName());
        values.put(SpecialistRepairCentreTable.COLUMN_ADDRESS_ID, spc.getAddress() == null ? null : spc.getAddress().getId());
        values.put(SpecialistRepairCentreTable.COLUMN_PHONE_NUMBER, spc.getPhoneNumber());
        values.put(SpecialistRepairCentreTable.COLUMN_EMAIL, spc.getEmail());
        return values;
    }

    @Override
    public int put(Database database, SpecialistRepairCentre spc) {
        int result = 0;

        // Save address
        result += database.put()
                .objects(spc.getAddress())
                .execute();

        // Save this
        result += super.put(database, spc);

        return result;
    }

    @Override
    public int delete(Database database, SpecialistRepairCentre spc) {
        int result = 0;

        // Delete address
        result += database.delete()
                .objects(spc.getAddress())
                .execute();

        // Delete this
        result += super.delete(database, spc);

        return result;
    }

    @Override
    public String getTableName() {
        return SpecialistRepairCentreTable.TABLE;
    }

}
