package gui;

import models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentDashboard extends javax.swing.JFrame {
    private User currentUser;
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            lblWelcome.setText("Welcome, Student " + user.getUsername());
        }
    }
    
    public StudentDashboard() {
        initComponents();
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
                StudentAttendanceSummaryForm summaryForm = new StudentAttendanceSummaryForm();
                summaryForm.setCurrentUser(currentUser);
                summaryForm.setVisible(true);
                dispose();
            }
        });
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new WelcomeDashboard().setVisible(true);
                dispose();
            }
        });
        
        buttonPanel.add(viewAttendanceBtn);
        buttonPanel.add(logoutBtn);
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        getContentPane().add(panel);
    }
    
    private javax.swing.JLabel lblWelcome;

 public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StudentDashboard().setVisible(true);
            }
        });
    }
}