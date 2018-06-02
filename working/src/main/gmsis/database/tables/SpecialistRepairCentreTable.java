package gmsis.database.tables;

public class SpecialistRepairCentreTable {

    public static final String TABLE = "spc";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS_ID = "addressId";
    public static final String COLUMN_PHONE_NUMBER = "phoneNumber";
    public static final String COLUMN_EMAIL = "email";

    public static String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_ADDRESS_ID + " INTEGER NOT NULL, "
                + COLUMN_PHONE_NUMBER + " TEXT NOT NULL, "
                + COLUMN_EMAIL + " TEXT NOT NULL "
                + ");";
    }

}
