// Airline.java
// Entity class for airlines
// Represents an airline with its name and airplanes it operates
package model;

import java.util.ArrayList;

public class Airline {
    private String name;
    private ArrayList<Airplane> airplanes;

    // === Constructors ===
    public Airline(String name) {
        this.name = name;
        this.airplanes = new ArrayList<>();
    }

    public Airline(String name, ArrayList<Airplane> airplanes) {
        this.name = name;
        this.airplanes = airplanes;
    }

    // === Getters ===
    public String getName() { return name; }
    public ArrayList<Airplane> getAirplanes() { return airplanes; }

    // === Setters ===
    public void setName(String name) { this.name = name; }
    public void setAirplanes(ArrayList<Airplane> airplanes) { this.airplanes = airplanes; }

    // === Other Methods ===
    public void addAirplane(Airplane airplane) { this.airplanes.add(airplane); }
    public void removeAirplane(Airplane airplane) { this.airplanes.remove(airplane); }

    @Override
    public String toString() {
        return "Airline{name='" + name + "', airplanes=" + airplanes.size() + "}";
    }
}
