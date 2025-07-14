package models;

import java.time.LocalDate;

public class Attendance {
    private int id;
    private String studentId;  // Changed from int to String
    private LocalDate date;
    private String status;
    private String studentName; // For display purposes
    
    public Attendance() {}
    
    public Attendance(String studentId, LocalDate date, String status) {  // Changed parameter type
        this.studentId = studentId;
        this.date = date;
        this.status = status;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getStudentId() { return studentId; }  // Changed return type
    public void setStudentId(String studentId) { this.studentId = studentId; }  // Changed parameter type
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    @Override
    public String toString() {
        return studentName + " - " + date + " - " + status;
    }
}