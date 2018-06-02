
package gmsis.authentication;

import gmsis.database.get.Query;
import gmsis.database.tables.UserTable;
import gmsis.App;
import gmsis.models.User;

public class Authentication {

    private User currentUser;
    private App application;

    public Authentication(App instance) {
        application = instance;
    }

    /**
     * Logs in the user, it checks the database for the given id and password. If the user exists and password matches
     * it returns the user object, else it will return null
     *
     * @param id       The users id
     * @param password The users password
     * @return The logged in User if successful or else null
     */
    public User login(int id, String password) {

        currentUser = application.getDatabase().get()
                .objects(User.class)
                .withQuery(Query.builder()
                        .table(UserTable.TABLE)
                        .where("id = ?")
                        .whereValues(id))
                .execute()
                .getSingle();

        if (currentUser != null) {
            if (!currentUser.getPassword().equals(password)) return null;
        }

        return currentUser;
    }

    /**
     * The currently logged in user object
     * @return The current user
     */
    public User getUser() {
        return currentUser;
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        currentUser = null;

        // Send the user back to the login screen
        application.setUI("login");
    }

    /**
     * Returns whether the user is logged in
     * @return
     */
    public boolean isLoggedIn() {
        return this.currentUser != null;
    }

}
