package dao;

import models.Attendance;
import utils.DerbyConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttendanceDAO {
    private StudentDAO studentDAO = new StudentDAO(); // To get students managed by a teacher

    public boolean markAttendance(String studentId, LocalDate date, String status, int recordedBy) {
        // First try to update existing record
        String updateSql = "UPDATE attendance SET status = ?, recorded_by = ? WHERE student_id = ? AND date = ?";
        
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            
            updateStmt.setString(1, status);
            if (recordedBy > 0) {
                updateStmt.setInt(2, recordedBy);
            } else {
                updateStmt.setNull(2, java.sql.Types.INTEGER);
            }
            updateStmt.setString(3, studentId);
            updateStmt.setDate(4, Date.valueOf(date));
            
            int rowsUpdated = updateStmt.executeUpdate();
            
            if (rowsUpdated == 0) {
                // No existing record, insert new one
                String insertSql = "INSERT INTO attendance (student_id, date, status, recorded_by) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, studentId);
                    insertStmt.setDate(2, Date.valueOf(date));
                    insertStmt.setString(3, status);
                    if (recordedBy > 0) {
                        insertStmt.setInt(4, recordedBy);
                    } else {
                        insertStmt.setNull(4, java.sql.Types.INTEGER);
                    }
                    
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

    // This method is already good for teacher's overall attendance
    public List<Attendance> getAttendanceByTeacher(int teacherId) throws SQLException {
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT a.*, s.name FROM attendance a " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "WHERE s.teacher_id = ? " + // Assuming students table has teacher_id
                     "ORDER BY a.date DESC";
        
        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(rs.getInt("id"));
                attendance.setStudentId(rs.getString("student_id"));
                attendance.setDate(rs.getDate("date").toLocalDate());
                attendance.setStatus(rs.getString("status"));
                attendance.setStudentName(rs.getString("name"));
                try {
                    attendance.setRecordedBy(rs.getInt("recorded_by"));
                } catch (SQLException e) { /* Column not found, ignore */ }
                attendances.add(attendance);
            }
        }
        return attendances;
    }

    // Helper to get student IDs for a given teacher
    private List<String> getStudentIdsForTeacher(int teacherId) {
        return studentDAO.getAllStudents(teacherId).stream()
                         .map(models.Student::getStudentId)
                         .collect(Collectors.toList());
    }

    public List<Attendance> getAttendanceByDate(LocalDate date, int teacherId) {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.*, s.name FROM attendance a " +
                    "JOIN students s ON a.student_id = s.student_id " +
                    "WHERE a.date = ?";
        
        List<String> studentIds = getStudentIdsForTeacher(teacherId);
        if (teacherId > 0 && studentIds.isEmpty()) {
            return attendanceList; // No students for this teacher, so no attendance
        }
        if (teacherId > 0) {
            String inClause = String.join(",", java.util.Collections.nCopies(studentIds.size(), "?"));
            sql += " AND a.student_id IN (" + inClause + ")";
        }
        sql += " ORDER BY s.name";

        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            stmt.setDate(paramIndex++, Date.valueOf(date));
            if (teacherId > 0) {
                for (String id : studentIds) {
                    stmt.setString(paramIndex++, id);
                }
            }
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(rs.getInt("id"));
                attendance.setStudentId(rs.getString("student_id"));
                attendance.setDate(rs.getDate("date").toLocalDate());
                attendance.setStatus(rs.getString("status"));
                attendance.setStudentName(rs.getString("name"));
                try {
                    attendance.setRecordedBy(rs.getInt("recorded_by"));
                } catch (SQLException e) { /* Column not found, ignore */ }
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            System.err.println("Get attendance by date error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return attendanceList;
    }
    
    // Overload for backward compatibility if no teacherId is provided
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return getAttendanceByDate(date, 0); // 0 indicates no teacher filter
    }

    public List<Attendance> getAttendanceByStudent(String studentId, int teacherId) {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.*, s.name FROM attendance a " +
                    "JOIN students s ON a.student_id = s.student_id " +
                    "WHERE a.student_id = ?";
        
        if (teacherId > 0) {
            sql += " AND s.teacher_id = ?"; // Filter by teacher if applicable
        }
        sql += " ORDER BY a.date DESC";

        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            if (teacherId > 0) {
                stmt.setInt(2, teacherId);
            }
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(rs.getInt("id"));
                attendance.setStudentId(rs.getString("student_id"));
                attendance.setDate(rs.getDate("date").toLocalDate());
                attendance.setStatus(rs.getString("status"));
                attendance.setStudentName(rs.getString("name"));
                try {
                    attendance.setRecordedBy(rs.getInt("recorded_by"));
                } catch (SQLException e) { /* Column not found, ignore */ }
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            System.err.println("Get attendance by student error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return attendanceList;
    }
    
    // Overload for backward compatibility
    public List<Attendance> getAttendanceByStudent(String studentId) {
        return getAttendanceByStudent(studentId, 0); // 0 indicates no teacher filter
    }

    public double getAttendancePercentage(String studentId, int teacherId) {
        String sql = "SELECT COUNT(*) as total, " +
                    "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) as present " +
                    "FROM attendance a JOIN students s ON a.student_id = s.student_id " +
                    "WHERE a.student_id = ?";
        
        if (teacherId > 0) {
            sql += " AND s.teacher_id = ?"; // Filter by teacher if applicable
        }

        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            if (teacherId > 0) {
                stmt.setInt(2, teacherId);
            }
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
    
    // Overload for backward compatibility
    public double getAttendancePercentage(String studentId) {
        return getAttendancePercentage(studentId, 0); // 0 indicates no teacher filter
    }

    public List<Attendance> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate, int teacherId) {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.*, s.name FROM attendance a " +
                    "JOIN students s ON a.student_id = s.student_id " +
                    "WHERE a.date BETWEEN ? AND ?";
        
        List<String> studentIds = getStudentIdsForTeacher(teacherId);
        if (teacherId > 0 && studentIds.isEmpty()) {
            return attendanceList; // No students for this teacher, so no attendance
        }
        if (teacherId > 0) {
            String inClause = String.join(",", java.util.Collections.nCopies(studentIds.size(), "?"));
            sql += " AND a.student_id IN (" + inClause + ")";
        }
        sql += " ORDER BY a.date DESC, s.name";

        try (Connection conn = DerbyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            stmt.setDate(paramIndex++, Date.valueOf(startDate));
            stmt.setDate(paramIndex++, Date.valueOf(endDate));
            if (teacherId > 0) {
                for (String id : studentIds) {
                    stmt.setString(paramIndex++, id);
                }
            }
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(rs.getInt("id"));
                attendance.setStudentId(rs.getString("student_id"));
                attendance.setDate(rs.getDate("date").toLocalDate());
                attendance.setStatus(rs.getString("status"));
                attendance.setStudentName(rs.getString("name"));
                try {
                    attendance.setRecordedBy(rs.getInt("recorded_by"));
                } catch (SQLException e) { /* Column not found, ignore */ }
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            System.err.println("Get attendance by date range error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return attendanceList;
    }
    
    // Overload for backward compatibility
    public List<Attendance> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
        return getAttendanceByDateRange(startDate, endDate, 0); // 0 indicates no teacher filter
    }
}
