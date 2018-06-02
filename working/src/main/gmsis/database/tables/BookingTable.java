package gmsis.database.tables;

public class BookingTable {
    
    public static final String TABLE = "bookings";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_VEHICLE_ID = "vehicleId";
    public static final String COLUMN_MECHANIC_ID = "mechanicId";
    public static final String COLUMN_BILL_ID = "billId";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DATE_START = "dateStart";
    public static final String COLUMN_DATE_END = "dateEnd";

    public static String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_VEHICLE_ID + " INTEGER NOT NULL, "
                + COLUMN_BILL_ID + " INTEGER, "
                + COLUMN_MECHANIC_ID + " INTEGER, "
                + COLUMN_TYPE + " TEXT NOT NULL, "
                + COLUMN_DATE_START + " DATETIME NOT NULL, "
                + COLUMN_DATE_END + " DATETIME NOT NULL"
                + ");";
    }
    
}
