Garage Management Information System
====================================
NOTE: We only provide an integrated system as we designed all modules to be
integrated from the start with only one 'Main'. This has been approved by 
Mustafa and our TAs.

On first run, the database is generated with test data and will be generated in 
the same directory as the jar file. Booking are generated in the past and future 
based around the current date of generation. 

Additionally both the SPC and Parts modules do not exist as the assigned team 
members did not participate.


## DEFAULT LOGINS
There are 5 default users that are able to login to the system, the admins may 
add edit or remove any of these.

John Doe     - ID: 10000 - Password: test - Role: ADMINISTRATOR
Lauren Green - ID: 20000 - Password: test - Role: USER
Ikbal Ali    - ID: 30000 - Password: test - Role: MECHANIC - Hourly Wage: £50.60
Joe Fields   - ID: 30303 - Password: test - Role: MECHANIC - Hourly Wage: £20.90
Ashley Brown - ID: 50000 - Password: test - Role: ADMINISTRATOR


## SEARCH
The search for all modules is full-filled by a persistent search bar at the top 
of the screen, it can search for Customers (by first and last name), 
Vehicles (by Registration, Model, Make and Type) and Bookings of any matching 
Customer or Vehicle.

The search is accessible from any screen on the top bar and immediately presents
search results as you type.


## CUSTOMER MODULE
The customer screen displays one main table, which shows the the existing 
private and business customers. The two types of customers are identified by a 
cell value in the table and as stated in the requirements the table also contains 
customer details such as first name, address, postcode, phone and email address.

The users is able to edit the customers' details directly from the table, from 
individual cell. To save the changes the user must commit (press enter) after 
the new data is input and click the button to save the data into the database.

Bookings for customers can be made by selecting a specific customer and clicking
on the button 'Edit Bookings' button. If there are existing bookings, those are 
listed and the user is also able to make new bookings.

The bill view is a separate screen which contains a table displaying all the bills.
The user will also be able to check the status and settle bills. The breakdown of 
the bill can be seen by viewing details which shows the booking details with the 
bill breakdown.

When first initialising the customer screen, the existing users both private and
business will be listed in the table. However, if the user wishes to search for 
a specific customer, he/she can use the search bar either at the top of the table. 


## VEHICLE MODULE
The vehicle screen contains two tables. First table shows available vehicles that 
are on the system, while the second table shows the further information accordingly. 
On selecting a vehicle from the former, the latter table displays the information
for that vehicle. 

On vehicle selection the vehicle details and booking can be edited and deleted.

To add a vehicle, user is redirected to customer screen to select a customer. 
Once a customer is selected and switched to vehicle screen, it will only show 
vehicles available for that customer.

If a vehicle was edited, on save the vehicle screen will display vehicle(s) of 
the customer who the vehicle belongs to.


## BOOKING MODULE
The booking screen shows an easy to use calendar view which can switch between 
different time periods. It shows daily bookings (One column for each mechanic 
that has a booking), Weekly bookings (Shows one mechanics weekly bookings at a time,
the mechanic can be changed in the drop down top right) and a Monthly view which
provides a quick overview with colour coded indicators for the numbers of bookings.

Double clicking on a day in monthly or weekly view takes you to the day view for
that day. Dragging vertically on the daily or weekly view allows you to create a
booking for that time quickly.

Double clicking a booking or selecting a booking and clicking the Edit Booking
button brings you to a list of all bookings for that customer with the booking 
you clicked pre-selected.

If the booking is in the future you may edit any details, if it is in progress 
or in the past you may complete the booking after updating the current mileage.
When completing a booking it creates a bill for the customer with the pricing 
breakdown as shown.

Note: You may add new parts or add parts to repair on the booking and optionally
send them to SPC (or the vehicle to SPC). However since both the SPC and Parts 
modules are not implemented this is not a fully completed feature. The list of 
SPCs is not editable, the price of a part is not editable etc. as this should be
handled by the SPC module but was not implemented by that team member. 

Only associating parts to a booking is supported. (As in the requirements for 
the diagnostic repair module)