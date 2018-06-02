package gmsis.models;

import gmsis.database.Lazy;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

public class Bill {

    private Lazy<Booking> booking;
    private Integer id;
    private boolean paid;
    private Date billDate;
    private Date paidDate;
    private double total;

    /**
     * Database constructor
     * @param id
     * @param booking
     * @param paid
     * @param billDate
     * @param paidDate
     */
    public Bill(Integer id, Lazy<Booking> booking, boolean paid, Date billDate, Date paidDate, double total) {
        this.id = id;
        this.booking = booking;
        this.paid = paid;
        this.billDate = billDate;
        this.paidDate = paidDate;
        this.total = total;
    }

    /**
     * Constructor for new Bill
     * @param paid
     * @param billDate
     * @param paidDate
     */
    public Bill(boolean paid, Date billDate, Date paidDate, double total) {
        this(null, new Lazy<>(() -> {
            throw new RuntimeException("Attempt to get Booking, except Lazy method has not been initialized. It was not loaded from database.");
        }), paid, billDate, paidDate, total);
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public Booking getBooking() {
        return this.booking.get();
    }

    public boolean isPaid() {
        return this.paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
    
    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }
    
    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public double getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", paid=" + paid +
                ", billDate=" + billDate +
                ", paidDate=" + paidDate +
                ", total=" + total +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return paid == bill.paid &&
                Objects.equals(id, bill.id) &&
                Objects.equals(total, bill.total) &&
                Objects.equals(billDate, bill.billDate) &&
                Objects.equals(paidDate, bill.paidDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paid, billDate, paidDate);
    }
}
