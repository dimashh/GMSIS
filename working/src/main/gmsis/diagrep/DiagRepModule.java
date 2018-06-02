package gmsis.diagrep;

import gmsis.App;
import gmsis.database.get.Query;
import gmsis.database.tables.BookingTable;
import gmsis.database.tables.UserTable;
import gmsis.models.Booking;
import gmsis.models.User;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DiagRepModule {

    private static final String HOLIDAYS_CSV_FILE = "/holidays.csv";

    private App application;
    private List<Holiday> holidays;
    private Map<Integer, OpeningHours> openingHours;
    private OpeningHours earliestAndLatestOpeningHours;

    /**
     *
     * @param instance
     */
    public DiagRepModule(App instance) {
        application = instance;
        holidays = new ArrayList<>();
        openingHours = new HashMap<>();

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        // Opening hours Mon (0) - Sun (6)
        openingHours.put(0, new OpeningHours(LocalTime.parse("09:00", timeFormat), LocalTime.parse("17:30", timeFormat)));
        openingHours.put(1, new OpeningHours(LocalTime.parse("09:00", timeFormat), LocalTime.parse("17:30", timeFormat)));
        openingHours.put(2, new OpeningHours(LocalTime.parse("09:00", timeFormat), LocalTime.parse("17:30", timeFormat)));
        openingHours.put(3, new OpeningHours(LocalTime.parse("09:00", timeFormat), LocalTime.parse("17:30", timeFormat)));
        openingHours.put(4, new OpeningHours(LocalTime.parse("09:00", timeFormat), LocalTime.parse("17:30", timeFormat)));
        openingHours.put(5, new OpeningHours(LocalTime.parse("09:00", timeFormat), LocalTime.parse("12:00", timeFormat)));

        // Calculate earliest and latest opening hours
        LocalTime earliestOpeningTime = null;
        LocalTime latestClosingTime = null;
        for(OpeningHours openingHour : openingHours.values()) {
            if(earliestOpeningTime == null || openingHour.getOpeningTime().isBefore(earliestOpeningTime)) {
                earliestOpeningTime = openingHour.getOpeningTime();
            }
            if(latestClosingTime == null || openingHour.getClosingTime().isAfter(latestClosingTime)) {
                latestClosingTime = openingHour.getClosingTime();
            }
        }
        earliestAndLatestOpeningHours = new OpeningHours(earliestOpeningTime, latestClosingTime);


        // Load holidays from csv file
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String line;
        InputStream in = getClass().getResourceAsStream(HOLIDAYS_CSV_FILE);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if(values[0].equals("Date")) continue; // Skip headers

                holidays.add(new Holiday(LocalDate.parse(values[0], dateFormat), values[1], values[2], "TRUE".equals(values[3])));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Make sure holidays is sorted, it should be but make sure
        holidays.sort(Comparator.comparing(Holiday::getDate));
    }

    /**
     * Gets a list of all mechanics
     * @return
     */
    public List<User> getMechanics() {
        return application.getDatabase().get()
                .objects(User.class)
                .withQuery(Query.builder()
                    .table(UserTable.TABLE)
                    .where(UserTable.COLUMN_ROLE + " = ?")
                    .whereValues(User.UserRole.MECHANIC))
                .execute()
                .getList();
    }

    /**
     * Selects bookings between two dates
     * @param from The date from (inclusive)
     * @param to The date to (exclusive)
     * @return
     */
    public List<Booking> getBookingsBetween(LocalDateTime from, LocalDateTime to) {
        if(from == null || to == null || !from.isBefore(to)) {
            throw new RuntimeException("Cannot select bookings between " + from + " and " + to);
        }

        // COLUMN_DATE_START <= from AND COLUMN_DATE_END >= to
        // COLUMN_DATE_START >= from AND COLUMN_DATE_START < to
        // COLUMN_DATE_END > from AND COLUMN_DATE_END <= to

        Timestamp tsFrom = Timestamp.valueOf(from);
        Timestamp tsTo = Timestamp.valueOf(to);

        return application.getDatabase().get()
                .objects(Booking.class)
                .withQuery(Query.builder()
                        .table(BookingTable.TABLE)
                        .where("(" + BookingTable.COLUMN_DATE_START + " <= ? AND " + BookingTable.COLUMN_DATE_END + " >= ?) OR " +
                                "(" + BookingTable.COLUMN_DATE_START + " >= ? AND " + BookingTable.COLUMN_DATE_START + " < ?) OR " +
                                "(" + BookingTable.COLUMN_DATE_END + " > ? AND " + BookingTable.COLUMN_DATE_END + " <= ?)")
                        .whereValues(tsFrom, tsTo, tsFrom, tsTo, tsFrom, tsTo))
                .execute()
                .getList();
    }

    /**
     * Gets all bookings within a time period for a specific mechanic
     * @param from
     * @param to
     * @param mechanic
     * @return
     */
    public List<Booking> getBookingsBetweenForMechanic(LocalDateTime from, LocalDateTime to, User mechanic) {
        if(from == null || to == null || !from.isBefore(to)) {
            throw new RuntimeException("Cannot select bookings between " + from + " and " + to);
        }

        Timestamp tsFrom = Timestamp.valueOf(from);
        Timestamp tsTo = Timestamp.valueOf(to);

        return application.getDatabase().get()
                .objects(Booking.class)
                .withQuery(Query.builder()
                        .table(BookingTable.TABLE)
                        .where("((" + BookingTable.COLUMN_DATE_START + " <= ? AND " + BookingTable.COLUMN_DATE_END + " >= ?) OR " +
                                        "(" + BookingTable.COLUMN_DATE_START + " >= ? AND " + BookingTable.COLUMN_DATE_START + " < ?) OR " +
                                        "(" + BookingTable.COLUMN_DATE_END + " > ? AND " + BookingTable.COLUMN_DATE_END + " <= ?)) AND " +
                                BookingTable.COLUMN_MECHANIC_ID + " = ?")
                        .whereValues(tsFrom, tsTo, tsFrom, tsTo, tsFrom, tsTo, mechanic.getId()))
                .execute()
                .getList();
    }

    /**
     * Get a list of all bookings
     * @return
     */
    public List<Booking> getAllBookings() {
        return application.getDatabase().get()
                .objects(Booking.class)
                .withQuery(Query.builder()
                        .table(BookingTable.TABLE))
                .execute()
                .getList();
    }

    /**
     * Adds a booking to the database
     * @param booking
     * @return
     */
    public boolean addBooking(Booking booking) {
        if(!isValidBooking(
                LocalDateTime.from(booking.getDateStart().toInstant()),
                LocalDateTime.from(booking.getDateEnd().toInstant()),
                booking.getMechanic(),
                booking.getId()
        )) return false;

        return application.getDatabase().put()
                .objects(booking)
                .execute() != 0;
    }

    public boolean addBookingRaw(Booking booking) {
        return application.getDatabase().put()
                .objects(booking)
                .execute() != 0;
    }

    /**
     * Deletes a booking from the database
     * @param booking
     * @return
     */
    public boolean deleteBooking(Booking booking) {
        return application.getDatabase().delete()
                .objects(booking)
                .execute() != 0;
    }

    /**
     * Checks if a booking period is valid (no overlaps with other bookings by the same mechanic, within opening hours,
     * and no holidays).
     * @param from
     * @param to
     * @param mechanic
     * @return
     */
    public boolean isValidBooking(LocalDateTime from, LocalDateTime to, User mechanic, Integer currentId) {
        OpeningHours dayOpeningHours = openingHours.get(Math.abs((from.getDayOfWeek().getValue() - 2) % 7) + 1);

        if(mechanic == null) return false;


        if(dayOpeningHours == null
                || from.toLocalTime().isBefore(dayOpeningHours.getOpeningTime())
                || to.toLocalTime().isAfter(dayOpeningHours.getClosingTime())) {
            return false;
        }

        for(Holiday holiday : holidays) {
            if(from.toLocalDate().equals(holiday.getDate()) || to.toLocalDate().equals(holiday.getDate())) {
                // Within holiday
                if(holiday.isBankHoliday()) return false;
            }
        }

        return getBookingsBetweenForMechanic(from, to, mechanic).stream()
                .filter(booking -> !Objects.equals(booking.getId(), currentId))
                .count() == 0;
    }


    /**
     * Get an ordered list of holidays between the dates from and to
     * @param from The date from
     * @param to The date to
     * @return
     */
    public List<Holiday> getHolidaysBetween(LocalDate from, LocalDate to) {
        List<Holiday> res = new ArrayList<>();

        for(Holiday holiday : holidays) {
            if(holiday.getDate().isBefore(from)) continue;
            if(holiday.getDate().isAfter(to)) break;
            res.add(holiday);
        }

        return res;
    }

    /**
     * Get opening hours, a map where the day of the week corresponds to an OpeningHour
     * @return
     */
    public Map<Integer, OpeningHours> getOpeningHours() {
        return openingHours;
    }

    public OpeningHours getEarliestAndLatestOpeningHours() {
        return earliestAndLatestOpeningHours;
    }

    public static class Holiday {
        private final LocalDate date;
        private final String name;
        private final String type;
        private final boolean bankHoliday;

        public Holiday(LocalDate date, String name, String type, boolean bankHoliday) {
            this.date = date;
            this.name = name;
            this.type = type;
            this.bankHoliday = bankHoliday;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public boolean isBankHoliday() {
            return bankHoliday;
        }
    }

    public static class OpeningHours {
        private final LocalTime openingTime;
        private final LocalTime closingTime;

        public OpeningHours(LocalTime openingTime, LocalTime closingTime) {
            this.openingTime = openingTime;
            this.closingTime = closingTime;
        }

        public LocalTime getOpeningTime() {
            return openingTime;
        }

        public LocalTime getClosingTime() {
            return closingTime;
        }
    }

}
