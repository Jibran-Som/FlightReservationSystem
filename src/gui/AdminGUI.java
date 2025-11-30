package gui;
import backend.*;
import model.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;



public class AdminGUI extends JFrame {
    private String currentUser;
    private JTabbedPane tabbedPane;

    // Flight Management Components
    private DefaultTableModel flightTableModel;
    private JTable flightTable;
    private JButton addFlightButton;
    private JButton editFlightButton;
    private JButton removeFlightButton;
    private JButton refreshFlightsButton;

    // User Management Components
    private DefaultTableModel userTableModel;
    private JTable userTable;
    private JButton addUserButton;
    private JButton editUserButton;
    private JButton removeUserButton;

    // System Management Components
    private JTextArea systemLogArea;
    private JButton generateReportButton;
    private JButton viewPromotionsButton;

    // Database Manager For Connectivity
    private DatabaseManager db = DatabaseManager.getInstance();

    public AdminGUI(String username) {
        this.currentUser = username;
        db.connect("admin_user", "admin_password");
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Flight Reservation System - Administrator Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, System Administrator: " + currentUser);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new LogoutButtonListener());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane for different functionalities
        tabbedPane = new JTabbedPane();

        // Flight Management Tab
        tabbedPane.addTab("Flight Management", createFlightManagementPanel());

        // User Management Tab
        tabbedPane.addTab("User Management", createUserManagementPanel());

        // System Management Tab
        tabbedPane.addTab("System Management", createSystemManagementPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createFlightManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addFlightButton = new JButton("Add New Flight");
        editFlightButton = new JButton("Edit Flight");
        removeFlightButton = new JButton("Remove Flight");
        refreshFlightsButton = new JButton("Refresh");

        addFlightButton.addActionListener(new AddFlightListener());
        editFlightButton.addActionListener(new EditFlightListener());
        removeFlightButton.addActionListener(new RemoveFlightListener());
        refreshFlightsButton.addActionListener(new RefreshFlightsListener());

        buttonPanel.add(addFlightButton);
        buttonPanel.add(editFlightButton);
        buttonPanel.add(removeFlightButton);
        buttonPanel.add(refreshFlightsButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Flight table
        String[] columnNames = {"Flight ID", "Airplane ID", "Route ID", "Departure Date", "Arrival Date", "Available Seats", "Length (HH:MM)", "Price"};
        Object[][] data = {}; // default in case of failure

        try {
            ArrayList<Flight> flights = db.getAllFlights();

            data = new Object[flights.size()][columnNames.length];

            for (int i = 0; i < flights.size(); i++) {
                Flight f = flights.get(i);

                data[i] = new Object[]{
                    f.getFlightID(),
                    f.getAirplane().getAirplaneID(),
                    f.getRoute().getRouteID(),
                    f.getDepartureDate().toSQLDate(),
                    f.getArrivalDate().toSQLDate(),
                    f.getAvailableSeats(),
                    f.getFlightTime(),
                    f.getPrice(),
                };
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // optionally: show dialog, log error, etc.
            System.err.println("Error loading flights from database.");
        }
        flightTableModel = new DefaultTableModel(data, columnNames);
        flightTable = new JTable(flightTableModel);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addUserButton = new JButton("Add User");
        editUserButton = new JButton("Edit User");
        removeUserButton = new JButton("Remove User");

        addUserButton.addActionListener(new AddUserListener());
        editUserButton.addActionListener(new EditUserListener());
        removeUserButton.addActionListener(new RemoveUserListener());

        buttonPanel.add(addUserButton);
        buttonPanel.add(editUserButton);
        buttonPanel.add(removeUserButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // User table
        String[] columnNames = {"Username", "User ID", "First Name", "Last Name", "Date of Birth", "Password", "Role", "Email"};
        Object[][] data = {}; // Empty for now
        try {
            ArrayList<Flight> flights = db.getAllFlights();

            data = new Object[flights.size()][columnNames.length];

            for (int i = 0; i < flights.size(); i++) {
                Flight f = flights.get(i);

                data[i] = new Object[]{
                    f.getFlightID(),
                    f.getAirplane().getAirplaneID(),
                    f.getRoute().getRouteID(),
                    f.getDepartureDate().toSQLDate(),
                    f.getArrivalDate().toSQLDate(),
                    f.getAvailableSeats(),
                    f.getFlightTime(),
                    f.getPrice(),
                };
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // optionally: show dialog, log error, etc.
            System.err.println("Error loading flights from database.");
        }
        userTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSystemManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        generateReportButton = new JButton("Generate System Report");
        viewPromotionsButton = new JButton("Manage Promotions");

        generateReportButton.addActionListener(new GenerateReportListener());
        viewPromotionsButton.addActionListener(new ViewPromotionsListener());

        buttonPanel.add(generateReportButton);
        buttonPanel.add(viewPromotionsButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // System log area
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("System Log"));

        systemLogArea = new JTextArea(15, 50);
        systemLogArea.setEditable(false);
        systemLogArea.setText("System initialized...\n");
        systemLogArea.append("Welcome, Administrator " + currentUser + "\n");
        systemLogArea.append("Current system time: " + java.time.LocalDateTime.now() + "\n");

        JScrollPane scrollPane = new JScrollPane(systemLogArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        JButton clearLogButton = new JButton("Clear Log");
        clearLogButton.addActionListener(new ClearLogListener());
        logPanel.add(clearLogButton, BorderLayout.SOUTH);

        panel.add(logPanel, BorderLayout.CENTER);

        return panel;
    }

    // Action Listeners
    private class AddFlightListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AddFlightDialog dialog = new AddFlightDialog(AdminGUI.this, flightTableModel);
            dialog.setVisible(true);
        }
    }

    private class EditFlightListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Please select a flight to edit.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Pass selected row data to the dialog
            Object flightID = flightTableModel.getValueAt(selectedRow, 0);
            Object airplaneID = flightTableModel.getValueAt(selectedRow, 1);
            Object routeID = flightTableModel.getValueAt(selectedRow, 2);
            Object departureDate = flightTableModel.getValueAt(selectedRow, 3);
            Object arrivalDate = flightTableModel.getValueAt(selectedRow, 4);
            Object seatsAvailable = flightTableModel.getValueAt(selectedRow, 5);
            Object flightTime = flightTableModel.getValueAt(selectedRow, 6);
            Object price = flightTableModel.getValueAt(selectedRow, 7);

            EditFlightDialog editDialog = new EditFlightDialog(
                    AdminGUI.this,
                    flightTableModel,
                    selectedRow,
                    flightID,
                    airplaneID,
                    routeID,
                    departureDate,
                    arrivalDate,
                    seatsAvailable,
                    flightTime,
                    price
            );

            editDialog.setVisible(true);
        }
    }


    private class RemoveFlightListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            int selectedRow = flightTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Please select a flight to remove.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(AdminGUI.this,
                    "Are you sure you want to delete this flight?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            int flightID = (int) flightTableModel.getValueAt(selectedRow, 0);

            int result = db.deleteFlight(flightID); 

            if (result >= 1) {
                flightTableModel.removeRow(selectedRow);

                systemLogArea.append("Deleted flight ID " + flightID +
                        " at " + java.time.LocalDateTime.now() + "\n");

                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Flight deleted successfully.",
                        "Flight Removed",
                        JOptionPane.INFORMATION_MESSAGE);

            } else if (result == 0) {
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "No flight was deleted. It may not exist.",
                        "Delete Failed",
                        JOptionPane.WARNING_MESSAGE);

            } else {  // result == -1
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "A database error occurred while deleting the flight.",
                        "SQL Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(AdminGUI.this,
                    "An unexpected error occurred while trying to delete the flight.\n" +
                    "Check console/logs for more details.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}



    private class RefreshFlightsListener implements ActionListener {
        @Override
            public void actionPerformed(ActionEvent e) {
            flightTableModel.setRowCount(0);

            ArrayList<Flight> data;
            try {
                data = db.getAllFlights();
                for (Flight f : data) {
                    Object[] row = {
                        f.getFlightID(),
                        f.getAirplane().getAirplaneID(),
                        f.getRoute().getRouteID(),
                        f.getDepartureDate().toSQLDate(),
                        f.getArrivalDate().toSQLDate(),
                        f.getAvailableSeats(),
                        f.getFlightTime(),
                        f.getPrice(),
                    };
                    flightTableModel.addRow(row);
                }
            systemLogArea.append("Flight list refreshed at: " + java.time.LocalDateTime.now() + "\n");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class AddUserListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AddUserDialog dialog = new AddUserDialog(AdminGUI.this, userTableModel);
            dialog.setVisible(true);
        }
    }

    private class EditUserListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(AdminGUI.this,
                "Edit User functionality will be implemented later.",
                "Edit User",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class RemoveUserListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(AdminGUI.this,
                "Remove User functionality will be implemented later.",
                "Remove User",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class GenerateReportListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            systemLogArea.append("System report generated at: " + java.time.LocalDateTime.now() + "\n");
            JOptionPane.showMessageDialog(AdminGUI.this,
                "System report generated.\n" +
                "This functionality will create detailed reports when implemented.",
                "Generate Report",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class ViewPromotionsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(AdminGUI.this,
                "Manage Promotions functionality will be implemented later.\n" +
                "This will include creating and managing monthly promotion news.",
                "Manage Promotions",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class ClearLogListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            systemLogArea.setText("Log cleared at: " + java.time.LocalDateTime.now() + "\n");
        }
    }

    private class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int result = JOptionPane.showConfirmDialog(AdminGUI.this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                dispose();
                new LoginGUI().setVisible(true);
            }
        }
    }

    private class AddFlightDialog extends JDialog {
        private JTextField airplaneIDField, routeIDField, departureField, arrivalField,
                seatsField, lengthField, priceField;
        private DefaultTableModel model;

        public AddFlightDialog(JFrame parent, DefaultTableModel model) {
            super(parent, "Add New Flight", true);
            this.model = model;
            setSize(400, 450);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            formPanel.add(new JLabel("Airplane ID:"));
            airplaneIDField = new JTextField();
            formPanel.add(airplaneIDField);

            formPanel.add(new JLabel("Route ID:"));
            routeIDField = new JTextField();
            formPanel.add(routeIDField);

            formPanel.add(new JLabel("Departure (YYYY-MM-DD):"));
            departureField = new JTextField();
            formPanel.add(departureField);

            formPanel.add(new JLabel("Arrival (YYYY-MM-DD):"));
            arrivalField = new JTextField();
            formPanel.add(arrivalField);

            formPanel.add(new JLabel("Available Seats:"));
            seatsField = new JTextField();
            formPanel.add(seatsField);

            formPanel.add(new JLabel("Length (HH:MM):"));
            lengthField = new JTextField();
            formPanel.add(lengthField);

            formPanel.add(new JLabel("Price:"));
            priceField = new JTextField();
            formPanel.add(priceField);

            add(formPanel, BorderLayout.CENTER);

            JButton saveButton = new JButton("Save Flight");
            saveButton.addActionListener(e -> saveFlight());
            add(saveButton, BorderLayout.SOUTH);
        }

        private void saveFlight() {
            try {
                int airplane_id = Integer.parseInt(airplaneIDField.getText());
                int route_id = Integer.parseInt(routeIDField.getText());
                String departureDate = departureField.getText();
                String arrivalDate = arrivalField.getText();
                CustomDate departure = CustomDate.StringToDate(departureDate);
                CustomDate arrival = CustomDate.StringToDate(arrivalDate);
                int seatsAvailable = Integer.parseInt(seatsField.getText());
                String flightTime = lengthField.getText();
                float price = Float.parseFloat(priceField.getText());
                Object[] rowData = {
                        db.insertFlight(airplane_id, route_id,  departure, arrival, seatsAvailable, flightTime, price),
                        airplane_id,
                        route_id,
                        departureDate,
                        arrivalDate,
                        seatsAvailable,
                        flightTime,
                        price,
                };
                flightTableModel.addRow(rowData);

                JOptionPane.showMessageDialog(this, "Flight added successfully.");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid input. Please check the fields.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
            }
        }
    }
    private class EditFlightDialog extends JDialog {
        private JTextField airplaneIDField, routeIDField, departureField, arrivalField,
                seatsField, lengthField, priceField;
        private DefaultTableModel model;
        private int rowIndex;
        private Object flightID;

        public EditFlightDialog(JFrame parent, DefaultTableModel model, int rowIndex,
                                Object flightID, Object airplaneID, Object routeID,
                                Object departureDate, Object arrivalDate, Object seatsAvailable,
                                Object flightTime, Object price) {
            super(parent, "Edit Flight", true);
            this.model = model;
            this.rowIndex = rowIndex;
            this.flightID = flightID;

            setSize(400, 450);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            formPanel.add(new JLabel("Airplane ID:"));
            airplaneIDField = new JTextField(airplaneID.toString());
            formPanel.add(airplaneIDField);

            formPanel.add(new JLabel("Route ID:"));
            routeIDField = new JTextField(routeID.toString());
            formPanel.add(routeIDField);

            formPanel.add(new JLabel("Departure (YYYY-MM-DD):"));
            departureField = new JTextField(departureDate.toString());
            formPanel.add(departureField);

            formPanel.add(new JLabel("Arrival (YYYY-MM-DD):"));
            arrivalField = new JTextField(arrivalDate.toString());
            formPanel.add(arrivalField);

            formPanel.add(new JLabel("Available Seats:"));
            seatsField = new JTextField(seatsAvailable.toString());
            formPanel.add(seatsField);

            formPanel.add(new JLabel("Length (HH:MM):"));
            lengthField = new JTextField(flightTime.toString());
            formPanel.add(lengthField);

            formPanel.add(new JLabel("Price:"));
            priceField = new JTextField(price.toString());
            formPanel.add(priceField);

            add(formPanel, BorderLayout.CENTER);

            JButton updateButton = new JButton("Update Flight");
            updateButton.addActionListener(e -> updateFlight());
            add(updateButton, BorderLayout.SOUTH);
        }

        private void updateFlight() {
            try {
                int airplane_id = Integer.parseInt(airplaneIDField.getText());
                int route_id = Integer.parseInt(routeIDField.getText());
                String departureDate = departureField.getText();
                String arrivalDate = arrivalField.getText();
                CustomDate departure = CustomDate.StringToDate(departureDate);
                CustomDate arrival = CustomDate.StringToDate(arrivalDate);
                int seatsAvailable = Integer.parseInt(seatsField.getText());
                String flightTime = lengthField.getText();
                float price = Float.parseFloat(priceField.getText());

                // Update database
                db.updateFlight((int)flightID, airplane_id, route_id, departure, arrival, seatsAvailable, flightTime, price);
                // Update table
                model.setValueAt(airplane_id, rowIndex, 1);
                model.setValueAt(route_id, rowIndex, 2);
                model.setValueAt(departureField.getText(), rowIndex, 3);
                model.setValueAt(arrivalField.getText(), rowIndex, 4);
                model.setValueAt(seatsAvailable, rowIndex, 5);
                model.setValueAt(flightTime, rowIndex, 6);
                model.setValueAt(price, rowIndex, 7);

                JOptionPane.showMessageDialog(this, "Flight updated successfully.");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid input. Please check the fields.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private class AddUserDialog extends JDialog {
        private JTextField usernameField, fNameField, lNameField, dobField, passwordField,
                roleField, emailField;
        private DefaultTableModel model;

        public AddUserDialog(JFrame parent, DefaultTableModel model) {
            super(parent, "Add New User", true);
            this.model = model;
            setSize(400, 450);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            formPanel.add(new JLabel("Username:"));
            usernameField = new JTextField();
            formPanel.add(usernameField);

            formPanel.add(new JLabel("First Name:"));
            fNameField = new JTextField();
            formPanel.add(fNameField);

            formPanel.add(new JLabel("Last Name:"));
            lNameField = new JTextField();
            formPanel.add(lNameField);

            formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
            dobField = new JTextField();
            formPanel.add(dobField);

            formPanel.add(new JLabel("Password:"));
            passwordField = new JTextField();
            formPanel.add(passwordField);

            formPanel.add(new JLabel("Role:"));
            roleField = new JTextField();
            formPanel.add(roleField);

            formPanel.add(new JLabel("Email:"));
            emailField = new JTextField();
            formPanel.add(emailField);

            add(formPanel, BorderLayout.CENTER);

            JButton saveButton = new JButton("Save User");
            saveButton.addActionListener(e -> saveUser());
            add(saveButton, BorderLayout.SOUTH);
        }

        private void saveUser() {
            try {
                String username = usernameField.getText();
                String fname = fNameField.getText();
                String lname = lNameField.getText();
                String dob = dobField.getText();
                String password = passwordField.getText();
                String role = passwordField.getText();
                String email = emailField.getText();
                int person_id = db.insertPerson(username, fname, lname, dob, password, role);
                Object[] rowData = null;
                if(role.equalsIgnoreCase("Customer")){
                    rowData = new Object[] {
                        person_id,
                        username,
                        fname,
                        lname,
                        dob,
                        password,
                        role,
                        email,
                    };
                    db.insertCustomer(person_id, email);
                }

                else if(role.equalsIgnoreCase("Agent")){
                    rowData = new Object[] {
                        person_id,
                        username,
                        fname,
                        lname,
                        dob,
                        password,
                        role,
                        email,
                    };
                    db.insertAgent(person_id);
                }
                flightTableModel.addRow(rowData);

                JOptionPane.showMessageDialog(this, "User added successfully.");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid input. Please check the fields.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminGUI("TestAdmin").setVisible(true);
            }
        });
    }
}
