import java.sql.*;

public class DB {

    // Connects to the database file scores.db (it is created if it does not exist)
    public static Connection connect() throws SQLException {
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
