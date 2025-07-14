package gui;

import models.User;
import dao.AttendanceDAO;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import models.Attendance;

public class StudentAttendanceSummaryForm extends javax.swing.JFrame {
    private User currentUser;
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            loadAttendanceData();
        }
    }
    
    private void loadAttendanceData() {
        AttendanceDAO attendanceDAO = new AttendanceDAO();
        List<Attendance> attendanceList = attendanceDAO.getAttendanceByStudent(currentUser.getUsername());
        
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        
        for (Attendance attendance : attendanceList) {
            model.addRow(new Object[]{
                attendance.getDate(),
                attendance.getStatus()
            });
        }
    }
    
    public StudentAttendanceSummaryForm() {
        initComponents();
    }
    
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("My Attendance Summary");
        setSize(500, 400);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        // Title label
        JLabel titleLabel = new JLabel("My Attendance Summary", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        jTable1 = new JTable();
        jTable1.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Date", "Status"}
        ));
        JScrollPane scrollPane = new JScrollPane(jTable1);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Back button
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> {
            StudentDashboard dashboard = new StudentDashboard();
            dashboard.setCurrentUser(currentUser);
            dashboard.setVisible(true);
            dispose();
        });
        panel.add(backButton, BorderLayout.SOUTH);
        
        getContentPane().add(panel);
    }
    
    private javax.swing.JTable jTable1;
 public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StudentAttendanceSummaryForm().setVisible(true);
            }
        });
    }
}