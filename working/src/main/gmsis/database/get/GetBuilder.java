package gmsis.database.get;

import gmsis.database.Database;

/**
 * Used to build a get query into the database, this is untyped, in order to be able to type correctly it creates a new
 * typed builder for the rest of the builder operations.
 * Typical usage looks like
 * database.get()
    .objects(MyObject.class)
    .withQuery(
        Query.builder()
        ...
        )
    .execute()
    .getSingle()
 */
public class GetBuilder {

    private Database database;
    
    public GetBuilder(Database database) {
        this.database = database;
    }
    
    public <E> GetBuilderTyped<E> objects(Class<E> modelClass) {
        return new GetBuilderTyped<>(database, database.getResolver(modelClass));
    }
}
