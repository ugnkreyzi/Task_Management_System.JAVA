
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class loginGUI extends JFrame {
    


    private JTextField usernameField;
    private JPasswordField passwordField;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new loginGUI(); // Create the loginGUI instance
            }
        });
    }

    public loginGUI() {
        setTitle("Login");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true); // Remove window border for sleek look

        // Background Panel with Custom Drawing
        JPanel bgPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bg = new ImageIcon("bg.png"); // Set your own background image here
                g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        bgPanel.setLayout(null);

        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBounds(50, 70, 300, 350);
        card.setBackground(new Color(0, 0, 0, 150)); // Semi-transparent black background
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setBounds(50, 20, 100, 30);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        usernameField = new JTextField();
        usernameField.setBounds(25, 70, 250, 40);
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        passwordField = new JPasswordField();
        passwordField.setBounds(25, 120, 250, 40);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JCheckBox remember = new JCheckBox("Remember me");
        remember.setBounds(25, 165, 120, 20);
        remember.setOpaque(false);
        remember.setForeground(Color.WHITE);

        JLabel forgot = new JLabel("Forgot Password?");
        forgot.setBounds(160, 165, 150, 20);
        forgot.setForeground(Color.WHITE);
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(25, 200, 250, 35);
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        JLabel registerLabel = new JLabel("Don't have an account? Register");
        registerLabel.setBounds(60, 250, 250, 20);
        registerLabel.setForeground(Color.WHITE);
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add actions to buttons
        loginButton.addActionListener(e -> login());
        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                signup();
            }
        });

        // Adding components to the card panel
        card.add(usernameField);
        card.add(passwordField);
        card.add(remember);
        card.add(forgot);
        card.add(loginButton);
        card.add(registerLabel);

        // Adding components to the background panel
        bgPanel.add(titleLabel);
        bgPanel.add(card);

        // Adding the background panel to the frame
        add(bgPanel);

        setVisible(true);
    }

    // Login functionality
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (DatabaseManager.authenticateUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose(); // Close the login window
            new TaskManagerGUI(username); // Go to the main app GUI
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }

    // Signup functionality with security question
    private void signup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Ask for security question during signup
        String[] questions = {"Your first pet's name?", "Your mother's maiden name?", "Your favorite book?"};
        String securityQuestion = (String) JOptionPane.showInputDialog(this,
                "Select a Security Question:",
                "Security Question",
                JOptionPane.QUESTION_MESSAGE,
                null,
                questions,
                questions[0]);

        String securityAnswer = JOptionPane.showInputDialog(this, "Enter your answer:");

        if (username.isEmpty() || password.isEmpty() || securityAnswer.isEmpty() || securityQuestion == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        // Save user with the security question and answer in the database
        if (DatabaseManager.registerUser(username, password, securityQuestion, securityAnswer)) {
            JOptionPane.showMessageDialog(this, "Signup successful! You can now log in.");
        } else {
            JOptionPane.showMessageDialog(this, "Signup failed. Username might already exist.");
        }
    }
}












