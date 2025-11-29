package model;

import backend.DatabaseManager;

import java.sql.SQLException;

public class Admin extends Person {
    private DatabaseManager db = DatabaseManager.getInstance();


    public Admin(int id, String firstName, String lastName, Date DoB) throws SQLException {
        super(firstName, lastName, DoB);
        this.setId(db.insertPerson(firstName, lastName, DoB.toSQLDate(), "admin_pass", "Admin"));
    }

    public Admin(int id, String firstName, String lastName, Date DoB, boolean inDatabase) throws SQLException {
        super(firstName, lastName, DoB);
        if(!inDatabase){
            this.setId(db.insertPerson(firstName, lastName, DoB.toSQLDate(), "admin_pass", "Admin"));
        }
    }



    @Override
    public String toString() {
        return "Admin{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", DoB=" + getDoB() +
                '}';
    }

}
