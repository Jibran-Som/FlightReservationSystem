// Flight.java
// Entity class for flights
// Represents a flight with its details

package model;

public class Flight {
    private int flightId;
    private Airplane airplane;
    private Route route;
    private CustomDate departureCustomDate;
    private CustomDate arrivalCustomDate;
    private int availableSeats;
    private String flightTime;
    private float price;

    // === Constructors ===
    public Flight(Airplane airplane, Route route, CustomDate departureCustomDate, CustomDate arrivalCustomDate,
                  int availableSeats, String flightTime, float price) {
        this.airplane = airplane;
        this.route = route;
        this.departureCustomDate = departureCustomDate;
        this.arrivalCustomDate = arrivalCustomDate;
        this.availableSeats = availableSeats;
        this.flightTime = flightTime;
        this.price = price;
    }

    public Flight(int flightId, Airplane airplane, Route route, CustomDate departureCustomDate, CustomDate arrivalCustomDate,
                  int availableSeats, String flightTime, float price) {
        this.flightId = flightId;
        this.airplane = airplane;
        this.route = route;
        this.departureCustomDate = departureCustomDate;
        this.arrivalCustomDate = arrivalCustomDate;
        this.availableSeats = availableSeats;
        this.flightTime = flightTime;
        this.price = price;
    }

    // === Getters ===
    public int getFlightID() { return flightId; }
    public Airplane getAirplane() { return airplane; }
    public Route getRoute() { return route; }
    public CustomDate getDepartureDate() { return departureCustomDate; }
    public CustomDate getArrivalDate() { return arrivalCustomDate; }
    public int getAvailableSeats() { return availableSeats; }
    public String getFlightTime() { return flightTime; }
    public float getPrice() { return price; }

    // === Setters ===
    public void setFlightID(int flightId) { this.flightId = flightId; }
    public void setAirplane(Airplane airplane) { this.airplane = airplane; }
    public void setRoute(Route route) { this.route = route; }
    public void setDepartureDate(CustomDate departureCustomDate) { this.departureCustomDate = departureCustomDate; }
    public void setArrivalDate(CustomDate arrivalCustomDate) { this.arrivalCustomDate = arrivalCustomDate; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public void setFlightTime(String flightTime) { this.flightTime = flightTime; }
    public void setPrice(float price) { this.price = price; }

    // === Other Methods ===
    @Override
    public String toString() {
        return "Flight{id=" + flightId + ", from=" + route.getDepartureLocation().getCity() +
            ", to=" + route.getArrivalLocation().getCity() + ", date=" + departureCustomDate +
            ", price=$" + price + "}";
    }
}
