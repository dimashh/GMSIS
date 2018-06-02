package gmsis;

import gmsis.database.Database;
import gmsis.database.DatabaseHelper;
import gmsis.database.tables.*;
import gmsis.models.*;

import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DefaultDatabaseHelper implements DatabaseHelper {

    @Override
    public boolean onMigration(Database database, int fromVersion, int toVersion) throws SQLException {
        // Drop all tables
        database.getConnection().prepareStatement(AddressTable.getDropTableQuery()).execute();
        database.getConnection().prepareStatement(BillTable.getDropTableQuery()).execute();
        database.getConnection().prepareStatement(BookingTable.getDropTableQuery()).execute();
        database.getConnection().prepareStatement(CustomerTable.getDropTableQuery()).execute();
        database.getConnection().prepareStatement(PartTable.getDropTableQuery()).execute();
        database.getConnection().prepareStatement(UserTable.getDropTableQuery()).execute();
        database.getConnection().prepareStatement(VehicleTable.getDropTableQuery()).execute();
        database.getConnection().prepareStatement(WarrantyTable.getDropTableQuery()).execute();
        database.getConnection().prepareStatement(SpecialistRepairCentreTable.getDropTableQuery()).execute();
        database.getConnection().prepareStatement(BookingRepairTable.getDropTableQuery()).execute();

        onCreate(database);

        return true;
    }

    @Override
    public void onCreate(Database database) throws SQLException {
        // Create all tables
        database.getConnection().prepareStatement(AddressTable.getCreateTableQuery()).execute();
        database.getConnection().prepareStatement(BillTable.getCreateTableQuery()).execute();
        database.getConnection().prepareStatement(BookingTable.getCreateTableQuery()).execute();
        database.getConnection().prepareStatement(CustomerTable.getCreateTableQuery()).execute();
        database.getConnection().prepareStatement(PartTable.getCreateTableQuery()).execute();
        database.getConnection().prepareStatement(UserTable.getCreateTableQuery()).execute();
        database.getConnection().prepareStatement(VehicleTable.getCreateTableQuery()).execute();
        database.getConnection().prepareStatement(WarrantyTable.getCreateTableQuery()).execute();
        database.getConnection().prepareStatement(SpecialistRepairCentreTable.getCreateTableQuery()).execute();
        database.getConnection().prepareStatement(BookingRepairTable.getCreateTableQuery()).execute();

        // USERS (5 users minimum as in spec)
        User user1 = new User(10000, "John", "Doe", "test", User.UserRole.ADMINISTRATOR, null);
        User user2 = new User(20000, "Lauren", "Green", "test", User.UserRole.USER, null);
        User user3 = new User(30000, "Ikbal", "Ali", "test", User.UserRole.MECHANIC, 50.6);
        User user4 = new User(30303, "Joe", "Fields", "test", User.UserRole.MECHANIC, 20.9);
        User user5 = new User(50000, "Ashley", "Brown", "test", User.UserRole.ADMINISTRATOR, null);

        database.put()
                .objects(user1, user2, user3, user4, user5)
                .execute();

        // SPECIALIST REPAIR CENTRES (Just add 2 for testing)
        SpecialistRepairCentre spc1 = new SpecialistRepairCentre(
                "Best Repairs",
                new Address("14 best road", "Best Town", "Best Place", "THT 455"),
                "010495869",
                "test@test.com"
        );

        SpecialistRepairCentre spc2 = new SpecialistRepairCentre(
                "Qwick Repairs",
                new Address("15 Repair Lane", "Oxford", "Oxfordshire", "OT2 98C"),
                "039506979",
                "qwick@repairs.com"
        );

        database.put()
                .objects(spc1, spc2)
                .execute();
        
        //default future date 
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();
        
        //default past date 
        Calendar b = Calendar.getInstance();
        b.setTime(today);
        b.add(Calendar.DATE, -1);
        Date yesterday = b.getTime();
        
        
        // CUSTOMERS (10 customers minimum)
        //VEHICLES (15 vehicles minimum)
        // CUSTOMER 1
        Customer customer1 = new Customer(Customer.CustomerType.INDIVIDUAL,
                "John", "Fisher", "003852305", "hi@foo.com",
                new Address(null, "5 York Road", "London", "London", "CHJF46"));

        Vehicle vehicle11 = new Vehicle(Vehicle.VehicleType.CAR, new Warranty("WarrantyCo",
                new Address("12 Mile End Road", "London", "London", "E1 4GF"), tomorrow),
                "HFJT 489", "Fiesta", "Ford", "1.2L", Vehicle.FuelType.DIESEL,
                "Blue", tomorrow, yesterday, 1000);

        Part part1 = new Part("Engine", "The cars engine", false, new Date(), new Date(), 60);
        vehicle11.addPart(part1);

        customer1.addVehicle(vehicle11);
        
        // CUSTOMER 2
        Customer customer2 = new Customer(Customer.CustomerType.INDIVIDUAL,
                "Paul", "Green", "009932322", "paul@hello.com",
                new Address(null, "14 Reginald Road", "London", "London", "E79HT"));
        
        Vehicle vehicle21 = new Vehicle(Vehicle.VehicleType.CAR,null ,
                "LARD 777", "Impreza", "Subaru", "3.5L", Vehicle.FuelType.PETROL,
                "Blue", tomorrow, yesterday, 25000);

        customer2.addVehicle(vehicle21);
        
        // CUSTOMER 3
        Customer customer3 = new Customer(Customer.CustomerType.BUSINESS,
                "Blue Sky Ltd.", "", "008697080", "hi@bluesky.com",
                new Address(null, "45 Green Road", "London", "London", "EJ7FH5"));

        Vehicle vehicle31 = new Vehicle(Vehicle.VehicleType.TRUCK, new Warranty("Warranties 4 U",
                new Address("2 Dark Lane", "London", "London", "EH8 JKG"), tomorrow),
                "KLED G87", "F-150", "Ford", "3.5L", Vehicle.FuelType.DIESEL,
                "Black", tomorrow, yesterday, 5600);
        
        Vehicle vehicle32 = new Vehicle(Vehicle.VehicleType.TRUCK, new Warranty("Warranties 4 U",
                new Address("2 Dark Lane", "London", "London", "EH8 JKG"), tomorrow),
                "CASW 122", "FH-Series", "Volvo", "3.0L", Vehicle.FuelType.DIESEL,
                "Navy", tomorrow, yesterday, 5300);
        
        
        customer3.addVehicle(vehicle31);
        customer3.addVehicle(vehicle32);
        
        
        // CUSTOMER 4
        
         Customer customer4 = new Customer(Customer.CustomerType.BUSINESS,
                "TechPhone LTD", "", "003834505", "techphone@foo.com",
                new Address(null, "6 Mary Road", "London", "London", "E110JU"));
        
        Vehicle vehicle41 = new Vehicle(Vehicle.VehicleType.CAR, new Warranty("WarrantyCo",
                new Address("12 Mile End Road", "London", "London", "E1 4GF"), tomorrow),
                "XAAA 102", "LS", "Lexus", "3.5L", Vehicle.FuelType.PETROL,
                "Orange", tomorrow, yesterday, 1290);
        
        Vehicle vehicle42 = new Vehicle(Vehicle.VehicleType.CAR,new Warranty("Warranties 4 U",
                new Address("2 Dark Lane", "London", "London", "EH8 JKG"), tomorrow),
                "PPAQ 124", "Armada", "Nissan", "3.5L", Vehicle.FuelType.PETROL,
                "Grey", tomorrow, yesterday, 1212);
        
        customer4.addVehicle(vehicle41);
        customer4.addVehicle(vehicle42);
        
        
        
        //CUSTOMER 5
        
        
         Customer customer5 = new Customer(Customer.CustomerType.BUSINESS,
                "Apple inc", "", "003843325", "apple@foo.com",
                new Address(null, "8 Oxford Street", "London", "London", "WE9HYT"));
        
        Vehicle vehicle51 = new Vehicle(Vehicle.VehicleType.VAN, null,
                "MNAQ 845", "Hiace", "Toyota", "1.5L", Vehicle.FuelType.DIESEL,
                "Navy", tomorrow, yesterday, 10560);
        
        Vehicle vehicle52 = new Vehicle(Vehicle.VehicleType.VAN, null,
                "KEWL 201", "Camry", "Toyota", "2.5L", Vehicle.FuelType.DIESEL,
                "Purple", tomorrow, yesterday, 10111);
        
        customer5.addVehicle(vehicle51);
        customer5.addVehicle(vehicle52);
        
        
         //CUSTOMER 6
        
        
         Customer customer6 = new Customer(Customer.CustomerType.BUSINESS,
                "NI Corp.", "", "003857305", "NI@foo.com",
                new Address(null, "7 Orange Road", "London", "London", "13 JJHHA"));
        
        Vehicle vehicle61 = new Vehicle(Vehicle.VehicleType.CAR, new Warranty("WarrantyCo",
                new Address("12 Mile End Road", "London", "London", "E1 4GF"), tomorrow),
                "ASFW 142", "Camaro", "Chevrolet", "3.0L", Vehicle.FuelType.PETROL,
                "Grey", tomorrow, yesterday, 563);
        
        Vehicle vehicle62 = new Vehicle(Vehicle.VehicleType.VAN, new Warranty("MotorEasy",
                new Address("51 Liverpool Street", "London", "London", "E1 7FA"), tomorrow),
                "DDDD 109", "Escalade", "Cadillac", "3.5L", Vehicle.FuelType.PETROL,
                "Yellow", tomorrow, yesterday, 4312);
        
        
        customer6.addVehicle(vehicle61);
        customer6.addVehicle(vehicle62);       

         //CUSTOMER 7
        
         Customer customer7 = new Customer(Customer.CustomerType.BUSINESS,
                "Prism corp.", "", "003857305", "Prism@foo.com",
                new Address(null, "23A Adams Road", "London", "London", "CDEF46"));
        
         Vehicle vehicle71 = new Vehicle(Vehicle.VehicleType.TRUCK, new Warranty("Warranties 4 U",
                new Address("2 Dark Lane", "London", "London", "EH8 JKG"), tomorrow),
                "MOW 125", "FMX", "Volvo", "D-13", Vehicle.FuelType.DIESEL,
                "Black", tomorrow, yesterday, 10000);
        
        Vehicle vehicle72 = new Vehicle(Vehicle.VehicleType.CAR, new Warranty("WarrantyCo",
                new Address("12 Mile End Road", "London", "London", "E1 4GF"), new Date()),
                "XOXO 101", "A7", "Audi", "4.0L", Vehicle.FuelType.PETROL,
                "Blue", new Date(), null, 6501);
              
        
        customer7.addVehicle(vehicle71);
        customer7.addVehicle(vehicle72);
        
        
         //CUSTOMER 8
        
        
        Customer customer8 = new Customer(Customer.CustomerType.INDIVIDUAL,
                "Christoper", "Brown", "0038235605", "Christoper@foo.com",
                new Address(null, "5 Herts Road", "London", "London", "CHYS46"));
        
        Vehicle vehicle81 = new Vehicle(Vehicle.VehicleType.CAR, new Warranty("MotorEasy",
                new Address("51 Liverpool Street", "London", "London", "E1 7FA"), new Date()),
                "KOLL 203", "X3", "BMW", "2.8L", Vehicle.FuelType.DIESEL,
                "Red", tomorrow, yesterday, 100);
        
        customer8.addVehicle(vehicle81);

        
//CUSTOMER 9
        
        
         Customer customer9 = new Customer(Customer.CustomerType.INDIVIDUAL,
                "Jhonson", "Smith", "003832455", "Jhonson@foo.com",
                new Address(null, "89 Green Road", "London", "London", "CHH676"));
        
        Vehicle vehicle91 = new Vehicle(Vehicle.VehicleType.CAR, new Warranty("Warranties 4 U",
                new Address("2 Dark Lane", "London", "London", "EH8 JKG"), tomorrow),
                "LAWT 290", "A4", "Audi", "2.8L", Vehicle.FuelType.DIESEL,
                "Blue", tomorrow, yesterday, 2012);
        
        customer9.addVehicle(vehicle91);
         //CUSTOMER 10
        
        
         Customer customer10 = new Customer(Customer.CustomerType.INDIVIDUAL,
                "Will", "Brad", "0038532435", "Will@foo.com",
                new Address(null, "8 Crosby Road", "London", "London", "E79LU"));
        
        Vehicle vehicle101 = new Vehicle(Vehicle.VehicleType.VAN, new Warranty("MotorEasy",
                new Address("51 Liverpool Street", "London", "London", "E1 7FA"), tomorrow),
                "QAZX 111", "Vito", "Mercedes", "2.5L", Vehicle.FuelType.DIESEL,
                "Black", tomorrow, yesterday, 6540);
       
        
        customer10.addVehicle(vehicle101);

        
        // BOOKINGS (20 minimum, 10 in past, 10 in future)
        Booking booking1  = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "20-03-2017 10:15", 45);
        BookingRepair bookingRepair1 = new BookingRepair(BookingRepair.RepairType.REPAIR_PART, part1, spc1, 200.0, new Date(), new Date());
        booking1.getBookingRepairs().add(bookingRepair1);

        Booking booking2  = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "21-03-2017 12:30", 90);
        Booking booking3  = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "23-03-2017 15:00", 75);
        Booking booking4  = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "24-03-2017 09:45", 60);
        Booking booking5  = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "25-03-2017 10:00", 120);
        Booking booking6  = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "27-03-2017 09:15", 85);
        Booking booking7  = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "27-03-2017 14:30", 90);
        Booking booking8  = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "28-03-2017 13:30", 90);
        Booking booking9  = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "29-03-2017 16:00", 30);
        Booking booking10 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "31-03-2017 15:00", 75);
        Booking booking11 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "31-03-2017 09:45", 60);
        Booking booking12 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "04-04-2017 10:00", 120);
        Booking booking13 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "06-04-2017 09:15", 85);
        Booking booking14 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "07-04-2017 14:30", 90);
        Booking booking15 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "10-04-2017 13:30", 90);
        Booking booking16 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "11-04-2017 16:00", 60);
        Booking booking17 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "12-04-2017 11:00", 75);
        Booking booking18 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "13-04-2017 11:00", 60);
        Booking booking19 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "18-04-2017 13:15", 45);
        Booking booking20 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "19-04-2017 16:00", 60);
        Booking booking21 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "21-04-2017 11:00", 120);
        Booking booking23 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "24-04-2017 13:30", 120);
        Booking booking22 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "25-04-2017 10:15", 45);
        Booking booking24 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "25-04-2017 12:00", 90);
        Booking booking25 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "27-04-2017 15:30", 45);
        Booking booking26 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "02-05-2017 09:00", 120);
        Booking booking27 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "03-05-2017 09:30", 60);
        Booking booking28 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "03-05-2017 10:45", 90);
        Booking booking29 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "04-05-2017 14:30", 75);
        Booking booking30 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "05-05-2017 13:15", 45);
        Booking booking31 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "05-05-2017 16:00", 60);
        Booking booking32 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "08-05-2017 11:00", 120);
        Booking booking33 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "09-05-2017 10:15", 45);
        Booking booking34 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "09-05-2017 13:30", 120);
        Booking booking35 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "10-05-2017 12:00", 90);
        Booking booking36 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "11-05-2017 15:30", 45);
        Booking booking37 = generateBooking(user3, Booking.BookingType.DIAGNOSIS_REPAIR, "12-05-2017 09:00", 120);
        Booking booking38 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "12-05-2017 09:30", 60);
        Booking booking39 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "13-05-2017 10:00", 90);
        Booking booking40 = generateBooking(user4, Booking.BookingType.DIAGNOSIS_REPAIR, "16-05-2017 14:30", 75);

        vehicle52.addBooking(booking1);
        vehicle31.addBooking(booking2);
        vehicle91.addBooking(booking3);
        vehicle11.addBooking(booking4);
        vehicle41.addBooking(booking5);
        vehicle31.addBooking(booking6);
        vehicle71.addBooking(booking7);
        vehicle21.addBooking(booking8);
        vehicle62.addBooking(booking9);
        vehicle32.addBooking(booking10);
        vehicle11.addBooking(booking11);
        vehicle41.addBooking(booking12);
        vehicle21.addBooking(booking13);
        vehicle31.addBooking(booking14);
        vehicle71.addBooking(booking15);
        vehicle11.addBooking(booking16);
        vehicle32.addBooking(booking17);
        vehicle52.addBooking(booking18);
        vehicle91.addBooking(booking19);
        vehicle81.addBooking(booking20);
        vehicle21.addBooking(booking21);
        vehicle42.addBooking(booking22);
        vehicle62.addBooking(booking23);
        vehicle11.addBooking(booking24);
        vehicle71.addBooking(booking25);
        vehicle31.addBooking(booking26);
        vehicle52.addBooking(booking27);
        vehicle21.addBooking(booking28);
        vehicle32.addBooking(booking29);
        vehicle81.addBooking(booking30);
        vehicle41.addBooking(booking31);
        vehicle21.addBooking(booking32);
        vehicle62.addBooking(booking33);
        vehicle91.addBooking(booking34);
        vehicle71.addBooking(booking35);
        vehicle11.addBooking(booking36);
        vehicle32.addBooking(booking37);
        vehicle101.addBooking(booking38);
        vehicle42.addBooking(booking39);
        vehicle31.addBooking(booking40);

        database.put()
                .objects(customer1, customer2, customer3, customer4, customer5, customer6, customer7, customer8, customer9, customer10)
                .execute();
    }

    private Booking generateBooking(User mechanic, Booking.BookingType bookingType, String timeStart, int durationInMinutes) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime bookingStartTime = LocalDateTime.parse(timeStart, dateFormatter);
        LocalDateTime bookingEndTime = bookingStartTime.plusMinutes(durationInMinutes);

        Date bookingStart = Date.from(bookingStartTime.atZone(ZoneId.systemDefault()).toInstant());
        Date bookingEnd = Date.from(bookingEndTime.atZone(ZoneId.systemDefault()).toInstant());

        Bill bill = null;
        if(bookingEnd.before(new Date()) && Math.random() < 0.6d) {
            bill = new Bill(false, bookingEnd, null, mechanic.getHourlyFee() * (ChronoUnit.MINUTES.between(bookingStartTime, bookingEndTime) / 60.0));
        }

        return new Booking(mechanic, bookingType, bill, bookingStart, bookingEnd);
    }
}
