package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.*;


public class DatabaseManager {

    private static DatabaseManager instance; // Singleton instance

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
        }
        catch (SQLException e) {
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
        }
        catch (SQLException e) {
            System.out.println("Error disconnecting from the database.");
        }
    }


    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        }
        catch (SQLException e) {
            return false;
        }
    }


    // INSERTION METHODS

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
            return -1; // ID was manually supplied
        }
    }


    public int insertPerson(String firstName, String lastName, String dateBorn, String role) throws SQLException {
        String[] columns = {"first_name", "last_name", "date_born", "role"};
        Object[] values = {firstName, lastName, dateBorn, role};
        return insert("person", columns, values, true); // expect generated ID
    }

    public int insertPerson(String firstName, String lastName, String dateBorn, String password, String role) throws SQLException {
        String[] columns = {"first_name", "last_name", "date_born", "password", "role"};
        Object[] values = {firstName, lastName, dateBorn, password, role};
        return insert("person", columns, values, true); // expect generated ID
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

    public int insertAirplane(Airline airline, String name, int flightNumber) throws SQLException {
        String[] columns = {"airline_name", "name", "flight_number"};
        Object[] values = {airline.getName(), name, flightNumber};
        return insert("airplane", columns, values, true);
    }

    public int insertAddress(String postalCode, int number, String street,
                             String city, String state, String country) throws SQLException {
        String[] columns = {"postal_code", "number", "street", "city", "state", "country"};
        Object[] values = {postalCode, number, street, city, state, country};
        return insert("address", columns, values, true);
    }

    public int insertFlight(int airplaneId, int routeId, Date departureDate,
                            Date arrivalDate, int availableSeats, String flightLength, float price) throws SQLException {
        java.sql.Date sqlDeparture = java.sql.Date.valueOf(departureDate.toSQLDate());
        java.sql.Date sqlArrival   = java.sql.Date.valueOf(arrivalDate.toSQLDate());
        String[] columns = {"airplane_id", "route_id", "departure_date", "arrival_date",
                "available_seats", "flight_length", "price"};
        Object[] values = {airplaneId, routeId, sqlDeparture, sqlArrival, availableSeats, flightLength, price};
        return insert("flight", columns, values, true);
    }

    public int insertBooking(int airplaneId, int originId, int destinationId) throws SQLException {
        String[] columns = {"airplane_id", "origin_id", "destination_number"};
        Object[] values = {airplaneId, originId, destinationId};
        return insert("booking", columns, values, true);
    }

    public int insertRoute(int origin_id, int destination_id) throws SQLException {
        String[] columns = {"origin_id", "destination_id"};
        Object[] values = {origin_id, destination_id};
        return insert("route", columns, values, true);
    }




    // SELECT METHODS THAT RETURN OBJECTS

    public ArrayList<Address> getAllAddresses() throws SQLException {
        ArrayList<Address> addresses = new ArrayList<>();
        String query = "SELECT * FROM address";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Address address = new Address(
                    rs.getString("postal_code"),
                    rs.getInt("number"),
                    rs.getString("street"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getString("country")
                );
                address.setAddressID(rs.getInt("address_id"));
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
                    airline,
                    rs.getString("name"),
                    rs.getInt("flight_number")
                );
                airplane.setAirplaneID(rs.getInt("airplane_id"));
                airplanes.add(airplane);
            }
        }
        return airplanes;
    }

    public ArrayList<Route> getAllRoutes() throws SQLException {
        ArrayList<Route> routes = new ArrayList<>();
        String query = "SELECT r.*, o.*, d.* " +
            "FROM route r " +
            "JOIN address o ON r.origin_id = o.address_id " +
            "JOIN address d ON r.destination_id = d.address_id";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Create origin address
                Address origin = new Address(
                    rs.getString("o.postal_code"),
                    rs.getInt("o.number"),
                    rs.getString("o.street"),
                    rs.getString("o.city"),
                    rs.getString("o.state"),
                    rs.getString("o.country")
                );
                origin.setAddressID(rs.getInt("origin_id"));

                // Create destination address
                Address destination = new Address(
                    rs.getString("d.postal_code"),
                    rs.getInt("d.number"),
                    rs.getString("d.street"),
                    rs.getString("d.city"),
                    rs.getString("d.state"),
                    rs.getString("d.country")
                );
                destination.setAddressID(rs.getInt("destination_id"));

                // Create route
                Route route = new Route(origin, destination);
                route.setRouteID(rs.getInt("route_id"));
                routes.add(route);
            }
        }
        return routes;
    }

    public ArrayList<Flight> getAllFlights() throws SQLException {
        ArrayList<Flight> flights = new ArrayList<>();
        String query = "SELECT f.*, ap.*, r.*, o.*, d.*, al.airline_name " +
            "FROM flight f " +
            "JOIN airplane ap ON f.airplane_id = ap.airplane_id " +
            "JOIN airline al ON ap.airline_name = al.airline_name " +
            "JOIN route r ON f.route_id = r.route_id " +
            "JOIN address o ON r.origin_id = o.address_id " +
            "JOIN address d ON r.destination_id = d.address_id";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Create airline
                Airline airline = new Airline(rs.getString("airline_name"));

                // Create airplane
                Airplane airplane = new Airplane(
                    airline,
                    rs.getString("ap.name"),
                    rs.getInt("ap.flight_number")
                );
                airplane.setAirplaneID(rs.getInt("airplane_id"));

                // Create origin address
                Address origin = new Address(
                    rs.getString("o.postal_code"),
                    rs.getInt("o.number"),
                    rs.getString("o.street"),
                    rs.getString("o.city"),
                    rs.getString("o.state"),
                    rs.getString("o.country")
                );
                origin.setAddressID(rs.getInt("origin_id"));

                // Create destination address
                Address destination = new Address(
                    rs.getString("d.postal_code"),
                    rs.getInt("d.number"),
                    rs.getString("d.street"),
                    rs.getString("d.city"),
                    rs.getString("d.state"),
                    rs.getString("d.country")
                );
                destination.setAddressID(rs.getInt("destination_id"));

                // Create route
                Route route = new Route(origin, destination);
                route.setRouteID(rs.getInt("route_id"));

                // Create dates
                java.sql.Date depDate = rs.getDate("departure_date");
                java.sql.Date arrDate = rs.getDate("arrival_date");
                Date departureDate = Date.StringToDate(depDate.toString());
                Date arrivalDate = Date.StringToDate(arrDate.toString());

                // Create flight
                Flight flight = new Flight(
                    airplane,
                    route,
                    departureDate,
                    arrivalDate,
                    rs.getInt("available_seats"),
                    rs.getString("flight_length"),
                    rs.getFloat("price")
                );
                flight.setFlightID(rs.getInt("flight_id"));
                flights.add(flight);
            }
        }
        return flights;
    }

    public ArrayList<Customer> getAllCustomers() throws SQLException {
        ArrayList<Customer> customers = new ArrayList<>();
        String query = "SELECT p.*, c.email FROM person p JOIN customer c ON p.person_id = c.customer_id WHERE p.role = 'Customer'";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Create date
                java.sql.Date dob = rs.getDate("date_born");
                Date dateOfBirth = dob != null ? Date.StringToDate(dob.toString()) : new Date();

                // Create customer (using simpler constructor)
                Customer customer = new Customer(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    dateOfBirth, true
                );
                customer.setId(rs.getInt("person_id"));
                customer.setEmail(rs.getString("email"));
                customers.add(customer);
            }
        }
        return customers;
    }

    public ArrayList<FlightAgent> getAllAgents() throws SQLException {
        ArrayList<FlightAgent> agents = new ArrayList<>();
        String query = "SELECT p.* FROM person p JOIN agent a ON p.person_id = a.agent_id WHERE p.role = 'FlightAgent'";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Create date
                java.sql.Date dob = rs.getDate("date_born");
                Date dateOfBirth = dob != null ? Date.StringToDate(dob.toString()) : new Date();

                // Create agent with empty client list initially
                FlightAgent agent = new FlightAgent(
                    rs.getInt("person_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    dateOfBirth,
                    new ArrayList<>(),
                    true
                );
                agents.add(agent);
            }
        }
        return agents;
    }

    public ArrayList<Admin> getAllAdmins() throws SQLException {
        ArrayList<Admin> admins = new ArrayList<>();
        String query = "SELECT p.* FROM person p WHERE p.role = 'Admin'";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Create date
                java.sql.Date dob = rs.getDate("date_born");
                Date dateOfBirth = dob != null ? Date.StringToDate(dob.toString()) : new Date();

                // Create admin
                Admin admin = new Admin(
                    rs.getInt("person_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    dateOfBirth,
                    true
                );
                admins.add(admin);
            }
        }
        return admins;
    }

    // GET BY ID METHODS

    public Customer getCustomerById(int customerId) throws SQLException {
        String query = "SELECT p.person_id, p.first_name, p.last_name, p.date_born, c.email " +
            "FROM person p JOIN customer c ON p.person_id = c.customer_id " +
            "WHERE p.person_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Create date
                java.sql.Date dob = rs.getDate("date_born");
                Date dateOfBirth = dob != null ? Date.StringToDate(dob.toString()) : new Date();

                // Create customer using a method that doesn't auto-insert
                Customer customer = createCustomerFromResultSet(rs, dateOfBirth);
                return customer;
            }
        }
        return null;
    }

    private Customer createCustomerFromResultSet(ResultSet rs, Date dateOfBirth) throws SQLException {


        Customer customer = new Customer(
            rs.getString("first_name"),
            rs.getString("last_name"),
            dateOfBirth, true
        );

        // Use reflection to set the ID without triggering persistence
        try {
            // Set the ID field directly
            java.lang.reflect.Field idField = Person.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(customer, rs.getInt("person_id"));

            // Set the email
            customer.setEmail(rs.getString("email"));

        } catch (Exception e) {
            throw new SQLException("Error creating customer from result set", e);
        }

        return customer;
    }

    public Flight getFlightById(int flightId) throws SQLException {
        String query = "SELECT f.*, ap.*, r.*, o.*, d.*, al.airline_name " +
            "FROM flight f " +
            "JOIN airplane ap ON f.airplane_id = ap.airplane_id " +
            "JOIN airline al ON ap.airline_name = al.airline_name " +
            "JOIN route r ON f.route_id = r.route_id " +
            "JOIN address o ON r.origin_id = o.address_id " +
            "JOIN address d ON r.destination_id = d.address_id " +
            "WHERE f.flight_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, flightId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Create airline
                Airline airline = new Airline(rs.getString("airline_name"));

                // Create airplane
                Airplane airplane = new Airplane(
                    airline,
                    rs.getString("ap.name"),
                    rs.getInt("ap.flight_number")
                );
                airplane.setAirplaneID(rs.getInt("airplane_id"));

                // Create origin address
                Address origin = new Address(
                    rs.getString("o.postal_code"),
                    rs.getInt("o.number"),
                    rs.getString("o.street"),
                    rs.getString("o.city"),
                    rs.getString("o.state"),
                    rs.getString("o.country")
                );
                origin.setAddressID(rs.getInt("origin_id"));

                // Create destination address
                Address destination = new Address(
                    rs.getString("d.postal_code"),
                    rs.getInt("d.number"),
                    rs.getString("d.street"),
                    rs.getString("d.city"),
                    rs.getString("d.state"),
                    rs.getString("d.country")
                );
                destination.setAddressID(rs.getInt("destination_id"));

                // Create route
                Route route = new Route(origin, destination);
                route.setRouteID(rs.getInt("route_id"));

                // Create dates
                java.sql.Date depDate = rs.getDate("departure_date");
                java.sql.Date arrDate = rs.getDate("arrival_date");
                Date departureDate = depDate != null ? Date.StringToDate(depDate.toString()) : new Date();
                Date arrivalDate = arrDate != null ? Date.StringToDate(arrDate.toString()) : new Date();

                // Create flight
                Flight flight = new Flight(
                    airplane,
                    route,
                    departureDate,
                    arrivalDate,
                    rs.getInt("available_seats"),
                    rs.getString("flight_length"),
                    rs.getFloat("price")
                );
                flight.setFlightID(rs.getInt("flight_id"));
                return flight;
            }
        }
        return null;
    }

    public ArrayList<Flight> getFlightsByRoute(int routeId) throws SQLException {
        ArrayList<Flight> flights = new ArrayList<>();
        String query = "SELECT flight_id FROM flight WHERE route_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, routeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Flight flight = getFlightById(rs.getInt("flight_id"));
                if (flight != null) {
                    flights.add(flight);
                }
            }
        }
        return flights;
    }

    public ArrayList<Flight> searchFlightsByCriteria(String originCity, String destinationCity, Date departureDate) throws SQLException {
        ArrayList<Flight> flights = new ArrayList<>();
        String query = "SELECT f.flight_id FROM flight f " +
            "JOIN route r ON f.route_id = r.route_id " +
            "JOIN address o ON r.origin_id = o.address_id " +
            "JOIN address d ON r.destination_id = d.address_id " +
            "WHERE o.city LIKE ? AND d.city LIKE ? AND f.departure_date = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + originCity + "%");
            pstmt.setString(2, "%" + destinationCity + "%");
            pstmt.setDate(3, java.sql.Date.valueOf(departureDate.toSQLDate()));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Flight flight = getFlightById(rs.getInt("flight_id"));
                if (flight != null) {
                    flights.add(flight);
                }
            }
        }
        return flights;
    }

    // Get bookings with customer and flight information
    public ArrayList<Booking> getAllBookings() throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.*, p.first_name, p.last_name, f.* " +
            "FROM booking b " +
            "JOIN person p ON b.customer_id = p.person_id " +
            "JOIN flight f ON b.flight_id = f.flight_id";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Get customer (simplified - you might want to use getCustomerById)
                Customer customer = getCustomerById(rs.getInt("customer_id"));

                // Get flight
                Flight flight = getFlightById(rs.getInt("flight_id"));

                // Create booking
                Booking booking = new Booking(
                    rs.getInt("booking_id"),
                    customer,
                    flight,
                    String.valueOf(rs.getInt("seat_number"))
                );
                bookings.add(booking);
            }
        }
        return bookings;
    }






















    // UPDATE METHODS


    public int updatePerson(Person person) throws SQLException {
        String[] columns = {"first_name", "last_name", "date_born"};
        String sqlDate = person.getDoB().toSQLDate();
        Object[] values = {person.getFirstName(), person.getLastName(), sqlDate};
        String whereClause = "person_id = ?";
        Object[] whereValues = {person.getId()};
        return update("person", columns, values, whereClause, whereValues);
    }

    public int updateFlight(int flightId, int airplaneId, int routeId, Date departureDate,
        Date arrivalDate, int availableSeats, String flightLength, float price) throws SQLException{
            String[] columns = {"airplane_id", "route_id", "departure_date", "arrival_date",
                "available_seats", "flight_length", "price"};
            java.sql.Date sqlDeparture = java.sql.Date.valueOf(departureDate.toSQLDate());
            java.sql.Date sqlArrival   = java.sql.Date.valueOf(arrivalDate.toSQLDate());
            Object[] values = {airplaneId, routeId, sqlDeparture, sqlArrival, availableSeats, flightLength, price};
            String whereClause = "flight_id = ?";
            Object[] whereValues = {flightId};
            return update("flight", columns, values, whereClause, whereValues);
        }


    public int update(String tableName, String[] columns, Object[] values, String whereClause, Object[] whereValues) throws SQLException {
        if (columns.length != values.length) {
            throw new IllegalArgumentException("Columns and values arrays must have the same length");
        }

        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");

        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]).append(" = ?");
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }

        if (whereClause != null && !whereClause.trim().isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            // Set the SET clause values
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(paramIndex++, values[i]);
            }

            // Set the WHERE clause values
            if (whereValues != null) {
                for (int i = 0; i < whereValues.length; i++) {
                    pstmt.setObject(paramIndex++, whereValues[i]);
                }
            }

            return pstmt.executeUpdate();
        }
    }
















    // DELETE
    public int delete(String tableName, String whereClause, Object[] whereValues) throws SQLException {
    if (whereClause == null || whereClause.trim().isEmpty()) {
        throw new IllegalArgumentException("WHERE clause cannot be null or empty for DELETE.");
    }

    String sql = "DELETE FROM " + tableName + " WHERE " + whereClause;

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

        // Bind WHERE values
        if (whereValues != null) {
            for (int i = 0; i < whereValues.length; i++) {
                pstmt.setObject(i + 1, whereValues[i]);
            }
        }

        return pstmt.executeUpdate();
        }
    }

    public int deleteFlight(int flight_id) throws SQLException{
        return delete("flight", "flight_id = ?", new Object[]{flight_id});
    }






















}
