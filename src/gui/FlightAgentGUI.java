package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FlightAgentGUI extends JFrame {
    private String currentUser;
    private JTabbedPane tabbedPane;

    // Flight Management Components
    private JTextField flightSearchField;
    private JButton searchFlightButton;
    private JTable flightTable;

    // Customer Management Components
    private JTextField customerSearchField;
    private JButton searchCustomerButton;
    private JTable customerTable;
    private JButton addCustomerButton;
    private JButton editCustomerButton;
    private JButton removeCustomerButton;

    // Booking Management Components
    private JTable bookingTable;
    private JButton createBookingButton;
    private JButton cancelBookingButton;
    private JButton viewBookingButton;

    public FlightAgentGUI(String username) {
        this.currentUser = username;
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Flight Reservation System - Flight Agent Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
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
        searchPanel.add(new JLabel("Search Flights:"));
        flightSearchField = new JTextField(20);
        searchPanel.add(flightSearchField);
        searchFlightButton = new JButton("Search");
        searchFlightButton.addActionListener(new SearchFlightListener());
        searchPanel.add(searchFlightButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Flight table
        String[] columnNames = {"Flight ID", "Airline", "Departure", "Arrival", "Date", "Available Seats", "Price"};
        Object[][] data = {}; // Empty for now
        flightTable = new JTable(data, columnNames);
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
        String[] columnNames = {"Customer ID", "First Name", "Last Name", "Email", "Phone", "Address"};
        Object[][] data = {}; // Empty for now
        customerTable = new JTable(data, columnNames);
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
        viewBookingButton = new JButton("View Booking Details");

        createBookingButton.addActionListener(new CreateBookingListener());
        cancelBookingButton.addActionListener(new CancelBookingListener());
        viewBookingButton.addActionListener(new ViewBookingListener());

        buttonPanel.add(createBookingButton);
        buttonPanel.add(cancelBookingButton);
        buttonPanel.add(viewBookingButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Booking table
        String[] columnNames = {"Booking ID", "Customer", "Flight", "Booking Date", "Status", "Seat Number"};
        Object[][] data = {}; // Empty for now
        bookingTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Action Listeners
    private class SearchFlightListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchTerm = flightSearchField.getText().trim();
            JOptionPane.showMessageDialog(FlightAgentGUI.this,
                "Searching flights for: " + searchTerm + "\n" +
                "This functionality will be implemented later.",
                "Search Flight",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class SearchCustomerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchTerm = customerSearchField.getText().trim();
            JOptionPane.showMessageDialog(FlightAgentGUI.this,
                "Searching customers for: " + searchTerm + "\n" +
                "This functionality will be implemented later.",
                "Search Customer",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class AddCustomerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(FlightAgentGUI.this,
                "Add Customer functionality will be implemented later.",
                "Add Customer",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class EditCustomerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(FlightAgentGUI.this,
                "Edit Customer functionality will be implemented later.",
                "Edit Customer",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class RemoveCustomerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(FlightAgentGUI.this,
                "Remove Customer functionality will be implemented later.",
                "Remove Customer",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class CreateBookingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(FlightAgentGUI.this,
                "Create Booking functionality will be implemented later.",
                "Create Booking",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class CancelBookingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(FlightAgentGUI.this,
                "Cancel Booking functionality will be implemented later.",
                "Cancel Booking",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class ViewBookingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(FlightAgentGUI.this,
                "View Booking functionality will be implemented later.",
                "View Booking",
                JOptionPane.INFORMATION_MESSAGE);
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
