package gmsis.database.tables;

public class VehicleTable {
    
    public static final String TABLE = "vehicles";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CUSTOMER_ID = "customerId";
    public static final String COLUMN_WARRANTY_ID = "warrantyId";
    public static final String COLUMN_REGISTRATION = "registration";
    public static final String COLUMN_MODEL = "model";
    public static final String COLUMN_MAKE = "make";
    public static final String COLUMN_ENGINE_SIZE = "engineSize";
    public static final String COLUMN_FUEL_TYPE = "fuelType";
    public static final String COLUMN_COLOUR = "colour";
    public static final String COLUMN_MOT_RENEWAL_DATE = "motRenewalDate";
    public static final String COLUMN_LAST_SERVICE_DATE = "lastServiceDate";
    public static final String COLUMN_CURRENT_MILEAGE = "currentMileage";

    public static String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TYPE + " TEXT NOT NULL, "
                + COLUMN_CUSTOMER_ID + " INTEGER NOT NULL, "
                + COLUMN_WARRANTY_ID + " INTEGER, "
                + COLUMN_REGISTRATION + " TEXT NOT NULL, "
                + COLUMN_MODEL + " TEXT NOT NULL, "
                + COLUMN_MAKE + " TEXT NOT NULL, "
                + COLUMN_ENGINE_SIZE + " TEXT NOT NULL, "
                + COLUMN_FUEL_TYPE + " TEXT NOT NULL, "
                + COLUMN_COLOUR + " TEXT NOT NULL, "
                + COLUMN_MOT_RENEWAL_DATE + " TIMESTAMP NOT NULL, "
                + COLUMN_LAST_SERVICE_DATE + " TIMESTAMP, "
                + COLUMN_CURRENT_MILEAGE + " NUMERIC NOT NULL"
                + ");";
    }
    
}
