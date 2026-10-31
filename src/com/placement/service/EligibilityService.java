package com.placement.service;

import com.placement.model.Job;
import com.placement.model.Student;

import java.util.*;

/**
 * Service for checking a student's eligibility for a specific job.
 */
public class EligibilityService {

    /**
     * Checks whether the given student meets the minimum requirements for the job.
     *
     * <p>The returned map contains:
     * <ul>
     *   <li>{@code eligible}        – {@code Boolean}: overall eligibility result</li>
     *   <li>{@code cgpaCheck}       – {@code Boolean}: true if student CGPA >= job minimum</li>
     *   <li>{@code skillsCheck}     – {@code Boolean}: true if student has at least one required skill</li>
     *   <li>{@code matchingSkills}  – {@code List<String>}: skills the student possesses</li>
     *   <li>{@code missingSkills}   – {@code List<String>}: skills the student lacks</li>
     * </ul>
     *
     * @param student fully-loaded Student (with skills)
     * @param job     target Job
     * @return map of eligibility details
     */
    public Map<String, Object> checkEligibility(Student student, Job job) {
        Map<String, Object> result = new LinkedHashMap<>();

        // --- CGPA check ---
        boolean cgpaCheck = student.getCgpa() >= job.getMinCgpa();

        // --- Skill check ---
        Set<String> studentSkillsLower = new HashSet<>();
        for (String s : student.getSkills()) {
            studentSkillsLower.add(s.trim().toLowerCase());
        }

        List<String> matchingSkills = new ArrayList<>();
        List<String> missingSkills  = new ArrayList<>();

        if (job.getRequiredSkills() != null && !job.getRequiredSkills().isEmpty()) {
            for (String req : job.getRequiredSkills().split(",")) {
                String trimmed = req.trim();
                if (studentSkillsLower.contains(trimmed.toLowerCase())) {
                    matchingSkills.add(trimmed);
                } else {
                    missingSkills.add(trimmed);
                }
            }
        }

        boolean skillsCheck = !matchingSkills.isEmpty();
        boolean eligible    = cgpaCheck && skillsCheck;

        result.put("eligible",       eligible);
        result.put("cgpaCheck",      cgpaCheck);
        result.put("skillsCheck",    skillsCheck);
        result.put("matchingSkills", matchingSkills);
        result.put("missingSkills",  missingSkills);

        // --- Print detailed report ---
        printEligibilityReport(student, job, result);

        return result;
    }

    /** Prints a formatted eligibility report to standard output. */
    @SuppressWarnings("unchecked")
    private void printEligibilityReport(Student student, Job job, Map<String, Object> result) {
        boolean eligible     = (Boolean) result.get("eligible");
        boolean cgpaCheck    = (Boolean) result.get("cgpaCheck");
        boolean skillsCheck  = (Boolean) result.get("skillsCheck");
        List<String> matching = (List<String>) result.get("matchingSkills");
        List<String> missing  = (List<String>) result.get("missingSkills");

        String line = "=".repeat(62);
        System.out.println();
        System.out.println("  " + line);
        System.out.println("          ELIGIBILITY REPORT");
        System.out.println("  " + line);
        System.out.printf("  %-22s : %s%n", "Student",   student.getName() + " (ID: " + student.getStudentId() + ")");
        System.out.printf("  %-22s : %s%n", "Job",       job.getTitle()    + " (ID: " + job.getJobId() + ")");
        System.out.printf("  %-22s : %s%n", "Company",   job.getCompanyName());
        System.out.println("  " + "-".repeat(62));

        // CGPA
        System.out.printf("  %-22s : %.2f / %.2f  %s%n",
            "CGPA Check",
            student.getCgpa(), job.getMinCgpa(),
            cgpaCheck ? "✔  PASS" : "✘  FAIL (below minimum)");

        // Skills
        System.out.printf("  %-22s : %s%n",
            "Skills Check",
            skillsCheck ? "✔  PASS (has at least one required skill)" : "✘  FAIL (no required skills matched)");

        System.out.printf("  %-22s : %s%n", "Matching Skills", matching.isEmpty() ? "None" : String.join(", ", matching));
        System.out.printf("  %-22s : %s%n", "Missing Skills",  missing.isEmpty()  ? "None" : String.join(", ", missing));

        // Overall
        System.out.println("  " + "-".repeat(62));
        if (eligible) {
            System.out.println("  OVERALL RESULT  :  ✔  ELIGIBLE  — You can apply for this job!");
        } else {
            System.out.println("  OVERALL RESULT  :  ✘  NOT ELIGIBLE");
            if (!cgpaCheck) {
                System.out.printf("     → Improve your CGPA from %.2f to at least %.2f%n",
                    student.getCgpa(), job.getMinCgpa());
            }
            if (!skillsCheck) {
                System.out.println("     → Acquire at least one of the required skills listed above.");
            }
        }
        System.out.println("  " + line);
    }
}
