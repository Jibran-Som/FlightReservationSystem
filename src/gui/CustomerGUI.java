package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerGUI extends JFrame {
    private String currentUser;

    // Flight Search Components
    private JTextField departureField;
    private JTextField destinationField;
    private JTextField dateField;
    private JButton searchFlightsButton;
    private JTable flightTable;
    private JButton bookFlightButton;

    // My Bookings Components
    private JTable bookingsTable;
    private JButton cancelBookingButton;
    private JButton viewBookingButton;

    // Profile Components
    private JTextField profileNameField;
    private JTextField profileEmailField;
    private JTextField profilePhoneField;
    private JButton updateProfileButton;

    public CustomerGUI(String username) {
        this.currentUser = username;
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Flight Reservation System - Customer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Create main panel with tabbed interface
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new LogoutButtonListener());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Flight Search & Booking Tab
        tabbedPane.addTab("Search & Book Flights", createFlightSearchPanel());

        // My Bookings Tab
        tabbedPane.addTab("My Bookings", createBookingsPanel());

        // My Profile Tab
        tabbedPane.addTab("My Profile", createProfilePanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createFlightSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search criteria panel
        JPanel searchPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));

        searchPanel.add(new JLabel("Departure:"));
        departureField = new JTextField();
        searchPanel.add(departureField);

        searchPanel.add(new JLabel("Destination:"));
        destinationField = new JTextField();
        searchPanel.add(destinationField);

        searchPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        searchPanel.add(dateField);

        searchPanel.add(new JLabel("")); // Empty label for spacing
        searchFlightsButton = new JButton("Search Flights");
        searchFlightsButton.addActionListener(new SearchFlightsListener());
        searchPanel.add(searchFlightsButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Available Flights"));

        String[] columnNames = {"Flight ID", "Airline", "Departure", "Arrival", "Date", "Time", "Price", "Available Seats"};
        Object[][] data = {}; // Empty for now
        flightTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        bookFlightButton = new JButton("Book Selected Flight");
        bookFlightButton.addActionListener(new BookFlightListener());
        resultsPanel.add(bookFlightButton, BorderLayout.SOUTH);

        panel.add(resultsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cancelBookingButton = new JButton("Cancel Booking");
        viewBookingButton = new JButton("View Booking Details");

        cancelBookingButton.addActionListener(new CancelBookingListener());
        viewBookingButton.addActionListener(new ViewBookingListener());

        buttonPanel.add(cancelBookingButton);
        buttonPanel.add(viewBookingButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Bookings table
        String[] columnNames = {"Booking ID", "Flight", "Departure", "Arrival", "Booking Date", "Status", "Seat Number"};
        Object[][] data = {}; // Empty for now
        bookingsTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));

        formPanel.add(new JLabel("Full Name:"));
        profileNameField = new JTextField();
        formPanel.add(profileNameField);

        formPanel.add(new JLabel("Email:"));
        profileEmailField = new JTextField();
        formPanel.add(profileEmailField);

        formPanel.add(new JLabel("Phone Number:"));
        profilePhoneField = new JTextField();
        formPanel.add(profilePhoneField);

        formPanel.add(new JLabel("")); // Empty label for spacing
        updateProfileButton = new JButton("Update Profile");
        updateProfileButton.addActionListener(new UpdateProfileListener());
        formPanel.add(updateProfileButton);

        panel.add(formPanel, BorderLayout.NORTH);

        return panel;
    }

    // Action Listeners
    private class SearchFlightsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String departure = departureField.getText().trim();
            String destination = destinationField.getText().trim();
            String date = dateField.getText().trim();

            JOptionPane.showMessageDialog(CustomerGUI.this,
                "Searching flights:\n" +
                "Departure: " + departure + "\n" +
                "Destination: " + destination + "\n" +
                "Date: " + date + "\n\n" +
                "This functionality will be implemented later.",
                "Search Flights",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class BookFlightListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(CustomerGUI.this,
                "Book Flight functionality will be implemented later.\n" +
                "This will include seat selection and payment processing.",
                "Book Flight",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class CancelBookingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(CustomerGUI.this,
                "Cancel Booking functionality will be implemented later.",
                "Cancel Booking",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class ViewBookingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(CustomerGUI.this,
                "View Booking Details functionality will be implemented later.",
                "View Booking",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class UpdateProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(CustomerGUI.this,
                "Update Profile functionality will be implemented later.",
                "Update Profile",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int result = JOptionPane.showConfirmDialog(CustomerGUI.this,
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
                new CustomerGUI("TestCustomer").setVisible(true);
            }
        });
    }
}
