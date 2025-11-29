package backend;

import model.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;


public class DatabaseManager {

    private static DatabaseManager instance; // Singleton instance

    private static final String URL = "jdbc:mysql://localhost:3306/FLIGHTRESERVE";
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
        }
    }

    public Connection getConnection() {
        return connection;
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

    public int insert(String tableName, String[] columns, Object[] values) throws SQLException {
        if (columns.length != values.length) {
            throw new IllegalArgumentException("Columns and values arrays must have the same length");
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

        // INSERT INTO person (first_name, last_name, date_born, username, password, role) VALUES (?, ?, ?, ?, ?, ?)

        placeholders.append(")");
        sql.append(placeholders);

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }

            // Get the generated key
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            else {
                throw new SQLException("Insert failed, no ID obtained.");
            }

        }
    }

    public int insertPerson(String firstName, String lastName, String dateBorn,
                            String username, String password, String role) throws SQLException {
        String[] columns = {"first_name", "last_name", "date_born", "username", "password", "role"};
        Object[] values = {firstName, lastName, dateBorn, username, password, role};
        return insert("person", columns, values);
    }

    public int insertCustomer(int personId) throws SQLException {
        String[] columns = {"customer_id"};
        Object[] values = {personId};
        return insert("customer", columns, values);
    }

    public int insertAgent(int personId) throws SQLException {
        String[] columns = {"agent_id"};
        Object[] values = {personId};
        return insert("agent", columns, values);
    }

    public int insertAirline(String airlineName) throws SQLException {
        String[] columns = {"airline_name"};
        Object[] values = {airlineName};
        return insert("airline", columns, values);
    }

    public int insertAddress(String postalCode, int number, String street,
                             String city, String state, String country) throws SQLException {
        String[] columns = {"postal_code", "number", "street", "city", "state", "country"};
        Object[] values = {postalCode, number, street, city, state, country};
        return insert("address", columns, values);
    }

    public int insertFlight(int airplaneId, int routeId, String departureDate,
                            String arrivalDate, int availableSeats, int flightLength, double price) throws SQLException {
        String[] columns = {"airplane_id", "route_id", "departure_date", "arrival_date",
                "available_seats", "flight_length", "price"};
        Object[] values = {airplaneId, routeId, departureDate, arrivalDate, availableSeats, flightLength, price};
        return insert("flight", columns, values);
    }

    public int insertBooking(int customerId, int flightId, int seatNumber) throws SQLException {
        String[] columns = {"customer_id", "flight_id", "seat_number"};
        Object[] values = {customerId, flightId, seatNumber};
        return insert("booking", columns, values);
    }




    // UPDATE METHODS


    public int updatePerson(Person person) throws SQLException {
        String[] columns = {"first_name", "last_name", "date_born"};
        Object[] values = {person.getFirstName(), person.getLastName(), person.getDoB()};
        String whereClause = "person_id = ?";
        Object[] whereValues = {person.getId()};
        return update("person", columns, values, whereClause, whereValues);
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
            // Set the SET clause values
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            // Set the WHERE clause values
            if (whereValues != null) {
                for (int i = 0; i < whereValues.length; i++) {
                    pstmt.setObject(values.length + i + 1, whereValues[i]);
                }
            }

            return pstmt.executeUpdate();
        }
    }



















}
