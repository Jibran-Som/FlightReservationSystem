public class Admin extends Person {


    public Admin(int id, String firstName, String lastName, Date DoB){
        super(id, firstName, lastName, DoB); 
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
