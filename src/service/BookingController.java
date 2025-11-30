// BookingService.java
package service;

import model.*;
import backend.DatabaseManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookingController {
    private DatabaseManager db = DatabaseManager.getInstance();

    public Booking createBooking(Customer customer, Flight flight, int seatNumber) throws SQLException {
        int bookingId = db.insertBooking(customer.getId(), flight.getFlightID(), seatNumber);
        return new Booking(bookingId, customer, flight, seatNumber);
    }

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

    public void cancelBooking(int bookingId) throws SQLException {
        db.deleteBooking(bookingId);
    }

    public ArrayList<Booking> getAllBookings() throws SQLException {
        return db.getAllBookings();
    }

    public void updateBooking(Booking booking) throws SQLException {
    db.updateBooking(booking.getBookingId(), 
                    booking.getCustomer().getId(), 
                    booking.getFlight().getFlightID(), 
                    booking.getSeatNumber());
    }



}
