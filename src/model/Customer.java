package model;

import backend.DatabaseManager;

import java.sql.SQLException;

public class Customer extends Person {

    private Address address;
    private String email;
    private String phoneNumber;
    private DatabaseManager db = DatabaseManager.getInstance();

    public Customer(String firstName, String lastName, Date DoB, Address address, String email, String phoneNumber) throws SQLException {
        super(firstName, lastName, DoB);
        // Insert into person table (no email parameter now)
        this.setId(db.insertPerson(firstName, lastName, DoB.toSQLDate(), "", "Customer"));
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        // Insert into customer table with email
        db.insertCustomer(this.getId(), email);
    }


    public Customer(String fn, String ln, Date dob) throws SQLException {
        super(fn, ln, dob);
        this.setId(db.insertPerson(fn, ln, dob.toSQLDate(), "Customer"));
        this.email = "";
        this.phoneNumber = "";
        db.insertCustomer(this.getId(), "");
    }


    public Customer(String fn, String ln, Date dob, boolean inDatabase) throws SQLException {

        super(fn, ln, dob);
        this.setId(db.insertPerson(fn, ln, dob.toSQLDate(), "Customer"));
        this.email = "";
        this.phoneNumber = "";

        if(!inDatabase) {
            db.insertCustomer(this.getId(), "");
        }
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", DoB=" + getDoB() +
                ", address=" + address +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }


}
