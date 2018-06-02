package gmsis.models;

import java.util.Date;
import java.util.Objects;

public class Part {

    private Integer id;
    private Integer vehicleId; // May be null (not installed)
    private String name;
    private String description;
    private boolean inRepair;
    private Date dateInstalled;
    private Date dateWarrantyExpires;
    private int cost;

    /**
     * Constructor for database
     * @param id
     * @param name
     * @param vehicleId
     * @param description
     * @param inRepair
     * @param dateInstalled
     * @param dateWarrantyExpires
     * @param cost
     */
    public Part(Integer id, Integer vehicleId, String name, String description,
            boolean inRepair, Date dateInstalled, Date dateWarrantyExpires,
            int cost) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.name = name;
        this.description = description;
        this.inRepair = inRepair;
        this.dateInstalled = dateInstalled;
        this.dateWarrantyExpires = dateWarrantyExpires;
        this.cost = cost;
    }

    /**
     * Constructor for new part
     * @param name
     * @param description
     * @param inRepair
     * @param dateInstalled
     * @param dateWarrantyExpires
     * @param cost
     */
    public Part(String name, String description,
                boolean inRepair, Date dateInstalled, Date dateWarrantyExpires,
                int cost) {
        this(null, null, name, description, inRepair, dateInstalled, dateWarrantyExpires, cost);
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVehicleId() {
        return this.vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isInRepair() {
        return this.inRepair;
    }

    public void setInRepair(boolean isInRepair) {
        this.inRepair = isInRepair;
    }
    
    public Date getDateInstalled() {
        return this.dateInstalled;
    }

    public void setDateInstalled(Date dateInstalled) {
        this.dateInstalled = dateInstalled;
    }
    
    public Date getDateWarrantyExpires() {
        return this.dateWarrantyExpires;
    }

    public void setDateWarrantyExpires(Date dateWarrantyExpires) {
        this.dateWarrantyExpires = dateWarrantyExpires;
    }
    
    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Part part = (Part) o;
        return inRepair == part.inRepair &&
                cost == part.cost &&
                Objects.equals(id, part.id) &&
                Objects.equals(vehicleId, part.vehicleId) &&
                Objects.equals(name, part.name) &&
                Objects.equals(description, part.description) &&
                Objects.equals(dateInstalled, part.dateInstalled) &&
                Objects.equals(dateWarrantyExpires, part.dateWarrantyExpires);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vehicleId, name, description, inRepair, dateInstalled, dateWarrantyExpires, cost);
    }

    @Override
    public String toString() {
        return "Part{" +
                "id=" + id +
                ", vehicleId=" + vehicleId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", inRepair=" + inRepair +
                ", dateInstalled=" + dateInstalled +
                ", dateWarrantyExpires=" + dateWarrantyExpires +
                ", cost=" + cost +
                '}';
    }
}
