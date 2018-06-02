package gmsis.database.resolvers;

import gmsis.database.BaseResolver;
import gmsis.database.Database;
import gmsis.database.delete.DeleteQuery;
import gmsis.database.put.InsertQuery;
import gmsis.database.put.UpdateQuery;
import gmsis.database.tables.PartTable;
import gmsis.models.Part;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PartResolver extends BaseResolver<Part> {

    @Override
    public DeleteQuery toDeleteQuery(Database db, Part part) {
        return DeleteQuery.builder()
                .table(PartTable.TABLE)
                .where(PartTable.COLUMN_ID + " = ?")
                .whereValues(part.getId());
    }
    
    @Override
    public InsertQuery toInsertQuery(Database db, Part part) {
        return InsertQuery.builder()
                .table(PartTable.TABLE);
    }
    
    @Override
    public UpdateQuery toUpdateQuery(Database db, Part part) {
        return UpdateQuery.builder()
                .table(PartTable.TABLE)
                .where(PartTable.COLUMN_ID + " = ?")
                .whereValues(part.getId());
    }
    
    @Override
    public void setObjectId(Database database, Part part, long id) {
        part.setId((int) id);
    }

    @Override
    public Part toObject(Database db, ResultSet results) throws SQLException {
        Integer id = results.getInt(PartTable.COLUMN_ID);
        String name = results.getString(PartTable.COLUMN_NAME);
        Integer vehicleId = results.getInt(PartTable.COLUMN_VEHICLE_ID);
        String description = results.getString(PartTable.COLUMN_DESCRIPTION);
        boolean inRepair = results.getBoolean(PartTable.COLUMN_IN_REPAIR);
        Timestamp installedTs = results.getTimestamp(PartTable.COLUMN_DATE_INSTALLED);
        Date installedDate = installedTs != null ? new Date(installedTs.getTime()) : null;
        Timestamp warrantyTs = results.getTimestamp(PartTable.COLUMN_DATE_WARRANTY_EXPIRES);
        Date warrantyDate = warrantyTs != null ? new Date(warrantyTs.getTime()) : null;
        int cost = results.getInt(PartTable.COLUMN_COST);
        
        return new Part(id, vehicleId, name, description, inRepair,
                installedDate, warrantyDate, cost);
    }
    
    @Override
    public Map<String, Object> toValues(Part part) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(PartTable.COLUMN_ID, part.getId());
        values.put(PartTable.COLUMN_NAME, part.getName());
        values.put(PartTable.COLUMN_VEHICLE_ID, part.getVehicleId());
        values.put(PartTable.COLUMN_DESCRIPTION, part.getDescription());
        values.put(PartTable.COLUMN_IN_REPAIR, part.isInRepair());
        values.put(PartTable.COLUMN_DATE_INSTALLED, part.getDateInstalled());
        values.put(PartTable.COLUMN_DATE_WARRANTY_EXPIRES, part.getDateWarrantyExpires());
        values.put(PartTable.COLUMN_COST, part.getCost());
        return values;
    }

    @Override
    public String getTableName() {
        return PartTable.TABLE;
    }
    
}
