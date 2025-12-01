// Admin.java
// Inherits from Person class
// Represents an admin user with administrative privileges
package model;

public class Admin extends Person {
    
    // === Constructors ===
    public Admin(int id, String username, String firstName, String lastName, CustomDate DoB) {
        super(id, username, firstName, lastName, DoB);
    }

    public Admin(String username, String firstName, String lastName, CustomDate DoB) {
        super(username, firstName, lastName, DoB);
    }

    // === Other Methods ===
    @Override
    public String toString() {
        return "Admin{id=" + getId() + ", username='" + getUsername() + "', name=" + getFirstName() + " " + getLastName() + "}";
    }
}