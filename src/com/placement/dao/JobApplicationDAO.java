package com.placement.dao;

import com.placement.config.DBConnection;
import com.placement.model.JobApplication;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for student job-application operations.
 */
public class JobApplicationDAO {

    // ================================================================
    // CREATE
    // ================================================================

    /**
     * Records a new job application for a student.
     * Prevents duplicate applications via the unique constraint.
     *
     * @param studentId the applying student's ID
     * @param jobId     the job being applied to
     * @return true if the application was successfully inserted
     */
    public boolean applyForJob(int studentId, int jobId) {
        if (hasApplied(studentId, jobId)) {
            System.out.println("You have already applied for this job.");
            return false;
        }
        String sql = "INSERT INTO job_applications (student_id, job_id, applied_date, status) " +
                     "VALUES (?, ?, CURDATE(), 'Pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, jobId);
            ps.executeUpdate();
            System.out.println("Application submitted successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error applying for job: " + e.getMessage());
            return false;
        }
    }

    // ================================================================
    // READ
    // ================================================================

    /**
     * Returns all applications for a student, joined with job and company details.
     *
     * @param studentId the student's primary key
     * @return list of JobApplication objects with display fields populated
     */
    public List<JobApplication> getApplicationsByStudent(int studentId) {
        List<JobApplication> list = new ArrayList<>();
        String sql = "SELECT ja.app_id, ja.student_id, ja.job_id, ja.applied_date, ja.status, " +
                     "j.title AS job_title, c.name AS company_name " +
                     "FROM job_applications ja " +
                     "JOIN jobs j     ON ja.job_id     = j.job_id " +
                     "JOIN companies c ON j.company_id = c.company_id " +
                     "WHERE ja.student_id = ? ORDER BY ja.applied_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JobApplication app = new JobApplication(
                        rs.getInt("app_id"),
                        rs.getInt("student_id"),
                        rs.getInt("job_id"),
                        rs.getDate("applied_date").toString(),
                        rs.getString("status")
                    );
                    app.setJobTitle(rs.getString("job_title"));
                    app.setCompanyName(rs.getString("company_name"));
                    list.add(app);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching applications: " + e.getMessage());
        }
        return list;
    }

    /**
     * Checks whether a student has already applied for a specific job.
     *
     * @param studentId the student's primary key
     * @param jobId     the job's primary key
     * @return true if a record already exists
     */
    public boolean hasApplied(int studentId, int jobId) {
        String sql = "SELECT COUNT(*) FROM job_applications WHERE student_id = ? AND job_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking application: " + e.getMessage());
        }
        return false;
    }

    // ================================================================
    // UPDATE
    // ================================================================

    /**
     * Updates the status of a specific application.
     *
     * @param appId  the application's primary key
     * @param status new status (Pending / Shortlisted / Rejected / Selected)
     */
    public void updateApplicationStatus(int appId, String status) {
        String sql = "UPDATE job_applications SET status = ? WHERE app_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, appId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("No application found with ID " + appId);
            } else {
                System.out.println("Application status updated to: " + status);
            }
        } catch (SQLException e) {
            System.err.println("Error updating application status: " + e.getMessage());
        }
    }
}
