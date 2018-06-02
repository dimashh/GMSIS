package gmsis.models;

import gmsis.database.Lazy;

import java.util.*;
import java.util.stream.Collectors;

public class Vehicle {

    private Integer id;
    private VehicleType vehicleType;
    private Integer customerId;
    private Warranty warranty;
    private String registration;
    private String model;
    private String make;
    private String engineSize;
    private FuelType fuelType;
    private String colour;
    private Date motRenewalDate;
    private Date lastServiceDate;
    private int currentMileage;
    private Lazy<List<Part>> parts;
    private Lazy<List<Booking>> bookings;
    private Lazy<Customer> customer;

    /**
     * Constructor for database
     * @param id
     * @param customerId
     * @param vehicleType
     * @param warranty
     * @param registration
     * @param model
     * @param make
     * @param engineSize
     * @param fuelType
     * @param colour
     * @param motRenewalDate
     * @param lastServiceDate
     * @param currentMileage
     * @param parts
     * @param bookings
     */
    public Vehicle(Integer id, Integer customerId, VehicleType vehicleType, Warranty warranty,
            String registration, String model, String make, String engineSize, FuelType fuelType,
            String colour, Date motRenewalDate, Date lastServiceDate, int currentMileage,
            Lazy<List<Part>> parts, Lazy<List<Booking>> bookings, Lazy<Customer> customer) {
        this.id = id;
        this.customerId = customerId;
        this.vehicleType = vehicleType;
        this.warranty = warranty;
        this.registration = registration;
        this.model = model;
        this.make = make;
        this.engineSize = engineSize;
        this.fuelType = fuelType;
        this.colour = colour;
        this.motRenewalDate = motRenewalDate;
        this.lastServiceDate = lastServiceDate;
        this.currentMileage = currentMileage;
        this.parts = parts;
        this.bookings = bookings;
        this.customer = customer;
    }

    /**
     * Constructor for new Vehicles
     * @param vehicleType
     * @param warranty
     * @param registration
     * @param model
     * @param make
     * @param engineSize
     * @param fuelType
     * @param colour
     * @param motRenewalDate
     * @param lastServiceDate
     * @param currentMileage
     */
    public Vehicle(VehicleType vehicleType, Warranty warranty, String registration, String model, String make, String engineSize,
                   FuelType fuelType, String colour, Date motRenewalDate, Date lastServiceDate, int currentMileage) {
        this(null, null, vehicleType, warranty, registration, model, make, engineSize, fuelType,
                colour, motRenewalDate, lastServiceDate, currentMileage,
                new Lazy<>(ArrayList::new),
                new Lazy<>(ArrayList::new),
                new Lazy<>(() -> {
                    throw new RuntimeException("Cannot load customer when not loaded from database");
                }));
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getCustomerId() {
        return this.customerId;
    }

    public Customer getCustomer() {
        return customer.get();
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public boolean hasWarranty() {
        return this.warranty != null;
    }

    public Warranty getWarranty() {
        return this.warranty;
    }

    public void setWarranty(Warranty warranty) {
        this.warranty = warranty;
    }
    
    public String getRegistration() {
        return this.registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }
    
    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMake() {
        return this.make;
    }

    public void setMake(String make) {
        this.make = make;
    }
    
    public String getEngineSize() {
        return this.engineSize;
    }

    public void setEngineSize(String engineSize) {
        this.engineSize = engineSize;
    }
    
    public FuelType getFuelType() {
        return this.fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }
    
    public String getColour() {
        return this.colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
    
    public Date getMotRenewalDate() {
        return this.motRenewalDate;
    }

    public void setMotRenewalDate(Date motRenewalDate) {
        this.motRenewalDate = motRenewalDate;
    }
    
    public Date getLastServiceDate() {
        return this.lastServiceDate;
    }

    public void setLastServiceDate(Date lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }
    
    public int getCurrentMileage() {
        return this.currentMileage;
    }

    public void setCurrentMileage(int currentMileage) {
        this.currentMileage = currentMileage;
    }
    
    public List<Part> getParts() {
        return this.parts.get();
    }

    public void addPart(Part part) {
        this.parts.get().add(part);
    }

    public void removePart(Part part) {
        this.parts.get().remove(part);
    }

    public List<Part> getPartsInstalled() {
        // FIXME: Filter out replaced parts
        return getParts().stream().filter(part -> part.getDateInstalled() != null).collect(Collectors.toList());
    }
    
    public List<Booking> getBookings() {
        return this.bookings.get();
    }

    public void addBooking(Booking booking) {
        this.bookings.get().add(booking);
    }

    public void removeBooking(Booking booking) {
        this.bookings.get().remove(booking);
    }

    public Booking getNextBooking() {
        return this.bookings.get() == null ? null :
                this.bookings.get().stream().min((o1, o2) -> {
                    if(o1.getDateStart().equals(o2.getDateStart())) return 0;
                    return o1.getDateStart().before(o2.getDateStart()) ? -1 : 1;
                }).orElse(null);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", vehicleType=" + vehicleType +
                ", customerId=" + customerId +
                ", warranty=" + warranty +
                ", registration='" + registration + '\'' +
                ", model='" + model + '\'' +
                ", make='" + make + '\'' +
                ", engineSize='" + engineSize + '\'' +
                ", fuelType=" + fuelType +
                ", colour='" + colour + '\'' +
                ", motRenewalDate=" + motRenewalDate +
                ", lastServiceDate=" + lastServiceDate +
                ", currentMileage=" + currentMileage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return currentMileage == vehicle.currentMileage &&
                Objects.equals(id, vehicle.id) &&
                vehicleType == vehicle.vehicleType &&
                Objects.equals(customerId, vehicle.customerId) &&
                Objects.equals(warranty, vehicle.warranty) &&
                Objects.equals(registration, vehicle.registration) &&
                Objects.equals(model, vehicle.model) &&
                Objects.equals(make, vehicle.make) &&
                Objects.equals(engineSize, vehicle.engineSize) &&
                fuelType == vehicle.fuelType &&
                Objects.equals(colour, vehicle.colour) &&
                Objects.equals(motRenewalDate, vehicle.motRenewalDate) &&
                Objects.equals(lastServiceDate, vehicle.lastServiceDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vehicleType, customerId, warranty, registration, model, make, engineSize,
                fuelType, colour, motRenewalDate, lastServiceDate, currentMileage);
    }

    public enum FuelType {
        DIESEL,
        PETROL
    }
    
    public enum VehicleType {
        CAR,
        VAN,
        TRUCK
    }

}
