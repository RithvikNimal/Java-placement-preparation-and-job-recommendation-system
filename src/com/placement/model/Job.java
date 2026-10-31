package com.placement.model;

/**
 * POJO representing a job posting in the placement system.
 */
public class Job {

    private int    jobId;
    private int    companyId;
    private String companyName;   // populated via JOIN
    private String title;
    private String description;
    private double minCgpa;
    private String requiredSkills; // comma-separated
    private String salaryRange;
    private String jobType;        // Full-Time | Internship | Part-Time
    private String deadline;       // stored as String (yyyy-MM-dd)

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------

    public Job() {}

    public Job(int jobId, int companyId, String companyName, String title,
               String description, double minCgpa, String requiredSkills,
               String salaryRange, String jobType, String deadline) {
        this.jobId          = jobId;
        this.companyId      = companyId;
        this.companyName    = companyName;
        this.title          = title;
        this.description    = description;
        this.minCgpa        = minCgpa;
        this.requiredSkills = requiredSkills;
        this.salaryRange    = salaryRange;
        this.jobType        = jobType;
        this.deadline       = deadline;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------

    public int getJobId()                          { return jobId; }
    public void setJobId(int jobId)                { this.jobId = jobId; }

    public int getCompanyId()                      { return companyId; }
    public void setCompanyId(int companyId)        { this.companyId = companyId; }

    public String getCompanyName()                 { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getTitle()                       { return title; }
    public void setTitle(String title)             { this.title = title; }

    public String getDescription()                 { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getMinCgpa()                     { return minCgpa; }
    public void setMinCgpa(double minCgpa)         { this.minCgpa = minCgpa; }

    public String getRequiredSkills()                      { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills)   { this.requiredSkills = requiredSkills; }

    public String getSalaryRange()                         { return salaryRange; }
    public void setSalaryRange(String salaryRange)         { this.salaryRange = salaryRange; }

    public String getJobType()                     { return jobType; }
    public void setJobType(String jobType)         { this.jobType = jobType; }

    public String getDeadline()                    { return deadline; }
    public void setDeadline(String deadline)       { this.deadline = deadline; }

    // ----------------------------------------------------------------
    // toString
    // ----------------------------------------------------------------

    @Override
    public String toString() {
        return String.format(
            "Job{id=%d, title='%s', company='%s', minCgpa=%.2f, type='%s', salary='%s', deadline='%s'}",
            jobId, title, companyName, minCgpa, jobType, salaryRange, deadline);
    }
}
