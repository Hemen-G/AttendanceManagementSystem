package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DerbyConnection {
    // Network Derby connection settings
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "1527";
    private static final String DB_NAME = "AttendanceSystemDB";
    private static final String DB_URL = "jdbc:derby://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + ";create=true";
    
    // Derby network server credentials
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load Derby network client driver
                Class.forName("org.apache.derby.jdbc.ClientDriver");
                
                // Create connection to network Derby with credentials
                connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                
                System.out.println("✓ Connected to Derby network database: " + DB_URL);
                System.out.println("✓ Using credentials: " + DB_USERNAME + "/" + DB_PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Derby network client driver not found!");
            System.err.println("  Please add derbyclient.jar to your classpath");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Failed to connect to Derby database!");
            System.err.println("  URL: " + DB_URL);
            System.err.println("  Error: " + e.getMessage());
            System.err.println("  SQL State: " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            
            if (e.getMessage().contains("Connection refused")) {
                System.err.println("  → SOLUTION: Start Derby Network Server first!");
                System.err.println("    Run: startNetworkServer (from Derby bin directory)");
            }
            e.printStackTrace();
        }
        return connection;
    }
    
    public static boolean testConnection() {
        try {
            Connection testConn = getConnection();
            if (testConn != null && !testConn.isClosed()) {
                System.out.println("✓ Database connection test successful!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Database connection test failed: " + e.getMessage());
        }
        return false;
    }
    
    public static boolean testConnectionWithDetails() {
        System.out.println("=== Derby Connection Test ===");
        System.out.println("Host: " + DB_HOST);
        System.out.println("Port: " + DB_PORT);
        System.out.println("Database: " + DB_NAME);
        System.out.println("URL: " + DB_URL);
        System.out.println("Username: " + DB_USERNAME);
        System.out.println("Password: " + DB_PASSWORD);
        System.out.println("=============================");
        
        try {
            Connection testConn = getConnection();
            if (testConn != null && !testConn.isClosed()) {
                System.out.println("✓ Database connection successful!");
                
                // Test if tables exist and show counts
                try (Statement stmt = testConn.createStatement()) {
                    // Test users table
                    try {
                        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                        if (rs.next()) {
                            System.out.println("✓ Users table: " + rs.getInt(1) + " records");
                        }
                    } catch (SQLException e) {
                        System.out.println("✗ Users table not accessible: " + e.getMessage());
                    }
                    
                    // Test students table
                    try {
                        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students");
                        if (rs.next()) {
                            System.out.println("✓ Students table: " + rs.getInt(1) + " records");
                        }
                    } catch (SQLException e) {
                        System.out.println("✗ Students table not accessible: " + e.getMessage());
                    }
                    
                    // Test attendance table
                    try {
                        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM attendance");
                        if (rs.next()) {
                            System.out.println("✓ Attendance table: " + rs.getInt(1) + " records");
                        }
                    } catch (SQLException e) {
                        System.out.println("✗ Attendance table not accessible: " + e.getMessage());
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed:");
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  SQL State: " + e.getSQLState());
            System.err.println("  Message: " + e.getMessage());
        }
        return false;
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Derby database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error closing connection: " + e.getMessage());
        }
    }
    
    // Main method to test the connection independently
    public static void main(String[] args) {
        System.out.println("=== Derby Connection Standalone Test ===");
        
        // Test the connection
        if (testConnectionWithDetails()) {
            System.out.println("\n🎉 SUCCESS: Database connection is working!");
            
            // Test basic operations
            try {
                Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                
                // Test query with proper column names (no role)
                ResultSet rs = stmt.executeQuery("SELECT id, username, role FROM users"); // Added role
                System.out.println("\n📋 Current users:");
                while (rs.next()) {
                    System.out.println("  - ID: " + rs.getInt("id") + ", Username: " + rs.getString("username") + ", Role: " + rs.getString("role"));
                }
                
                // Test students query
                rs = stmt.executeQuery("SELECT student_id, name, course, user_id, teacher_id FROM students"); // Added user_id, teacher_id
                System.out.println("\n📋 Current students:");
                while (rs.next()) {
                    System.out.println("  - " + rs.getString("student_id") + ": " + rs.getString("name") + " (" + rs.getString("course") + "), User ID: " + rs.getInt("user_id") + ", Teacher ID: " + rs.getInt("teacher_id"));
                }
                
            } catch (SQLException e) {
                System.err.println("✗ Error testing operations: " + e.getMessage());
                e.printStackTrace();
            }
            
        } else {
            System.out.println("\n❌ FAILED: Database connection is not working!");
        }
        
        closeConnection();
    }
}
