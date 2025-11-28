package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
            System.out.println("Error connecting to the database:");
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
            e.printStackTrace();
        }
    }
}
