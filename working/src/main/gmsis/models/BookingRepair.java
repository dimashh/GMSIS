package gmsis.models;

import gmsis.database.Lazy;
import java.util.Date;
import java.util.Objects;

public class BookingRepair {

    private Integer id;
    private Integer bookingId;
    private Part part;
    private SpecialistRepairCentre spc;
    private RepairType repairType;
    private Double spcPrice;
    private Date spcDeliveryDate;
    private Date spcReturnDate;
    private Lazy<Booking> lazyBooking;

    public BookingRepair(Integer id, Integer bookingId, Part part, SpecialistRepairCentre spc, RepairType repairType,
                         Double spcPrice, Date spcDeliveryDate, Date spcReturnDate, Lazy<Booking> lazyBooking) {
        this.id = id;
        this.bookingId = bookingId;
        this.part = part;
        this.spc = spc;
        this.repairType = repairType;
        this.spcPrice = spcPrice;
        this.spcDeliveryDate = spcDeliveryDate;
        this.spcReturnDate = spcReturnDate;
        this.lazyBooking = lazyBooking;

    }

    public BookingRepair(RepairType repairType, Part part, SpecialistRepairCentre spc, Double spcPrice, Date spcDeliveryDate, Date spcReturnDate) {
        this(null, null, part, spc, repairType, spcPrice, spcDeliveryDate, spcReturnDate,
                new Lazy<>(() -> {
                    throw new RuntimeException("Cannot get booking when not loaded from database");
                }));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public SpecialistRepairCentre getSpc() {
        return spc;
    }

    public void setSpc(SpecialistRepairCentre spc) {
        this.spc = spc;
    }

    public boolean isSpc() {
        return spc != null;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public void setRepairType(RepairType repairType) {
        this.repairType = repairType;
    }

    public Date getSpcDeliveryDate() {
        return spcDeliveryDate;
    }

    public void setSpcDeliveryDate(Date spcDeliveryDate) {
        this.spcDeliveryDate = spcDeliveryDate;
    }

    public Date getSpcReturnDate() {
        return spcReturnDate;
    }

    public void setSpcReturnDate(Date spcReturnDate) {
        this.spcReturnDate = spcReturnDate;
    }

    public Booking getBooking() {
        return lazyBooking.get();
    }


    public double getPrice() {
        switch (repairType) {
            case NEW_PART:
                return getPart().getCost();
            case REPAIR_PART:
                return isSpc() ? spcPrice : getPart().getCost();
            case REPAIR_VEHICLE:
                return spcPrice;
        }
        return 0;
    }

    public Double getSpcPrice() {
        return spcPrice;
    }

    public enum RepairType {
        REPAIR_PART, NEW_PART, REPAIR_VEHICLE
    }

    @Override
    public String toString() {
        return "BookingRepair{" +
                "id=" + id +
                ", bookingId=" + bookingId +
                ", part=" + part +
                ", spc=" + spc +
                ", repairType=" + repairType +
                ", spcPrice=" + spcPrice +
                ", spcDeliveryDate=" + spcDeliveryDate +
                ", spcReturnDate=" + spcReturnDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingRepair that = (BookingRepair) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(bookingId, that.bookingId) &&
                Objects.equals(part, that.part) &&
                Objects.equals(spc, that.spc) &&
                repairType == that.repairType &&
                Objects.equals(spcPrice, that.spcPrice) &&
                Objects.equals(spcDeliveryDate, that.spcDeliveryDate) &&
                Objects.equals(spcReturnDate, that.spcReturnDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookingId, part, spc, repairType, spcPrice, spcDeliveryDate, spcReturnDate);
    }
}
