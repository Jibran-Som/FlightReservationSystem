// CustomerController.java
package service;

import backend.DatabaseManager;
import java.sql.SQLException;
import java.util.ArrayList;
import model.*;

public class CustomerController {
    private DatabaseManager db = DatabaseManager.getInstance();

    // Creates a new customer by inserting the customer details into the database
    // First, inserts the customer as a "Person" and then inserts the customer-specific details
    // Returns a Customer object with the relevant details
    public Customer createCustomer(String username, String firstName, String lastName, CustomDate dob,
                                   String email, String phoneNumber) throws SQLException {
        // Insert the person details into the person table with the provided username
        int personId = db.insertPerson(username, firstName, lastName, dob.toSQLDate(), "Customer");

        // Insert customer-specific details into the customer table
        db.insertCustomer(personId, email);

        // Return a new Customer object with the inserted details
        return new Customer(personId, username, firstName, lastName, dob, email);
    }

    // Retrieves a customer by their customerId from the database
    // Returns a Customer object with the details from the database    
    public Customer getCustomer(int customerId) throws SQLException {
        return db.getCustomerById(customerId);
    }

    // Retrieves all customers in the system as an ArrayList
    // Returns a list of Customer objects for all customers stored in the database
    public ArrayList<Customer> getAllCustomersAsArray() throws SQLException {
        return db.getAllCustomers();
    }

    // Updates an existing customer's information in the database
    // First, updates the person's details (e.g., name, DOB) and then updates the customer's specific details  
    public void updateCustomer(Customer customer) throws SQLException {
        db.updatePerson(customer);
        db.updateCustomer(customer.getId(), customer.getEmail());

    }

    // Deletes a customer from the database by customerId
    // Returns true if the deletion was successful (based on the number of affected rows)
    // If the deletion fails, throws an SQLException    
    public boolean deleteCustomer(int customerId) throws SQLException {
        try {
            int result = db.deleteCustomer(customerId);
            return result > 0;
        } catch (SQLException e) {
            throw new SQLException("Error deleting customer: " + e.getMessage(), e);
        }
    }



}
