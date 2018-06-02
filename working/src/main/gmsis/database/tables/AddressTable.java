package gmsis.database.tables;

public class AddressTable {
    
    public static final String TABLE = "addresses";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ADDRESS_LINE = "address_line";
    public static final String COLUMN_TOWN = "town";
    public static final String COLUMN_COUNTY = "county";
    public static final String COLUMN_POSTCODE = "postcode";

    public static String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ADDRESS_LINE + " TEXT NOT NULL, "
                + COLUMN_TOWN + " TEXT NOT NULL, "
                + COLUMN_COUNTY + " TEXT NOT NULL, "
                + COLUMN_POSTCODE + " TEXT NOT NULL"
                + ");";
    }
    
}
