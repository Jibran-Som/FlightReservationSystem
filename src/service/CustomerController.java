// CustomerController.java
package service;

import model.*;
import backend.DatabaseManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerController {
    private DatabaseManager db = DatabaseManager.getInstance();

    // Update to include username parameter
    public Customer createCustomer(String username, String firstName, String lastName, CustomDate dob,
                                   String email, String phoneNumber) throws SQLException {
        // Insert into person table with username
        int personId = db.insertPerson(username, firstName, lastName, dob.toSQLDate(), "Customer");

        // Insert into customer table
        db.insertCustomer(personId, email);

        // Return customer object with username
        return new Customer(personId, username, firstName, lastName, dob, email);
    }

    public Customer getCustomer(int customerId) throws SQLException {
        return db.getCustomerById(customerId);
    }

    public ArrayList<Customer> getAllCustomersAsArray() throws SQLException {
        return db.getAllCustomers();
    }

    public void updateCustomer(Customer customer) throws SQLException {
        db.updatePerson(customer);
        db.updateCustomer(customer.getId(), customer.getEmail());

    }
}