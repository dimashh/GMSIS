package gmsis.database.tables;

public class PartTable {
    
    public static final String TABLE = "parts";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_VEHICLE_ID = "vehicleId";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IN_REPAIR = "inRepair";
    public static final String COLUMN_DATE_INSTALLED = "dateInstalled";
    public static final String COLUMN_DATE_WARRANTY_EXPIRES = "dateWarrantyExpires";
    public static final String COLUMN_COST = "cost";

    public static String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_VEHICLE_ID + " INTEGER NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + COLUMN_IN_REPAIR + " BOOLEAN NOT NULL, "
                + COLUMN_DATE_INSTALLED + " TIMESTAMP, "
                + COLUMN_DATE_WARRANTY_EXPIRES + " TIMESTAMP NOT NULL, "
                + COLUMN_COST + " INTEGER NOT NULL"
                + ");";
    }
    
}
