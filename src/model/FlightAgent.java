package model;


import backend.DatabaseManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class FlightAgent extends Person {
    private ArrayList<Customer> clients;
    private DatabaseManager db = DatabaseManager.getInstance();


    public FlightAgent(int id, String firstName, String lastName, Date DoB, ArrayList<Customer> clients) throws SQLException {
        super(firstName, lastName, DoB);
        this.setId(db.insertPerson(firstName, lastName, DoB.toSQLDate(), "flight_pass", "FlightAgent"));
        this.clients = clients;
    }



    public FlightAgent(int id, String firstName, String lastName, Date DoB, ArrayList<Customer> clients, boolean inDatabase) throws SQLException {
        super(firstName, lastName, DoB);
        this.clients = clients;

        if (!inDatabase) {
            this.setId(db.insertPerson(firstName, lastName, DoB.toSQLDate(), "flight_pass", "FlightAgent"));
        }
    }



    public ArrayList<Customer> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Customer> clients) {
        this.clients = clients;
    }




    public void addClient(Customer customer) {
        this.clients.add(customer);
    }

    public void removeClient(Customer customer) {
        this.clients.remove(customer);
    }

    @Override
    public String toString() {
        return "FlightAgent{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", DoB=" + getDoB() +
                ", clients=" + clients +
                '}';
    }

}
