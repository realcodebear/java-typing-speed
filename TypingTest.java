import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class TypingTest extends JFrame implements ActionListener, KeyListener {

    String[] sentences = {
        "Java is a popular language used to build apps for phones, computers and servers.",
        "Swing gives Java programs windows, buttons, text boxes and tables.",
        "A good programmer writes code that other people can read easily.",
        "JDBC lets a Java program save data in a database that stays after the app closes.",
        "Practice every day and your typing speed will grow faster than you expect.",
        "The quick brown fox jumps over the lazy dog near the river bank."
    };

    String playerName;
    String sentence = "";
    long startTime;
    int wpm;
    int accuracy;

    JTextArea sentenceArea;
    JTextArea typingArea;
    JLabel timeLabel;
    JLabel speedLabel;
    JLabel accuracyLabel;
    JButton startButton;
    JButton logoutButton;
    DefaultTableModel model;
    Timer timer;

    TypingTest(String name) {
        playerName = name;

        setTitle("Typing Speed Test");
        setSize(1000, 600);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Login.BACKGROUND);

        // ----- top: indigo header band with title, player name, logout, start -----
        JPanel header = new JPanel(null);
        header.setBackground(Login.BLUE);
        header.setBounds(0, 0, 1000, 84);
        add(header);

        header.add(Login.makeLabel("Typing Speed Test", 26, Font.BOLD, Color.WHITE, 30, 14));
        header.add(Login.makeLabel("Press Start and type the sentence as fast as you can", 14, Font.PLAIN, new Color(199, 210, 254), 30, 50));
        header.add(Login.makeLabel("Player: " + playerName, 15, Font.BOLD, Color.WHITE, 650, 18));

        logoutButton = Login.makeLink("Logout", 650, 46);
        logoutButton.setSize(60, 24);   // only as wide as the word, so it does not cover the Start button
        logoutButton.setForeground(new Color(199, 210, 254));
        logoutButton.addActionListener(this);
        header.add(logoutButton);

        startButton = Login.makeButton("Start test", 840, 20, 130);
        startButton.setBackground(Color.WHITE);   // white button on the indigo header
        startButton.setForeground(Login.BLUE);
        startButton.addActionListener(this);
        header.add(startButton);

        // ----- left white box: sentence, typing area, results -----
        JPanel testPanel = new JPanel(null);
        testPanel.setBackground(Color.WHITE);
        testPanel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        testPanel.setBounds(30, 104, 600, 440);
        add(testPanel);

        testPanel.add(Login.makeLabel("TYPE THIS", 12, Font.BOLD, Login.GREY, 25, 18));
        sentenceArea = new JTextArea("Press Start to get a sentence.");
        styleTextArea(sentenceArea);
        sentenceArea.setBackground(Login.SOFT);
        sentenceArea.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        sentenceArea.setEditable(false);
        sentenceArea.setBounds(25, 42, 550, 110);
        testPanel.add(sentenceArea);

        testPanel.add(Login.makeLabel("YOUR TYPING", 12, Font.BOLD, Login.GREY, 25, 170));
        typingArea = new JTextArea();
        styleTextArea(typingArea);
        typingArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Login.BLUE, 2),        // indigo frame around the typing box
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        typingArea.setEditable(false);
        typingArea.setBounds(25, 194, 550, 110);
        typingArea.addKeyListener(this);
        testPanel.add(typingArea);

        // three coloured result boxes
        timeLabel = makeResultBox(testPanel, "TIME", "0s", 25, new Color(241, 245, 249), Login.DARK);
        speedLabel = makeResultBox(testPanel, "SPEED", "0 WPM", 215, Login.SOFT, Login.BLUE);
        accuracyLabel = makeResultBox(testPanel, "ACCURACY", "0%", 405, new Color(220, 252, 231), Login.GREEN);

        // ----- right white box: leaderboard -----
        JPanel boardPanel = new JPanel(null);
        boardPanel.setBackground(Color.WHITE);
        boardPanel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        boardPanel.setBounds(650, 104, 320, 440);
        add(boardPanel);

        boardPanel.add(Login.makeLabel("Leaderboard", 20, Font.BOLD, Login.DARK, 20, 18));
        boardPanel.add(Login.makeLabel("Top 10 fastest typists", 13, Font.PLAIN, Login.GREY, 20, 48));

        String[] columns = { "#", "Name", "WPM", "Accuracy" };
        int[] widths = { 40, 120, 60, 80 };   // width of each column in pixels (total 300)
        model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(31);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setShowGrid(false);                         // no lines between cells
        table.setIntercellSpacing(new Dimension(0, 0));   // no gaps between cells
        table.setDefaultRenderer(Object.class, new ScoreRenderer());   // our colours (class at the bottom)
        table.setDefaultEditor(Object.class, null);   // the user cannot edit the cells
        table.setTableHeader(null);                   // hide Java's grey header, we make our own below

        // our own header strip: light indigo with indigo column names
        JPanel tableHeader = new JPanel(null);
        tableHeader.setBackground(Login.SOFT);
        tableHeader.setBounds(10, 80, 300, 32);
        boardPanel.add(tableHeader);

        int x = 0;
        for (int i = 0; i < columns.length; i++) {
            JLabel heading = Login.makeLabel(columns[i], 12, Font.BOLD, Login.BLUE, x, 6);
            heading.setSize(widths[i], 20);
            if (i == 1) {
                heading.setLocation(x + 10, 6);   // the name column is on the left, with a little space
            } else {
                heading.setHorizontalAlignment(SwingConstants.CENTER);
            }
            tableHeader.add(heading);
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);   // same width as the heading
            x = x + widths[i];
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBounds(10, 114, 300, 312);
        boardPanel.add(scrollPane);

        // timer that calls actionPerformed every 1000 milliseconds (1 second)
        timer = new Timer(1000, this);

        loadScores();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Makes one coloured box with a small title and a big number, and returns the number label
    JLabel makeResultBox(JPanel parent, String title, String startText, int x, Color background, Color numberColor) {
        JPanel box = new JPanel(null);
        box.setBackground(background);
        box.setBounds(x, 322, 170, 96);
        parent.add(box);

        box.add(Login.makeLabel(title, 12, Font.BOLD, Login.GREY, 16, 12));
        JLabel number = Login.makeLabel(startText, 32, Font.BOLD, numberColor, 16, 38);
        box.add(number);
        return number;
    }

    // Big font, line wrapping and inner padding for a text area
    void styleTextArea(JTextArea area) {
        area.setFont(new Font("SansSerif", Font.PLAIN, 22));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
    }

    // Runs when Start or Logout is clicked, and every second from the timer
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            startTest();
        }
        if (e.getSource() == logoutButton) {
            timer.stop();
            new Login();
            dispose();
        }
        if (e.getSource() == timer) {
            updateResults();
        }
    }

    void startTest() {
        // pick a random sentence: Math.random() gives a number from 0.0 to 0.99
        int index = (int) (Math.random() * sentences.length);
        sentence = sentences[index];

        sentenceArea.setText(sentence);
        timeLabel.setText("0s");
        speedLabel.setText("0 WPM");
        accuracyLabel.setText("0%");
        typingArea.setText("");
        typingArea.setForeground(Login.DARK);
        typingArea.setEditable(true);
        typingArea.requestFocus();

        startTime = System.currentTimeMillis();
        timer.start();
    }

    // Works out the time, speed and accuracy and shows them on screen
    void updateResults() {
        String typed = typingArea.getText();

        long timeTaken = System.currentTimeMillis() - startTime;   // in milliseconds
        double seconds = timeTaken / 1000.0;
        double minutes = seconds / 60.0;

        // speed: 1 word = 5 letters
        double words = typed.length() / 5.0;
        wpm = (int) (words / minutes);

        // accuracy: how many letters match the sentence
        int correct = 0;
        for (int i = 0; i < typed.length(); i++) {
            if (i < sentence.length()) {
                if (typed.charAt(i) == sentence.charAt(i)) {
                    correct = correct + 1;
                }
            }
        }
        if (typed.length() == 0) {
            accuracy = 0;
        } else {
            accuracy = correct * 100 / typed.length();
        }

        timeLabel.setText((int) seconds + "s");
        speedLabel.setText(wpm + " WPM");
        accuracyLabel.setText(accuracy + "%");
    }

    // Runs after every key the user types
    public void keyReleased(KeyEvent e) {
        if (timer.isRunning() == false) {
            return;
        }

        String typed = typingArea.getText();

        // red text if there is a mistake, dark text if everything is right so far
        if (sentence.startsWith(typed)) {
            typingArea.setForeground(Login.DARK);
        } else {
            typingArea.setForeground(Login.RED);
        }

        // the test ends when the user has typed as many letters as the sentence
        if (typed.length() >= sentence.length()) {
            finishTest();
        }
    }

    // Stop Enter and Tab from adding a new line or tab in the typing box
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB) {
            e.consume();
        }
    }

    // KeyListener needs this method too, but we don't use it
    public void keyTyped(KeyEvent e) {
    }

    void finishTest() {
        timer.stop();
        updateResults();
        typingArea.setEditable(false);

        // save the score in the database
        try {
            Connection con = DB.connect();
            PreparedStatement ps = con.prepareStatement("INSERT INTO scores VALUES (?, ?, ?)");
            ps.setString(1, playerName);
            ps.setInt(2, wpm);
            ps.setInt(3, accuracy);
            ps.executeUpdate();
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        loadScores();
        JOptionPane.showMessageDialog(this, "Speed: " + wpm + " WPM\nAccuracy: " + accuracy + "%");
    }

    // Reads the 10 best scores from the database and shows them in the table
    void loadScores() {
        model.setRowCount(0);   // empty the table first
        try {
            Connection con = DB.connect();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM scores ORDER BY wpm DESC LIMIT 10");
            int rank = 1;
            while (rs.next()) {
                String name = rs.getString("name");
                int speed = rs.getInt("wpm");
                String acc = rs.getInt("accuracy") + "%";
                Object[] row = { rank, name, speed, acc };
                model.addRow(row);
                rank = rank + 1;
            }
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}

// Decides how each cell of the leaderboard looks:
// gold, silver and bronze rows for the top 3, light stripes for the rest
class ScoreRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        // first let the normal renderer put the text in the cell
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // background colour of the row
        if (isSelected) {
            setBackground(Login.SOFT);                    // the row you clicked
        } else if (row == 0) {
            setBackground(new Color(254, 243, 199));      // 1st place: gold
        } else if (row == 1) {
            setBackground(new Color(226, 232, 240));      // 2nd place: silver
        } else if (row == 2) {
            setBackground(new Color(254, 215, 170));      // 3rd place: bronze
        } else if (row % 2 == 0) {
            setBackground(Color.WHITE);                   // even rows: white
        } else {
            setBackground(new Color(248, 250, 252));      // odd rows: very light grey
        }
        setForeground(Login.DARK);

        // the name is on the left, everything else in the middle
        if (column == 1) {
            setHorizontalAlignment(SwingConstants.LEFT);
        } else {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        // rank (#) and speed (WPM) in bold
        if (column == 0 || column == 2) {
            setFont(getFont().deriveFont(Font.BOLD));
        }

        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));   // space inside each cell
        return this;
    }
}
