package models;

import java.time.LocalDate;

public class Attendance {
   private int id;
    private String studentId;
    private String courseId; // This field is in the model but not actively used in DAOs or forms for filtering
    private LocalDate date;
    private String status;
    private int recordedBy; // User ID of the teacher who recorded it
    private String studentName; // For display purposes
    
    public Attendance() {}
    
    public Attendance(String studentId, LocalDate date, String status, int recordedBy, String courseId) {
        this.studentId = studentId;
        this.date = date;
        this.status = status;
        this.recordedBy = recordedBy;
        this.courseId = courseId;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getRecordedBy() { return recordedBy; }
    public void setRecordedBy(int recordedBy) { this.recordedBy = recordedBy; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    @Override
    public String toString() {
        return studentName + " - " + date + " - " + status;
    }
}
