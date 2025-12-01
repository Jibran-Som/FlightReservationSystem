// BookingService.java
package service;

import backend.DatabaseManager;
import java.sql.SQLException;
import java.util.ArrayList;
import model.*;

public class BookingController {
    private DatabaseManager db = DatabaseManager.getInstance();

    // Creates a new booking by inserting it into the database with customer and flight details
    // Returns a Booking object with the assigned bookingId
    public Booking createBooking(Customer customer, Flight flight, int seatNumber) throws SQLException {
        int bookingId = db.insertBooking(customer.getId(), flight.getFlightID(), seatNumber);
        return new Booking(bookingId, customer, flight, seatNumber);
    }

    // Retrieves all bookings made by a specific customer based on their customerId
    // Returns a list of Booking objects for that customer
    public ArrayList<Booking> getCustomerBookings(int customerId) throws SQLException {
        ArrayList<Booking> allBookings = db.getAllBookings();
        ArrayList<Booking> customerBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            if (booking.getCustomer().getId() == customerId) {
                customerBookings.add(booking);
            }
        }
        return customerBookings;
    }

    // Cancels a booking by deleting the booking record from the database using the bookingId
    public void cancelBooking(int bookingId) throws SQLException {
        db.deleteBooking(bookingId);
    }

    // Retrieves all bookings in the system
    // Returns a list of all Booking objects stored in the database    
    public ArrayList<Booking> getAllBookings() throws SQLException {
        return db.getAllBookings();
    }

    // Updates an existing booking's details in the database (customer, flight, seat number)
    public void updateBooking(Booking booking) throws SQLException {
    db.updateBooking(booking.getBookingId(), 
                    booking.getCustomer().getId(), 
                    booking.getFlight().getFlightID(), 
                    booking.getSeatNumber());
    }



}
