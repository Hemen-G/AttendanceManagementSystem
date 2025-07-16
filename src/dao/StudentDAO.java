package dao;

import models.Student;
import utils.DerbyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    
    // Get all students, optionally filtered by teacherId
    public List<Student> getAllStudents(int teacherId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        if (teacherId > 0) {
            sql += " WHERE teacher_id = ?"; // Filter by teacher_id if provided
        }
        sql += " ORDER BY name";

        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (teacherId > 0) {
                stmt.setInt(1, teacherId);
            }

            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getString("student_id"));
                student.setName(rs.getString("name"));
                student.setCourse(rs.getString("course"));
                student.setSection(rs.getString("section"));

                try {
                    student.setYear(rs.getInt("academic_year"));
                } catch (SQLException e1) {
                    try {
                        student.setYear(rs.getInt("year"));
                    } catch (SQLException e2) {
                        student.setYear(1); // Default value
                    }
                }
                
                // Fetch userId and teacherId if columns exist
                try {
                    student.setUserId(rs.getInt("user_id"));
                } catch (SQLException e) { /* Column not found, ignore */ }
                try {
                    student.setTeacherId(rs.getInt("teacher_id"));
                } catch (SQLException e) { /* Column not found, ignore */ }

                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Get all students error: " + e.getMessage());
            e.printStackTrace();
        }

        return students;
    }
    
    // Overload for backward compatibility (returns all students if no teacherId is provided)
    public List<Student> getAllStudents() {
        return getAllStudents(0); // 0 indicates no teacher filter
    }

    // Get student by their associated user ID (for student logins)
    public Student getStudentByUserId(int userId) {
        String sql = "SELECT * FROM students WHERE user_id = ?";
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getString("student_id"));
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
                student.setUserId(rs.getInt("user_id"));
                try {
                    student.setTeacherId(rs.getInt("teacher_id"));
                } catch (SQLException e) { /* Column not found, ignore */ }
                return student;
            }
        } catch (SQLException e) {
            System.err.println("Get student by user ID error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Get student by their student ID
    public Student getStudentById(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getString("student_id"));
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
                // Fetch userId and teacherId if columns exist
                try {
                    student.setUserId(rs.getInt("user_id"));
                } catch (SQLException e) { /* Column not found, ignore */ }
                try {
                    student.setTeacherId(rs.getInt("teacher_id"));
                } catch (SQLException e) { /* Column not found, ignore */ }
                
                return student;
            }
        } catch (SQLException e) {
            System.err.println("Get student by ID error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Add a new student with optional user_id and teacher_id
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_id, name, course, section, academic_year, user_id, teacher_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, student.getStudentId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getCourse());
            stmt.setString(4, student.getSection());
            stmt.setInt(5, student.getYear());
            
            // Set user_id and teacher_id, handling 0 for null/not set
            if (student.getUserId() > 0) {
                stmt.setInt(6, student.getUserId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            if (student.getTeacherId() > 0) {
                stmt.setInt(7, student.getTeacherId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Fallback for schemas without user_id or teacher_id
            if (e.getSQLState().startsWith("42X") || e.getSQLState().startsWith("42Y")) { // Column not found or syntax error
                System.err.println("Attempting to add student without user_id/teacher_id columns. Error: " + e.getMessage());
                String fallbackSql = "INSERT INTO students (student_id, name, course, section, academic_year) VALUES (?, ?, ?, ?, ?)";
                try (Connection conn = DerbyConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(fallbackSql)) {
                    stmt.setString(1, student.getStudentId());
                    stmt.setString(2, student.getName());
                    stmt.setString(3, student.getCourse());
                    stmt.setString(4, student.getSection());
                    stmt.setInt(5, student.getYear());
                    return stmt.executeUpdate() > 0;
                } catch (SQLException e2) {
                    System.err.println("Fallback add student error: " + e2.getMessage());
                    e2.printStackTrace();
                }
            } else {
                System.err.println("Add student error: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    // Update an existing student, including user_id and teacher_id
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, course = ?, section = ?, academic_year = ?, user_id = ?, teacher_id = ? WHERE student_id = ?";
        
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getCourse());
            stmt.setString(3, student.getSection());
            stmt.setInt(4, student.getYear());
            
            if (student.getUserId() > 0) {
                stmt.setInt(5, student.getUserId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            if (student.getTeacherId() > 0) {
                stmt.setInt(6, student.getTeacherId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            stmt.setString(7, student.getStudentId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Fallback for schemas without user_id or teacher_id
            if (e.getSQLState().startsWith("42X") || e.getSQLState().startsWith("42Y")) {
                System.err.println("Attempting to update student without user_id/teacher_id columns. Error: " + e.getMessage());
                String fallbackSql = "UPDATE students SET name = ?, course = ?, section = ?, academic_year = ? WHERE student_id = ?";
                try (Connection conn = DerbyConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(fallbackSql)) {
                    stmt.setString(1, student.getName());
                    stmt.setString(2, student.getCourse());
                    stmt.setString(3, student.getSection());
                    stmt.setInt(4, student.getYear());
                    stmt.setString(5, student.getStudentId());
                    return stmt.executeUpdate() > 0;
                } catch (SQLException e2) {
                    System.err.println("Fallback update student error: " + e2.getMessage());
                    e2.printStackTrace();
                }
            } else {
                System.err.println("Update student error: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean deleteStudent(String studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete student error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
