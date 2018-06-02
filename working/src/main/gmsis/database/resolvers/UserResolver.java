package gmsis.database.resolvers;

import gmsis.database.BaseResolver;
import gmsis.database.Database;
import gmsis.database.delete.DeleteQuery;
import gmsis.database.put.InsertQuery;
import gmsis.database.put.UpdateQuery;
import gmsis.database.tables.UserTable;
import gmsis.models.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserResolver extends BaseResolver<User> {

    @Override
    public DeleteQuery toDeleteQuery(Database db, User user) {
        return DeleteQuery.builder()
                .table(UserTable.TABLE)
                .where(UserTable.COLUMN_ID + " = ?")
                .whereValues(user.getId());
    }
    
    @Override
    public InsertQuery toInsertQuery(Database db, User user) {
        return InsertQuery.builder()
                .table(UserTable.TABLE);
    }
    
    @Override
    public UpdateQuery toUpdateQuery(Database db, User user) {
        return UpdateQuery.builder()
                .table(UserTable.TABLE)
                .where(UserTable.COLUMN_ID + " = ?")
                .whereValues(user.getId());
    }
    
    @Override
    public void setObjectId(Database database, User user, long id) {
        user.setId((int) id);
    }

    @Override
    public User toObject(Database db, ResultSet results) throws SQLException {
        String surname = results.getString(UserTable.COLUMN_SURNAME);
        String firstname = results.getString(UserTable.COLUMN_FIRSTNAME);
        String password = results.getString(UserTable.COLUMN_PASSWORD);
        int id = results.getInt(UserTable.COLUMN_ID);
        User.UserRole userRole = User.UserRole.valueOf(results.getString(UserTable.COLUMN_ROLE));
        Double hourlyFee = results.getDouble(UserTable.COLUMN_HOURLY_FEE);
        if(results.wasNull()) hourlyFee = null;
        
        return new User(id, firstname, surname, password, userRole, hourlyFee);
    }
    
    @Override
    public Map<String, Object> toValues(User user) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(UserTable.COLUMN_ID, user.getId());
        values.put(UserTable.COLUMN_FIRSTNAME, user.getFirstname());
        values.put(UserTable.COLUMN_SURNAME, user.getSurname());
        values.put(UserTable.COLUMN_PASSWORD, user.getPassword());
        values.put(UserTable.COLUMN_HOURLY_FEE, user.getHourlyFee());
        values.put(UserTable.COLUMN_ROLE, user.getUserRole());
        return values;
    }

    @Override
    public String getTableName() {
        return UserTable.TABLE;
    }
    
}
