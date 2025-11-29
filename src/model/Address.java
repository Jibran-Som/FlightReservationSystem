package model;

public class Address {

    private int number;
    private String street;
    private String city;
    private String state;
    private String country;


    public Address() {
        this.number = 0;
        this.street = "";
        this.city = "";
        this.state = "";
        this.country = "";
    }

    public Address(int number, String street, String city, String state, String country) {
        this.number = number;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
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
