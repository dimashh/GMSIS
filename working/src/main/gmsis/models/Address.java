package gmsis.models;

import java.util.Objects;

public class Address {

    private Integer id;
    private String addressLine;
    private String town;
    private String county;
    private String postcode;

    /**
     * Constructor for database
     * @param id
     * @param addressLine
     * @param town
     * @param county
     * @param postcode
     */
    public Address(Integer id, String addressLine, String town, String county, String postcode) {
        this.id = id;
        this.addressLine = addressLine;
        this.town = town;
        this.county = county;
        this.postcode = postcode;
    }

    /**
     * Constructor for new Address
     * @param addressLine
     * @param town
     * @param county
     * @param postcode
     */
    public Address(String addressLine, String town, String county, String postcode) {
        this(null, addressLine, town, county, postcode);
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddressLine() {
        return this.addressLine;
    }
    
    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getTown() {
        return town;
    }
    
    public void setTown(String town) {
        this.town = town;
    }
    
    public String getCounty() {
        return county;
    }
    
    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostcode() {
        return postcode;
    }
    
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", addressLine='" + addressLine + '\'' +
                ", town='" + town + '\'' +
                ", county='" + county + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(id, address.id) &&
                Objects.equals(addressLine, address.addressLine) &&
                Objects.equals(town, address.town) &&
                Objects.equals(county, address.county) &&
                Objects.equals(postcode, address.postcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, addressLine, town, county, postcode);
    }
}
