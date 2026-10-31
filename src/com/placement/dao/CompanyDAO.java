package com.placement.dao;

import com.placement.config.DBConnection;
import com.placement.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for all company-related database operations.
 */
public class CompanyDAO {

    // ================================================================
    // CREATE
    // ================================================================

    /**
     * Inserts a new company and returns the generated company_id.
     *
     * @param company Company object with all required fields
     * @return generated primary key, or -1 on failure
     */
    public int addCompany(Company company) {
        String sql = "INSERT INTO companies (name, industry, location, website) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, company.getName());
            ps.setString(2, company.getIndustry());
            ps.setString(3, company.getLocation());
            ps.setString(4, company.getWebsite());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding company: " + e.getMessage());
        }
        return -1;
    }

    // ================================================================
    // READ
    // ================================================================

    /**
     * Retrieves all companies ordered by company_id.
     *
     * @return list of all companies
     */
    public List<Company> getAllCompanies() {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT * FROM companies ORDER BY company_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching companies: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves a single company by its primary key.
     *
     * @param companyId primary key
     * @return Company object, or null if not found
     */
    public Company getCompanyById(int companyId) {
        String sql = "SELECT * FROM companies WHERE company_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching company by ID: " + e.getMessage());
        }
        return null;
    }

    // ================================================================
    // UPDATE
    // ================================================================

    /**
     * Updates an existing company's details.
     *
     * @param company Company with updated fields (companyId must be set)
     */
    public void updateCompany(Company company) {
        String sql = "UPDATE companies SET name=?, industry=?, location=?, website=? WHERE company_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, company.getName());
            ps.setString(2, company.getIndustry());
            ps.setString(3, company.getLocation());
            ps.setString(4, company.getWebsite());
            ps.setInt(5, company.getCompanyId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("No company found with ID " + company.getCompanyId());
            }
        } catch (SQLException e) {
            System.err.println("Error updating company: " + e.getMessage());
        }
    }

    // ================================================================
    // DELETE
    // ================================================================

    /**
     * Deletes a company by ID (cascades to jobs and applications).
     *
     * @param companyId primary key of company to delete
     */
    public void deleteCompany(int companyId) {
        String sql = "DELETE FROM companies WHERE company_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, companyId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("No company found with ID " + companyId);
            } else {
                System.out.println("Company deleted successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting company: " + e.getMessage());
        }
    }

    // ================================================================
    // Helper
    // ================================================================

    /** Maps the current ResultSet row to a Company object. */
    private Company mapRow(ResultSet rs) throws SQLException {
        return new Company(
            rs.getInt("company_id"),
            rs.getString("name"),
            rs.getString("industry"),
            rs.getString("location"),
            rs.getString("website")
        );
    }
}
