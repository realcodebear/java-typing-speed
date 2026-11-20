import java.sql.*;

public class DB {

    // Connects to the database file scores.db (it is created if it does not exist)
    public static Connection connect() throws SQLException {
        // Step 1 of JDBC: load the SQLite driver class from the jar in the lib folder
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite driver jar not found. Run the app with run.bat, or with: "
                    + "java -cp \".;lib\\sqlite-jdbc-3.47.1.0.jar\" Login");
        }

        // Step 2: open the connection
        return DriverManager.getConnection("jdbc:sqlite:scores.db");
    }

    // Makes the two tables the first time the app runs
    public static void createTables() {
        try {
            Connection con = connect();
            Statement st = con.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, name TEXT, password TEXT)");
            st.execute("CREATE TABLE IF NOT EXISTS scores (name TEXT, wpm INTEGER, accuracy INTEGER)");
            con.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
