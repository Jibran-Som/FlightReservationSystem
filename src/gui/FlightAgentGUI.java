package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import service.FlightController;
import service.CustomerController;
import service.BookingController;
import model.*;
import backend.DatabaseManager;
import java.util.ArrayList;

public class FlightAgentGUI extends JFrame {
    private String currentUser;
    private JTabbedPane tabbedPane;
    
    // Controllers
    private FlightController flightController;
    private CustomerController customerController;
    private BookingController bookingController;
    private DatabaseManager db;

    // Flight Management Components
    private JTextField flightSearchField;
    private JComboBox<String> flightSearchType;
    private JButton searchFlightButton;
    private JButton refreshFlightsButton;
    private JTable flightTable;
    private DefaultTableModel flightTableModel;

    // Customer Management Components
    private JTextField customerSearchField;
    private JButton searchCustomerButton;
    private JButton refreshCustomersButton;
    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private JButton addCustomerButton;
    private JButton editCustomerButton;
    private JButton removeCustomerButton;

    // Booking Management Components
    private JTable bookingTable;
    private DefaultTableModel bookingTableModel;
    private JButton createBookingButton;
    private JButton cancelBookingButton;
    private JButton refreshBookingsButton;

    public FlightAgentGUI(String username) {
        this.currentUser = username;
        initializeControllers();
        initializeGUI();
        loadInitialData();
    }

