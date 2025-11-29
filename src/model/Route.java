package model;

public class Route {

    private int routeId;
    private Address departureLocation;
    private Address arrivalLocation;

    public Route(Address departureLocation, Address arrivalLocation) {
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
    }

    public Address getDepartureLocation() {
        return departureLocation;
    }

    public Address getArrivalLocation() {
        return arrivalLocation;
    }

    public void setDepartureLocation(Address departureLocation) {
        this.departureLocation = departureLocation;
    }

    public void setArrivalLocation(Address arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }

    @Override
    public String toString() {
        return "Route [departureLocation=" + departureLocation + ", arrivalLocation=" + arrivalLocation + "]";
    }
}
