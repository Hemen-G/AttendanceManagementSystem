package models;

public class Student {
    private String studentId;  // Changed from int to String
    private String name;
    private String course;
    private int year;
    private String section;
    
    public Student() {}
    
    public Student(String studentId, String name, String course, int year, String section) {
        this.studentId = studentId;
        this.name = name;
        this.course = course;
        this.year = year;
        this.section = section;
    }
    
    // Getters and Setters
    public String getStudentId() { return studentId; }  // Changed return type
    public void setStudentId(String studentId) { this.studentId = studentId; }  // Changed parameter type
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    
    @Override
    public String toString() {
        return studentId + " - " + name + " (" + course + " Year " + year + " Section " + section + ")";
    }
}