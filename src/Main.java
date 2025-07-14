
import gui.WelcomeDashboard;
import utils.DerbyConnection;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

// In Main.java
public class Main {
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.out.println("Nimbus look and feel not available, using default");
        }
        
        // Test database connection with detailed output
        System.out.println("=== Database Connection Test ===");
        if (!DerbyConnection.testConnectionWithDetails()) {
            JOptionPane.showMessageDialog(null, 
                "Cannot connect to database!\n\n" +
                "Please check:\n" +
                "1. Derby server is running (run startNetworkServer)\n" +
                "2. Database 'AttendanceSystemDB' exists\n" +
                "3. Port 1527 is available\n" +
                "4. Derby client driver is in classpath\n\n" +
                "Check console for detailed error information.", 
                "Database Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            
            int choice = JOptionPane.showConfirmDialog(null,
                "Do you want to continue without database connection?\n" +
                "(Application may not work properly)",
                "Continue?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (choice != JOptionPane.YES_OPTION) {
                System.exit(1);
            }
        }
        
        // Start with welcome dashboard
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WelcomeDashboard().setVisible(true);
            }
        });
    }
}