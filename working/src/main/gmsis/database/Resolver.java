package gmsis.database;

import gmsis.database.get.Query;
import gmsis.database.get.GetResult;
import gmsis.database.delete.DeleteQuery;

/**
 * A resolver is used to resolve an object into the database and vice-versa, it allows the translation of java object
 * into sql tables and back. Each object to be stored in the database should have a resolver. Note that a Resolver is
 * very low level and instead you should extend the {@link BaseResolver} which provides a default implementation of
 * put/delete/get behaviours and provides a further level of abtsraction.
 * @param <T>
 */
public interface Resolver<T> {
    
    String getTableName();

    /**
     * Get operation in the database, given the database instance for raw querying and a query it is used
     * to map the query to a GetResult of the correct object type for the query.
     * @param database The database instance
     * @param query The query to be used
     * @return The result as {@link GetResult} object
     */
    GetResult<T> get(Database database, Query query);

    /**
     * Put operation in the database, given the database instance and an object of the correct type, it should either
     * update or insert the object into the database
     * @param database The database instance
     * @param object The object to be updated or inserted into the database
     * @return The number of table rows affected (if 0 the operation probably failed)
     */
    int put(Database database, T object);

    /**
     * Delete operation in the database, given the database instance and an object of the correct type, it deletes the
     * object from the database if it exists.
     * @param database The database instance
     * @param object The object to be deleted
     * @return The number of table rows affected (if 0 the operation probably failed)
     */
    int delete(Database database, T object);
}
