package gmsis.database;

import gmsis.Log;
import gmsis.database.get.Query;
import gmsis.database.resolvers.UserResolver;
import gmsis.database.tables.UserTable;
import gmsis.models.User;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestUserDatabase {
    
    private static final User TEST_USER_1 = new User(1, "hi", "test", "foo", User.UserRole.ADMINISTRATOR, null);
    private static final User TEST_USER_2 = new User(1, "hi", "test", "anotherpassword", User.UserRole.ADMINISTRATOR, null);
    private static final User TEST_USER_3 = new User("user2", "hello", "foo", User.UserRole.USER, null);
    
    private Database database;
    
    public TestUserDatabase() {
        database = new Database();
        database.init("test.db", null);
        database.registerModel(User.class, new UserResolver());
        
        // Load test data
        database.put()
                .objects(TEST_USER_1)
                .execute();
    }
    
    @Test
    public void testGetUser() {
        // Get Test User 1
        List<User> results = database.get()
                .objects(User.class)
                .withQuery(Query.builder()
                    .table(UserTable.TABLE)
                    .where("id = ?")
                    .whereValues(TEST_USER_1.getId()))
                .execute()
                .getList();
        
        // Check Test User 1 was received
        assertEquals(1, results.size());
        assertEquals(TEST_USER_1, results.get(0));
    }
    
    @Test
    public void testUpdateUser() {
        // Update Test User 1 to be Test User 2 (as they share same id)
        int rowsAffected = database.put()
                .objects(TEST_USER_2)
                .execute();
        
        assertEquals(1, rowsAffected);
        
        // Test User 1 (which is the same) should now be same as Test User 2
        User user = database.get()
                .objects(User.class)
                .withQuery(Query.builder()
                    .table(UserTable.TABLE)
                    .where("id = ?")
                    .whereValues(TEST_USER_1.getId()))
                .execute()
                .getSingle();
        
        // Check they are the same Test User 2 == Test User 1
        assertEquals(TEST_USER_2, user);
    }
    
    @Test
    public void testDeleteUser() {
        // Delete Test User 2
        int rowsAffected = database.delete()
                .objects(TEST_USER_2)
                .execute();
        
        assertEquals(1, rowsAffected);
        
        // Check User Table empty
        List<User> users = database.get()
                .objects(User.class)
                .withQuery(Query.builder()
                    .table(UserTable.TABLE))
                .execute()
                .getList();
        
        // Test empty
        assertEquals(0, users.size());
    }
    
    @Test
    public void testAutoInsertId() {
        int rowsAffected = database.put()
                .objects(TEST_USER_3)
                .execute();
        
        assertEquals(1, rowsAffected);
        assertNotNull(TEST_USER_3.getId());

        Log.debug("Inserted id: " + TEST_USER_3.getId());
    }
    
    
    
}
