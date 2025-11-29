package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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



    //SELECT Methods

    public Object[][] getAllFlights() {
        String query = "SELECT flight_id, airplane_id, route_id, departure_date, arrival_date, available_seats, flight_length, price FROM flight";
        try (PreparedStatement pst = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = pst.executeQuery()) {

            // Count rows
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();

            Object[][] data = new Object[rowCount][8];
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getInt("flight_id");
                data[i][1] = rs.getInt("airplane_id");
                data[i][2] = rs.getInt("route_id");
                data[i][3] = rs.getDate("departure_date").toString();
                data[i][4] = rs.getDate("arrival_date").toString();
                data[i][5] = rs.getInt("available_seats");
                data[i][6] = rs.getString("flight_length");
                data[i][7] = rs.getFloat("price");
                i++;
            }
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            return new Object[0][0];
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
