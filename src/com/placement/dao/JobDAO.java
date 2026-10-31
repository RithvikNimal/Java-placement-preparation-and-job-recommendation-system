package com.placement.dao;

import com.placement.config.DBConnection;
import com.placement.model.Job;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for all job-related database operations.
 */
public class JobDAO {

    // ================================================================
    // CREATE
    // ================================================================

    /**
     * Inserts a new job posting and returns the generated job_id.
     *
     * @param job Job object with all required fields
     * @return generated primary key, or -1 on failure
     */
    public int addJob(Job job) {
        String sql = "INSERT INTO jobs (company_id, title, description, min_cgpa, required_skills, " +
                     "salary_range, job_type, deadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, job.getCompanyId());
            ps.setString(2, job.getTitle());
            ps.setString(3, job.getDescription());
            ps.setDouble(4, job.getMinCgpa());
            ps.setString(5, job.getRequiredSkills());
            ps.setString(6, job.getSalaryRange());
            ps.setString(7, job.getJobType());
            ps.setString(8, job.getDeadline());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding job: " + e.getMessage());
        }
        return -1;
    }

    // ================================================================
    // READ
    // ================================================================

    /**
     * Retrieves all jobs joined with their company name, ordered by job_id.
     *
     * @return list of all jobs
     */
    public List<Job> getAllJobs() {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT j.*, c.name AS company_name FROM jobs j " +
                     "JOIN companies c ON j.company_id = c.company_id ORDER BY j.job_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching jobs: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves a single job by its primary key (includes company name via JOIN).
     *
     * @param jobId primary key
     * @return Job object, or null if not found
     */
    public Job getJobById(int jobId) {
        String sql = "SELECT j.*, c.name AS company_name FROM jobs j " +
                     "JOIN companies c ON j.company_id = c.company_id WHERE j.job_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching job by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all jobs posted by a specific company.
     *
     * @param companyId the company's primary key
     * @return list of jobs for that company
     */
    public List<Job> getJobsByCompany(int companyId) {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT j.*, c.name AS company_name FROM jobs j " +
                     "JOIN companies c ON j.company_id = c.company_id WHERE j.company_id = ? ORDER BY j.job_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching jobs by company: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves all jobs of a specific type (Full-Time, Internship, Part-Time).
     *
     * @param type job type string
     * @return list of matching jobs
     */
    public List<Job> getJobsByType(String type) {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT j.*, c.name AS company_name FROM jobs j " +
                     "JOIN companies c ON j.company_id = c.company_id WHERE j.job_type = ? ORDER BY j.job_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching jobs by type: " + e.getMessage());
        }
        return list;
    }

    // ================================================================
    // UPDATE
    // ================================================================

    /**
     * Updates an existing job posting.
     *
     * @param job Job with updated fields (jobId must be set)
     */
    public void updateJob(Job job) {
        String sql = "UPDATE jobs SET company_id=?, title=?, description=?, min_cgpa=?, " +
                     "required_skills=?, salary_range=?, job_type=?, deadline=? WHERE job_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, job.getCompanyId());
            ps.setString(2, job.getTitle());
            ps.setString(3, job.getDescription());
            ps.setDouble(4, job.getMinCgpa());
            ps.setString(5, job.getRequiredSkills());
            ps.setString(6, job.getSalaryRange());
            ps.setString(7, job.getJobType());
            ps.setString(8, job.getDeadline());
            ps.setInt(9, job.getJobId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("No job found with ID " + job.getJobId());
            }
        } catch (SQLException e) {
            System.err.println("Error updating job: " + e.getMessage());
        }
    }

    // ================================================================
    // DELETE
    // ================================================================

    /**
     * Deletes a job by ID (cascades to applications).
     *
     * @param jobId primary key of the job to delete
     */
    public void deleteJob(int jobId) {
        String sql = "DELETE FROM jobs WHERE job_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("No job found with ID " + jobId);
            } else {
                System.out.println("Job deleted successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting job: " + e.getMessage());
        }
    }

    // ================================================================
    // Helper
    // ================================================================

    /** Maps the current ResultSet row to a Job object. */
    private Job mapRow(ResultSet rs) throws SQLException {
        String deadline = rs.getDate("deadline") != null ? rs.getDate("deadline").toString() : "N/A";
        return new Job(
            rs.getInt("job_id"),
            rs.getInt("company_id"),
            rs.getString("company_name"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getDouble("min_cgpa"),
            rs.getString("required_skills"),
            rs.getString("salary_range"),
            rs.getString("job_type"),
            deadline
        );
    }
}
