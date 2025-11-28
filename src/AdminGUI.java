
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminGUI extends JFrame {
    private String currentUser;
    private JTabbedPane tabbedPane;
    
    // Flight Management Components
    private JTable flightTable;
    private JButton addFlightButton;
    private JButton editFlightButton;
    private JButton removeFlightButton;
    private JButton refreshFlightsButton;
    
    // User Management Components
    private JTable userTable;
    private JButton addUserButton;
    private JButton editUserButton;
    private JButton removeUserButton;
    
    // System Management Components
    private JTextArea systemLogArea;
    private JButton generateReportButton;
    private JButton viewPromotionsButton;

    public AdminGUI(String username) {
        this.currentUser = username;
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
        String[] columnNames = {"Flight ID", "Flight Number", "Airline", "Departure", "Destination", "Date", "Time", "Capacity", "Price", "Status"};
        Object[][] data = {}; // Empty for now
        flightTable = new JTable(data, columnNames);
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
        String[] columnNames = {"User ID", "Username", "First Name", "Last Name", "Role", "Email", "Status"};
        Object[][] data = {}; // Empty for now
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
            JOptionPane.showMessageDialog(AdminGUI.this,
                "Add Flight functionality will be implemented later.",
                "Add Flight",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class EditFlightListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(AdminGUI.this,
                "Edit Flight functionality will be implemented later.",
                "Edit Flight",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class RemoveFlightListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(AdminGUI.this,
                "Remove Flight functionality will be implemented later.",
                "Remove Flight",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class RefreshFlightsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            systemLogArea.append("Flight list refreshed at: " + java.time.LocalDateTime.now() + "\n");
            JOptionPane.showMessageDialog(AdminGUI.this,
                "Flight list refreshed.\n" +
                "This functionality will load actual data from database when implemented.",
                "Refresh Flights",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class AddUserListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(AdminGUI.this,
                "Add User functionality will be implemented later.",
                "Add User",
                JOptionPane.INFORMATION_MESSAGE);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminGUI("TestAdmin").setVisible(true);
            }
        });
    }
}
