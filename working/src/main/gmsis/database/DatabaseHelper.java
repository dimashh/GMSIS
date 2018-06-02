package gmsis.database;

import java.sql.SQLException;

public interface DatabaseHelper {

    boolean onMigration(Database database, int fromVersion, int toVersion) throws SQLException;

    void onCreate(Database database) throws SQLException;

}
