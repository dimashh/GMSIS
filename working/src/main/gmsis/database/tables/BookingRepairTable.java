package gmsis.database.tables;

public class BookingRepairTable {

    public static final String TABLE = "bookingRepair";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_BOOKING_ID = "bookingId";
    public static final String COLUMN_PART_ID = "partId";
    public static final String COLUMN_REPAIR_TYPE = "repairType";
    public static final String COLUMN_SPC_PRICE = "spcPrice";
    public static final String COLUMN_SPC_ID = "spcId";
    public static final String COLUMN_SPC_DELIVERY_DATE = "spcDeliveryDate";
    public static final String COLUMN_SPC_RETURN_DATE = "spcReturnDate";

    public static String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BOOKING_ID + " INTEGER NOT NULL, "
                + COLUMN_PART_ID + " INTEGER, "
                + COLUMN_REPAIR_TYPE + " TEXT NOT NULL, "
                + COLUMN_SPC_PRICE + " NUMERIC, "
                + COLUMN_SPC_ID + " INTEGER, "
                + COLUMN_SPC_DELIVERY_DATE + " DATETIME, "
                + COLUMN_SPC_RETURN_DATE + " DATETIME "
                + ");";
    }

}
