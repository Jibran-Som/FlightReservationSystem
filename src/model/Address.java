package model;

import backend.DatabaseManager;
import java.sql.SQLException;

public class Address {
    private int addressID;
    private String postalCode;
    private int number;
    private String street;
    private String city;
    private String state;
    private String country;
    private DatabaseManager db = DatabaseManager.getInstance();


    public Address() throws SQLException{
        this.postalCode = "";
        this.number = 0;
        this.street = "";
        this.city = "";
        this.state = "";
        this.country = "";
        this.setAddressID(db.insertAddress(postalCode, number, street, city, state, country));
    }

    public Address(String postalCode, int number, String street, String city, String state, String country) throws SQLException{
        this.postalCode = postalCode;
        this.number = number;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.setAddressID(db.insertAddress(postalCode, number, street, city, state, country));
    }

    public int getAddressID(){
        return this.addressID;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public int getNumber() {
        return number;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public void setAddressID(int addressID){
        this.addressID = addressID;
    }

    public void setPostalCode(String postalCode){
        this.postalCode = postalCode;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }




    @Override
    public String toString() {
        return number + " " + street + ", " + city + ", " + state + ", " + country;
    }
}
