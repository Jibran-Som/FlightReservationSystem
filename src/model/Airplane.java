// Airplane.java
// Entity class for airplanes
// Represents an airplane with its details and routes it flies
package model;

import java.util.ArrayList;

public class Airplane {
    private int airplaneID;
    private Airline airline;
    private String name;
    private String status;
    private int flightNumber;
    private ArrayList<Route> routesFlying;

    // === Constructors ===
    public Airplane(Airline airline, String name, int flightNumber) {
        this.airline = airline;
        this.name = name;
        this.flightNumber = flightNumber;
        this.routesFlying = new ArrayList<>();
    }

    public Airplane(int airplaneID, Airline airline, String name, String status, int flightNumber, ArrayList<Route> routesFlying) {
        this.airplaneID = airplaneID;
        this.airline = airline;
        this.name = name;
        this.status = status;
        this.flightNumber = flightNumber;
        this.routesFlying = routesFlying;
    }

    // === Getters ===
    public int getAirplaneID() { return airplaneID; }
    public Airline getAirline() { return airline; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public int getFlightNumber() { return flightNumber; }
    public ArrayList<Route> getRoutesFlying() { return routesFlying; }

    // === Setters ===
    public void setAirplaneID(int airplaneID) { this.airplaneID = airplaneID; }
    public void setAirline(Airline airline) { this.airline = airline; }
    public void setName(String name) { this.name = name; }
    public void setStatus(String status) { this.status = status; }
    public void setFlightNumber(int flightNumber) { this.flightNumber = flightNumber; }
    public void setRoutesFlying(ArrayList<Route> routesFlying) { this.routesFlying = routesFlying; }

    // === Other Methods ===

    // Managing routesFlying
    public void addRoute(Route route) { this.routesFlying.add(route); }
    public void removeRoute(Route route) { this.routesFlying.remove(route); }

    @Override
    public String toString() {
        return "Airplane{id=" + airplaneID + ", name='" + name + "', airline=" + airline.getName() +
            ", flightNumber=" + flightNumber + "}";
    }
}
