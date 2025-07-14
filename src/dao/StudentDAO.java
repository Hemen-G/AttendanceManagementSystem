package dao;

import models.Student;
import utils.DerbyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY name";
        
        try (Connection conn = DerbyConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("Executing query: " + sql);
            
            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getString("student_id"));  // Changed to getString
                student.setName(rs.getString("name"));
                student.setCourse(rs.getString("course"));
                student.setSection(rs.getString("section"));
                
                // Try different possible column names for year
                try {
                    student.setYear(rs.getInt("academic_year"));
                } catch (SQLException e1) {
                    try {
                        student.setYear(rs.getInt("year"));
                    } catch (SQLException e2) {
                        student.setYear(1); // Default value
                    }
                }
                
                students.add(student);
                System.out.println("Loaded student: " + student.getName());
            }
            
            System.out.println("Total students loaded: " + students.size());
            
        } catch (SQLException e) {
            System.err.println("Get all students error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return students;
    }
    
    public boolean addStudent(Student student) {
        String[] possibleSqls = {
            "INSERT INTO students (student_id, name, course, section, academic_year) VALUES (?, ?, ?, ?, ?)",
            "INSERT INTO students (student_id, name, course, section, year) VALUES (?, ?, ?, ?, ?)",
            "INSERT INTO students (student_id, name, course, section) VALUES (?, ?, ?, ?)"
        };
        
        System.out.println("Adding student: " + student.getStudentId() + ", " + student.getName() + ", " + 
                          student.getCourse() + ", " + student.getSection() + ", " + student.getYear());
        
        for (String sql : possibleSqls) {
            try (Connection conn = DerbyConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                System.out.println("Trying SQL: " + sql);
                
                stmt.setString(1, student.getStudentId());  // Changed to setString
                stmt.setString(2, student.getName());
                stmt.setString(3, student.getCourse());
                stmt.setString(4, student.getSection());
                
                if (sql.contains("year")) {
                    stmt.setInt(5, student.getYear());
                }
                
                int result = stmt.executeUpdate();
                System.out.println("Insert result: " + result);
                
                if (result > 0) {
                    return true;
                }
                
            } catch (SQLException e) {
                System.err.println("Failed with SQL: " + sql);
                System.err.println("Error: " + e.getMessage());
            }
        }
        
        return false;
    }
    
    public boolean updateStudent(Student student) {
        String[] possibleSqls = {
            "UPDATE students SET name = ?, course = ?, section = ?, academic_year = ? WHERE student_id = ?",
            "UPDATE students SET name = ?, course = ?, section = ?, year = ? WHERE student_id = ?",
            "UPDATE students SET name = ?, course = ?, section = ? WHERE student_id = ?"
        };
        
        for (String sql : possibleSqls) {
            try (Connection conn = DerbyConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, student.getName());
                stmt.setString(2, student.getCourse());
                stmt.setString(3, student.getSection());
                
                if (sql.contains("year")) {
                    stmt.setInt(4, student.getYear());
                    stmt.setString(5, student.getStudentId());  // Changed to setString
                } else {
                    stmt.setString(4, student.getStudentId());  // Changed to setString
                }
                
                int result = stmt.executeUpdate();
                if (result > 0) {
                    return true;
                }
                
            } catch (SQLException e) {
                System.err.println("Update failed with SQL: " + sql);
                System.err.println("Error: " + e.getMessage());
            }
        }
        
        return false;
    }
    
    public boolean deleteStudent(String studentId) {  // Changed parameter type
        String sql = "DELETE FROM students WHERE student_id = ?";
        
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);  // Changed to setString
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete student error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Student getStudentById(String studentId) {  // Changed parameter type
        String sql = "SELECT * FROM students WHERE student_id = ?";
        
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);  // Changed to setString
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getString("student_id"));  // Changed to getString
                student.setName(rs.getString("name"));
                student.setCourse(rs.getString("course"));
                student.setSection(rs.getString("section"));
                
                try {
                    student.setYear(rs.getInt("academic_year"));
                } catch (SQLException e1) {
                    try {
                        student.setYear(rs.getInt("year"));
                    } catch (SQLException e2) {
                        student.setYear(1);
                    }
                }
                
                return student;
            }
        } catch (SQLException e) {
            System.err.println("Get student by ID error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<String> getAllCourses() {
        List<String> courses = new ArrayList<>();
        String sql = "SELECT DISTINCT course FROM students ORDER BY course";
        
        try (Connection conn = DerbyConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courses.add(rs.getString("course"));
            }
        } catch (SQLException e) {
            System.err.println("Get courses error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return courses;
    }
}