package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends JFrame {
    private JComboBox<String> roleComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;

    public LoginGUI() {
        initializeGUI();
    }

    private void initializeGUI() {
        // Set up the main frame
        setTitle("Flight Reservation System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
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

        // Role selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        String[] roles = {"Customer", "Flight Agent", "System Administrator"};
        roleComboBox = new JComboBox<>(roles);
        formPanel.add(roleComboBox, gbc);

        // Username field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
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
            String role = (String) roleComboBox.getSelectedItem();

            // Basic validation
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginGUI.this,
                    "Please enter both username and password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Here you would typically validate credentials against a database
            // For now, we'll use a simple simulation
            if (authenticateUser(username, password, role)) {
                JOptionPane.showMessageDialog(LoginGUI.this,
                    "Login successful! Welcome " + role + ": " + username,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                // Close login window and open the main application for the specific role
                dispose();
                openMainApplication(role, username);
            } else {
                JOptionPane.showMessageDialog(LoginGUI.this,
                    "Invalid username, password, or role selection.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);

                // Clear password field
                passwordField.setText("");
                usernameField.requestFocus();
            }
        }

        private boolean authenticateUser(String username, String password, String role) {
            // This is a simple simulation - in real implementation,
            // you would query the database to validate credentials

            // For demo purposes, accept any non-empty credentials
            // In your actual implementation, replace this with database authentication
            return !username.isEmpty() && !password.isEmpty();
        }

        private void openMainApplication(String role, String username) {
            // This method would open the appropriate main application window
            // based on the user's role

            switch (role) {
                case "Customer":
                    // Open Customer GUI
                    new CustomerGUI(username).setVisible(true);
                    System.out.println("Opening Customer interface for: " + username);
                    break;
                case "Flight Agent":
                    // Open Flight Agent GUI
                    new FlightAgentGUI(username).setVisible(true);
                    System.out.println("Opening Flight Agent interface for: " + username);
                    break;
                case "System Administrator":
                    // Open Admin GUI
                    new AdminGUI(username).setVisible(true);
                    System.out.println("Opening System Administrator interface for: " + username);
                    break;
            }

            // // For now, just show a message
            // JOptionPane.showMessageDialog(null,
            //     role + " Dashboard would open here for user: " + username + "\n" +
            //     "Implementation of main application windows is pending.",
            //     "Application Launch",
            //     JOptionPane.INFORMATION_MESSAGE);
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
        // Set system look and feel


        // Create and show the login GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginGUI().setVisible(true);
            }
        });
    }
}
