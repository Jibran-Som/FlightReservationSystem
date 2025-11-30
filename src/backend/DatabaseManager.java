// DatabaseManager.java
package backend;

import model.*;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String URL = "jdbc:mysql://localhost:3306/FLIGHTRESERVE?useSSL=false&allowPublicKeyRetrieval=true";
    private String user;
    private String password;
    private Connection connection;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void connect(String user, String password) {
        this.user = user;
        this.password = password;
        try {
            connection = DriverManager.getConnection(URL, this.user, this.password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database.");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from the database.");
            }
        } catch (SQLException e) {
            System.out.println("Error disconnecting from the database.");
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // INSERT METHODS
    public int insert(String tableName, String[] columns, Object[] values, boolean expectGeneratedKey) throws SQLException {
        if (columns.length != values.length) {
            throw new IllegalArgumentException("Columns and values arrays must match");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder placeholders = new StringBuilder(") VALUES (");

        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            placeholders.append("?");
            if (i < columns.length - 1) {
                sql.append(", ");
                placeholders.append(", ");
            }
        }
        placeholders.append(")");
        sql.append(placeholders);

        PreparedStatement pstmt;
        if (expectGeneratedKey) {
            pstmt = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
        } else {
            pstmt = connection.prepareStatement(sql.toString());
        }

        for (int i = 0; i < values.length; i++) {
            pstmt.setObject(i + 1, values[i]);
        }

        int affectedRows = pstmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Insert failed, no rows affected.");
        }

        if (expectGeneratedKey) {
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Insert failed, no ID obtained.");
            }
        } else {
            return -1;
        }
    }

    public int insertPerson(String username, String firstName, String lastName, String dateBorn, String role) throws SQLException {
        String[] columns = {"username", "first_name", "last_name", "date_born", "role"};
        Object[] values = {username, firstName, lastName, dateBorn, role};
        return insert("person", columns, values, true);
    }

    public int insertPerson(String username, String firstName, String lastName, String dateBorn, String password, String role) throws SQLException {
        String[] columns = {"username", "first_name", "last_name", "date_born", "password", "role"};
        Object[] values = {username, firstName, lastName, dateBorn, password, role};
        return insert("person", columns, values, true);
    }

    public void insertCustomer(int personId, String email) throws SQLException {
        String[] columns = {"customer_id", "email"};
        Object[] values = {personId, email};
        insert("customer", columns, values, false);
    }

    public int insertAgent(int personId) throws SQLException {
        String[] columns = {"agent_id"};
        Object[] values = {personId};
        return insert("agent", columns, values, false);
    }

    public int insertAirline(String airlineName) throws SQLException {
        String[] columns = {"airline_name"};
        Object[] values = {airlineName};
        return insert("airline", columns, values, false);
    }

    public int insertAirplane(String airlineName, String name, int flightNumber) throws SQLException {
        String[] columns = {"airline_name", "name", "flight_number"};
        Object[] values = {airlineName, name, flightNumber};
        return insert("airplane", columns, values, true);
    }

    public int insertAddress(String postalCode, int number, String street, String city, String state, String country) throws SQLException {
        String[] columns = {"postal_code", "number", "street", "city", "state", "country"};
        Object[] values = {postalCode, number, street, city, state, country};
        return insert("address", columns, values, true);
    }

    public int insertFlight(int airplaneId, int routeId, CustomDate departureDate, CustomDate arrivalDate, int availableSeats, String flightLength, float price) throws SQLException {
        java.sql.Date sqlDeparture = java.sql.Date.valueOf(departureDate.toSQLDate());
        java.sql.Date sqlArrival = java.sql.Date.valueOf(arrivalDate.toSQLDate());
        String[] columns = {"airplane_id", "route_id", "departure_date", "arrival_date", "available_seats", "flight_length", "price"};
        Object[] values = {airplaneId, routeId, sqlDeparture, sqlArrival, availableSeats, flightLength, price};
        return insert("flight", columns, values, true);
    }

    public int insertBooking(int customerId, int flightId, int seatNumber) throws SQLException {
        String[] columns = {"customer_id", "flight_id", "seat_number"};
        Object[] values = {customerId, flightId, seatNumber};
        return insert("booking", columns, values, true);
    }

    public int insertRoute(int origin_id, int destination_id) throws SQLException {
        String[] columns = {"origin_id", "destination_id"};
        Object[] values = {origin_id, destination_id};
        return insert("route", columns, values, true);
    }



    // SELECT METHODS
    public ArrayList<Address> getAllAddresses() throws SQLException {
        ArrayList<Address> addresses = new ArrayList<>();
        String query = "SELECT * FROM address";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Address address = new Address(
                    rs.getInt("address_id"),
                    rs.getString("postal_code"),
                    rs.getInt("number"),
                    rs.getString("street"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getString("country")
                );
                addresses.add(address);
            }
        }
        return addresses;
    }

    public ArrayList<Airline> getAllAirlines() throws SQLException {
        ArrayList<Airline> airlines = new ArrayList<>();
        String query = "SELECT * FROM airline";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Airline airline = new Airline(rs.getString("airline_name"));
                airlines.add(airline);
            }
        }
        return airlines;
    }

    public ArrayList<Airplane> getAllAirplanes() throws SQLException {
        ArrayList<Airplane> airplanes = new ArrayList<>();
        String query = "SELECT a.*, al.airline_name FROM airplane a JOIN airline al ON a.airline_name = al.airline_name";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Airline airline = new Airline(rs.getString("airline_name"));
                Airplane airplane = new Airplane(
                    rs.getInt("airplane_id"),
                    airline,
                    rs.getString("name"),
                    "",
                    rs.getInt("flight_number"),
                    new ArrayList<>()
                );
                airplanes.add(airplane);
            }
        }
        return airplanes;
    }

    public ArrayList<Route> getAllRoutes() throws SQLException {
        ArrayList<Route> routes = new ArrayList<>();
        String query = "SELECT r.*, o.*, d.* FROM route r " +
            "JOIN address o ON r.origin_id = o.address_id " +
            "JOIN address d ON r.destination_id = d.address_id";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Address origin = new Address(
                    rs.getInt("o.address_id"),
                    rs.getString("o.postal_code"),
                    rs.getInt("o.number"),
                    rs.getString("o.street"),
                    rs.getString("o.city"),
                    rs.getString("o.state"),
                    rs.getString("o.country")
                );

                Address destination = new Address(
                    rs.getInt("d.address_id"),
                    rs.getString("d.postal_code"),
                    rs.getInt("d.number"),
                    rs.getString("d.street"),
                    rs.getString("d.city"),
                    rs.getString("d.state"),
                    rs.getString("d.country")
                );

                Route route = new Route(rs.getInt("route_id"), origin, destination);
                routes.add(route);
            }
        }
        return routes;
    }

    public ArrayList<Flight> getAllFlights() throws SQLException {
        ArrayList<Flight> flights = new ArrayList<>();
        String query = "SELECT * FROM flight";



        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {


            while (rs.next()) {
                Route route = getRouteById(rs.getInt("route_id"));
                Airplane airplane = getAirplaneById(rs.getInt("airplane_id"));


                Flight flight = new Flight(
                    rs.getInt("flight_id"),
                    airplane, // airplane would be fetched separately
                    route,
                    CustomDate.StringToDate(rs.getDate("departure_date").toString()),
                    CustomDate.StringToDate(rs.getDate("arrival_date").toString()),
                    rs.getInt("available_seats"),
                    rs.getString("flight_length"),
                    rs.getFloat("price")
                );
                flights.add(flight);
            }
        }
        return flights;
    }

    public Airplane getAirplaneById(int airplaneId) throws SQLException {
        String query = "SELECT a.*, al.airline_name FROM airplane a " +
            "JOIN airline al ON a.airline_name = al.airline_name " +
            "WHERE a.airplane_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, airplaneId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Airline airline = new Airline(rs.getString("airline_name"));
                return new Airplane(
                    rs.getInt("airplane_id"),
                    airline,
                    rs.getString("name"),
                    "Active", // default status
                    rs.getInt("flight_number"),
                    new ArrayList<>()
                );
            }
        }
        return null;
    }

    public Route getRouteById(int routeId) throws SQLException {
        String query = "SELECT r.*, o.*, d.* FROM route r " +
            "JOIN address o ON r.origin_id = o.address_id " +
            "JOIN address d ON r.destination_id = d.address_id " +
            "WHERE r.route_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, routeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Address origin = new Address(
                    rs.getInt("o.address_id"),
                    rs.getString("o.postal_code"),
                    rs.getInt("o.number"),
                    rs.getString("o.street"),
                    rs.getString("o.city"),
                    rs.getString("o.state"),
                    rs.getString("o.country")
                );

                Address destination = new Address(
                    rs.getInt("d.address_id"),
                    rs.getString("d.postal_code"),
                    rs.getInt("d.number"),
                    rs.getString("d.street"),
                    rs.getString("d.city"),
                    rs.getString("d.state"),
                    rs.getString("d.country")
                );

                return new Route(rs.getInt("route_id"), origin, destination);
            }
        }
        return null;
    }


    public ArrayList<Customer> getAllCustomers() throws SQLException {
        ArrayList<Customer> customers = new ArrayList<>();
        String query = "SELECT p.person_id, p.username, p.first_name, p.last_name, p.date_born, c.email " +
            "FROM person p JOIN customer c ON p.person_id = c.customer_id " +
            "WHERE p.role = 'Customer'";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                java.sql.Date dob = rs.getDate("date_born");
                CustomDate dateOfBirth = dob != null ? CustomDate.StringToDate(dob.toString()) : new CustomDate(1900, 01, 01);

                Customer customer = new Customer(
                    rs.getInt("person_id"),
                    rs.getString("username"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    dateOfBirth,
                    rs.getString("email")
                );
                customers.add(customer);
            }
        }
        return customers;
    }

    public ArrayList<Admin> getAllAdmins() throws SQLException {
        ArrayList<Admin> admins = new ArrayList<>();
        String query = "SELECT * FROM person WHERE role = 'Admin'";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                java.sql.Date dob = rs.getDate("date_born");
                CustomDate dateOfBirth = dob != null ? CustomDate.StringToDate(dob.toString()) : new CustomDate(1900, 1, 1);

                Admin admin = new Admin(
                    rs.getInt("person_id"),
                    rs.getString("username"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    dateOfBirth
                );
                admins.add(admin);
            }
        }
        return admins;
    }

    public ArrayList<FlightAgent> getAllAgents() throws SQLException {
        ArrayList<FlightAgent> agents = new ArrayList<>();
        String query = "SELECT p.* FROM person p " +
            "JOIN agent a ON p.person_id = a.agent_id " +
            "WHERE p.role = 'FlightAgent'";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                java.sql.Date dob = rs.getDate("date_born");
                CustomDate dateOfBirth = dob != null ? CustomDate.StringToDate(dob.toString()) : new CustomDate(1900, 1, 1);

                FlightAgent agent = new FlightAgent(
                    rs.getInt("person_id"),
                    rs.getString("username"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    dateOfBirth,
                    new ArrayList<>() // Empty clients list - can be populated separately
                );
                agents.add(agent);
            }
        }
        return agents;
    }


    public ArrayList<Booking> getAllBookings() throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.* FROM booking b";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Customer customer = getCustomerById(rs.getInt("customer_id"));
                Flight flight = getFlightById(rs.getInt("flight_id"));

                Booking booking = new Booking(
                    rs.getInt("booking_id"),
                    customer,
                    flight,
                    rs.getInt("seat_number")
                );
                bookings.add(booking);
            }
        }
        return bookings;
    }

    public Customer getCustomerById(int customerId) throws SQLException {
        String query = "SELECT p.person_id, p.username, p.first_name, p.last_name, p.date_born, c.email " +
            "FROM person p JOIN customer c ON p.person_id = c.customer_id " +
            "WHERE p.person_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                java.sql.Date dob = rs.getDate("date_born");
                CustomDate dateOfBirth = dob != null ? CustomDate.StringToDate(dob.toString()) : new CustomDate(1900, 01, 01);

                return new Customer(
                    rs.getInt("person_id"),
                    rs.getString("username"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    dateOfBirth,
                    rs.getString("email")
                );
            }
        }
        return null;
    }

    public Flight getFlightById(int flightId) throws SQLException {
        // Simplified - in real implementation, join with related tables
        String query = "SELECT * FROM flight WHERE flight_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, flightId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Flight(
                    rs.getInt("flight_id"),
                    null, // airplane
                    null, // route
                    CustomDate.StringToDate(rs.getDate("departure_date").toString()),
                    CustomDate.StringToDate(rs.getDate("arrival_date").toString()),
                    rs.getInt("available_seats"),
                    rs.getString("flight_length"),
                    rs.getFloat("price")
                );
            }
        }
        return null;
    }

    // UPDATE METHODS
    public int updatePerson(Person person) throws SQLException {
        String[] columns = {"username", "first_name", "last_name", "date_born"};
        String sqlDate = person.getDoB().toSQLDate();
        Object[] values = {person.getUsername(), person.getFirstName(), person.getLastName(), sqlDate};
        String whereClause = "person_id = ?";
        Object[] whereValues = {person.getId()};
        return update("person", columns, values, whereClause, whereValues);
    }

    public int updateCustomer(int customerId, String email) throws SQLException {
        String[] columns = {"email"};
        Object[] values = {email};
        String whereClause = "customer_id = ?";
        Object[] whereValues = {customerId};
        return update("customer", columns, values, whereClause, whereValues);
    }

    public int updateFlight(int flightId, int airplaneId, int routeId, CustomDate departureDate,
                            CustomDate arrivalDate, int availableSeats, String flightLength, float price) throws SQLException {
        String[] columns = {"airplane_id", "route_id", "departure_date", "arrival_date",
            "available_seats", "flight_length", "price"};

        java.sql.Date sqlDeparture = java.sql.Date.valueOf(departureDate.toSQLDate());
        java.sql.Date sqlArrival = java.sql.Date.valueOf(arrivalDate.toSQLDate());

        Object[] values = {airplaneId, routeId, sqlDeparture, sqlArrival, availableSeats, flightLength, price};
        String whereClause = "flight_id = ?";
        Object[] whereValues = {flightId};

        return update("flight", columns, values, whereClause, whereValues);
    }

    public int updateAirplane(int airplaneId, String airlineName, String name, int flightNumber) throws SQLException {
        String[] columns = {"airline_name", "name", "flight_number"};
        Object[] values = {airlineName, name, flightNumber};
        String whereClause = "airplane_id = ?";
        Object[] whereValues = {airplaneId};
        return update("airplane", columns, values, whereClause, whereValues);
    }

    public int updateAddress(int addressId, String postalCode, int number, String street,
                             String city, String state, String country) throws SQLException {
        String[] columns = {"postal_code", "number", "street", "city", "state", "country"};
        Object[] values = {postalCode, number, street, city, state, country};
        String whereClause = "address_id = ?";
        Object[] whereValues = {addressId};
        return update("address", columns, values, whereClause, whereValues);
    }

    public int updateRoute(int routeId, int originId, int destinationId) throws SQLException {
        String[] columns = {"origin_id", "destination_id"};
        Object[] values = {originId, destinationId};
        String whereClause = "route_id = ?";
        Object[] whereValues = {routeId};
        return update("route", columns, values, whereClause, whereValues);
    }

    public int updateBooking(int bookingId, int customerId, int flightId, int seatNumber) throws SQLException {
        String[] columns = {"customer_id", "flight_id", "seat_number"};
        Object[] values = {customerId, flightId, seatNumber};
        String whereClause = "booking_id = ?";
        Object[] whereValues = {bookingId};
        return update("booking", columns, values, whereClause, whereValues);
    }


    public int update(String tableName, String[] columns, Object[] values, String whereClause, Object[] whereValues) throws SQLException {
        if (columns.length != values.length) {
            throw new IllegalArgumentException("Columns and values arrays must have the same length");
        }

        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]).append(" = ?");
            if (i < columns.length - 1) sql.append(", ");
        }
        sql.append(" WHERE ").append(whereClause);

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            for (Object value : values) {
                pstmt.setObject(paramIndex++, value);
            }
            for (Object whereValue : whereValues) {
                pstmt.setObject(paramIndex++, whereValue);
            }
            return pstmt.executeUpdate();
        }
    }

    // DELETE METHODS
    public int delete(String tableName, String whereClause, Object[] whereValues) throws SQLException {
        if (whereClause == null || whereClause.trim().isEmpty()) {
            throw new IllegalArgumentException("WHERE clause cannot be null or empty for DELETE.");
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + whereClause;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (whereValues != null) {
                for (int i = 0; i < whereValues.length; i++) {
                    pstmt.setObject(i + 1, whereValues[i]);
                }
            }
            return pstmt.executeUpdate();
        }
    }

    public int deleteFlight(int flightId) throws SQLException {
        return delete("flight", "flight_id = ?", new Object[]{flightId});
    }

    public int deleteBooking(int bookingId) throws SQLException {
        return delete("booking", "booking_id = ?", new Object[]{bookingId});
    }

    public int deletePerson(int personId) throws SQLException {
        // First check if person is a customer and delete from customer table
        try {
            delete("customer", "customer_id = ?", new Object[]{personId});
        } catch (SQLException e) {
            // Ignore if not a customer
        }

        // Check if person is an agent and delete from agent table
        try {
            delete("agent", "agent_id = ?", new Object[]{personId});
        } catch (SQLException e) {
            // Ignore if not an agent
        }

        // Delete from agent_customer if exists
        try {
            delete("agent_customer", "agent_id = ?", new Object[]{personId});
            delete("agent_customer", "customer_id = ?", new Object[]{personId});
        } catch (SQLException e) {
            // Ignore if no agent_customer records
        }

        // Finally delete from person table
        return delete("person", "person_id = ?", new Object[]{personId});
    }

    public int deleteCustomer(int customerId) throws SQLException {
        // First delete any bookings by this customer
        delete("booking", "customer_id = ?", new Object[]{customerId});

        // Delete from agent_customer table
        delete("agent_customer", "customer_id = ?", new Object[]{customerId});

        // Delete from customer table
        int result = delete("customer", "customer_id = ?", new Object[]{customerId});

        // Also delete from person table
        delete("person", "person_id = ?", new Object[]{customerId});

        return result;
    }

    public int deleteAgent(int agentId) throws SQLException {
        // First delete from agent_customer table
        delete("agent_customer", "agent_id = ?", new Object[]{agentId});

        // Delete from agent table
        int result = delete("agent", "agent_id = ?", new Object[]{agentId});

        // Also delete from person table
        delete("person", "person_id = ?", new Object[]{agentId});

        return result;
    }

    public int deleteAirline(String airlineName) throws SQLException {
        // First get all airplanes for this airline
        ArrayList<Airplane> airplanes = getAllAirplanes();
        for (Airplane airplane : airplanes) {
            if (airplane.getAirline().getName().equals(airlineName)) {
                deleteAirplane(airplane.getAirplaneID());
            }
        }

        // Then delete the airline
        return delete("airline", "airline_name = ?", new Object[]{airlineName});
    }

    public void deleteAirplane(int airplaneId) throws SQLException {
        // First get all flights using this airplane
        ArrayList<Flight> flights = getAllFlights();
        for (Flight flight : flights) {
            if (flight.getAirplane() != null && flight.getAirplane().getAirplaneID() == airplaneId) {
                deleteFlight(flight.getFlightID());
            }
        }

        delete("airplane", "airplane_id = ?", new Object[]{airplaneId});
    }

    public int deleteAddress(int addressId) throws SQLException {
        // First check if address is used in any routes
        ArrayList<Route> routes = getAllRoutes();
        for (Route route : routes) {
            if (route.getDepartureLocation().getAddressID() == addressId ||
                route.getArrivalLocation().getAddressID() == addressId) {
                throw new SQLException("Cannot delete address: it is being used in route ID " + route.getRouteID());
            }
        }

        // If not used, delete the address
        return delete("address", "address_id = ?", new Object[]{addressId});
    }

    public int deleteRoute(int routeId) throws SQLException {
        // First get all flights using this route
        ArrayList<Flight> flights = getAllFlights();
        for (Flight flight : flights) {
            if (flight.getRoute() != null && flight.getRoute().getRouteID() == routeId) {
                throw new SQLException("Cannot delete route: it is being used in flight ID " + flight.getFlightID());
            }
        }

        // If no flights use this route, delete it
        return delete("route", "route_id = ?", new Object[]{routeId});
    }

    public int deleteBookingByCustomer(int customerId) throws SQLException {
        return delete("booking", "customer_id = ?", new Object[]{customerId});
    }

    public int deleteBookingByFlight(int flightId) throws SQLException {
        return delete("booking", "flight_id = ?", new Object[]{flightId});
    }

    public int deleteAgentCustomerRelationship(int agentId, int customerId) throws SQLException {
        return delete("agent_customer", "agent_id = ? AND customer_id = ?", new Object[]{agentId, customerId});
    }





}
