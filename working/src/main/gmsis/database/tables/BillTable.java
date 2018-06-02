package gmsis.database.tables;

public class BillTable {
    
    public static final String TABLE = "bills";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PAID = "paid";
    public static final String COLUMN_BILL_DATE = "billDate";
    public static final String COLUMN_PAID_DATE = "paidDate";
    public static final String COLUMN_TOTAL = "total";

    public static String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PAID + " BOOLEAN NOT NULL, "
                + COLUMN_BILL_DATE + " DATETIME NOT NULL, "
                + COLUMN_PAID_DATE + " DATETIME, "
                + COLUMN_TOTAL + " NUMERIC NOT NULL"
                + ");";
    }
    
}
