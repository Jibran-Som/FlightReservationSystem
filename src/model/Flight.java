package model;

public class Flight {

    private int flightId;
    private Airplane airplane;
    private Route route;
    private Date departureDate;
    private Date arrivalDate;
    private int availableSeats;
    private int flightTime;
    private float price;

    public Flight(Airplane airplane, Route route, Date departureDate, Date arrivalDate, int availableSeats,
            int flightTime, float price) {
        this.airplane = airplane;
        this.route = route;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.availableSeats = availableSeats;
        this.flightTime = flightTime;
        this.price = price;
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

    public int getFlightTime() {
        return flightTime;
    }

    public float getPrice() {
        return price;
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

    public void setFlightTime(int flightTime) {
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
