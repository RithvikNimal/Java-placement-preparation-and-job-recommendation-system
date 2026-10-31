package com.placement;

import com.placement.config.DBConnection;
import com.placement.dao.*;
import com.placement.model.*;
import com.placement.service.*;

import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Placement Preparation &amp; Job Recommendation System.
 *
 * <p>Run from the project root after compiling all sources:
 * <pre>
 *   javac -cp lib/mysql-connector-java.jar -d out/classes src/com/placement/**&#47;*.java
 *   java  -cp "out/classes;lib/mysql-connector-java.jar" com.placement.Main
 * </pre>
 */
public class Main {

    // ── DAOs ──────────────────────────────────────────────────────────
    private static final StudentDAO        studentDAO   = new StudentDAO();
    private static final CompanyDAO        companyDAO   = new CompanyDAO();
    private static final JobDAO            jobDAO       = new JobDAO();
    private static final JobApplicationDAO appDAO       = new JobApplicationDAO();

    // ── Services ──────────────────────────────────────────────────────
    private static final RecommendationService recService = new RecommendationService();
    private static final EligibilityService    eligService = new EligibilityService();
    private static final SkillGapService       gapService  = new SkillGapService();

    private static final Scanner scanner = new Scanner(System.in);

    // ================================================================
    // MAIN
    // ================================================================

