package gmsis.models;

import java.util.Date;
import java.util.Objects;

public class Warranty {

    private Integer id;
    private String companyName;
    private Address companyAddress;
    private Date expiryDate;

    /**
     * Constructor for database
     * @param id
     * @param companyName
     * @param companyAddress
     * @param expiryDate
     */
    public Warranty(Integer id, String companyName, Address companyAddress, Date expiryDate) {
        this.id = id;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.expiryDate = expiryDate;
    }

    /**
     * Constructor for new Warranty
     * @param companyName
     * @param companyAddress
     * @param expiryDate
     */
    public Warranty(String companyName, Address companyAddress, Date expiryDate) {
        this(null, companyName, companyAddress, expiryDate);
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Address getCompanyAddress() {
        return this.companyAddress;
    }

    public void setCompanyAddress(Address companyAddress) {
        this.companyAddress = companyAddress;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "Warranty{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", companyAddress=" + companyAddress +
                ", expiryDate=" + expiryDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warranty warranty = (Warranty) o;
        return Objects.equals(id, warranty.id) &&
                Objects.equals(companyName, warranty.companyName) &&
                Objects.equals(companyAddress, warranty.companyAddress) &&
                Objects.equals(expiryDate, warranty.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyName, companyAddress, expiryDate);
    }
}
