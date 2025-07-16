package gui;

import dao.StudentDAO;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Student;
import utils.SessionManager;

public class StudentDashboard extends javax.swing.JFrame {
    private User currentUser;
    private Student currentStudent; // To store the student object linked to the user

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            lblWelcome.setText("Welcome, " + user.getUsername());
            loadStudentData(); // Load student-specific data after user is set
        }
    }

    public StudentDashboard() throws SQLException {
        // Check session and role immediately in constructor
        if (!SessionManager.isLoggedIn() || !SessionManager.hasRole("Student")) {
            JOptionPane.showMessageDialog(this,
                                         "Access denied - Student privileges required or not logged in.",
                                         "Authorization Error",
                                         JOptionPane.ERROR_MESSAGE);
            new LoginForm().setVisible(true);
            throw new SQLException("Access Denied"); // Throw to prevent further initialization
        }
        initComponents();
        setLocationRelativeTo(null); // Center the form
        // setCurrentUser will be called from LoginForm after successful login
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Student Dashboard");
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Welcome label
        lblWelcome = new JLabel("Welcome, Student", JLabel.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(lblWelcome, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        JButton viewAttendanceBtn = new JButton("View My Attendance");
        viewAttendanceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentUser != null && currentStudent != null) {
                    StudentAttendanceSummaryForm summaryForm = new StudentAttendanceSummaryForm();
                    summaryForm.setCurrentUser(currentUser); // Pass the user
                    summaryForm.setStudent(currentStudent); // Pass the student
                    summaryForm.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Student data not loaded. Please try logging in again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null,
                                                         "Are you sure you want to logout?",
                                                         "Confirm Logout",
                                                         JOptionPane.YES_NO_OPTION,
                                                         JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    SessionManager.logout(); // Clear session
                    new LoginForm().setVisible(true);
                    dispose();
                }
            }
        });

        buttonPanel.add(viewAttendanceBtn);
        buttonPanel.add(logoutBtn);

        panel.add(buttonPanel, BorderLayout.CENTER);

        getContentPane().add(panel);
    }

    private void loadStudentData() {
        if (currentUser != null) {
            StudentDAO studentDAO = new StudentDAO();
            currentStudent = studentDAO.getStudentByUserId(currentUser.getId());
            if (currentStudent == null) {
                JOptionPane.showMessageDialog(this, "No student record found for this user. Please contact admin.", "Data Error", JOptionPane.ERROR_MESSAGE);
                // Optionally, log out or disable features if student data isn't found
                SessionManager.logout();
                new LoginForm().setVisible(true);
                this.dispose();
            }
        }
    }

    private javax.swing.JLabel lblWelcome;

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // For testing, you might temporarily set a dummy user or ensure login flow
                // new StudentDashboard().setVisible(true);
                // It's better to always go through LoginForm for proper session management
                new LoginForm().setVisible(true);
            }
        });
    }
}
