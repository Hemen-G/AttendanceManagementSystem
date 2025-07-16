package models;

public class Student {
    private String studentId;
    private String name;
    private String course;
    private int year;
    private String section;
    private int userId; // Links to User.id if this student has a login account
    private int teacherId; // Links to User.id of the teacher managing this student
    
    public Student() {}
    
    public Student(String studentId, String name, String course, int year, String section) {
        this.studentId = studentId;
        this.name = name;
        this.course = course;
        this.year = year;
        this.section = section;
        // Default to 0 for unlinked user/teacher
        this.userId = 0; 
        this.teacherId = 0;
    }

    public Student(String studentId, String name, String course, int year, String section, int userId, int teacherId) {
        this.studentId = studentId;
        this.name = name;
        this.course = course;
        this.year = year;
        this.section = section;
        this.userId = userId;
        this.teacherId = teacherId;
    }
    
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }
    
    @Override
    public String toString() {
        return studentId + " - " + name + " (" + course + " Year " + year + " Section " + section + ")";
    }
}
