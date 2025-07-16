/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author HG
 */

package utils;
import models.User;

public class SessionManager {
    private static User currentUser;
    private static long lastActivity;
    private static final long TIMEOUT = 30 * 60 * 1000; // 30 minutes
    
    public static void updateLastActivity() {
        lastActivity = System.currentTimeMillis();
    }
    
    public static boolean isSessionValid() {
        // Check if logged in and session hasn't timed out
        return isLoggedIn() && (System.currentTimeMillis() - lastActivity) < TIMEOUT;
    }
    
    public static void setCurrentUser(User user) {
        currentUser = user;
        updateLastActivity(); // Update activity on login
    }
    
    public static User getCurrentUser() {
        // Optionally, check session validity here before returning user
        if (isSessionValid()) {
            updateLastActivity(); // Keep session alive if accessed
            return currentUser;
        }
        logout(); // If session invalid, log out
        return null;
    }
    
    public static void logout() {
        currentUser = null;
        lastActivity = 0; // Reset activity time
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static boolean hasRole(String role) {
        return isLoggedIn() && role.equals(currentUser.getRole());
    }
}
