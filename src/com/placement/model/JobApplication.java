package com.placement.model;

/**
 * POJO representing a student's job application.
 */
public class JobApplication {

    private int    appId;
    private int    studentId;
    private int    jobId;
    private String appliedDate; // yyyy-MM-dd
    private String status;      // Pending | Shortlisted | Rejected | Selected

    // Extra display fields populated via JOIN
    private String jobTitle;
    private String companyName;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------

    public JobApplication() {}

    public JobApplication(int appId, int studentId, int jobId, String appliedDate, String status) {
        this.appId       = appId;
        this.studentId   = studentId;
        this.jobId       = jobId;
        this.appliedDate = appliedDate;
        this.status      = status;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------

    public int getAppId()                        { return appId; }
    public void setAppId(int appId)              { this.appId = appId; }

    public int getStudentId()                    { return studentId; }
    public void setStudentId(int studentId)      { this.studentId = studentId; }

    public int getJobId()                        { return jobId; }
    public void setJobId(int jobId)              { this.jobId = jobId; }

    public String getAppliedDate()               { return appliedDate; }
    public void setAppliedDate(String appliedDate) { this.appliedDate = appliedDate; }

    public String getStatus()                    { return status; }
    public void setStatus(String status)         { this.status = status; }

    public String getJobTitle()                  { return jobTitle; }
    public void setJobTitle(String jobTitle)     { this.jobTitle = jobTitle; }

    public String getCompanyName()                       { return companyName; }
    public void setCompanyName(String companyName)       { this.companyName = companyName; }

    // ----------------------------------------------------------------
    // toString
    // ----------------------------------------------------------------

    @Override
    public String toString() {
        return String.format(
            "JobApplication{appId=%d, studentId=%d, jobId=%d, job='%s', company='%s', date='%s', status='%s'}",
            appId, studentId, jobId, jobTitle, companyName, appliedDate, status);
    }
}
