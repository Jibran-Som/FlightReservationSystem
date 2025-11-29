import backend.*;
import java.sql.SQLException;
import model.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {

        DatabaseManager db = DatabaseManager.getInstance();
        db.connect("admin_user", "admin_password");

        Date date = new Date();
        Address add = new Address();
        Address add1 = new Address("TTT", 123, "ayo", "pause", "popo", "mane");
        Route route = new Route(add, add1);
        System.out.println("Route ID: " + route.getRouteID());
        Airline airline = new Airline("Yo");
        Airplane airplane = new Airplane(airline, "Uhh", 2);
        System.out.println("Airplane ID: " + airplane.getAirplaneID());
        Flight f = new Flight(airplane, route, date, date, 0, "11:11", 10.12f);

    }
}
