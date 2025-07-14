package dao;

import models.Attendance;
import utils.DerbyConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    
    public boolean markAttendance(String studentId, LocalDate date, String status) {  // Changed to String
        // First try to update existing record
        String updateSql = "UPDATE attendance SET status = ? WHERE student_id = ? AND date = ?";
        
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            
            updateStmt.setString(1, status);
            updateStmt.setString(2, studentId);  // Changed to setString
            updateStmt.setDate(3, Date.valueOf(date));
            
            int rowsUpdated = updateStmt.executeUpdate();
            
            if (rowsUpdated == 0) {
                // No existing record, insert new one
                String insertSql = "INSERT INTO attendance (student_id, date, status) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, studentId);  // Changed to setString
                    insertStmt.setDate(2, Date.valueOf(date));
                    insertStmt.setString(3, status);
                    
                    return insertStmt.executeUpdate() > 0;
                }
            }
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("Mark attendance error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.*, s.name FROM attendance a " +
                    "JOIN students s ON a.student_id = s.student_id " +
                    "WHERE a.date = ? ORDER BY s.name";
        
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(rs.getInt("id"));
                attendance.setStudentId(rs.getString("student_id"));  // Changed to getString
                attendance.setDate(rs.getDate("date").toLocalDate());
                attendance.setStatus(rs.getString("status"));
                attendance.setStudentName(rs.getString("name"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            System.err.println("Get attendance by date error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return attendanceList;
    }
    
    public List<Attendance> getAttendanceByStudent(String studentId) {  // Changed to String
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.*, s.name FROM attendance a " +
                    "JOIN students s ON a.student_id = s.student_id " +
                    "WHERE a.student_id = ? ORDER BY a.date DESC";
        
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);  // Changed to setString
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(rs.getInt("id"));
                attendance.setStudentId(rs.getString("student_id"));  // Changed to getString
                attendance.setDate(rs.getDate("date").toLocalDate());
                attendance.setStatus(rs.getString("status"));
                attendance.setStudentName(rs.getString("name"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            System.err.println("Get attendance by student error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return attendanceList;
    }
    
    public double getAttendancePercentage(String studentId) {  // Changed to String
        String sql = "SELECT COUNT(*) as total, " +
                    "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) as present " +
                    "FROM attendance WHERE student_id = ?";
        
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);  // Changed to setString
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total");
                int present = rs.getInt("present");
                
                if (total > 0) {
                    return (double) present / total * 100;
                }
            }
        } catch (SQLException e) {
            System.err.println("Get attendance percentage error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    public List<Attendance> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.*, s.name FROM attendance a " +
                    "JOIN students s ON a.student_id = s.student_id " +
                    "WHERE a.date BETWEEN ? AND ? " +
                    "ORDER BY a.date DESC, s.name";
        
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(rs.getInt("id"));
                attendance.setStudentId(rs.getString("student_id"));  // Changed to getString
                attendance.setDate(rs.getDate("date").toLocalDate());
                attendance.setStatus(rs.getString("status"));
                attendance.setStudentName(rs.getString("name"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            System.err.println("Get attendance by date range error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return attendanceList;
    }
}