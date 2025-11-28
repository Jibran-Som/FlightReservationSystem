import java.lang.reflect.Array;
import java.util.ArrayList;

public class FlightAgent extends Person {
    private ArrayList<Customer> clients; 

    public FlightAgent(int id, String firstName, String lastName, Date DoB, ArrayList<Customer> clients){
        super(id, firstName, lastName, DoB); 
        this.clients = clients;
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
