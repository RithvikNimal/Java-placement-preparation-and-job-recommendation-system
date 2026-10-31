package com.placement.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton JDBC connection helper for the Placement System.
 * Change USER / PASSWORD to match your local MySQL setup.
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/placement_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = "root"; // <-- change this

    private static Connection connection = null;

    // Private constructor – utility class
    private DBConnection() {}

    /**
     * Returns the singleton Connection, opening it if necessary.
     *
     * @return active {@link Connection}
     * @throws SQLException if the driver is absent or the DB is unreachable
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException(
                    "MySQL JDBC Driver not found. Place mysql-connector-java.jar in the lib/ folder.", e);
            }
        }
        return connection;
    }

    /**
     * Closes the singleton connection gracefully.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
