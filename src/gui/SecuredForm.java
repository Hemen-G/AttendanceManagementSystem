package gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import utils.SessionManager;

public abstract class SecuredForm extends JFrame {
    
    public SecuredForm(String requiredRole) {
        // Check if user is logged in
        if (!SessionManager.isLoggedIn()) {
            showAccessDenied();
            return;
        }
        
        // Check if user has required role
        if (requiredRole != null && !SessionManager.hasRole(requiredRole)) {
            showAccessDenied();
            return;
        }
    }
    
    private void showAccessDenied() {
        JOptionPane.showMessageDialog(this, 
            "You don't have permission to access this page",
            "Access Denied", 
            JOptionPane.ERROR_MESSAGE);
        new LoginForm().setVisible(true);
        dispose();
    }
    
    // Common initialization for all secured forms
    protected void initialize() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}