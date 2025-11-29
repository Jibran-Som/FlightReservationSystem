package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/FLIGHTRESERVE?useSSL=false&allowPublicKeyRetrieval=true";
    
    public LoginGUI() {
        initializeGUI();
    }

    private void initializeGUI() {
        // Set up the main frame
        setTitle("Flight Reservation System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null); // Center the window
        setResizable(false);

        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create header panel
        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("Flight Reservation System");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(headerLabel);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Login"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);

        // Add action listeners
        loginButton.addActionListener(new LoginButtonListener());
        cancelButton.addActionListener(new CancelButtonListener());

        // Allow Enter key to trigger login
        getRootPane().setDefaultButton(loginButton);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            // Basic validation
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginGUI.this,
                    "Please enter both username and password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Authenticate against database and get role
            String role = authenticateUser(username, password);
            
            if (role != null) {
                JOptionPane.showMessageDialog(LoginGUI.this,
                    "Login successful! Welcome " + getRoleDisplayName(role) + ": " + username,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                // Close login window and open the main application for the specific role
                dispose();
                openMainApplication(role, username);
            } else {
                JOptionPane.showMessageDialog(LoginGUI.this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);

                // Clear password field
                passwordField.setText("");
                usernameField.requestFocus();
            }
        }

        private String authenticateUser(String username, String password) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                // Try all database users to find the right connection
                String[][] dbUsers = {
                    {"admin_user", "admin_password"},
                    {"agent_user", "agent_password"}, 
                    {"customer_user", "customer_password"}
                };
                
                for (String[] dbUser : dbUsers) {
                    String dbUsername = dbUser[0];
                    String dbPassword = dbUser[1];
                    
                    try {
                        conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);
                        
                        // Query to check user credentials in person table
                        String sql = "SELECT p.role FROM person p WHERE p.username = ? AND p.password = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, username);
                        pstmt.setString(2, password);
                        
                        rs = pstmt.executeQuery();
                        
                        if (rs.next()) {
                            // User found! Return their role
                            return rs.getString("role");
                        }
                    } catch (SQLException ex) {
                        // Try next user type
                        continue;
                    } finally {
                        // Close resources for this attempt
                        try { if (rs != null) rs.close(); } catch (SQLException ex) {}
                        try { if (pstmt != null) pstmt.close(); } catch (SQLException ex) {}
                        try { if (conn != null) conn.close(); } catch (SQLException ex) {}
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(LoginGUI.this,
                    "Database connection error: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
            return null; // Authentication failed
        }

        private String getRoleDisplayName(String role) {
            switch (role) {
                case "Customer": return "Customer";
                case "FlightAgent": return "Flight Agent";
                case "Admin": return "System Administrator";
                default: return "User";
            }
        }

        private void openMainApplication(String role, String username) {
            switch (role) {
                case "Customer":
                    new CustomerGUI(username).setVisible(true);
                    break;
                case "FlightAgent":
                    new FlightAgentGUI(username).setVisible(true);
                    break;
                case "Admin":
                    new AdminGUI(username).setVisible(true);
                    break;
                default:
                    // If role doesn't match, show error
                    JOptionPane.showMessageDialog(LoginGUI.this,
                        "Unknown user role: " + role,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class CancelButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int result = JOptionPane.showConfirmDialog(LoginGUI.this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        // Create and show the login GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginGUI().setVisible(true);
            }
        });
    }
}