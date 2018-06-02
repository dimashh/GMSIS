package gmsis.database.put;

import gmsis.database.Database;
import gmsis.database.Resolver;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A PutBuilder for creating and executing a Put operation on the database, typical usage looks like
 * database.put()
    .objects(myObject)
    .execute()
 */
public class PutBuilder {

    private Database database;
    private Set<Object> putObjects;
    
    public PutBuilder(Database database) {
        this.database = database;
        this.putObjects = new HashSet<>();
    }
    
    public PutBuilder objects(Object ...objects) {
        for(Object o : objects) {
            if(o instanceof Collection) putObjects.addAll((Collection) o);
            else putObjects.add(o);
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

            for (Object o : putObjects) {
                if (o != null) {
                    Resolver resolver = database.getResolver(o.getClass());
                    if (resolver != null) {
                        rowsAffected += resolver.put(database, o);
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