    private void initializeControllers() {
        this.db = DatabaseManager.getInstance();
        this.flightController = new FlightController();
        this.customerController = new CustomerController();
        this.bookingController = new BookingController();
        
        // Connect to database (you might want to handle this differently)
        try {
            db.connect("agent_user", "agent_password");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Database connection failed: " + e.getMessage(),
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeGUI() {
        setTitle("Flight Reservation System - Flight Agent Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, Flight Agent: " + currentUser);
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

        // Customer Management Tab
        tabbedPane.addTab("Customer Management", createCustomerManagementPanel());

        // Booking Management Tab
        tabbedPane.addTab("Booking Management", createBookingManagementPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createFlightManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        flightSearchField = new JTextField(20);
        searchPanel.add(flightSearchField);
        
        flightSearchType = new JComboBox<>(new String[]{"All", "By Origin", "By Destination", "By Date"});
        searchPanel.add(flightSearchType);
        
        searchFlightButton = new JButton("Search");
        searchFlightButton.addActionListener(new SearchFlightListener());
        searchPanel.add(searchFlightButton);
        
        refreshFlightsButton = new JButton("Refresh");
        refreshFlightsButton.addActionListener(e -> refreshFlightsTable());
        searchPanel.add(refreshFlightsButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Flight table
        String[] columnNames = {"Flight ID", "Airline", "Origin", "Destination", "Departure", "Arrival", "Available Seats", "Price"};
        flightTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        flightTable = new JTable(flightTableModel);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCustomerManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search and button panel
        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Customers:"));
        customerSearchField = new JTextField(20);
        searchPanel.add(customerSearchField);
        searchCustomerButton = new JButton("Search");
        searchCustomerButton.addActionListener(new SearchCustomerListener());
        searchPanel.add(searchCustomerButton);
        
        refreshCustomersButton = new JButton("Refresh");
        refreshCustomersButton.addActionListener(e -> refreshCustomersTable());
        searchPanel.add(refreshCustomersButton);

        topPanel.add(searchPanel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addCustomerButton = new JButton("Add Customer");
        editCustomerButton = new JButton("Edit Customer");
        removeCustomerButton = new JButton("Remove Customer");

        addCustomerButton.addActionListener(new AddCustomerListener());
        editCustomerButton.addActionListener(new EditCustomerListener());
        removeCustomerButton.addActionListener(new RemoveCustomerListener());

        buttonPanel.add(addCustomerButton);
        buttonPanel.add(editCustomerButton);
        buttonPanel.add(removeCustomerButton);

        topPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Customer table
        String[] columnNames = {"Customer ID", "Username", "First Name", "Last Name", "Email", "Date of Birth"};
        customerTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(customerTableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBookingManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        createBookingButton = new JButton("Create New Booking");
        cancelBookingButton = new JButton("Cancel Booking");
        refreshBookingsButton = new JButton("Refresh Bookings");

        createBookingButton.addActionListener(new CreateBookingListener());
        cancelBookingButton.addActionListener(new CancelBookingListener());
        refreshBookingsButton.addActionListener(e -> refreshBookingsTable());

        buttonPanel.add(createBookingButton);
        buttonPanel.add(cancelBookingButton);
        buttonPanel.add(refreshBookingsButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Booking table
        String[] columnNames = {"Booking ID", "Customer", "Flight ID", "Departure", "Arrival", "Seat Number"};
        bookingTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingTable = new JTable(bookingTableModel);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadInitialData() {
        refreshFlightsTable();
        refreshCustomersTable();
        refreshBookingsTable();
    }

    private void refreshFlightsTable() {
        try {
            flightTableModel.setRowCount(0); // Clear existing data
            ArrayList<Flight> flights = flightController.getAllFlights();
            
            for (Flight flight : flights) {
                Object[] row = {
                    flight.getFlightID(),
                    flight.getAirplane() != null ? flight.getAirplane().getAirline().getName() : "N/A",
                    flight.getRoute() != null ? flight.getRoute().getDepartureLocation().getCity() : "N/A",
                    flight.getRoute() != null ? flight.getRoute().getArrivalLocation().getCity() : "N/A",
                    flight.getDepartureDate().toString(),
                    flight.getArrivalDate().toString(),
                    flight.getAvailableSeats(),
                    String.format("$%.2f", flight.getPrice())
                };
                flightTableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading flights: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshCustomersTable() {
        try {
            customerTableModel.setRowCount(0);
            ArrayList<Customer> customers = customerController.getAllCustomersAsArray();
            
            for (Customer customer : customers) {
                Object[] row = {
                    customer.getId(),
                    customer.getUsername(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail(),
                    customer.getDoB().toString()
                };
                customerTableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading customers: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshBookingsTable() {
        try {
            bookingTableModel.setRowCount(0);
            ArrayList<Booking> bookings = bookingController.getAllBookings();
            
            for (Booking booking : bookings) {
                Object[] row = {
                    booking.getBookingId(),
                    booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName(),
                    booking.getFlight().getFlightID(),
                    booking.getFlight().getDepartureDate().toString(),
                    booking.getFlight().getArrivalDate().toString(),
                    booking.getSeatNumber()
                };
                bookingTableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading bookings: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Action Listeners
    private class SearchFlightListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchTerm = flightSearchField.getText().trim();
            String searchType = (String) flightSearchType.getSelectedItem();
            
            if (searchTerm.isEmpty()) {
                refreshFlightsTable();
                return;
            }
            
            try {
                flightTableModel.setRowCount(0);
                ArrayList<Flight> allFlights = flightController.getAllFlights();
                ArrayList<Flight> filteredFlights = new ArrayList<>();
                
                for (Flight flight : allFlights) {
                    boolean matches = false;
                    switch (searchType) {
                        case "By Origin":
                            if (flight.getRoute() != null && 
                                flight.getRoute().getDepartureLocation().getCity().toLowerCase().contains(searchTerm.toLowerCase())) {
                                matches = true;
                            }
                            break;
                        case "By Destination":
                            if (flight.getRoute() != null && 
                                flight.getRoute().getArrivalLocation().getCity().toLowerCase().contains(searchTerm.toLowerCase())) {
                                matches = true;
                            }
                            break;
                        case "By Date":
                            if (flight.getDepartureDate().toString().contains(searchTerm)) {
                                matches = true;
                            }
                            break;
                        case "All":
                        default:
                            if ((flight.getRoute() != null && 
                                 (flight.getRoute().getDepartureLocation().getCity().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                  flight.getRoute().getArrivalLocation().getCity().toLowerCase().contains(searchTerm.toLowerCase()))) ||
                                flight.getDepartureDate().toString().contains(searchTerm) ||
                                String.valueOf(flight.getFlightID()).contains(searchTerm)) {
                                matches = true;
                            }
                            break;
                    }
                    
                    if (matches) {
                        filteredFlights.add(flight);
                    }
                }
                
                // Add filtered flights to table
                for (Flight flight : filteredFlights) {
                    Object[] row = {
                        flight.getFlightID(),
                        flight.getAirplane() != null ? flight.getAirplane().getAirline().getName() : "N/A",
                        flight.getRoute() != null ? flight.getRoute().getDepartureLocation().getCity() : "N/A",
                        flight.getRoute() != null ? flight.getRoute().getArrivalLocation().getCity() : "N/A",
                        flight.getDepartureDate().toString(),
                        flight.getArrivalDate().toString(),
                        flight.getAvailableSeats(),
                        String.format("$%.2f", flight.getPrice())
                    };
                    flightTableModel.addRow(row);
                }
                
                if (filteredFlights.isEmpty()) {
                    JOptionPane.showMessageDialog(FlightAgentGUI.this,
                        "No flights found matching your search criteria.",
                        "Search Results",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(FlightAgentGUI.this,
                    "Error searching flights: " + ex.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SearchCustomerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchTerm = customerSearchField.getText().trim();
            
            if (searchTerm.isEmpty()) {
                refreshCustomersTable();
                return;
            }
            
            try {
                customerTableModel.setRowCount(0);
                ArrayList<Customer> allCustomers = customerController.getAllCustomersAsArray();
                
                for (Customer customer : allCustomers) {
                    if (customer.getFirstName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        customer.getLastName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        customer.getUsername().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        customer.getEmail().toLowerCase().contains(searchTerm.toLowerCase())) {
                        
                        Object[] row = {
                            customer.getId(),
                            customer.getUsername(),
                            customer.getFirstName(),
                            customer.getLastName(),
                            customer.getEmail(),
                            customer.getDoB().toString()
                        };
                        customerTableModel.addRow(row);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(FlightAgentGUI.this,
                    "Error searching customers: " + ex.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class AddCustomerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Create a dialog for adding a new customer
            JTextField usernameField = new JTextField();
            JTextField firstNameField = new JTextField();
            JTextField lastNameField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField dobField = new JTextField(); // Could be improved with date picker

            Object[] message = {
                "Username:", usernameField,
                "First Name:", firstNameField,
                "Last Name:", lastNameField,
                "Email:", emailField,
                "Date of Birth (YYYY-MM-DD):", dobField
            };

            int option = JOptionPane.showConfirmDialog(FlightAgentGUI.this,
                message, "Add New Customer", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    // Validate input
                    if (usernameField.getText().trim().isEmpty() ||
                        firstNameField.getText().trim().isEmpty() ||
                        lastNameField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(FlightAgentGUI.this,
                            "Please fill in all required fields.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    CustomDate dob = CustomDate.StringToDate(dobField.getText().trim());
                    Customer newCustomer = customerController.createCustomer(
                        usernameField.getText().trim(),
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        dob,
                        emailField.getText().trim(),
                        "" // phone number - you can add this field if needed
                    );

                    JOptionPane.showMessageDialog(FlightAgentGUI.this,
                        "Customer added successfully! ID: " + newCustomer.getId(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                    refreshCustomersTable();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FlightAgentGUI.this,
                        "Error adding customer: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class EditCustomerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(FlightAgentGUI.this,
                    "Please select a customer to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int customerId = (int) customerTableModel.getValueAt(selectedRow, 0);
            
            try {
                Customer customer = customerController.getCustomer(customerId);
                
                // Create edit dialog
                JTextField usernameField = new JTextField(customer.getUsername());
                JTextField firstNameField = new JTextField(customer.getFirstName());
                JTextField lastNameField = new JTextField(customer.getLastName());
                JTextField emailField = new JTextField(customer.getEmail());
                JTextField dobField = new JTextField(customer.getDoB().toSQLDate());

                Object[] message = {
                    "Username:", usernameField,
                    "First Name:", firstNameField,
                    "Last Name:", lastNameField,
                    "Email:", emailField,
                    "Date of Birth (YYYY-MM-DD):", dobField
                };

                int option = JOptionPane.showConfirmDialog(FlightAgentGUI.this,
                    message, "Edit Customer", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    // Update customer
                    customer.setUsername(usernameField.getText().trim());
                    customer.setFirstName(firstNameField.getText().trim());
                    customer.setLastName(lastNameField.getText().trim());
                    customer.setEmail(emailField.getText().trim());
                    customer.setDoB(CustomDate.StringToDate(dobField.getText().trim()));
                    
                    customerController.updateCustomer(customer);
                    
                    JOptionPane.showMessageDialog(FlightAgentGUI.this,
                        "Customer updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                    refreshCustomersTable();
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(FlightAgentGUI.this,
                    "Error editing customer: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class RemoveCustomerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(FlightAgentGUI.this,
                    "Please select a customer to remove.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int customerId = (int) customerTableModel.getValueAt(selectedRow, 0);
            String customerName = customerTableModel.getValueAt(selectedRow, 2) + " " + 
                                customerTableModel.getValueAt(selectedRow, 3);

            int confirm = JOptionPane.showConfirmDialog(FlightAgentGUI.this,
                "Are you sure you want to remove customer: " + customerName + "?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    db.deleteCustomer(customerId);
                    
                    JOptionPane.showMessageDialog(FlightAgentGUI.this,
                        "Customer removed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                    refreshCustomersTable();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FlightAgentGUI.this,
                        "Error removing customer: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class CreateBookingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Create a dialog for creating a new booking
            try {
                // Get lists for selection
                ArrayList<Customer> customers = customerController.getAllCustomersAsArray();
                ArrayList<Flight> flights = flightController.getAllFlights();
                
                if (customers.isEmpty() || flights.isEmpty()) {
                    JOptionPane.showMessageDialog(FlightAgentGUI.this,
                        "No customers or flights available for booking.",
                        "Cannot Create Booking",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Create combo boxes for selection
                JComboBox<Customer> customerCombo = new JComboBox<>(customers.toArray(new Customer[0]));
                JComboBox<Flight> flightCombo = new JComboBox<>(flights.toArray(new Flight[0]));
                JTextField seatField = new JTextField();
                
                // Custom renderers for combo boxes
                customerCombo.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof Customer) {
                            Customer c = (Customer) value;
                            setText(c.getFirstName() + " " + c.getLastName() + " (" + c.getUsername() + ")");
                        }
                        return this;
                    }
                });
                
                flightCombo.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof Flight) {
                            Flight f = (Flight) value;
                            String origin = f.getRoute() != null ? f.getRoute().getDepartureLocation().getCity() : "N/A";
                            String destination = f.getRoute() != null ? f.getRoute().getArrivalLocation().getCity() : "N/A";
                            setText("Flight " + f.getFlightID() + ": " + origin + " to " + destination + " - " + f.getDepartureDate());
                        }
                        return this;
                    }
                });
                
                Object[] message = {
                    "Customer:", customerCombo,
                    "Flight:", flightCombo,
                    "Seat Number:", seatField
                };
                
                int option = JOptionPane.showConfirmDialog(FlightAgentGUI.this,
                    message, "Create New Booking", JOptionPane.OK_CANCEL_OPTION);
                
                if (option == JOptionPane.OK_OPTION) {
                    Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
                    Flight selectedFlight = (Flight) flightCombo.getSelectedItem();
                    int seatNumber = Integer.parseInt(seatField.getText().trim());
                    
                    // Check if seat is available
                    if (selectedFlight.getAvailableSeats() <= 0) {
                        JOptionPane.showMessageDialog(FlightAgentGUI.this,
                            "No available seats on this flight.",
                            "Booking Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    Booking newBooking = bookingController.createBooking(selectedCustomer, selectedFlight, seatNumber);
                    
                    JOptionPane.showMessageDialog(FlightAgentGUI.this,
                        "Booking created successfully! Booking ID: " + newBooking.getBookingId(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    refreshBookingsTable();
                    refreshFlightsTable(); // Update available seats
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(FlightAgentGUI.this,
                    "Error creating booking: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class CancelBookingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = bookingTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(FlightAgentGUI.this,
                    "Please select a booking to cancel.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int bookingId = (int) bookingTableModel.getValueAt(selectedRow, 0);
            String bookingInfo = "Booking ID: " + bookingId + 
                               ", Customer: " + bookingTableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(FlightAgentGUI.this,
                "Are you sure you want to cancel this booking?\n" + bookingInfo,
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    bookingController.cancelBooking(bookingId);
                    
                    JOptionPane.showMessageDialog(FlightAgentGUI.this,
                        "Booking cancelled successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                    refreshBookingsTable();
                    refreshFlightsTable(); // Update available seats

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FlightAgentGUI.this,
                        "Error cancelling booking: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int result = JOptionPane.showConfirmDialog(FlightAgentGUI.this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                db.disconnect();
                dispose();
                new LoginGUI().setVisible(true);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FlightAgentGUI("TestAgent").setVisible(true);
            }
        });
    }
}