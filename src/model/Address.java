// Address.java
// Entity class for addresses
// Represents a physical address with its details
package model;

public class Address {
    private int addressID;
    private String postalCode;
    private int number;
    private String street;
    private String city;
    private String state;
    private String country;

    // === Constructors ===
    public Address() {
        this.postalCode = "";
        this.number = 0;
        this.street = "";
        this.city = "";
        this.state = "";
        this.country = "";
    }

    public Address(String postalCode, int number, String street, String city, String state, String country) {
        this.postalCode = postalCode;
        this.number = number;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public Address(int addressID, String postalCode, int number, String street, String city, String state, String country) {
        this.addressID = addressID;
        this.postalCode = postalCode;
        this.number = number;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    // === Getters ===
    public int getAddressID() { return addressID; }
    public String getPostalCode() { return postalCode; }
    public int getNumber() { return number; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getCountry() { return country; }

    // === Setters ===
    public void setAddressID(int addressID) { this.addressID = addressID; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setNumber(int number) { this.number = number; }
    public void setStreet(String street) { this.street = street; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setCountry(String country) { this.country = country; }

    // === Other Methods ===
    @Override
    public String toString() {
        return number + " " + street + ", " + city + ", " + state + ", " + country;
    }
}
