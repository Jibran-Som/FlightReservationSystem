package model;

import backend.DatabaseManager;
import java.sql.SQLException;

public class Flight {

    private int flightId;
    private Airplane airplane;
    private Route route;
    private Date departureDate;
    private Date arrivalDate;
    private int availableSeats;
    private String flightTime;
    private float price;
    private DatabaseManager db = DatabaseManager.getInstance();

    public Flight(Airplane airplane, Route route, Date departureDate, Date arrivalDate, int availableSeats,
            String flightTime, float price) throws SQLException {
        this.airplane = airplane;
        this.route = route;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.availableSeats = availableSeats;
        this.flightTime = flightTime;
        this.price = price;
        this.setFlightID(db.insertFlight(this.airplane.getAirplaneID(), this.route.getRouteID(), departureDate, arrivalDate, availableSeats, flightTime, price));
    }

    public int getFlightID() {
        return flightId;
    }

    public Airplane getAirplane() {
        return airplane;
    }

    public Route getRoute() {
        return route;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public String getFlightTime() {
        return flightTime;
    }

    public float getPrice() {
        return price;
    }

    public void setFlightID(int flightId) {
        this.flightId = flightId;
    }

    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public void setFlightTime(String flightTime) {
        this.flightTime = flightTime;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Flight [airplane=" + airplane + ", route=" + route + ", departureDate=" + departureDate
                + ", arrivalDate=" + arrivalDate + ", availableSeats=" + availableSeats + ", flightTime=" + flightTime
                + ", price=" + price + "]";
    }
}
