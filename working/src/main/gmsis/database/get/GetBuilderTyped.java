package gmsis.database.get;

import gmsis.Log;
import gmsis.database.Database;
import gmsis.database.Resolver;

import java.sql.SQLException;

/**
 * The typed version of the GetBuilder, it ensures the GetResult returned is of the correct type
 * @param <T>
 */
public class GetBuilderTyped<T> {

    private Database database;
    private Resolver<T> resolver;
    private Query query = null;

    public GetBuilderTyped(Database database, Resolver<T> resolver) {
        this.database = database;
        this.resolver = resolver;
    }

    public GetBuilderTyped<T> withQuery(Query query) {
        this.query = query;
        return this;
    }

    /**
     * Executes the query
     * @return
     */
    public GetResult<T> execute() {
        if(database.getQueryCache().has(query)) {
            return database.getQueryCache().get(query);
        } else {
            boolean isRoot = !database.isExecuting();
            try {
                if (isRoot) {
                    database.setExecuting(true);
                    database.getConnection().setAutoCommit(false);
                }

                GetResult<T> result = resolver.get(database, query);
                database.getQueryCache().put(query, result);

                if (isRoot) {
                    database.getConnection().commit();
                    database.getConnection().setAutoCommit(true);
                }

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            } finally {
                if(isRoot) {
                    database.setExecuting(false);
                }
            }
        }
    }

}