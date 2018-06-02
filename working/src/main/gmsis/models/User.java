package gmsis.models;

import java.util.Objects;

public class User {

    private Integer id;
    private String surname;
    private String firstname;
    private String password;
    private UserRole userRole;
    private Double hourlyFee;
    
    /**
     * Constructor for database
     * @param id
     * @param firstname
     * @param surname
     * @param password
     * @param userRole
     * @param hourlyFee
     */
    public User(Integer id, String firstname, String surname, String password, UserRole userRole, Double hourlyFee) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;
        this.password = password;
        this.userRole = userRole;
        this.hourlyFee = hourlyFee;
    }
    
    /**
     * Constructor for new users
     * @param firstname
     * @param surname
     * @param password
     * @param userRole
     * @param hourlyFee
     */
    public User(String firstname, String surname, String password, UserRole userRole, Double hourlyFee) {
        this(null, firstname, surname, password, userRole, hourlyFee);
    }

    public Integer getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getFirstname() {
        return this.firstname;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setFirstname(String fn) {
        this.firstname= fn;
    }
    
    public void setSurname(String sn) {
        this.surname= sn;
    }
    
    public boolean isAdministrator() {
        return this.userRole == UserRole.ADMINISTRATOR;
    }

    public boolean isMechanic() {
        return this.userRole == UserRole.MECHANIC;
    }

    public Double getHourlyFee() {
        return this.hourlyFee;
    }

    public void setHourlyFee(Double hourlyFee) {
        this.hourlyFee = hourlyFee;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", surname='" + surname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", password='" + password + '\'' +
                ", userRole=" + userRole +
                ", hourlyFee=" + hourlyFee +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(surname, user.surname) &&
                Objects.equals(firstname, user.firstname) &&
                Objects.equals(password, user.password) &&
                userRole == user.userRole &&
                Objects.equals(hourlyFee, user.hourlyFee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, surname, firstname, password, userRole, hourlyFee);
    }

    public enum UserRole {
        USER("User"),
        ADMINISTRATOR("Administrator"),
        MECHANIC("Mechanic");
        
        private String name;

        UserRole(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
        
        public static UserRole fromName(String name) {
            for(UserRole c : UserRole.values()) {
                if(c.getName().equalsIgnoreCase(name)) return c;
            }
            return null;
        }
        
    }
    
}
