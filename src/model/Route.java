package model;

import backend.DatabaseManager;
import java.sql.SQLException;

public class Route {

    private int routeId;
    private Address departureLocation;
    private Address arrivalLocation;
    private DatabaseManager db = DatabaseManager.getInstance();

    public Route(Address departureLocation, Address arrivalLocation) throws SQLException {
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        int departureID = this.departureLocation.getAddressID();
        int arrivalID = this.arrivalLocation.getAddressID();
        this.setRouteID(db.insertRoute(departureID, arrivalID));
    }
    public int getRouteID(){
        return this.routeId;
    }
    public Address getDepartureLocation() {
        return departureLocation;
    }

    public Address getArrivalLocation() {
        return arrivalLocation;
    }

    public void setRouteID(int routeId){
        this.routeId = routeId;
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
