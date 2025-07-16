package gui;

import models.User;
import models.Student;
import models.Attendance;
import dao.AttendanceDAO;
import dao.StudentDAO; // Import StudentDAO
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentAttendanceSummaryForm extends javax.swing.JFrame {
    private User currentUser;
    private Student currentStudent; // The specific student whose attendance is being viewed
    private AttendanceDAO attendanceDAO;

    public StudentAttendanceSummaryForm() {
        initComponents();
        setLocationRelativeTo(null); // Center the form
        attendanceDAO = new AttendanceDAO();
    }

    // Method to set the current user (from session)
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // Method to set the specific student whose attendance is to be displayed
    public void setStudent(Student student) {
        this.currentStudent = student;
        loadAttendanceSummary();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); // Changed to DISPOSE_ON_CLOSE
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
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        });
        JScrollPane scrollPane = new JScrollPane(jTable1);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> {
            try {
                StudentDashboard dashboard = new StudentDashboard();
                dashboard.setCurrentUser(currentUser); // Pass the current user back
                dashboard.setVisible(true);
                dispose();
            } catch (SQLException ex) {
                Logger.getLogger(StudentAttendanceSummaryForm.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error returning to dashboard: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(backButton, BorderLayout.SOUTH);

        getContentPane().add(panel);
    }

    private void loadAttendanceSummary() {
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "Student information not available.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Clear existing data

        try {
            // Fetch attendance for the specific student
            List<Attendance> studentAttendance = attendanceDAO.getAttendanceByStudent(currentStudent.getStudentId());

            if (studentAttendance.isEmpty()) {
                model.addRow(new Object[]{"No records found", ""});
            } else {
                for (Attendance attendance : studentAttendance) {
                    model.addRow(new Object[]{attendance.getDate().toString(), attendance.getStatus()});
                }
            }
            
            // Display attendance percentage
            double percentage = attendanceDAO.getAttendancePercentage(currentStudent.getStudentId());
            JLabel percentageLabel = new JLabel(String.format("Overall Attendance: %.2f%%", percentage), JLabel.CENTER);
            percentageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            // Add this label to the panel, perhaps in a new sub-panel or at the bottom
            // For simplicity, let's add it to the NORTH of the main panel, below the title
            // You might need a more sophisticated layout for this.
            // For now, let's just show it in a dialog or update the title.
            // A better approach would be to add a dedicated JLabel to the form's layout.
            // For demonstration, let's update the title or show a message.
            setTitle(String.format("My Attendance Summary (%.2f%%)", percentage));

        } catch (Exception e) {
            Logger.getLogger(StudentAttendanceSummaryForm.class.getName()).log(Level.SEVERE, "Error loading student attendance summary", e);
            JOptionPane.showMessageDialog(this, "Error loading attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private javax.swing.JTable jTable1;

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // For testing, you might need to set a dummy user and student
                StudentAttendanceSummaryForm form = new StudentAttendanceSummaryForm();
                // User dummyUser = new User(); dummyUser.setId(1); dummyUser.setUsername("teststudent"); dummyUser.setRole("Student");
                // Student dummyStudent = new Student("S001", "Test Student", "CS", 2, "A"); dummyStudent.setUserId(1);
                // form.setCurrentUser(dummyUser);
                // form.setStudent(dummyStudent);
                form.setVisible(true);
            }
        });
    }
}
