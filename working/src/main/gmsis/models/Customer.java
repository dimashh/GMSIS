package gmsis.models;

import gmsis.database.Lazy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Customer  {

    private Integer id;
    private CustomerType type;
    private String firstname;
    private String surname;
    private String phoneNumber;
    private String email;
    private Address address;
    private Lazy<List<Vehicle>> vehicles;

    /**
     * Constructor for database
     * @param id
     * @param type
     * @param firstname
     * @param surname
     * @param phoneNumber
     * @param email
     * @param address
     * @param vehicles
     */
    public Customer(Integer id, CustomerType type, String firstname,
            String surname, String phoneNumber, String email, Address address,
            Lazy<List<Vehicle>> vehicles) {
        this.id = id;
        this.type = type;
        this.firstname = firstname;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.vehicles = vehicles;
    }

    /**
     * Constructor for new Customer
     * @param customerType
     * @param firstname
     * @param surname
     * @param phoneNumber
     * @param email
     * @param address
     */
    public Customer(CustomerType customerType, String firstname,
                    String surname, String phoneNumber, String email, Address address) {
        this(null, customerType, firstname, surname, phoneNumber, email, address, new Lazy<>(() -> { return new ArrayList<>(); }));
    }

    public Integer getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setType(Customer.CustomerType type) {
        this.type = type;
    }

    public CustomerType getType() {
        return type;
    }
    
    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Vehicle> getVehicles() {
        return this.vehicles.get();
    }

    public void removeVehicle(Vehicle vehicle) {
        this.vehicles.get().remove(vehicle);
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicles.get().add(vehicle);
    }

    public List<Booking> getBookings() {
        List<Booking> bookings = new ArrayList<>();
        for(Vehicle v : vehicles.get()) {
            bookings.addAll(v.getBookings());
        }
        return bookings;
    }
    
    public List<Bill> getBills() {
        return getBookings().stream().filter(booking -> booking.getBill() != null).map(Booking::getBill).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", type=" + type +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", address=" + address +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) &&
                type == customer.type &&
                Objects.equals(firstname, customer.firstname) &&
                Objects.equals(surname, customer.surname) &&
                Objects.equals(phoneNumber, customer.phoneNumber) &&
                Objects.equals(email, customer.email) &&
                Objects.equals(address, customer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, firstname, surname, phoneNumber, email, address);
    }

    public enum CustomerType {
        BUSINESS("Business"),
        INDIVIDUAL("Individual");

        private String name;

        CustomerType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static CustomerType fromName(String name) {
            for(CustomerType c : CustomerType.values()) {
                if(c.getName().equalsIgnoreCase(name)) return c;
            }
            return null;
        }
    }
}
