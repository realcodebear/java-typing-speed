import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class Login extends JFrame implements ActionListener {

    // colours used in all windows
    public static Color BLUE = new Color(79, 70, 229);     // indigo: buttons, header
    public static Color DARK = new Color(15, 23, 42);      // almost black: main text
    public static Color GREY = new Color(100, 116, 139);   // small grey text
    public static Color RED = new Color(220, 38, 38);      // mistakes
    public static Color GREEN = new Color(22, 163, 74);    // accuracy number
    public static Color BACKGROUND = new Color(241, 245, 249); // light grey window background
    public static Color SOFT = new Color(238, 242, 255);   // very light indigo boxes

    //
    JPanel loginPanel;
    JPanel registerPanel;

    JTextField loginUserField;
    JPasswordField loginPassField;
    JButton loginButton;
    JButton goToRegisterButton;

    JTextField registerNameField;
    JTextField registerUserField;
    JPasswordField registerPassField;
    JButton registerButton;
    JButton goToLoginButton;

    Login() {
        setTitle("Typing Speed Test - Login"); // PARENT CLASS JFRAME KA METHOD
        setSize(1000, 600);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ----- left side: blue panel with the app name -----
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBackground(BLUE);
        leftPanel.setBounds(0, 0, 580, 600);
        add(leftPanel);

        // four white "keyboard keys" that spell TYPE
        String word = "TYPE";
        for (int i = 0; i < word.length(); i++) {
            JLabel key = new JLabel(String.valueOf(word.charAt(i)), SwingConstants.CENTER);
            key.setFont(new Font("SansSerif", Font.BOLD, 26));
            key.setForeground(BLUE);
            key.setBackground(Color.WHITE);
            key.setOpaque(true);   // so the white background is painted
            key.setBounds(60 + i * 64, 110, 54, 54);
            leftPanel.add(key);
        }

        leftPanel.add(makeLabel("Typing Speed Test", 44, Font.BOLD, Color.WHITE, 60, 200));
        leftPanel.add(makeLabel("How fast can you type?", 20, Font.PLAIN, new Color(224, 231, 255), 60, 262));

        // three short feature lines
        Color featureColor = new Color(199, 210, 254);
        leftPanel.add(makeLabel("✓   Live speed and accuracy every second", 16, Font.PLAIN, featureColor, 60, 330));
        leftPanel.add(makeLabel("✓   Mistakes turn red while you type", 16, Font.PLAIN, featureColor, 60, 366));
        leftPanel.add(makeLabel("✓   Scores saved in a database leaderboard", 16, Font.PLAIN, featureColor, 60, 402));

        leftPanel.add(makeLabel("Made with Java Swing + JDBC", 13, Font.PLAIN, new Color(165, 180, 252), 60, 515));

        // ----- right side: login form -----
        loginPanel = new JPanel(null);
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBounds(580, 0, 420, 600);
        add(loginPanel);

        // makeLabel function hai jo jlabel to tyar karke return karta hai
        loginPanel.add(makeLabel("Welcome back", 14, Font.PLAIN, GREY, 50, 160));
        loginPanel.add(makeLabel("Login", 30, Font.BOLD, DARK, 50, 180));

        loginPanel.add(makeLabel("Username", 13, Font.BOLD, DARK, 50, 245));
        loginUserField = makeField(new JTextField(), 50, 267, 320);
        loginPanel.add(loginUserField);

        loginPanel.add(makeLabel("Password", 13, Font.BOLD, DARK, 50, 323));
        loginPassField = new JPasswordField();
        makeField(loginPassField, 50, 345, 320);
        loginPassField.addActionListener(this); // pressing Enter in the password box also logs in
        loginPanel.add(loginPassField);

        loginButton = makeButton("Login", 50, 411, 320);
        loginButton.addActionListener(this);
        loginPanel.add(loginButton);

        goToRegisterButton = makeLink("New here? Create an account", 50, 469);
        goToRegisterButton.addActionListener(this);
        loginPanel.add(goToRegisterButton);

        // ----- right side: register form (hidden at the start) -----
        registerPanel = new JPanel(null);
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setBounds(580, 0, 420, 600);
        registerPanel.setVisible(false);
        add(registerPanel);

        registerPanel.add(makeLabel("Create your account", 14, Font.PLAIN, GREY, 50, 120));
        registerPanel.add(makeLabel("Register", 30, Font.BOLD, DARK, 50, 140));

        registerPanel.add(makeLabel("Full name", 13, Font.BOLD, DARK, 50, 205));
        registerNameField = makeField(new JTextField(), 50, 227, 320);
        registerPanel.add(registerNameField);

        registerPanel.add(makeLabel("Username", 13, Font.BOLD, DARK, 50, 283));
        registerUserField = makeField(new JTextField(), 50, 305, 320);
        registerPanel.add(registerUserField);

        registerPanel.add(makeLabel("Password", 13, Font.BOLD, DARK, 50, 361));
        registerPassField = new JPasswordField();
        makeField(registerPassField, 50, 383, 320);
        registerPanel.add(registerPassField);

        registerButton = makeButton("Create account", 50, 449, 320);
        registerButton.addActionListener(this);
        registerPanel.add(registerButton);

        goToLoginButton = makeLink("Already have an account? Login", 50, 507);
        goToLoginButton.addActionListener(this);
        registerPanel.add(goToLoginButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // This runs when any of the four buttons is clicked
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton || e.getSource() == loginPassField) {
            login();
        }
        if (e.getSource() == registerButton) {
            register();
        }
        if (e.getSource() == goToRegisterButton) {
            loginPanel.setVisible(false);
            registerPanel.setVisible(true);
        }
        if (e.getSource() == goToLoginButton) {
            registerPanel.setVisible(false);
            loginPanel.setVisible(true);
        }
    }

    void login() {
        String username = loginUserField.getText();
        String password = new String(loginPassField.getPassword());

        try {
            Connection con = DB.connect();
            PreparedStatement ps = con.prepareStatement("SELECT name FROM users WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                con.close();
                new TypingTest(name); // open the typing test
                dispose(); // close this login window
            } else {
                con.close();
                JOptionPane.showMessageDialog(this, "Wrong username or password");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    void register() {
        String name = registerNameField.getText();
        String username = registerUserField.getText();
        String password = new String(registerPassField.getPassword());

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return;
        }

        try {
            Connection con = DB.connect();
            PreparedStatement ps = con.prepareStatement("INSERT INTO users VALUES (?, ?, ?)");
            ps.setString(1, username);
            ps.setString(2, name);
            ps.setString(3, password);
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Account created. Please log in.");
            registerNameField.setText("");
            registerUserField.setText("");
            registerPassField.setText("");
            loginUserField.setText(username); // fill in the new username to save typing
            registerPanel.setVisible(false);
            loginPanel.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "This username is already taken");
        }
    }

    // ----- helper methods: they make styled components (TypingTest uses them too)
    // -----

    public static JLabel makeLabel(String text, int size, int style, Color color, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", style, size));
        label.setForeground(color);
        label.setBounds(x, y, 500, size + 12);
        return label;
    }

    public static JTextField makeField(JTextField field, int x, int y, int width) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        field.setBounds(x, y, width, 40);
        return field;
    }

    public static JButton makeButton(String text, int x, int y, int width) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBounds(x, y, width, 44);
        return button;
    }

    // A button that looks like a blue text link
    public static JButton makeLink(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(BLUE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBounds(x, y, 320, 24);
        return button;
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        DB.createTables();
        new Login();
    }
}
