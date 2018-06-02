package gmsis.models;

import gmsis.database.Lazy;

import java.util.*;

public class Booking {

    private Integer id;
    private Integer vehicleId;
    private Lazy<User> mechanic;
    private Lazy<Customer> customer;
    private Lazy<Vehicle> vehicle;
    private Lazy<List<BookingRepair>> bookingRepairs;
    private Bill bill;
    private BookingType type;
    private Date dateStart;
    private Date dateEnd;

    /**
     * Constructor for Booking from database
     * @param id
     * @param vehicleId
     * @param type
     * @param bill
     * @param dateStart
     * @param dateEnd
     * @param mechanic
     * @param customer
     * @param vehicle
     */
    public Booking(Integer id, Integer vehicleId, BookingType type, Bill bill, Date dateStart,
                   Date dateEnd, Lazy<List<BookingRepair>> bookingRepairs, Lazy<User> mechanic, Lazy<Customer> customer, Lazy<Vehicle> vehicle) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.bookingRepairs = bookingRepairs;
        this.mechanic = mechanic;
        this.customer = customer;
        this.vehicle = vehicle;
        this.bill = bill;
        this.type = type;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    /**
     * Constructor for new Booking
     * @param vehicleId
     * @param mechanic
     * @param type
     * @param bill
     * @param dateStart
     * @param dateEnd
     */
    public Booking(Integer vehicleId, User mechanic, BookingType type, Bill bill,  Date dateStart, Date dateEnd) {
        this(null, vehicleId, type, bill, dateStart, dateEnd, new Lazy<>(ArrayList::new), new Lazy<>(() -> {
            return mechanic;
        }), new Lazy<>(() -> {
            throw new RuntimeException("Attempt to get Customer, except Lazy method has not been initialized. It was not loaded from database.");
        }), new Lazy<>(() -> {
            throw new RuntimeException("Attempt to get Vehicle, except Lazy method has not been initialized. It was not loaded from database.");
        }));
    }

    /**
     * Constructor for new Booking where you are not adding the booking directly to database, it must be added to a vehicle
     * and then insert the vehicle. Since it must be attached to a vehicle either through a vehicle object or by using the
     * other constructor with a vehicle Id
     * @param mechanic
     * @param type
     * @param bill
     * @param dateStart
     * @param dateEnd
     */
    public Booking(User mechanic, BookingType type, Bill bill, Date dateStart, Date dateEnd) {
        this(null, mechanic, type, bill, dateStart, dateEnd);
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Integer getVehicleId() {
        return this.vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public User getMechanic() {
        return mechanic.get();
    }

    public void setMechanic(User user) {
        mechanic.set(user);
    }

    public Vehicle getVehicle() {
        return vehicle.get();
    }

    public Customer getCustomer() {
        return customer.get();
    }
    
    public Bill getBill() {
        return this.bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
    
    public BookingType getType() {
        return type;
    }

    public void setType(BookingType type) {
        this.type = type;
    }
    
    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date date) {
        this.dateStart = date;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date date) {
        this.dateEnd = date;
    }

    public List<BookingRepair> getBookingRepairs() {
        return bookingRepairs.get();
    }

    public enum BookingType {
        DIAGNOSIS_REPAIR("Diagnosis Repair"),
        SCHEDULED_MAINTENANCE("Scheduled Maintenance");
        
        private String name;
        
        BookingType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }

        public static BookingType fromName(String name) {
            for(BookingType b : BookingType.values()) {
                if(b.getName().equals(name)) return b;
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", vehicleId=" + vehicleId +
                ", mechanic=" + mechanic.get() +
                ", bill=" + bill +
                ", type=" + type +
                ", dateStart=" + dateStart +
                ", dateEnd=" + dateEnd +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) &&
                Objects.equals(vehicleId, booking.vehicleId) &&
                Objects.equals(mechanic.get(), booking.mechanic.get()) &&
                Objects.equals(bill, booking.bill) &&
                type == booking.type &&
                Objects.equals(dateStart, booking.dateStart) &&
                Objects.equals(dateEnd, booking.dateEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vehicleId, mechanic.get(), bill, type, dateStart, dateEnd);
    }

}
