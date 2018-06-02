package gmsis.database.delete;

import gmsis.database.Database;
import gmsis.database.Resolver;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DeleteBuilder {

    private Database database;
    private Set<Object> deleteObjects;
    
    public DeleteBuilder(Database database) {
        this.database = database;
        this.deleteObjects = new HashSet<>();
    }

    public DeleteBuilder objects(Object ...objects) {
        for(Object o : objects) {
            if(o instanceof Collection) deleteObjects.addAll((Collection) o);
            else deleteObjects.add(o);
        }
        return this;
    }
    
    public int execute() {
        int rowsAffected = 0;

        boolean isRoot = !database.isExecuting();
        try {
            if (isRoot) {
                database.setExecuting(true);
                database.getConnection().setAutoCommit(false);
            }

            for(Object o : deleteObjects) {
                if(o != null) {
                    Resolver resolver = database.getResolver(o.getClass());
                    if(resolver != null) {
                        rowsAffected += resolver.delete(database, o);
                        database.getQueryCache().clear(resolver.getTableName());
                    }
                }
            }

            if (isRoot) {
                database.getConnection().commit();
                database.getConnection().setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(isRoot) {
                database.setExecuting(false);
            }
        }
        
        return rowsAffected;
    }

}