    public static void main(String[] args) {
        printWelcome();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1 -> studentManagementMenu();
                case 2 -> companyManagementMenu();
                case 3 -> jobManagementMenu();
                case 4 -> jobRecommendationsMenu();
                case 5 -> eligibilityCheckerMenu();
                case 6 -> skillGapAnalysisMenu();
                case 7 -> applyForJobMenu();
                case 8 -> viewMyApplicationsMenu();
                case 9 -> {
                    System.out.println("\n  Closing database connection...");
                    DBConnection.closeConnection();
                    System.out.println("  Thank you for using the Placement System. Goodbye!\n");
                    running = false;
                }
                default -> System.out.println("  Invalid choice. Please try again.\n");
            }
        }
        scanner.close();
    }

    // ================================================================
    // WELCOME & MENUS
    // ================================================================

    private static void printWelcome() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║     PLACEMENT PREPARATION & JOB RECOMMENDATION      ║");
        System.out.println("  ║                     SYSTEM  v1.0                    ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMainMenu() {
        System.out.println("  ┌──────────────────────────────────────────────────────┐");
        System.out.println("  │                    MAIN MENU                         │");
        System.out.println("  ├──────────────────────────────────────────────────────┤");
        System.out.println("  │  1. Student Management                               │");
        System.out.println("  │  2. Company Management                               │");
        System.out.println("  │  3. Job Management                                   │");
        System.out.println("  │  4. Job Recommendations                              │");
        System.out.println("  │  5. Eligibility Checker                              │");
        System.out.println("  │  6. Skill Gap Analysis                               │");
        System.out.println("  │  7. Apply for Job                                    │");
        System.out.println("  │  8. View My Applications                             │");
        System.out.println("  │  9. Exit                                             │");
        System.out.println("  └──────────────────────────────────────────────────────┘");
    }

    // ================================================================
    // 1. STUDENT MANAGEMENT
    // ================================================================

    private static void studentManagementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("  ── STUDENT MANAGEMENT ──────────────────────────────────");
            System.out.println("  1. Add New Student");
            System.out.println("  2. View All Students");
            System.out.println("  3. View Student Profile (by ID)");
            System.out.println("  4. Update Student");
            System.out.println("  5. Delete Student");
            System.out.println("  6. Search by Branch");
            System.out.println("  0. Back to Main Menu");
            System.out.println("  ────────────────────────────────────────────────────────");
            int choice = readInt("  Enter choice: ");
            switch (choice) {
                case 1 -> addNewStudent();
                case 2 -> viewAllStudents();
                case 3 -> viewStudentProfile();
                case 4 -> updateStudent();
                case 5 -> deleteStudent();
                case 6 -> searchByBranch();
                case 0 -> back = true;
                default -> System.out.println("  Invalid choice.\n");
            }
        }
    }

    private static void addNewStudent() {
        System.out.println("\n  ── ADD NEW STUDENT ──");
        String name  = readString("  Full Name         : ");
        String email = readString("  Email             : ");
        double cgpa  = readDouble("  CGPA (0.00-10.00) : ");
        String branch = readString("  Branch            : ");
        int    year   = readInt(   "  Year of Passing   : ");
        String phone  = readString("  Phone Number      : ");

        Student student = new Student(0, name, email, cgpa, branch, year, phone);
        int newId = studentDAO.addStudent(student);

        if (newId == -1) {
            System.out.println("  Failed to add student. (Email may already exist.)");
            return;
        }
        System.out.println("  Student added with ID: " + newId);

        // Add skills
        System.out.println("  \n  Now add skills (enter blank to stop):");
        while (true) {
            String skill = readString("  Skill name (or blank to stop): ");
            if (skill.isBlank()) break;
            System.out.println("  Proficiency: 1=Beginner  2=Intermediate  3=Advanced");
            int lvl = readInt("  Choose (1-3): ");
            String level = switch (lvl) {
                case 2  -> "Intermediate";
                case 3  -> "Advanced";
                default -> "Beginner";
            };
            studentDAO.addSkill(newId, skill, level);
            System.out.println("  Skill '" + skill + "' (" + level + ") added.");
        }

        // Add interests
        System.out.println("  \n  Now add interest areas (enter blank to stop):");
        while (true) {
            String interest = readString("  Interest area (or blank to stop): ");
            if (interest.isBlank()) break;
            studentDAO.addInterest(newId, interest);
            System.out.println("  Interest '" + interest + "' added.");
        }
        System.out.println("  Student profile created successfully!\n");
    }

    private static void viewAllStudents() {
        List<Student> students = studentDAO.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("\n  No students found.\n");
            return;
        }
        System.out.println();
        String header = String.format("  %-4s  %-22s  %-28s  %-5s  %-32s  %-4s",
            "ID", "Name", "Email", "CGPA", "Branch", "Year");
        String border = "  " + "-".repeat(header.length() - 2);
        System.out.println(border);
        System.out.println(header);
        System.out.println(border);
        for (Student s : students) {
            System.out.printf("  %-4d  %-22s  %-28s  %-5.2f  %-32s  %-4d%n",
                s.getStudentId(), s.getName(), s.getEmail(),
                s.getCgpa(), s.getBranch(), s.getYearOfPassing());
        }
        System.out.println(border);
        System.out.println("  Total: " + students.size() + " student(s)\n");
    }

    private static void viewStudentProfile() {
        int id = readInt("\n  Enter Student ID: ");
        Student s = studentDAO.getStudentById(id);
        if (s == null) {
            System.out.println("  Student not found.\n");
            return;
        }
        System.out.println();
        String line = "  " + "═".repeat(55);
        System.out.println(line);
        System.out.println("  STUDENT PROFILE");
        System.out.println(line);
        System.out.printf("  %-20s : %d%n",    "Student ID",     s.getStudentId());
        System.out.printf("  %-20s : %s%n",    "Name",           s.getName());
        System.out.printf("  %-20s : %s%n",    "Email",          s.getEmail());
        System.out.printf("  %-20s : %.2f%n",  "CGPA",           s.getCgpa());
        System.out.printf("  %-20s : %s%n",    "Branch",         s.getBranch());
        System.out.printf("  %-20s : %d%n",    "Year of Passing", s.getYearOfPassing());
        System.out.printf("  %-20s : %s%n",    "Phone",          s.getPhone());
        System.out.printf("  %-20s : %s%n",    "Skills",
            s.getSkills().isEmpty() ? "(none)" : String.join(", ", s.getSkills()));
        System.out.printf("  %-20s : %s%n",    "Interests",
            s.getInterests().isEmpty() ? "(none)" : String.join(", ", s.getInterests()));
        System.out.println(line);
        System.out.println();
    }

    private static void updateStudent() {
        int id = readInt("\n  Enter Student ID to update: ");
        Student existing = studentDAO.getStudentById(id);
        if (existing == null) {
            System.out.println("  Student not found.\n");
            return;
        }
        System.out.println("  (Press Enter to keep current value)");
        String name   = readStringWithDefault("  Name   [" + existing.getName() + "]: ",   existing.getName());
        String email  = readStringWithDefault("  Email  [" + existing.getEmail() + "]: ",  existing.getEmail());
        String cgpaStr = readStringWithDefault("  CGPA   [" + existing.getCgpa() + "]: ",  String.valueOf(existing.getCgpa()));
        String branch = readStringWithDefault("  Branch [" + existing.getBranch() + "]: ", existing.getBranch());
        String yearStr = readStringWithDefault("  Year   [" + existing.getYearOfPassing() + "]: ", String.valueOf(existing.getYearOfPassing()));
        String phone  = readStringWithDefault("  Phone  [" + existing.getPhone() + "]: ",  existing.getPhone());

        double cgpa;
        int    year;
        try { cgpa = Double.parseDouble(cgpaStr); } catch (NumberFormatException e) { cgpa = existing.getCgpa(); }
        try { year = Integer.parseInt(yearStr);   } catch (NumberFormatException e) { year = existing.getYearOfPassing(); }

        existing.setName(name);
        existing.setEmail(email);
        existing.setCgpa(cgpa);
        existing.setBranch(branch);
        existing.setYearOfPassing(year);
        existing.setPhone(phone);

        studentDAO.updateStudent(existing);
        System.out.println("  Student updated successfully.\n");
    }

    private static void deleteStudent() {
        int id = readInt("\n  Enter Student ID to delete: ");
        String confirm = readString("  Are you sure? (yes/no): ");
        if ("yes".equalsIgnoreCase(confirm.trim())) {
            studentDAO.deleteStudent(id);
        } else {
            System.out.println("  Deletion cancelled.\n");
        }
    }

    private static void searchByBranch() {
        String branch = readString("\n  Enter branch to search: ");
        List<Student> students = studentDAO.searchStudentsByBranch(branch);
        if (students.isEmpty()) {
            System.out.println("  No students found in branch: " + branch + "\n");
            return;
        }
        System.out.println("\n  Results for branch: " + branch);
        System.out.printf("  %-4s  %-22s  %-5s  %-35s%n", "ID", "Name", "CGPA", "Branch");
        System.out.println("  " + "-".repeat(70));
        for (Student s : students) {
            System.out.printf("  %-4d  %-22s  %-5.2f  %-35s%n",
                s.getStudentId(), s.getName(), s.getCgpa(), s.getBranch());
        }
        System.out.println("  Found " + students.size() + " student(s).\n");
    }

    // ================================================================
    // 2. COMPANY MANAGEMENT
    // ================================================================

    private static void companyManagementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("  ── COMPANY MANAGEMENT ──────────────────────────────────");
            System.out.println("  1. Add New Company");
            System.out.println("  2. View All Companies");
            System.out.println("  3. View Company Details (by ID)");
            System.out.println("  4. Update Company");
            System.out.println("  5. Delete Company");
            System.out.println("  0. Back to Main Menu");
            System.out.println("  ────────────────────────────────────────────────────────");
            int choice = readInt("  Enter choice: ");
            switch (choice) {
                case 1 -> addNewCompany();
                case 2 -> viewAllCompanies();
                case 3 -> viewCompanyDetails();
                case 4 -> updateCompany();
                case 5 -> deleteCompany();
                case 0 -> back = true;
                default -> System.out.println("  Invalid choice.\n");
            }
        }
    }

    private static void addNewCompany() {
        System.out.println("\n  ── ADD NEW COMPANY ──");
        String name     = readString("  Company Name : ");
        String industry = readString("  Industry     : ");
        String location = readString("  Location     : ");
        String website  = readString("  Website URL  : ");

        Company company = new Company(0, name, industry, location, website);
        int newId = companyDAO.addCompany(company);
        if (newId == -1) {
            System.out.println("  Failed to add company.\n");
        } else {
            System.out.println("  Company added with ID: " + newId + "\n");
        }
    }

    private static void viewAllCompanies() {
        List<Company> companies = companyDAO.getAllCompanies();
        if (companies.isEmpty()) {
            System.out.println("\n  No companies found.\n");
            return;
        }
        System.out.println();
        System.out.printf("  %-4s  %-20s  %-20s  %-25s%n", "ID", "Name", "Industry", "Location");
        System.out.println("  " + "-".repeat(75));
        for (Company c : companies) {
            System.out.printf("  %-4d  %-20s  %-20s  %-25s%n",
                c.getCompanyId(), c.getName(), c.getIndustry(), c.getLocation());
        }
        System.out.println("  Total: " + companies.size() + " company(ies)\n");
    }

    private static void viewCompanyDetails() {
        int id = readInt("\n  Enter Company ID: ");
        Company c = companyDAO.getCompanyById(id);
        if (c == null) {
            System.out.println("  Company not found.\n");
            return;
        }
        System.out.println();
        System.out.println("  COMPANY DETAILS");
        System.out.println("  " + "-".repeat(50));
        System.out.printf("  %-15s : %d%n",  "Company ID", c.getCompanyId());
        System.out.printf("  %-15s : %s%n",  "Name",       c.getName());
        System.out.printf("  %-15s : %s%n",  "Industry",   c.getIndustry());
        System.out.printf("  %-15s : %s%n",  "Location",   c.getLocation());
        System.out.printf("  %-15s : %s%n",  "Website",    c.getWebsite());
        System.out.println();
    }

    private static void updateCompany() {
        int id = readInt("\n  Enter Company ID to update: ");
        Company existing = companyDAO.getCompanyById(id);
        if (existing == null) {
            System.out.println("  Company not found.\n");
            return;
        }
        System.out.println("  (Press Enter to keep current value)");
        String name     = readStringWithDefault("  Name     [" + existing.getName()     + "]: ", existing.getName());
        String industry = readStringWithDefault("  Industry [" + existing.getIndustry() + "]: ", existing.getIndustry());
        String location = readStringWithDefault("  Location [" + existing.getLocation() + "]: ", existing.getLocation());
        String website  = readStringWithDefault("  Website  [" + existing.getWebsite()  + "]: ", existing.getWebsite());

        existing.setName(name);
        existing.setIndustry(industry);
        existing.setLocation(location);
        existing.setWebsite(website);
        companyDAO.updateCompany(existing);
        System.out.println("  Company updated successfully.\n");
    }

    private static void deleteCompany() {
        int id = readInt("\n  Enter Company ID to delete: ");
        String confirm = readString("  Are you sure? (yes/no): ");
        if ("yes".equalsIgnoreCase(confirm.trim())) {
            companyDAO.deleteCompany(id);
        } else {
            System.out.println("  Deletion cancelled.\n");
        }
    }

    // ================================================================
    // 3. JOB MANAGEMENT
    // ================================================================

    private static void jobManagementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("  ── JOB MANAGEMENT ──────────────────────────────────────");
            System.out.println("  1. Add New Job");
            System.out.println("  2. View All Jobs");
            System.out.println("  3. View Job Details (by ID)");
            System.out.println("  4. Update Job");
            System.out.println("  5. Delete Job");
            System.out.println("  6. View Jobs by Company");
            System.out.println("  7. View Jobs by Type");
            System.out.println("  0. Back to Main Menu");
            System.out.println("  ────────────────────────────────────────────────────────");
            int choice = readInt("  Enter choice: ");
            switch (choice) {
                case 1 -> addNewJob();
                case 2 -> viewAllJobs();
                case 3 -> viewJobDetails();
                case 4 -> updateJob();
                case 5 -> deleteJob();
                case 6 -> viewJobsByCompany();
                case 7 -> viewJobsByType();
                case 0 -> back = true;
                default -> System.out.println("  Invalid choice.\n");
            }
        }
    }

    private static void addNewJob() {
        System.out.println("\n  ── ADD NEW JOB ──");
        viewAllCompanies();
        int companyId = readInt("  Company ID        : ");
        String title  = readString("  Job Title         : ");
        String desc   = readString("  Description       : ");
        double minCgpa = readDouble("  Min CGPA          : ");
        String skills  = readString("  Required Skills   : (comma-separated, e.g. Java,SQL) ");
        String salary  = readString("  Salary Range      : ");
        System.out.println("  Job Type: 1=Full-Time  2=Internship  3=Part-Time");
        int typeChoice = readInt("  Choose (1-3): ");
        String jobType = switch (typeChoice) {
            case 2  -> "Internship";
            case 3  -> "Part-Time";
            default -> "Full-Time";
        };
        String deadline = readString("  Deadline (YYYY-MM-DD): ");

        Job job = new Job(0, companyId, null, title, desc, minCgpa, skills, salary, jobType, deadline);
        int newId = jobDAO.addJob(job);
        if (newId == -1) {
            System.out.println("  Failed to add job.\n");
        } else {
            System.out.println("  Job added with ID: " + newId + "\n");
        }
    }

    private static void viewAllJobs() {
        List<Job> jobs = jobDAO.getAllJobs();
        if (jobs.isEmpty()) {
            System.out.println("\n  No jobs found.\n");
            return;
        }
        System.out.println();
        System.out.printf("  %-4s  %-30s  %-16s  %-11s  %-5s  %-10s%n",
            "ID", "Title", "Company", "Type", "CGPA", "Deadline");
        System.out.println("  " + "-".repeat(82));
        for (Job j : jobs) {
            System.out.printf("  %-4d  %-30s  %-16s  %-11s  %-5.2f  %-10s%n",
                j.getJobId(), truncate(j.getTitle(), 30), truncate(j.getCompanyName(), 16),
                j.getJobType(), j.getMinCgpa(), j.getDeadline());
        }
        System.out.println("  Total: " + jobs.size() + " job(s)\n");
    }

    private static void viewJobDetails() {
        int id = readInt("\n  Enter Job ID: ");
        Job j = jobDAO.getJobById(id);
        if (j == null) {
            System.out.println("  Job not found.\n");
            return;
        }
        System.out.println();
        System.out.println("  JOB DETAILS");
        System.out.println("  " + "-".repeat(60));
        System.out.printf("  %-20s : %d%n",   "Job ID",           j.getJobId());
        System.out.printf("  %-20s : %s%n",   "Title",            j.getTitle());
        System.out.printf("  %-20s : %s%n",   "Company",          j.getCompanyName());
        System.out.printf("  %-20s : %s%n",   "Type",             j.getJobType());
        System.out.printf("  %-20s : %.2f%n", "Min CGPA",         j.getMinCgpa());
        System.out.printf("  %-20s : %s%n",   "Salary Range",     j.getSalaryRange());
        System.out.printf("  %-20s : %s%n",   "Deadline",         j.getDeadline());
        System.out.printf("  %-20s : %s%n",   "Required Skills",  j.getRequiredSkills());
        System.out.printf("  %-20s : %s%n",   "Description",      j.getDescription());
        System.out.println();
    }

    private static void updateJob() {
        int id = readInt("\n  Enter Job ID to update: ");
        Job existing = jobDAO.getJobById(id);
        if (existing == null) {
            System.out.println("  Job not found.\n");
            return;
        }
        System.out.println("  (Press Enter to keep current value)");
        String title  = readStringWithDefault("  Title          [" + existing.getTitle() + "]: ", existing.getTitle());
        String desc   = readStringWithDefault("  Description    [" + truncate(existing.getDescription(), 30) + "]: ", existing.getDescription());
        String cgpaS  = readStringWithDefault("  Min CGPA       [" + existing.getMinCgpa() + "]: ", String.valueOf(existing.getMinCgpa()));
        String skills = readStringWithDefault("  Req. Skills    [" + existing.getRequiredSkills() + "]: ", existing.getRequiredSkills());
        String salary = readStringWithDefault("  Salary Range   [" + existing.getSalaryRange() + "]: ", existing.getSalaryRange());
        String type   = readStringWithDefault("  Job Type       [" + existing.getJobType() + "]: ", existing.getJobType());
        String deadline = readStringWithDefault("  Deadline       [" + existing.getDeadline() + "]: ", existing.getDeadline());

        double minCgpa;
        try { minCgpa = Double.parseDouble(cgpaS); } catch (NumberFormatException e) { minCgpa = existing.getMinCgpa(); }

        existing.setTitle(title);
        existing.setDescription(desc);
        existing.setMinCgpa(minCgpa);
        existing.setRequiredSkills(skills);
        existing.setSalaryRange(salary);
        existing.setJobType(type);
        existing.setDeadline(deadline);

        jobDAO.updateJob(existing);
        System.out.println("  Job updated successfully.\n");
    }

    private static void deleteJob() {
        int id = readInt("\n  Enter Job ID to delete: ");
        String confirm = readString("  Are you sure? (yes/no): ");
        if ("yes".equalsIgnoreCase(confirm.trim())) {
            jobDAO.deleteJob(id);
        } else {
            System.out.println("  Deletion cancelled.\n");
        }
    }

    private static void viewJobsByCompany() {
        int cid = readInt("\n  Enter Company ID: ");
        List<Job> jobs = jobDAO.getJobsByCompany(cid);
        if (jobs.isEmpty()) {
            System.out.println("  No jobs found for company ID " + cid + "\n");
            return;
        }
        System.out.println("\n  Jobs for Company ID " + cid + ":");
        printJobTable(jobs);
    }

    private static void viewJobsByType() {
        System.out.println("\n  Job Types: Full-Time  |  Internship  |  Part-Time");
        String type = readString("  Enter type: ");
        List<Job> jobs = jobDAO.getJobsByType(type);
        if (jobs.isEmpty()) {
            System.out.println("  No jobs found of type '" + type + "'\n");
            return;
        }
        System.out.println("\n  Jobs of type: " + type);
        printJobTable(jobs);
    }

    private static void printJobTable(List<Job> jobs) {
        System.out.printf("  %-4s  %-30s  %-16s  %-5s  %-10s%n",
            "ID", "Title", "Company", "CGPA", "Deadline");
        System.out.println("  " + "-".repeat(72));
        for (Job j : jobs) {
            System.out.printf("  %-4d  %-30s  %-16s  %-5.2f  %-10s%n",
                j.getJobId(), truncate(j.getTitle(), 30), truncate(j.getCompanyName(), 16),
                j.getMinCgpa(), j.getDeadline());
        }
        System.out.println();
    }

    // ================================================================
    // 4. JOB RECOMMENDATIONS
    // ================================================================

    private static void jobRecommendationsMenu() {
        System.out.println();
        int studentId = readInt("  Enter your Student ID: ");
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            System.out.println("  Student not found.\n");
            return;
        }
        System.out.println("\n  Finding recommendations for: " + student.getName());
        System.out.println("  CGPA: " + student.getCgpa() + " | Skills: " + student.getSkills());
        recService.getRecommendedJobs(student);
        System.out.println();
    }

    // ================================================================
    // 5. ELIGIBILITY CHECKER
    // ================================================================

    private static void eligibilityCheckerMenu() {
        System.out.println();
        int studentId = readInt("  Enter Student ID: ");
        int jobId     = readInt("  Enter Job ID    : ");

        Student student = studentDAO.getStudentById(studentId);
        Job job = jobDAO.getJobById(jobId);

        if (student == null) { System.out.println("  Student not found.\n"); return; }
        if (job == null)     { System.out.println("  Job not found.\n");     return; }

        eligService.checkEligibility(student, job);
        System.out.println();
    }

    // ================================================================
    // 6. SKILL GAP ANALYSIS
    // ================================================================

    private static void skillGapAnalysisMenu() {
        System.out.println();
        int studentId = readInt("  Enter Student ID: ");
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            System.out.println("  Student not found.\n");
            return;
        }

        // Overall gap first
        gapService.analyzeOverallGap(student);

        // Specific job analysis
        String specific = readString("\n  Analyse gap for a specific job? (yes/no): ");
        if ("yes".equalsIgnoreCase(specific.trim())) {
            viewAllJobs();
            int jobId = readInt("  Enter Job ID: ");
            Job job = jobDAO.getJobById(jobId);
            if (job == null) {
                System.out.println("  Job not found.\n");
            } else {
                gapService.analyzeSkillGap(student, job);
            }
        }
        System.out.println();
    }

    // ================================================================
    // 7. APPLY FOR JOB
    // ================================================================

    private static void applyForJobMenu() {
        System.out.println();
        int studentId = readInt("  Enter your Student ID: ");
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            System.out.println("  Student not found.\n");
            return;
        }

        System.out.println("  Logged in as: " + student.getName());

        // Show eligible jobs
        System.out.println("\n  Available Jobs:");
        viewAllJobs();

        int jobId = readInt("  Enter Job ID to apply for: ");
        Job job = jobDAO.getJobById(jobId);
        if (job == null) {
            System.out.println("  Job not found.\n");
            return;
        }

        // Show eligibility first
        eligService.checkEligibility(student, job);
        String confirm = readString("\n  Proceed with application? (yes/no): ");
        if ("yes".equalsIgnoreCase(confirm.trim())) {
            appDAO.applyForJob(studentId, jobId);
        } else {
            System.out.println("  Application cancelled.\n");
        }
    }

    // ================================================================
    // 8. VIEW MY APPLICATIONS
    // ================================================================

    private static void viewMyApplicationsMenu() {
        System.out.println();
        int studentId = readInt("  Enter your Student ID: ");
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            System.out.println("  Student not found.\n");
            return;
        }

        List<JobApplication> apps = appDAO.getApplicationsByStudent(studentId);
        System.out.println("\n  Applications for: " + student.getName());
        if (apps.isEmpty()) {
            System.out.println("  You have not applied for any jobs yet.\n");
            return;
        }
        System.out.printf("  %-6s  %-30s  %-18s  %-12s  %-12s%n",
            "AppID", "Job Title", "Company", "Date", "Status");
        System.out.println("  " + "-".repeat(84));
        for (JobApplication a : apps) {
            System.out.printf("  %-6d  %-30s  %-18s  %-12s  %-12s%n",
                a.getAppId(),
                truncate(a.getJobTitle(), 30),
                truncate(a.getCompanyName(), 18),
                a.getAppliedDate(),
                a.getStatus());
        }
        System.out.println("  Total: " + apps.size() + " application(s)\n");
    }

    // ================================================================
    // INPUT HELPERS
    // ================================================================

    /** Reads a non-empty string from stdin. */
    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Reads a string; returns {@code defaultValue} if the user presses Enter
     * without typing anything.
     */
    private static String readStringWithDefault(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    /** Reads an integer, retrying on invalid input. */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid integer.");
            }
        }
    }

    /** Reads a double, retrying on invalid input. */
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid number (e.g. 8.50).");
            }
        }
    }

    /** Truncates a string to {@code maxLen} characters, appending "…" if needed. */
    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 1) + "…";
    }
}
