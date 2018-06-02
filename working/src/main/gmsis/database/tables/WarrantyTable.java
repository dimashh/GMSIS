package gmsis.database.tables;

public class WarrantyTable {
    
    public static final String TABLE = "warranty";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_COMPANY_NAME = "companyName";
    public static final String COLUMN_COMPANY_ADDRESS_ID = "companyAddressId";
    public static final String COLUMN_EXPIRY_DATE = "expiryDate";

    public static String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_COMPANY_NAME + " TEXT NOT NULL, "
                + COLUMN_COMPANY_ADDRESS_ID + " INTEGER NOT NULL, "
                + COLUMN_EXPIRY_DATE + " TIMESTAMP NOT NULL"
                + ");";
    }
    
}
