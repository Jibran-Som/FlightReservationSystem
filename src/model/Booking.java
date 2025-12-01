// Booking.java
// Entity class for bookings
// Represents a booking made by a customer for a flight

package model;

public class Booking {
    private int bookingId;
    private Customer customer;
    private Flight flight;
    private int seatNumber;

    // === Constructors ===
    public Booking(Customer customer, Flight flight, int seatNumber) {
        this.customer = customer;
        this.flight = flight;
        this.seatNumber = seatNumber;
    }

    public Booking(int bookingId, Customer customer, Flight flight, int seatNumber) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.flight = flight;
        this.seatNumber = seatNumber;
    }

    // === Getters ===
    public int getBookingId() { return bookingId; }
    public Customer getCustomer() { return customer; }
    public Flight getFlight() { return flight; }
    public int getSeatNumber() { return seatNumber; }

    // === Setters ===
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setFlight(Flight flight) { this.flight = flight; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    // === Other Methods ===
    @Override
    public String toString() {
        return "Booking{id=" + bookingId + ", customer=" + customer.getFirstName() +
            " " + customer.getLastName() + ", flight=" + flight.getFlightID() +
            ", seat=" + seatNumber + "}";
    }
}
