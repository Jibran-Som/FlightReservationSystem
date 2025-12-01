package gui;
import backend.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;

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

    // Route Management Components
    private DefaultTableModel routeTableModel;
    private JTable routeTable;
    private JButton addRouteButton;
    private JButton editRouteButton;
    private JButton removeRouteButton;
    private JButton refreshRoutesButton;

    // Airplane Management Components
    private DefaultTableModel airplaneTableModel;
    private JTable airplaneTable;
    private JButton addAirplaneButton;
    private JButton editAirplaneButton;
    private JButton removeAirplaneButton;
    private JButton refreshAirplanesButton;

    // Promotion Management Components
    private DefaultTableModel promotionTableModel;
    private JTable promotionTable;
    private JButton addPromotionButton;
    private JButton editPromotionButton;
    private JButton removePromotionButton;
    private JButton refreshPromotionsButton;

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
        setSize(1100, 750);
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

        // Route Management Tab
        tabbedPane.addTab("Route Management", createRouteManagementPanel());

        // Airplane Management Tab
        tabbedPane.addTab("Airplane Management", createAirplaneManagementPanel());

        // Promotion Management Tab
        tabbedPane.addTab("Promotion Management", createPromotionManagementPanel());

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
            System.err.println("Error loading flights from database.");
        }
        flightTableModel = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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
        String[] columnNames = {"User ID", "Username", "First Name", "Last Name", "Date of Birth", "Role", "Email"};
        Object[][] data = {};
        try {
            ArrayList<Object> people = new ArrayList<>();
            ArrayList<Customer> customers = db.getAllCustomers();
            ArrayList<Admin> admins = db.getAllAdmins();
            ArrayList<FlightAgent> agents = db.getAllAgents();
            people.addAll(customers);
            people.addAll(admins);
            people.addAll(agents);

            people.sort(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                int id1 = getId(o1);
                int id2 = getId(o2);
                return Integer.compare(id1, id2);
            }

            private int getId(Object obj) {
                if (obj instanceof Customer) {
                    return ((Customer) obj).getId();
                } else if (obj instanceof Admin) {
                    return ((Admin) obj).getId();
                } else if (obj instanceof FlightAgent) {
                    return ((FlightAgent) obj).getId();
                } else {
                    return 0;
                }
            }
        });
            data = new Object[people.size()][columnNames.length];

            for (int i = 0; i < people.size(); i++) {
                Object p = people.get(i);
                if(p instanceof Customer){
                    data[i] = new Object[]{
                        ((Customer) p).getId(),
                        ((Customer) p).getUsername(),
                        ((Customer) p).getFirstName(),
                        ((Customer) p).getLastName(),
                        ((Customer) p).getDoB().toSQLDate(),
                        "Customer",
                        ((Customer) p).getEmail()
                    };
                }
                else if(p instanceof FlightAgent){
                    data[i] = new Object[]{
                        ((FlightAgent) p).getId(),
                        ((FlightAgent) p).getUsername(),
                        ((FlightAgent) p).getFirstName(),
                        ((FlightAgent) p).getLastName(),
                        ((FlightAgent) p).getDoB().toSQLDate(),
                        "FlightAgent",
                        ""
                    };
                }
                else if(p instanceof Admin){
                    data[i] = new Object[]{
                        ((Admin) p).getId(),
                        ((Admin) p).getUsername(),
                        ((Admin) p).getFirstName(),
                        ((Admin) p).getLastName(),
                        ((Admin) p).getDoB().toSQLDate(),
                        "Admin",
                        ""
                    };
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error loading users from database.");
        }
        userTableModel = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRouteManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addRouteButton = new JButton("Add Route");
        editRouteButton = new JButton("Edit Route");
        removeRouteButton = new JButton("Remove Route");
        refreshRoutesButton = new JButton("Refresh");

        addRouteButton.addActionListener(new AddRouteListener());
        editRouteButton.addActionListener(new EditRouteListener());
        removeRouteButton.addActionListener(new RemoveRouteListener());
        refreshRoutesButton.addActionListener(new RefreshRoutesListener());

        buttonPanel.add(addRouteButton);
        buttonPanel.add(editRouteButton);
        buttonPanel.add(removeRouteButton);
        buttonPanel.add(refreshRoutesButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Route table
        String[] columnNames = {"Route ID", "Origin Address", "Origin City", "Origin Country", 
                               "Destination Address", "Destination City", "Destination Country"};
        Object[][] data = {};
        try {
            ArrayList<Route> routes = db.getAllRoutes();
            data = new Object[routes.size()][columnNames.length];

            for (int i = 0; i < routes.size(); i++) {
                Route r = routes.get(i);
                Address origin = r.getDepartureLocation();
                Address destination = r.getArrivalLocation();
                
                String originAddress = origin.getStreet() + " " + origin.getNumber() + ", " + origin.getPostalCode();
                String destinationAddress = destination.getStreet() + " " + destination.getNumber() + ", " + destination.getPostalCode();
                
                data[i] = new Object[]{
                    r.getRouteID(),
                    originAddress,
                    origin.getCity(),
                    origin.getCountry(),
                    destinationAddress,
                    destination.getCity(),
                    destination.getCountry()
                };
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error loading routes from database.");
        }
        
        routeTableModel = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        routeTable = new JTable(routeTableModel);
        JScrollPane scrollPane = new JScrollPane(routeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAirplaneManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addAirplaneButton = new JButton("Add Airplane");
        editAirplaneButton = new JButton("Edit Airplane");
        removeAirplaneButton = new JButton("Remove Airplane");
        refreshAirplanesButton = new JButton("Refresh");

        addAirplaneButton.addActionListener(new AddAirplaneListener());
        editAirplaneButton.addActionListener(new EditAirplaneListener());
        removeAirplaneButton.addActionListener(new RemoveAirplaneListener());
        refreshAirplanesButton.addActionListener(new RefreshAirplanesListener());

        buttonPanel.add(addAirplaneButton);
        buttonPanel.add(editAirplaneButton);
        buttonPanel.add(removeAirplaneButton);
        buttonPanel.add(refreshAirplanesButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Airplane table
        String[] columnNames = {"Airplane ID", "Airline", "Name", "Flight Number"};
        Object[][] data = {};
        try {
            ArrayList<Airplane> airplanes = db.getAllAirplanes();
            data = new Object[airplanes.size()][columnNames.length];

            for (int i = 0; i < airplanes.size(); i++) {
                Airplane a = airplanes.get(i);
                data[i] = new Object[]{
                    a.getAirplaneID(),
                    a.getAirline().getName(),
                    a.getName(),
                    a.getFlightNumber()
                };
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error loading airplanes from database.");
        }
        
        airplaneTableModel = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        airplaneTable = new JTable(airplaneTableModel);
        JScrollPane scrollPane = new JScrollPane(airplaneTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPromotionManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPromotionButton = new JButton("Add Promotion");
        editPromotionButton = new JButton("Edit Promotion");
        removePromotionButton = new JButton("Remove Promotion");
        refreshPromotionsButton = new JButton("Refresh");

        addPromotionButton.addActionListener(new AddPromotionListener());
        editPromotionButton.addActionListener(new EditPromotionListener());
        removePromotionButton.addActionListener(new RemovePromotionListener());
        refreshPromotionsButton.addActionListener(new RefreshPromotionsListener());

        buttonPanel.add(addPromotionButton);
        buttonPanel.add(editPromotionButton);
        buttonPanel.add(removePromotionButton);
        buttonPanel.add(refreshPromotionsButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Promotion table
        String[] columnNames = {"Promo Code", "Discount Rate", "Description", "Start Date"};
        Object[][] data = {};
        try {
            ArrayList<Promotion> promotions = db.getAllPromotions();
            data = new Object[promotions.size()][columnNames.length];

            for (int i = 0; i < promotions.size(); i++) {
                Promotion p = promotions.get(i);
                data[i] = new Object[]{
                    p.getPromoCode(),
                    p.getDiscountRate() * 100 + "%",
                    p.getDescription(),
                    p.getStartDate().toSQLDate()
                };
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error loading promotions from database.");
        }
        
        promotionTableModel = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        promotionTable = new JTable(promotionTableModel);
        JScrollPane scrollPane = new JScrollPane(promotionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

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
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Flight deleted successfully.",
                        "Flight Removed",
                        JOptionPane.INFORMATION_MESSAGE);

            } else if (result == 0) {
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "No flight was deleted. It may not exist.",
                        "Delete Failed",
                        JOptionPane.WARNING_MESSAGE);

            } else {
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
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Please select a user to edit.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object personID = userTableModel.getValueAt(selectedRow, 0);
            Object username = userTableModel.getValueAt(selectedRow, 1);
            Object fname = userTableModel.getValueAt(selectedRow, 2);
            Object lname = userTableModel.getValueAt(selectedRow, 3);
            Object dob = userTableModel.getValueAt(selectedRow, 4);
            Object role = userTableModel.getValueAt(selectedRow, 5);
            Object email = userTableModel.getValueAt(selectedRow, 6);

            EditUserDialog editDialog = null;
            try {
                editDialog = new EditUserDialog(
                        AdminGUI.this,
                        userTableModel,
                        selectedRow,
                        personID,
                        username,
                        fname,
                        lname,
                        dob,
                        role,
                        email
                );
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            editDialog.setVisible(true);
        }
    }

    private class RemoveUserListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        try {
            int selectedRow = userTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Please select a user to remove.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(AdminGUI.this,
                    "Are you sure you want to delete this user?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            int personID = (int) userTableModel.getValueAt(selectedRow, 0);
            String role = (String) userTableModel.getValueAt(selectedRow, 5);
            int result = 0;
            if(role.equalsIgnoreCase("Customer")){
                result = db.deleteCustomer(personID);
            }
            else if(role.equalsIgnoreCase("FlightAgent")){
                result = db.deleteAgent(personID);
            }
            else if(role.equalsIgnoreCase("Admin")){
                result = db.deletePerson(personID);
            }

            if (result >= 1) {
                userTableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "User deleted successfully.",
                        "Flight Removed",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (result == 0) {
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "No user was deleted. They may not exist.",
                        "Delete Failed",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "A database error occurred while deleting the user.",
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

    // Route Action Listeners
    private class AddRouteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AddRouteDialog dialog = new AddRouteDialog(AdminGUI.this, routeTableModel);
            dialog.setVisible(true);
        }
    }

    private class EditRouteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = routeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Please select a route to edit.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Object routeID = routeTableModel.getValueAt(selectedRow, 0);
            // EditRouteDialog editDialog = new EditRouteDialog(
            //         AdminGUI.this,
            //         routeTableModel,
            //         selectedRow,
            //         routeID
            // );

            // editDialog.setVisible(true);
        }
    }

    private class RemoveRouteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int selectedRow = routeTable.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(AdminGUI.this,
                            "Please select a route to remove.",
                            "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int routeID = (int) routeTableModel.getValueAt(selectedRow, 0);
                String origin = (String) routeTableModel.getValueAt(selectedRow, 2);
                String destination = (String) routeTableModel.getValueAt(selectedRow, 5);

                int confirm = JOptionPane.showConfirmDialog(AdminGUI.this,
                        "Are you sure you want to delete this route?\n\n" +
                        "Route ID: " + routeID + "\n" +
                        "From: " + origin + "\n" +
                        "To: " + destination,
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION) return;

                int result = db.deleteRoute(routeID);

                if (result >= 1) {
                    routeTableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(AdminGUI.this,
                            "Route deleted successfully.",
                            "Route Removed",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (result == 0) {
                    JOptionPane.showMessageDialog(AdminGUI.this,
                            "No route was deleted. It may not exist.",
                            "Delete Failed",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(AdminGUI.this,
                            "Cannot delete route: it is being used by one or more flights.",
                            "Delete Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Error deleting route: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class RefreshRoutesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            routeTableModel.setRowCount(12);
            ArrayList<Route> routes;
            try {
                routes = db.getAllRoutes();
                for (Route r : routes) {
                    Address origin = r.getDepartureLocation();
                    Address destination = r.getArrivalLocation();
                    
                    String originAddress = origin.getStreet() + " " + origin.getNumber() + ", " + origin.getPostalCode();
                    String destinationAddress = destination.getStreet() + " " + destination.getNumber() + ", " + destination.getPostalCode();
                    
                    Object[] row = {
                        r.getRouteID(),
                        originAddress,
                        origin.getCity(),
                        origin.getCountry(),
                        destinationAddress,
                        destination.getCity(),
                        destination.getCountry()
                    };
                    routeTableModel.addRow(row);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Error loading routes: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Airplane Action Listeners
    private class AddAirplaneListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AddAirplaneDialog dialog = new AddAirplaneDialog(AdminGUI.this, airplaneTableModel);
            dialog.setVisible(true);
        }
    }


    private class EditAirplaneListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = airplaneTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Please select an airplane to edit.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object airplaneID = airplaneTableModel.getValueAt(selectedRow, 0);
            Object airline = airplaneTableModel.getValueAt(selectedRow, 1);
            Object name = airplaneTableModel.getValueAt(selectedRow, 2);
            Object flightNumber = airplaneTableModel.getValueAt(selectedRow, 3);

            EditAirplaneDialog editDialog = new EditAirplaneDialog(
                    AdminGUI.this,
                    airplaneTableModel,
                    selectedRow,
                    airplaneID,
                    airline,
                    name,
                    flightNumber
            );

            editDialog.setVisible(true);
        }
    }

    private class RemoveAirplaneListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int selectedRow = airplaneTable.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(AdminGUI.this,
                            "Please select an airplane to remove.",
                            "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int airplaneID = (int) airplaneTableModel.getValueAt(selectedRow, 0);
                String airline = (String) airplaneTableModel.getValueAt(selectedRow, 1);
                String name = (String) airplaneTableModel.getValueAt(selectedRow, 2);

                int confirm = JOptionPane.showConfirmDialog(AdminGUI.this,
                        "Are you sure you want to delete this airplane?\n\n" +
                        "Airplane ID: " + airplaneID + "\n" +
                        "Airline: " + airline + "\n" +
                        "Name: " + name + "\n\n" +
                        "WARNING: This will also delete all flights using this airplane!",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION) return;

                db.deleteAirplane(airplaneID);
                airplaneTableModel.removeRow(selectedRow);

                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Airplane deleted successfully.",
                        "Airplane Removed",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Error deleting airplane: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class RefreshAirplanesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            airplaneTableModel.setRowCount(0);
            ArrayList<Airplane> airplanes;
            try {
                airplanes = db.getAllAirplanes();
                for (Airplane a : airplanes) {
                    Object[] row = {
                        a.getAirplaneID(),
                        a.getAirline().getName(),
                        a.getName(),
                        a.getFlightNumber()
                    };
                    airplaneTableModel.addRow(row);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Error loading airplanes: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Promotion Action Listeners
    private class AddPromotionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AddPromotionDialog dialog = new AddPromotionDialog(AdminGUI.this, promotionTableModel);
            dialog.setVisible(true);
        }
    }

    private class EditPromotionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = promotionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Please select a promotion to edit.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object promoCode = promotionTableModel.getValueAt(selectedRow, 0);
            Object discountRateStr = promotionTableModel.getValueAt(selectedRow, 1);
            Object description = promotionTableModel.getValueAt(selectedRow, 2);
            Object startDate = promotionTableModel.getValueAt(selectedRow, 3);

            double discountRate = 0;
            if (discountRateStr != null) {
                String rateStr = discountRateStr.toString().replace("%", "");
                try {
                    discountRate = Double.parseDouble(rateStr) / 100;
                } catch (NumberFormatException ex) {
                    discountRate = 0;
                }
            }

            EditPromotionDialog editDialog = new EditPromotionDialog(
                    AdminGUI.this,
                    promotionTableModel,
                    selectedRow,
                    promoCode,
                    discountRate,
                    description,
                    startDate
            );

            editDialog.setVisible(true);
        }
    }

    private class RemovePromotionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int selectedRow = promotionTable.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(AdminGUI.this,
                            "Please select a promotion to remove.",
                            "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String promoCode = (String) promotionTableModel.getValueAt(selectedRow, 0);
                String discountRate = (String) promotionTableModel.getValueAt(selectedRow, 1);
                String description = (String) promotionTableModel.getValueAt(selectedRow, 2);

                int confirm = JOptionPane.showConfirmDialog(AdminGUI.this,
                        "Are you sure you want to delete this promotion?\n\n" +
                        "Code: " + promoCode + "\n" +
                        "Discount: " + discountRate + "\n" +
                        "Description: " + description,
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION) return;

                int result = db.deletePromotion(promoCode);

                if (result >= 1) {
                    promotionTableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(AdminGUI.this,
                            "Promotion deleted successfully.",
                            "Promotion Removed",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (result == 0) {
                    JOptionPane.showMessageDialog(AdminGUI.this,
                            "No promotion was deleted. It may not exist.",
                            "Delete Failed",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(AdminGUI.this,
                            "A database error occurred while deleting the promotion.",
                            "SQL Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "An unexpected error occurred while trying to delete the promotion.\n" +
                        "Check console/logs for more details.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class RefreshPromotionsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            promotionTableModel.setRowCount(0);
            ArrayList<Promotion> promotions;
            try {
                promotions = db.getAllPromotions();
                for (Promotion p : promotions) {
                    Object[] row = {
                        p.getPromoCode(),
                        p.getDiscountRate() * 100 + "%",
                        p.getDescription(),
                        p.getStartDate().toSQLDate()
                    };
                    promotionTableModel.addRow(row);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(AdminGUI.this,
                        "Error loading promotions: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
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

    // Dialog Classes
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

                db.updateFlight((int)flightID, airplane_id, route_id, departure, arrival, seatsAvailable, flightTime, price);
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
        private JTextField usernameField, fNameField, lNameField, dobField,
            emailField;
        private JPasswordField passwordField;
        private DefaultTableModel model;
        private String roleField;

        public AddUserDialog(JFrame parent, DefaultTableModel model) {
            super(parent, "Add New User", true);
            this.model = model;
            setSize(400, 500);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
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
            passwordField = new JPasswordField();
            formPanel.add(passwordField);

            formPanel.add(new JLabel("Role:"));
            String[] roles = {"Admin", "FlightAgent", "Customer"};
            JComboBox<String> roleDropdown = new JComboBox<>(roles);
            roleDropdown.addActionListener(evt -> roleField = (String) roleDropdown.getSelectedItem());
            formPanel.add(roleDropdown);
            roleField = (String) roleDropdown.getSelectedItem();

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
                String role = roleField;
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || fname.isEmpty() || lname.isEmpty() ||
                    dob.isEmpty() || role.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Please fill in all required fields.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() < 6) {
                    int result = JOptionPane.showConfirmDialog(this,
                        "Password is shorter than 6 characters. Continue anyway?",
                        "Weak Password",
                        JOptionPane.YES_NO_OPTION);
                    if (result != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                int person_id = db.insertPerson(username, fname, lname, dob, password, role);
                Object[] rowData = null;

                if(role.equalsIgnoreCase("Customer")){
                    rowData = new Object[] {
                        person_id,
                        username,
                        fname,
                        lname,
                        dob,
                        role,
                        email,
                    };
                    db.insertCustomer(person_id, email);
                }
                else if(role.equalsIgnoreCase("FlightAgent") || role.equalsIgnoreCase("Agent")){
                    rowData = new Object[] {
                        person_id,
                        username,
                        fname,
                        lname,
                        dob,
                        role,
                        "",
                    };
                    db.insertAgent(person_id);
                }
                else if(role.equalsIgnoreCase("Admin")){
                    rowData = new Object[] {
                        person_id,
                        username,
                        fname,
                        lname,
                        dob,
                        role,
                        "",
                    };
                }

                userTableModel.addRow(rowData);
                JOptionPane.showMessageDialog(this, "User added successfully.");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Invalid input. Please check the fields.\nError: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private class EditUserDialog extends JDialog {
        private JTextField usernameField, fNameField, lNameField, dobField, emailField, passfield;
        private DefaultTableModel model;
        private int rowIndex;
        private Object personID;
        private String role;

        public EditUserDialog(JFrame parent, DefaultTableModel model, int rowIndex,
                              Object personID, Object username, Object fName, Object lName, Object dob, Object role, Object email) throws SQLException {
            super(parent, "Edit User", true);
            this.model = model;
            this.rowIndex = rowIndex;
            this.personID = personID;
            this.role = (String) role;

            setSize(400, 550);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(12, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            formPanel.add(new JLabel("Username:"));
            usernameField = new JTextField(username.toString());
            formPanel.add(usernameField);

            formPanel.add(new JLabel("First Name:"));
            fNameField = new JTextField(fName.toString());
            formPanel.add(fNameField);

            formPanel.add(new JLabel("Last Name:"));
            lNameField = new JTextField(lName.toString());
            formPanel.add(lNameField);

            formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
            dobField = new JTextField(dob.toString());
            formPanel.add(dobField);

            formPanel.add(new JLabel("Current Password:"));
            try {
                String currentPassword = db.getPasswordForUser((int) personID);
                passfield = new JTextField(currentPassword);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            formPanel.add(passfield);

            if(role.equals("Customer")){
                formPanel.add(new JLabel("Email:"));
                emailField = new JTextField(email.toString());
                formPanel.add(emailField);
            }

            add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dispose());

            JButton updateButton = new JButton("Update User");
            updateButton.addActionListener(e -> updateUser());

            buttonPanel.add(cancelButton);
            buttonPanel.add(updateButton);

            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void updateUser() {
            try {
                String username = usernameField.getText();
                String fName = fNameField.getText();
                String lName = lNameField.getText();
                String dob = dobField.getText();
                String email = "";
                String newPassword = passfield.getText();
                CustomDate dateOfBirth = CustomDate.StringToDate(dob);

                if (username.isEmpty() || fName.isEmpty() || lName.isEmpty() || dob.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Please fill in all required fields.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!newPassword.isEmpty()) {
                    db.updatePasswordDirectly((int)personID, newPassword);
                }

                if(this.role.equals("Customer")){
                    email = emailField.getText();
                    Customer c = new Customer((int)personID, username, fName, lName, dateOfBirth, email);
                    db.updateCustomer((int)personID, email);
                    db.updatePerson(c);
                }
                else if(this.role.equals("FlightAgent")){
                    FlightAgent fa = new FlightAgent((int)personID, username, fName, lName, dateOfBirth, null);
                    db.updatePerson(fa);
                }
                else if(this.role.equals("Admin")){
                    Admin a = new Admin((int)personID, username, fName, lName, dateOfBirth);
                    db.updatePerson(a);
                }

                if (!newPassword.isEmpty()) {
                    db.updatePasswordDirectly((int)personID, newPassword);
                }

                model.setValueAt(username, rowIndex, 1);
                model.setValueAt(fName, rowIndex, 2);
                model.setValueAt(lName, rowIndex, 3);
                model.setValueAt(dob, rowIndex, 4);
                if(this.role.equals("Customer")) {
                    model.setValueAt(email, rowIndex, 6);
                }

                JOptionPane.showMessageDialog(this, "User updated successfully.");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Invalid input. Please check the fields.\nError: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Route Dialog Classes
    // Replace the AddRouteDialog class with this improved version
    private class AddRouteDialog extends JDialog {
        private JTextField originStreetField, originNumberField, originPostalField, originCityField, originStateField, originCountryField;
        private JTextField destStreetField, destNumberField, destPostalField, destCityField, destStateField, destCountryField;
        private DefaultTableModel model;

        public AddRouteDialog(JFrame parent, DefaultTableModel model) {
            super(parent, "Add New Route", true);
            this.model = model;
            setSize(600, 550); // Slightly larger for better spacing
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout(10, 10));

            // Main panel with padding
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            // Create tabbed pane for Origin and Destination
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // Origin Tab
            JPanel originPanel = createAddressPanel("Origin");
            tabbedPane.addTab("Origin", originPanel);
            
            // Destination Tab
            JPanel destPanel = createAddressPanel("Destination");
            tabbedPane.addTab("Destination", destPanel);
            
            mainPanel.add(tabbedPane, BorderLayout.CENTER);
            
            // Button panel at the bottom
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dispose());
            
            JButton saveButton = new JButton("Save Route");
            saveButton.addActionListener(e -> saveRoute());
            
            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
            
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            add(mainPanel);
        }
        
        private JPanel createAddressPanel(String type) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Header
            JLabel headerLabel = new JLabel(type + " Address");
            headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 14f));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(0, 0, 15, 0);
            panel.add(headerLabel, gbc);
            
            gbc.gridwidth = 1;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Street
            gbc.gridy = 1;
            gbc.gridx = 0;
            panel.add(new JLabel("Street:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            if (type.equals("Origin")) {
                originStreetField = new JTextField(20);
                panel.add(originStreetField, gbc);
            } else {
                destStreetField = new JTextField(20);
                panel.add(destStreetField, gbc);
            }
            
            // Number
            gbc.gridy = 2;
            gbc.gridx = 0;
            gbc.weightx = 0;
            panel.add(new JLabel("Number:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            if (type.equals("Origin")) {
                originNumberField = new JTextField(10);
                panel.add(originNumberField, gbc);
            } else {
                destNumberField = new JTextField(10);
                panel.add(destNumberField, gbc);
            }
            
            // Postal Code
            gbc.gridy = 3;
            gbc.gridx = 0;
            gbc.weightx = 0;
            panel.add(new JLabel("Postal Code:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            if (type.equals("Origin")) {
                originPostalField = new JTextField(10);
                panel.add(originPostalField, gbc);
            } else {
                destPostalField = new JTextField(10);
                panel.add(destPostalField, gbc);
            }
            
            // City
            gbc.gridy = 4;
            gbc.gridx = 0;
            gbc.weightx = 0;
            panel.add(new JLabel("City:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            if (type.equals("Origin")) {
                originCityField = new JTextField(20);
                panel.add(originCityField, gbc);
            } else {
                destCityField = new JTextField(20);
                panel.add(destCityField, gbc);
            }
            
            // State/Province
            gbc.gridy = 5;
            gbc.gridx = 0;
            gbc.weightx = 0;
            panel.add(new JLabel("State/Province:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            if (type.equals("Origin")) {
                originStateField = new JTextField(15);
                panel.add(originStateField, gbc);
            } else {
                destStateField = new JTextField(15);
                panel.add(destStateField, gbc);
            }
            
            // Country
            gbc.gridy = 6;
            gbc.gridx = 0;
            gbc.weightx = 0;
            panel.add(new JLabel("Country:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            if (type.equals("Origin")) {
                originCountryField = new JTextField(15);
                panel.add(originCountryField, gbc);
            } else {
                destCountryField = new JTextField(15);
                panel.add(destCountryField, gbc);
            }
            
            // Add filler to push everything up
            gbc.gridy = 7;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.weighty = 1.0;
            panel.add(Box.createVerticalGlue(), gbc);
            
            return panel;
        }

        private void saveRoute() {
            try {
                // Create origin address
                String originPostal = originPostalField.getText().trim();
                int originNumber = Integer.parseInt(originNumberField.getText().trim());
                String originStreet = originStreetField.getText().trim();
                String originCity = originCityField.getText().trim();
                String originState = originStateField.getText().trim();
                String originCountry = originCountryField.getText().trim();

                // Create destination address
                String destPostal = destPostalField.getText().trim();
                int destNumber = Integer.parseInt(destNumberField.getText().trim());
                String destStreet = destStreetField.getText().trim();
                String destCity = destCityField.getText().trim();
                String destState = destStateField.getText().trim();
                String destCountry = destCountryField.getText().trim();

                // Validate required fields
                if (originStreet.isEmpty() || originCity.isEmpty() || originCountry.isEmpty() ||
                    destStreet.isEmpty() || destCity.isEmpty() || destCountry.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Please fill in all required fields (Street, City, Country).",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Insert addresses
                int originId = db.insertAddress(originPostal, originNumber, originStreet, originCity, originState, originCountry);
                int destId = db.insertAddress(destPostal, destNumber, destStreet, destCity, destState, destCountry);

                // Insert route
                int routeId = db.insertRoute(originId, destId);

                // Add to table
                String originAddress = originStreet + " " + originNumber + ", " + originPostal;
                String destAddress = destStreet + " " + destNumber + ", " + destPostal;
                
                Object[] rowData = {
                    routeId,
                    originAddress,
                    originCity,
                    originCountry,
                    destAddress,
                    destCity,
                    destCountry
                };
                model.addRow(rowData);

                JOptionPane.showMessageDialog(this, "Route added successfully.");
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Invalid number format. Please enter valid numbers for address numbers.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error adding route: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // In the AddAirplaneDialog class:
    private class AddAirplaneDialog extends JDialog {
        private JComboBox<String> airlineComboBox; // Change from JTextField to JComboBox
        private JTextField modelField, flightNumberField;
        private DefaultTableModel model;

        public AddAirplaneDialog(JFrame parent, DefaultTableModel model) {
            super(parent, "Add New Airplane", true);
            this.model = model;
            setSize(400, 300);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            formPanel.add(new JLabel("Airline:"));

            // Create and populate the dropdown
            ArrayList<Airline> airlines = new ArrayList<>();
            try {
                airlines = db.getAllAirlines();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error loading airlines: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            // Extract airline names for the dropdown
            String[] airlineNames = new String[airlines.size()];
            for (int i = 0; i < airlines.size(); i++) {
                airlineNames[i] = airlines.get(i).getName();
            }

            airlineComboBox = new JComboBox<>(airlineNames);
            formPanel.add(airlineComboBox);

            formPanel.add(new JLabel("Model Name:"));
            modelField = new JTextField();
            formPanel.add(modelField);

            formPanel.add(new JLabel("Flight Number:"));
            flightNumberField = new JTextField();
            formPanel.add(flightNumberField);

            add(formPanel, BorderLayout.CENTER);

            JButton saveButton = new JButton("Save Airplane");
            saveButton.addActionListener(e -> saveAirplane());
            add(saveButton, BorderLayout.SOUTH);
        }

        private void saveAirplane() {
            try {
                String airlineName = (String) airlineComboBox.getSelectedItem();
                String modelName = modelField.getText();
                int flightNumber = Integer.parseInt(flightNumberField.getText());

                // Validate that an airline is selected
                if (airlineName == null || airlineName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please select an airline.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int airplaneId = db.insertAirplane(airlineName, modelName, flightNumber);
                Object[] rowData = {
                        airplaneId,
                        airlineName,
                        modelName,
                        flightNumber
                };
                airplaneTableModel.addRow(rowData);

                JOptionPane.showMessageDialog(this, "Airplane added successfully.");
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid flight number. Please enter a valid number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving airplane: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // In the EditAirplaneDialog class:
    private class EditAirplaneDialog extends JDialog {
        private JComboBox<String> airlineComboBox; // Change from JTextField to JComboBox
        private JTextField modelField, flightNumberField;
        private DefaultTableModel model;
        private int rowIndex;
        private Object airplaneID;
        private String originalAirlineName;

        public EditAirplaneDialog(JFrame parent, DefaultTableModel model, int rowIndex,
                                  Object airplaneID, Object airlineName, Object modelName, Object flightNumber) {
            super(parent, "Edit Airplane", true);
            this.model = model;
            this.rowIndex = rowIndex;
            this.airplaneID = airplaneID;
            this.originalAirlineName = airlineName.toString();

            setSize(400, 300);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            formPanel.add(new JLabel("Airline:"));

            // Create and populate the dropdown
            ArrayList<Airline> airlines = new ArrayList<>();
            try {
                airlines = db.getAllAirlines();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error loading airlines: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            // Extract airline names for the dropdown
            String[] airlineNames = new String[airlines.size()];
            for (int i = 0; i < airlines.size(); i++) {
                airlineNames[i] = airlines.get(i).getName();
            }

            airlineComboBox = new JComboBox<>(airlineNames);

            // Set the selected item to the current airline
            for (int i = 0; i < airlineNames.length; i++) {
                if (airlineNames[i].equals(originalAirlineName)) {
                    airlineComboBox.setSelectedIndex(i);
                    break;
                }
            }

            formPanel.add(airlineComboBox);

            formPanel.add(new JLabel("Model Name:"));
            modelField = new JTextField(modelName.toString());
            formPanel.add(modelField);

            formPanel.add(new JLabel("Flight Number:"));
            flightNumberField = new JTextField(flightNumber.toString());
            formPanel.add(flightNumberField);

            add(formPanel, BorderLayout.CENTER);

            JButton updateButton = new JButton("Update Airplane");
            updateButton.addActionListener(e -> updateAirplane());
            add(updateButton, BorderLayout.SOUTH);
        }

        private void updateAirplane() {
            try {
                String airlineName = (String) airlineComboBox.getSelectedItem();
                String modelName = modelField.getText();
                int flightNumber = Integer.parseInt(flightNumberField.getText());

                // Validate that an airline is selected
                if (airlineName == null || airlineName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please select an airline.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update database
                db.updateAirplane((int)airplaneID, airlineName, modelName, flightNumber);

                // Update table
                model.setValueAt(airlineName, rowIndex, 1);
                model.setValueAt(modelName, rowIndex, 2);
                model.setValueAt(flightNumber, rowIndex, 3);

                JOptionPane.showMessageDialog(this, "Airplane updated successfully.");
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid flight number. Please enter a valid number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error updating airplane: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }


    // Promotion Dialog Classes
    private class AddPromotionDialog extends JDialog {
        private JTextField promoCodeField, discountRateField, descriptionField, startDateField;
        private DefaultTableModel model;

        public AddPromotionDialog(JFrame parent, DefaultTableModel model) {
            super(parent, "Add New Promotion", true);
            this.model = model;
            setSize(400, 350);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            formPanel.add(new JLabel("Promo Code:"));
            promoCodeField = new JTextField();
            formPanel.add(promoCodeField);

            formPanel.add(new JLabel("Discount Rate (%):"));
            discountRateField = new JTextField();
            formPanel.add(discountRateField);

            formPanel.add(new JLabel("Description:"));
            descriptionField = new JTextField();
            formPanel.add(descriptionField);

            formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
            startDateField = new JTextField();
            formPanel.add(startDateField);

            add(formPanel, BorderLayout.CENTER);

            JButton saveButton = new JButton("Save Promotion");
            saveButton.addActionListener(e -> savePromotion());
            add(saveButton, BorderLayout.SOUTH);
        }

        private void savePromotion() {
            try {
                String promoCode = promoCodeField.getText().trim();
                String discountRateStr = discountRateField.getText().trim();
                String description = descriptionField.getText().trim();
                String startDateStr = startDateField.getText().trim();

                if (promoCode.isEmpty() || discountRateStr.isEmpty() || description.isEmpty() || startDateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double discountRate = Double.parseDouble(discountRateStr);
                if (discountRate <= 0 || discountRate > 100) {
                    JOptionPane.showMessageDialog(this,
                        "Discount rate must be between 0.01% and 100%.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                CustomDate startDate = CustomDate.StringToDate(startDateStr);
                db.insertPromotion(promoCode, discountRate / 100, description, startDate);

                Object[] rowData = {
                    promoCode,
                    discountRate + "%",
                    description,
                    startDateStr
                };
                model.addRow(rowData);

                JOptionPane.showMessageDialog(this, "Promotion added successfully.");
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Invalid discount rate. Please enter a valid number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error adding promotion: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private class EditPromotionDialog extends JDialog {
        private JTextField promoCodeField, discountRateField, descriptionField, startDateField;
        private DefaultTableModel model;
        private int rowIndex;
        private Object originalPromoCode;

        public EditPromotionDialog(JFrame parent, DefaultTableModel model, int rowIndex,
                                  Object promoCode, Object discountRate, Object description, Object startDate) {
            super(parent, "Edit Promotion", true);
            this.model = model;
            this.rowIndex = rowIndex;
            this.originalPromoCode = promoCode;

            setSize(400, 350);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            formPanel.add(new JLabel("Promo Code:"));
            promoCodeField = new JTextField(promoCode.toString());
            formPanel.add(promoCodeField);

            formPanel.add(new JLabel("Discount Rate (%):"));
            discountRateField = new JTextField(String.valueOf((double)discountRate * 100));
            formPanel.add(discountRateField);

            formPanel.add(new JLabel("Description:"));
            descriptionField = new JTextField(description.toString());
            formPanel.add(descriptionField);

            formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
            startDateField = new JTextField(startDate.toString());
            formPanel.add(startDateField);

            add(formPanel, BorderLayout.CENTER);

            JButton updateButton = new JButton("Update Promotion");
            updateButton.addActionListener(e -> updatePromotion());
            add(updateButton, BorderLayout.SOUTH);
        }

        private void updatePromotion() {
            try {
                String promoCode = promoCodeField.getText().trim();
                String discountRateStr = discountRateField.getText().trim();
                String description = descriptionField.getText().trim();
                String startDateStr = startDateField.getText().trim();

                if (promoCode.isEmpty() || discountRateStr.isEmpty() || description.isEmpty() || startDateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double discountRate = Double.parseDouble(discountRateStr);
                if (discountRate <= 0 || discountRate > 100) {
                    JOptionPane.showMessageDialog(this,
                        "Discount rate must be between 0.01% and 100%.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                CustomDate startDate = CustomDate.StringToDate(startDateStr);
                db.updatePromotion(originalPromoCode.toString(), (int)discountRate, description, startDate);

                model.setValueAt(promoCode, rowIndex, 0);
                model.setValueAt(discountRate + "%", rowIndex, 1);
                model.setValueAt(description, rowIndex, 2);
                model.setValueAt(startDateStr, rowIndex, 3);

                JOptionPane.showMessageDialog(this, "Promotion updated successfully.");
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Invalid discount rate. Please enter a valid number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error updating promotion: " + ex.getMessage(),
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