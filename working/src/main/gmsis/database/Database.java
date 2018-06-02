package gmsis.database;

import gmsis.Log;
import gmsis.database.put.PutBuilder;
import gmsis.database.get.GetBuilder;
import gmsis.database.delete.DeleteBuilder;

import java.sql.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    /**
     * The current schema version, increment this when the database changes and add appropriate migration
     */
    private static final int DATABASE_SCHEMA_VERSION = 6;

    private Connection connection = null;
    private QueryCache queryCache = null;
    private HashMap<Class, Resolver> resolvers = new HashMap<>();
    private boolean isExecuting = false;

    /**
     * Initialize the database, called when setting up the database
     * @param path The path to the sqlite file for the database e.g. "data.db"
     */
    public void init(String path, DatabaseHelper helper) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);

            queryCache = new QueryCache();

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("PRAGMA user_version;");
            int version = rs.getInt(1);
            rs.close();

            if(version != DATABASE_SCHEMA_VERSION) {
                Log.debug("Database version was {0} but Schema version is {1}", version, DATABASE_SCHEMA_VERSION);

                // Create default data
                if(version == 0) {
                    helper.onCreate(this);
                } else {
                    helper.onMigration(this, version, DATABASE_SCHEMA_VERSION);
                }

                statement = connection.createStatement();
                statement.execute("PRAGMA user_version = " + DATABASE_SCHEMA_VERSION + ";");
            }
            
        } catch (SQLException ex) {
            Log.error("{0}", ex);
            throw new RuntimeException("Database connection failed!", ex);
        }
    }
    
    /**
     * Close database
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                Log.error("Failed to close database connection: {0}", ex);
            }
        }
    }
    
    /**
     * Register a model with a resolver
     * @param modelClass The class of the model
     * @param resolver The resolver for the model
     */
    public void registerModel(Class modelClass, Resolver resolver) {
        Log.debug("Registering new model {0} in database", modelClass.getSimpleName());
        resolvers.put(modelClass, resolver);
    }
    
    /**
     * Perform a get operation on database
     * @return 
     */
    public GetBuilder get() {
        return new GetBuilder(this);
    }
    
    /**
     * Perform a put operation on database
     * @return 
     */
    public PutBuilder put() {
        return new PutBuilder(this);
    }
    
    /**
     * Perform a delete operation on database
     * @return 
     */
    public DeleteBuilder delete() {
        return new DeleteBuilder(this);
    }
    
    // Internal database use only
    public <T> Resolver<T> getResolver(Class<T> modelClass) {
        return resolvers.get(modelClass);
    }

    // Internal database use only
    public Connection getConnection() {
        if(connection == null) throw new RuntimeException("Attempt to get database connection before it was initialized");
        return connection;
    }

    public QueryCache getQueryCache() {
        if(queryCache == null) throw new RuntimeException("Attempt to get query cache before database was initialized");
        return queryCache;
    }

    public void setExecuting(boolean isExecuting) {
        this.isExecuting = isExecuting;
    }

    public boolean isExecuting() {
        return isExecuting;
    }
}
