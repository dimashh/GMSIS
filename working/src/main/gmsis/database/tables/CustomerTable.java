package gmsis.database.tables;

public class CustomerTable {
    public static final String TABLE = "customers";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_SURNAME = "surname";
    public static final String COLUMN_ADDRESS_ID = "addressId";
    public static final String COLUMN_PHONE_NUMBER = "phoneNumber";
    public static final String COLUMN_EMAIL = "email";

    public static String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FIRSTNAME + " TEXT NOT NULL, "
                + COLUMN_SURNAME + " TEXT NOT NULL, "
                + COLUMN_ADDRESS_ID + " INTEGER NOT NULL, "
                + COLUMN_TYPE + " TEXT NOT NULL, "
                + COLUMN_PHONE_NUMBER + " TEXT NOT NULL, "
                + COLUMN_EMAIL + " TEXT NOT NULL"
                + ");";
    }
}
