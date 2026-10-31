package com.placement.dao;

import com.placement.config.DBConnection;
import com.placement.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for all student-related database operations.
 */
public class StudentDAO {

    // ================================================================
    // CREATE
    // ================================================================

    /**
     * Inserts a new student record and returns the generated student_id.
     *
     * @param student Student object with all required fields set
     * @return generated primary key, or -1 on failure
     */
    public int addStudent(Student student) {
        String sql = "INSERT INTO students (name, email, cgpa, branch, year_of_passing, phone) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setDouble(3, student.getCgpa());
            ps.setString(4, student.getBranch());
            ps.setInt(5, student.getYearOfPassing());
            ps.setString(6, student.getPhone());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Adds a skill entry for a student.
     *
     * @param studentId        the owning student's ID
     * @param skillName        skill name (e.g. "Java")
     * @param proficiencyLevel one of Beginner / Intermediate / Advanced
     */
    public void addSkill(int studentId, String skillName, String proficiencyLevel) {
        String sql = "INSERT INTO skills (student_id, skill_name, proficiency_level) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, skillName);
            ps.setString(3, proficiencyLevel);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding skill: " + e.getMessage());
        }
    }

    /**
     * Adds an interest entry for a student.
     *
     * @param studentId    the owning student's ID
     * @param interestArea area of interest (e.g. "Machine Learning")
     */
    public void addInterest(int studentId, String interestArea) {
        String sql = "INSERT INTO interests (student_id, interest_area) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, interestArea);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding interest: " + e.getMessage());
        }
    }

    // ================================================================
    // READ
    // ================================================================

    /**
     * Retrieves all students (without skills/interests for list view).
     *
     * @return list of all students
     */
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves a single student by ID and populates their skills and interests.
     *
     * @param studentId primary key
     * @return fully-populated Student, or null if not found
     */
    public Student getStudentById(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student student = mapRow(rs);
                    student.setSkills(getStudentSkills(studentId));
                    student.setInterests(getStudentInterests(studentId));
                    return student;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns all skill names for a given student.
     *
     * @param studentId the student's primary key
     * @return list of skill names
     */
    public List<String> getStudentSkills(int studentId) {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT skill_name FROM skills WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    skills.add(rs.getString("skill_name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching skills: " + e.getMessage());
        }
        return skills;
    }

    /**
     * Returns all interest areas for a given student.
     *
     * @param studentId the student's primary key
     * @return list of interest area strings
     */
    public List<String> getStudentInterests(int studentId) {
        List<String> interests = new ArrayList<>();
        String sql = "SELECT interest_area FROM interests WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    interests.add(rs.getString("interest_area"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching interests: " + e.getMessage());
        }
        return interests;
    }

    /**
     * Searches students whose branch matches the given string (case-insensitive).
     *
     * @param branch branch name to search
     * @return matching students
     */
    public List<Student> searchStudentsByBranch(String branch) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE LOWER(branch) LIKE LOWER(?) ORDER BY student_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + branch + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching students: " + e.getMessage());
        }
        return list;
    }

    // ================================================================
    // UPDATE
    // ================================================================

    /**
     * Updates the core fields of an existing student record.
     *
     * @param student Student with updated fields (studentId must be set)
     */
    public void updateStudent(Student student) {
        String sql = "UPDATE students SET name=?, email=?, cgpa=?, branch=?, year_of_passing=?, phone=? WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setDouble(3, student.getCgpa());
            ps.setString(4, student.getBranch());
            ps.setInt(5, student.getYearOfPassing());
            ps.setString(6, student.getPhone());
            ps.setInt(7, student.getStudentId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("No student found with ID " + student.getStudentId());
            }
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
        }
    }

    // ================================================================
    // DELETE
    // ================================================================

    /**
     * Deletes a student by ID (cascades to skills, interests, applications).
     *
     * @param studentId primary key of the student to delete
     */
    public void deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("No student found with ID " + studentId);
            } else {
                System.out.println("Student deleted successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
        }
    }

    // ================================================================
    // Helper
    // ================================================================

    /** Maps the current ResultSet row to a Student object. */
    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("student_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getDouble("cgpa"),
            rs.getString("branch"),
            rs.getInt("year_of_passing"),
            rs.getString("phone")
        );
    }
}
