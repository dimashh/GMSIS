package gmsis.database.tables;

public class UserTable {
    
    public static final String TABLE = "users";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_SURNAME = "surname";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_HOURLY_FEE = "hourlyFee";
    public static final String COLUMN_ROLE = "role";

    public static String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FIRSTNAME + " TEXT NOT NULL, "
                + COLUMN_SURNAME + " TEXT NOT NULL, "
                + COLUMN_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_HOURLY_FEE + " NUMERIC, "
                + COLUMN_ROLE + " TEXT NOT NULL"
                + ");";
    }
    
}
