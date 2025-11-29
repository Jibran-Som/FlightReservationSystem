package model;

import backend.DatabaseManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class Airline {

    private String name;
    private ArrayList<Airplane> airplanes;
    private DatabaseManager db = DatabaseManager.getInstance();

    public Airline(String name, ArrayList<Airplane> airplanes) throws SQLException{
        this.name = name;
        this.airplanes = airplanes;
        db.insertAirline(name);
    }

    public Airline(String name) throws SQLException{
        this.name = name;
        db.insertAirline(name);
    }

    public String getName(){
        return this.name;
    }

    public ArrayList<Airplane> getAirplanes(){
        return this.airplanes;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setAirplanes(ArrayList<Airplane> airplanes){
        this.airplanes = airplanes;
    }

    public void addAirplane(Airplane airplane){
        this.airplanes.add(airplane);
    }

    public void removeAirplane(Airplane airplane){
        this.airplanes.remove(airplane);
    }

    @Override
    public String toString() {
        return "Airline [name=" + name + ", airplanes=" + airplanes + "]";
    }
}
