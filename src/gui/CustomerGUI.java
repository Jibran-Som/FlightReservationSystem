package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import service.FlightController;
import service.CustomerController;
import service.BookingController;
import service.PromotionManager;
import model.*;
import backend.DatabaseManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CustomerGUI extends JFrame {
    private String currentUser;
    private Customer currentCustomer;

    // Controllers
    private FlightController flightController;
    private CustomerController customerController;
    private BookingController bookingController;
    private DatabaseManager db;
    private PromotionManager promotionManager;

    // Flight Search Components
    private JTextField departureField;
    private JTextField destinationField;
    private JTextField dateField;
    private JButton searchFlightsButton;
    private JButton clearSearchButton;
    private JTable flightTable;
    private DefaultTableModel flightTableModel;
    private JButton bookFlightButton;

    // My Bookings Components
    private JTable bookingsTable;
    private DefaultTableModel bookingsTableModel;
    private JButton cancelBookingButton;
    private JButton refreshBookingsButton;

    // Profile Components
    private JTextField profileFirstNameField;
    private JTextField profileLastNameField;
    private JTextField profileEmailField;
    private JTextField profileUsernameField;
    private JTextField profileDobField;
    private JTextField profilePassField;
    private JButton updateProfileButton;

    // Promotions Components
    private JButton viewPromotionsButton;
    private boolean isSubscribed = false;

    public CustomerGUI(String username) {
        this.currentUser = username;
        initializeControllers();
        loadCustomerData();
        initializeGUI();
        loadInitialData();
        initializePromotions();
    }

    private void initializeControllers() {
        this.db = DatabaseManager.getInstance();
        this.flightController = new FlightController();
        this.customerController = new CustomerController();
        this.bookingController = new BookingController();
        this.promotionManager = PromotionManager.getInstance();

        // Connect to database
        try {
            db.connect("customer_user", "customer_password");
            System.out.println("Connected to the database as customer: " + currentUser);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Database connection failed: " + e.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializePromotions() {
        if (currentCustomer != null) {
            // Load promotions but don't auto-subscribe
            promotionManager.loadPromotionsFromDatabase();
        }
    }

    private void loadCustomerData() {
        try {
            // Find customer by username
            ArrayList<Customer> allCustomers = customerController.getAllCustomersAsArray();
            for (Customer customer : allCustomers) {
                if (customer.getUsername().equals(currentUser)) {
                    this.currentCustomer = customer;
                    break;
                }
            }

            if (currentCustomer == null) {
                JOptionPane.showMessageDialog(this,
                    "Could not load customer data for: " + currentUser,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                System.out.println("Customer data loaded: " + currentCustomer.getFirstName() + " " + currentCustomer.getLastName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading customer data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeGUI() {
        setTitle("Flight Reservation System - Customer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create main panel with tabbed interface
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        String welcomeName = currentCustomer != null ?
            currentCustomer.getFirstName() + " " + currentCustomer.getLastName() : currentUser;
        JLabel welcomeLabel = new JLabel("Welcome, " + welcomeName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel headerButtonPanel = new JPanel(new FlowLayout());

        // Subscription toggle button - starts as "Subscribe to Promotions"
        JButton subscriptionButton = new JButton("Subscribe to Promotions");
        subscriptionButton.addActionListener(e -> toggleSubscription(subscriptionButton));
        headerButtonPanel.add(subscriptionButton);

        viewPromotionsButton = new JButton("View Promotions");
        viewPromotionsButton.addActionListener(new ViewPromotionsListener());
        headerButtonPanel.add(viewPromotionsButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new LogoutButtonListener());
        headerButtonPanel.add(logoutButton);

        headerPanel.add(headerButtonPanel, BorderLayout.EAST);

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

        searchPanel.add(new JLabel("Departure City:"));
        departureField = new JTextField();
        searchPanel.add(departureField);

        searchPanel.add(new JLabel("Destination City:"));
        destinationField = new JTextField();
        searchPanel.add(destinationField);

        searchPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        searchPanel.add(dateField);

        searchPanel.add(new JLabel("")); // Empty label for spacing

        JPanel buttonPanel = new JPanel(new FlowLayout());
        searchFlightsButton = new JButton("Search Flights");
        searchFlightsButton.addActionListener(new SearchFlightsListener());
        buttonPanel.add(searchFlightsButton);

        clearSearchButton = new JButton("Clear");
        clearSearchButton.addActionListener(e -> clearSearchFields());
        buttonPanel.add(clearSearchButton);

        searchPanel.add(buttonPanel);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Available Flights"));

        String[] columnNames = {"Flight ID", "Airline", "Departure", "Destination", "Date", "Duration", "Price", "Available Seats"};
        flightTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightTable = new JTable(flightTableModel);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        refreshBookingsButton = new JButton("Refresh My Bookings");

        cancelBookingButton.addActionListener(new CancelBookingListener());
        refreshBookingsButton.addActionListener(e -> refreshBookingsTable());

        buttonPanel.add(cancelBookingButton);
        buttonPanel.add(refreshBookingsButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Bookings table
        String[] columnNames = {"Booking ID", "Flight ID", "Airline", "Departure", "Destination", "Date", "Seat Number"};
        bookingsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingsTable = new JTable(bookingsTableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));

        formPanel.add(new JLabel("Username:"));
        profileUsernameField = new JTextField();
        profileUsernameField.setEditable(false);
        formPanel.add(profileUsernameField);

        formPanel.add(new JLabel("First Name:"));
        profileFirstNameField = new JTextField();
        formPanel.add(profileFirstNameField);

        formPanel.add(new JLabel("Last Name:"));
        profileLastNameField = new JTextField();
        formPanel.add(profileLastNameField);

        formPanel.add(new JLabel("Email:"));
        profileEmailField = new JTextField();
        formPanel.add(profileEmailField);

        formPanel.add(new JLabel("Current Password:"));
        profilePassField = new JTextField();
        formPanel.add(profilePassField);

        formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        profileDobField = new JTextField();
        formPanel.add(profileDobField);

        formPanel.add(new JLabel("")); // Empty label for spacing
        updateProfileButton = new JButton("Update Profile");
        updateProfileButton.addActionListener(new UpdateProfileListener());
        formPanel.add(updateProfileButton);

        panel.add(formPanel, BorderLayout.NORTH);

        // Load current profile data
        loadProfileData();

        return panel;
    }

    private void loadInitialData() {
        refreshBookingsTable();
    }

    private void loadProfileData() {
        if (currentCustomer != null) {
            profileUsernameField.setText(currentCustomer.getUsername());
            profileFirstNameField.setText(currentCustomer.getFirstName());
            profileLastNameField.setText(currentCustomer.getLastName());
            profileEmailField.setText(currentCustomer.getEmail());
            if (currentCustomer.getDoB() != null) {
                profileDobField.setText(currentCustomer.getDoB().toSQLDate());
            }
            try {
                profilePassField.setText(db.getPasswordForUser(currentCustomer.getId()));
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshBookingsTable() {
        try {
            bookingsTableModel.setRowCount(0);
            if (currentCustomer == null) return;

            ArrayList<Booking> allBookings = bookingController.getAllBookings();

            for (Booking booking : allBookings) {
                if (booking.getCustomer().getId() == currentCustomer.getId()) {
                    Flight flight = booking.getFlight();
                    ArrayList<Flight> flights = flightController.getAllFlights();
                    for(Flight f : flights){
                        if(flight.getFlightID() == f.getFlightID()){
                            flight = f;
                        }
                    }
                    Object[] row = {
                        booking.getBookingId(),
                        flight.getFlightID(),
                        flight.getAirplane() != null ? flight.getAirplane().getAirline().getName() : "N/A",
                        flight.getRoute() != null ? flight.getRoute().getDepartureLocation().getCity() : "N/A",
                        flight.getRoute() != null ? flight.getRoute().getArrivalLocation().getCity() : "N/A",
                        flight.getDepartureDate().toString(),
                        booking.getSeatNumber()
                    };
                    bookingsTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading bookings: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSearchFields() {
        departureField.setText("");
        destinationField.setText("");
        dateField.setText("");
        flightTableModel.setRowCount(0);
    }

    private void toggleSubscription(JButton button) {
        if (currentCustomer != null) {
            if (isSubscribed) {
                promotionManager.removeObserver(currentCustomer);
                button.setText("Subscribe to Promotions");
                isSubscribed = false;
                System.out.println("Customer " + currentCustomer.getUsername() + " unsubscribed from promotions");
                JOptionPane.showMessageDialog(this,
                    "You have unsubscribed from promotion notifications.",
                    "Unsubscribed",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                promotionManager.registerObserver(currentCustomer);
                button.setText("Unsubscribe from Promotions");
                isSubscribed = true;
                System.out.println("Customer " + currentCustomer.getUsername() + " subscribed to promotions");

                // Notify about current promotions when subscribing
                java.util.List<Promotion> activePromotions = promotionManager.getActivePromotions();
                if (!activePromotions.isEmpty()) {
                    System.out.println("Notifying newly subscribed customer about " + activePromotions.size() + " current promotions");
                    for (Promotion promotion : activePromotions) {
                        currentCustomer.update(promotion);
                    }
                }

                JOptionPane.showMessageDialog(this,
                    "You have subscribed to promotion notifications.",
                    "Subscribed",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Action Listeners
    private class SearchFlightsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String departure = departureField.getText().trim();
            String destination = destinationField.getText().trim();
            String date = dateField.getText().trim();

            if (departure.isEmpty() && destination.isEmpty() && date.isEmpty()) {
                JOptionPane.showMessageDialog(CustomerGUI.this,
                    "Please enter at least one search criteria.",
                    "Search Criteria Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                flightTableModel.setRowCount(0);
                ArrayList<Flight> allFlights = flightController.getAllFlights();
                ArrayList<Flight> matchingFlights = new ArrayList<>();

                for (Flight flight : allFlights) {
                    boolean matches = true;

                    // Check departure city
                    if (!departure.isEmpty() && flight.getRoute() != null) {
                        String departureCity = flight.getRoute().getDepartureLocation().getCity();
                        if (!departureCity.toLowerCase().contains(departure.toLowerCase())) {
                            matches = false;
                        }
                    }

                    // Check destination city
                    if (matches && !destination.isEmpty() && flight.getRoute() != null) {
                        String destinationCity = flight.getRoute().getArrivalLocation().getCity();
                        if (!destinationCity.toLowerCase().contains(destination.toLowerCase())) {
                            matches = false;
                        }
                    }

                    // Check date
                    if (matches && !date.isEmpty()) {
                        String flightDate = flight.getDepartureDate().toSQLDate();
                        if (!flightDate.equals(date)) {
                            matches = false;
                        }
                    }

                    // Check available seats
                    if (matches && flight.getAvailableSeats() <= 0) {
                        matches = false;
                    }

                    if (matches) {
                        matchingFlights.add(flight);
                    }
                }

                // Display matching flights
                for (Flight flight : matchingFlights) {
                    Object[] row = {
                        flight.getFlightID(),
                        flight.getAirplane() != null ? flight.getAirplane().getAirline().getName() : "N/A",
                        flight.getRoute() != null ? flight.getRoute().getDepartureLocation().getCity() : "N/A",
                        flight.getRoute() != null ? flight.getRoute().getArrivalLocation().getCity() : "N/A",
                        flight.getDepartureDate().toString(),
                        flight.getFlightTime(),
                        String.format("$%.2f", flight.getPrice()),
                        flight.getAvailableSeats()
                    };
                    flightTableModel.addRow(row);
                }

                if (matchingFlights.isEmpty()) {
                    JOptionPane.showMessageDialog(CustomerGUI.this,
                        "No flights found matching your search criteria.",
                        "Search Results",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(CustomerGUI.this,
                        "Found " + matchingFlights.size() + " flight(s) matching your criteria.",
                        "Search Results",
                        JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(CustomerGUI.this,
                    "Error searching flights: " + ex.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class BookFlightListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(CustomerGUI.this,
                    "Please select a flight to book.",
                    "No Flight Selected",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (currentCustomer == null) {
                JOptionPane.showMessageDialog(CustomerGUI.this,
                    "Cannot book flight: Customer data not loaded.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int flightId = (int) flightTableModel.getValueAt(selectedRow, 0);
                String airline = (String) flightTableModel.getValueAt(selectedRow, 1);
                String departure = (String) flightTableModel.getValueAt(selectedRow, 2);
                String destination = (String) flightTableModel.getValueAt(selectedRow, 3);
                double originalPrice = Double.parseDouble(((String) flightTableModel.getValueAt(selectedRow, 6)).replace("$", "").replace(",", ""));
                int availableSeats = (int) flightTableModel.getValueAt(selectedRow, 7);

                if (availableSeats <= 0) {
                    JOptionPane.showMessageDialog(CustomerGUI.this,
                        "Sorry, this flight is fully booked.",
                        "Flight Full",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Seat selection dialog
                String seatNumberStr = JOptionPane.showInputDialog(CustomerGUI.this,
                    "Flight: " + airline + " - " + departure + " to " + destination + "\n" +
                    "Original Price: $" + String.format("%.2f", originalPrice) + "\n" +
                    "Available seats: " + availableSeats + "\n\n" +
                    "Enter seat number:",
                    "Book Flight - Seat Selection",
                    JOptionPane.QUESTION_MESSAGE);

                if (seatNumberStr != null && !seatNumberStr.trim().isEmpty()) {
                    try {
                        int seatNumber = Integer.parseInt(seatNumberStr.trim());

                        // Basic seat validation
                        if (seatNumber <= 0 || seatNumber > 500) {
                            JOptionPane.showMessageDialog(CustomerGUI.this,
                                "Please enter a valid seat number (1-500).",
                                "Invalid Seat Number",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Get the flight object
                        Flight selectedFlight = flightController.getFlight(flightId);
                        if (selectedFlight == null) {
                            JOptionPane.showMessageDialog(CustomerGUI.this,
                                "Error: Could not find selected flight.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Check if seat is already taken
                        boolean seatTaken = false;
                        ArrayList<Booking> allBookings = bookingController.getAllBookings();
                        for (Booking booking : allBookings) {
                            if (booking.getFlight().getFlightID() == flightId &&
                                booking.getSeatNumber() == seatNumber) {
                                seatTaken = true;
                                break;
                            }
                        }

                        if (seatTaken) {
                            JOptionPane.showMessageDialog(CustomerGUI.this,
                                "Seat " + seatNumber + " is already taken on this flight.\n" +
                                "Please choose a different seat.",
                                "Seat Unavailable",
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        // Apply promo code
                        double finalPrice = originalPrice;
                        Promotion appliedPromotion = null;

                        String promoCode = JOptionPane.showInputDialog(CustomerGUI.this,
                            "Enter promo code (or leave blank for no discount):",
                            "Promo Code",
                            JOptionPane.QUESTION_MESSAGE);

                        if (promoCode != null && !promoCode.trim().isEmpty()) {
                            appliedPromotion = promotionManager.getPromotionByCode(promoCode.trim());
                            if (appliedPromotion != null) {
                                double discount = appliedPromotion.getDiscountRate();
                                finalPrice = originalPrice * (1 - discount);
                                System.out.println("Promo code " + promoCode + " applied. Discount: " + (discount * 100) + "%. Original: $" + originalPrice + ", Final: $" + finalPrice);
                                JOptionPane.showMessageDialog(CustomerGUI.this,
                                    "Promo code applied! Discount: " + (discount * 100) + "%\n" +
                                    "New price: $" + String.format("%.2f", finalPrice),
                                    "Discount Applied",
                                    JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                System.out.println("Invalid promo code attempted: " + promoCode);
                                JOptionPane.showMessageDialog(CustomerGUI.this,
                                    "Invalid promo code. Continuing with original price.",
                                    "Invalid Promo Code",
                                    JOptionPane.WARNING_MESSAGE);
                            }
                        }

                        Object[] options = {"Credit Card", "PayPal"};
                        int choice = JOptionPane.showOptionDialog(CustomerGUI.this,
                            "Final Price: $" + String.format("%.2f", finalPrice) +
                            (appliedPromotion != null ? " (with " + (appliedPromotion.getDiscountRate() * 100) + "% discount)" : "") +
                            "\nPlease choose a payment method:",
                            "Payment Method",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                            null, options, options[0]);

                        if (choice == -1) {
                            // User cancelled the payment choice
                            return;
                        }
                        else if(choice == 0){
                            CreditCardPaymentDialog cardDialog = new CreditCardPaymentDialog(CustomerGUI.this);
                            cardDialog.setVisible(true);
                            Card card = new Card(cardDialog.getCardNumber(), cardDialog.getHolderName(), cardDialog.getExpiryDate(), cardDialog.getCvv());
                            currentCustomer.setPaymentStrategy(new CreditCardPayment(card));
                            if (!cardDialog.isConfirmed()) {
                                return;
                            }

                            String payed = currentCustomer.processPayment(finalPrice);
                            if(payed.equals("Failed")){
                                JOptionPane.showMessageDialog(CustomerGUI.this,
                                "Payment failed. Please try again.",
                                "Payment Failure",
                                JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                            else{
                                JOptionPane.showMessageDialog(CustomerGUI.this,
                                    "Paid via Credit Card: $" + String.format("%.2f", finalPrice),
                                    "Payment Successful",
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                        }

                        else if(choice == 1){
                            PayPalPaymentDialog ppDialog = new PayPalPaymentDialog(CustomerGUI.this);
                            ppDialog.setVisible(true);
                            Card card = new Card(ppDialog.getCardNumber(), ppDialog.getHolderName(), ppDialog.getExpiryDate(), ppDialog.getCvv());
                            currentCustomer.setPaymentStrategy(new PayPalPayment(card));
                            if (!ppDialog.isConfirmed()) {
                                return;
                            }

                            String payed = currentCustomer.processPayment(finalPrice);
                            if(payed.equals("Failed")){
                                JOptionPane.showMessageDialog(CustomerGUI.this,
                                "Payment failed. Please try again.",
                                "Payment Failure",
                                JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                            else{
                                JOptionPane.showMessageDialog(CustomerGUI.this,
                                    "Paid via PayPal: $" + String.format("%.2f", finalPrice),
                                    "Payment Successful",
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        // Create the booking
                        Booking newBooking = bookingController.createBooking(currentCustomer, selectedFlight, seatNumber);

                        System.out.println("Booking created: ID " + newBooking.getBookingId() + " for customer " + currentCustomer.getUsername());

                        JOptionPane.showMessageDialog(CustomerGUI.this,
                            "Booking confirmed!\n\n" +
                            "Booking ID: " + newBooking.getBookingId() + "\n" +
                            "Flight: " + airline + " - " + departure + " to " + destination + "\n" +
                            "Seat: " + seatNumber + "\n" +
                            "Date: " + selectedFlight.getDepartureDate().toString() + "\n" +
                            (appliedPromotion != null ? "Discount Applied: " + (appliedPromotion.getDiscountRate() * 100) + "% (" + appliedPromotion.getPromoCode() + ")\n" : "") +
                            "Final Price: $" + String.format("%.2f", finalPrice),
                            "Booking Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                        // Refresh tables
                        refreshBookingsTable();
                        // Re-run search to update available seats
                        new SearchFlightsListener().actionPerformed(e);

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(CustomerGUI.this,
                            "Please enter a valid number for seat.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(CustomerGUI.this,
                    "Error booking flight: " + ex.getMessage(),
                    "Booking Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class CancelBookingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(CustomerGUI.this,
                    "Please select a booking to cancel.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int bookingId = (int) bookingsTableModel.getValueAt(selectedRow, 0);
            String flightInfo = "Flight " + bookingsTableModel.getValueAt(selectedRow, 1) +
                              " - " + bookingsTableModel.getValueAt(selectedRow, 3) +
                              " to " + bookingsTableModel.getValueAt(selectedRow, 4);

            int confirm = JOptionPane.showConfirmDialog(CustomerGUI.this,
                "Are you sure you want to cancel this booking?\n\n" +
                "Booking ID: " + bookingId + "\n" +
                flightInfo + "\n" +
                "Seat: " + bookingsTableModel.getValueAt(selectedRow, 6),
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    bookingController.cancelBooking(bookingId);
                    System.out.println("Booking " + bookingId + " cancelled for customer " + currentCustomer.getUsername());

                    JOptionPane.showMessageDialog(CustomerGUI.this,
                        "Booking cancelled successfully!",
                        "Cancellation Confirmed",
                        JOptionPane.INFORMATION_MESSAGE);

                    refreshBookingsTable();
                    // Refresh flight search to update available seats
                    new SearchFlightsListener().actionPerformed(e);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CustomerGUI.this,
                        "Error cancelling booking: " + ex.getMessage(),
                        "Cancellation Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class UpdateProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentCustomer == null) {
                JOptionPane.showMessageDialog(CustomerGUI.this,
                    "Cannot update profile: Customer data not loaded.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String firstName = profileFirstNameField.getText().trim();
            String lastName = profileLastNameField.getText().trim();
            String email = profileEmailField.getText().trim();
            String dob = profileDobField.getText().trim();
            String pass = profilePassField.getText().trim();

            // Validation
            if (firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(CustomerGUI.this,
                    "First name and last name are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (email.isEmpty() || !email.contains("@")) {
                JOptionPane.showMessageDialog(CustomerGUI.this,
                    "Please enter a valid email address.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Update customer object
                currentCustomer.setFirstName(firstName);
                currentCustomer.setLastName(lastName);
                currentCustomer.setEmail(email);

                if (!dob.isEmpty()) {
                    currentCustomer.setDoB(CustomDate.StringToDate(dob));
                }
                if(!pass.isEmpty()) {
                    try {
                        db.updatePasswordDirectly(currentCustomer.getId(), pass);
                    }
                    catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }

                // Update in database
                customerController.updateCustomer(currentCustomer);
                System.out.println("Customer profile updated: " + currentCustomer.getUsername());

                JOptionPane.showMessageDialog(CustomerGUI.this,
                    "Profile updated successfully!",
                    "Profile Updated",
                    JOptionPane.INFORMATION_MESSAGE);

                // Update welcome message
                String welcomeName = currentCustomer.getFirstName() + " " + currentCustomer.getLastName();
                setTitle("Flight Reservation System - " + welcomeName + "'s Dashboard");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(CustomerGUI.this,
                    "Error updating profile: " + ex.getMessage(),
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ViewPromotionsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Show available promotions
            StringBuilder promotionsText = new StringBuilder();
            promotionsText.append("=== CURRENT PROMOTIONS ===\n\n");

            java.util.List<Promotion> activePromotions = promotionManager.getActivePromotions();

            if (activePromotions.isEmpty()) {
                promotionsText.append("No active promotions at the moment.\n");
                promotionsText.append("Check back later for special offers!");
            } else {
                for (Promotion promotion : activePromotions) {
                    promotionsText.append("Code: ").append(promotion.getPromoCode()).append("\n");
                    promotionsText.append("Discount: ").append(promotion.getDiscountRate() * 100).append("%\n");
                    promotionsText.append("Description: ").append(promotion.getDescription()).append("\n");
                    promotionsText.append("Start Date: ").append(promotion.getStartDate().toString()).append("\n");
                    promotionsText.append("------------------------\n");
                }
            }

            // Add subscription info
            promotionsText.append("\n=== SUBSCRIPTION STATUS ===\n");
            promotionsText.append("You are ").append(isSubscribed ? "SUBSCRIBED" : "NOT SUBSCRIBED");
            promotionsText.append(" to promotion notifications.\n");

            JTextArea textArea = new JTextArea(20, 50);
            textArea.setText(promotionsText.toString());
            textArea.setEditable(false);
            textArea.setCaretPosition(0);

            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(CustomerGUI.this, scrollPane,
                "Current Promotions", JOptionPane.INFORMATION_MESSAGE);
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
                if (currentCustomer != null && isSubscribed) {
                    System.out.println("Customer " + currentCustomer.getUsername() + " logging out (was subscribed)");
                } else if (currentCustomer != null) {
                    System.out.println("Customer " + currentCustomer.getUsername() + " logging out (was not subscribed)");
                }
                db.disconnect();
                dispose();
                new LoginGUI().setVisible(true);
            }
        }
    }

    public class CreditCardPaymentDialog extends JDialog {
        private JTextField cardNumberField;
        private JTextField holderNameField;
        private JTextField expiryDateField;
        private JTextField cvvField;
        private boolean isConfirmed = false;

        public CreditCardPaymentDialog(Frame parent) {
            super(parent, "Enter Credit Card Details", true);

            // Create the input fields and labels
            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

            panel.add(new JLabel("Card Number:"));
            cardNumberField = new JTextField();
            panel.add(cardNumberField);

            panel.add(new JLabel("Holder Name:"));
            holderNameField = new JTextField();
            panel.add(holderNameField);

            panel.add(new JLabel("Expiry Date (MM/YY):"));
            expiryDateField = new JTextField();
            panel.add(expiryDateField);

            panel.add(new JLabel("CVV:"));
            cvvField = new JTextField();
            panel.add(cvvField);

            // Add buttons for confirmation
            JPanel buttonPanel = new JPanel();
            JButton confirmButton = new JButton("Confirm");
            String regex = "^(0[1-9]|1[0-2])\\/\\d{2}$";
            confirmButton.addActionListener(e -> {
                // Validate the fields before confirming
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(getExpiryDate());
                if (!getCardNumber().isEmpty() && !getHolderName().isEmpty() && !getExpiryDate().isEmpty()
                    && !getCvv().isEmpty() && matcher.matches()) {
                    isConfirmed = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields correctly.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(confirmButton);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dispose());
            buttonPanel.add(cancelButton);

            panel.add(buttonPanel);

            add(panel);
            setSize(350, 250);
            setLocationRelativeTo(parent);
        }
        public String getCardNumber() {
            return cardNumberField.getText().trim();
        }

        public String getHolderName() {
            return holderNameField.getText().trim();
        }

        public String getExpiryDate() {
            return expiryDateField.getText().trim();
        }

        public String getCvv() {
            return cvvField.getText().trim();
        }

        public boolean isConfirmed(){
            return this.isConfirmed;
        }
    }

    public class PayPalPaymentDialog extends JDialog {
        private JTextField cardNumberField;
        private JTextField holderNameField;
        private JTextField expiryDateField;
        private JTextField cvvField;
        private boolean isConfirmed = false;

        public PayPalPaymentDialog(Frame parent) {
            super(parent, "Enter PayPal Details", true);

            // Create the input fields and labels
            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

            panel.add(new JLabel("Card Number:"));
            cardNumberField = new JTextField();
            panel.add(cardNumberField);

            panel.add(new JLabel("Holder Name:"));
            holderNameField = new JTextField();
            panel.add(holderNameField);

            panel.add(new JLabel("Expiry Date (MM/YY):"));
            expiryDateField = new JTextField();
            panel.add(expiryDateField);

            panel.add(new JLabel("CVV:"));
            cvvField = new JTextField();
            panel.add(cvvField);

            // Add buttons for confirmation
            JPanel buttonPanel = new JPanel();
            JButton confirmButton = new JButton("Confirm");
            String regex = "^(0[1-9]|1[0-2])\\/\\d{2}$";
            confirmButton.addActionListener(e -> {
                // Validate the fields before confirming
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(getExpiryDate());
                if (!getCardNumber().isEmpty() && !getHolderName().isEmpty() && !getExpiryDate().isEmpty()
                    && !getCvv().isEmpty() && matcher.matches()) {
                    isConfirmed = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields correctly.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(confirmButton);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dispose());
            buttonPanel.add(cancelButton);

            panel.add(buttonPanel);

            add(panel);
            setSize(350, 250);
            setLocationRelativeTo(parent);
        }

        public String getCardNumber() {
            return cardNumberField.getText().trim();
        }

        public String getHolderName() {
            return holderNameField.getText().trim();
        }

        public String getExpiryDate() {
            return expiryDateField.getText().trim();
        }

        public String getCvv() {
            return cvvField.getText().trim();
        }

        public boolean isConfirmed(){
            return this.isConfirmed;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CustomerGUI("johns").setVisible(true); // Using sample username from database
            }
        });
    }
}
