// FlightService.java
package service;

import backend.DatabaseManager;
import java.sql.SQLException;
import java.util.ArrayList;
import model.*;

public class FlightController {
    private DatabaseManager db = DatabaseManager.getInstance();

    // Creates a new flight by inserting the flight details into the database
    // Returns a Flight object with the assigned flightId and details
    public Flight createFlight(Airplane airplane, Route route, CustomDate departureCustomDate,
                               CustomDate arrivalCustomDate, int availableSeats, String flightTime, float price) throws SQLException {
        // Insert flight details into the database and get the generated flightId
        int flightId = db.insertFlight(airplane.getAirplaneID(), route.getRouteID(),
            departureCustomDate, arrivalCustomDate, availableSeats, flightTime, price);
        // Return a new Flight object with the inserted flight details
        return new Flight(flightId, airplane, route, departureCustomDate, arrivalCustomDate, availableSeats, flightTime, price);
    }

    // Retrieves all flights in the system from the database
    // Returns a list of all Flight objects stored in the database
    public ArrayList<Flight> getAllFlights() throws SQLException {
        return db.getAllFlights(); // Fetch all flights from the database
    }

    // Retrieves a flight by its flightId from the database
    // Returns a Flight object with the details of the specified flight
    public Flight getFlight(int flightId) throws SQLException {
        return db.getFlightById(flightId);
    }

    // Searches for flights based on the given origin city, destination city, and departure date
    // Filters flights that match the departure date and other search parameters
    // Returns a list of matching Flight objects

    public ArrayList<Flight> searchFlights(String originCity, String destinationCity, CustomDate customDate) throws SQLException {
        // Retrieve all flights and filter based on the custom date
        ArrayList<Flight> allFlights = getAllFlights();
        ArrayList<Flight> results = new ArrayList<>();

        // Iterate through each flight and check if the departure date matches the search date
        for (Flight flight : allFlights) {
            if (flight.getDepartureDate().toString().equals(customDate.toString())) {
                results.add(flight); // Add the matching flight to the results list
            }
        }
        return results; // Return the list of matching flights
    }



    // Add this to your DatabaseManager.java

}
