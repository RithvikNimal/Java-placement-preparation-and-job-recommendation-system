package com.placement.service;

import com.placement.dao.JobDAO;
import com.placement.model.Job;
import com.placement.model.Student;

import java.util.*;

/**
 * Service that analyses the gap between a student's current skills
 * and the skills required by target jobs, and suggests learning resources.
 */
public class SkillGapService {

    private final JobDAO jobDAO = new JobDAO();

    /** Hard-coded learning resource suggestions keyed by skill name (lower-case). */
    private static final Map<String, String> RESOURCES = new HashMap<>();

    static {
        RESOURCES.put("java",             "Oracle Java Tutorial (docs.oracle.com/javase), Udemy – Java Masterclass (Tim Buchalka)");
        RESOURCES.put("python",           "Python.org Official Tutorial, Coursera – Python for Everybody (U. Michigan)");
        RESOURCES.put("data structures",  "GeeksforGeeks DSA Course, Coursera – Algorithms Specialization (Stanford)");
        RESOURCES.put("algorithms",       "GeeksforGeeks DSA Course, MIT OpenCourseWare 6.006");
        RESOURCES.put("sql",              "W3Schools SQL Tutorial, Mode Analytics SQL Tutorial");
        RESOURCES.put("spring boot",      "Spring.io Official Guides, Udemy – Spring Boot 3 & Spring Framework 6 (Chad Darby)");
        RESOURCES.put("rest api",         "REST API Tutorial (restfulapi.net), Baeldung Spring REST guides");
        RESOURCES.put("machine learning", "Coursera – Machine Learning Specialization (Andrew Ng), fast.ai");
        RESOURCES.put("tensorflow",       "TensorFlow Official Tutorials (tensorflow.org), Coursera – DeepLearning.AI TF Developer");
        RESOURCES.put("statistics",       "Khan Academy Statistics & Probability, StatQuest YouTube channel");
        RESOURCES.put("html",             "MDN Web Docs – HTML, freeCodeCamp Responsive Web Design");
        RESOURCES.put("css",              "MDN Web Docs – CSS, The Odin Project");
        RESOURCES.put("javascript",       "javascript.info, freeCodeCamp JavaScript Algorithms and Data Structures");
        RESOURCES.put("aws",              "AWS Training (aws.amazon.com/training), A Cloud Guru AWS courses");
        RESOURCES.put("linux",            "Linux Foundation Training, OverTheWire: Bandit (for CLI practice)");
        RESOURCES.put("networking",       "Cisco CCNA Self-Study, Professor Messer CompTIA Network+ (free)");
        RESOURCES.put("system design",    "Grokking the System Design Interview (educative.io), System Design Primer (GitHub)");
        RESOURCES.put("docker",           "Docker Official Docs Getting Started, TechWorld with Nana – Docker Tutorial");
        RESOURCES.put("kubernetes",       "Kubernetes.io Official Tutorial, KodeKloud Kubernetes for Beginners");
        RESOURCES.put("react",            "React Official Docs (react.dev), Full Stack Open (Helsinki University)");
        RESOURCES.put("git",              "Pro Git Book (git-scm.com/book), Atlassian Git Tutorials");
    }

    // ================================================================
    // Public API
    // ================================================================

    /**
     * Analyses the skill gap between the student and a specific job,
     * and prints missing skills along with learning resource suggestions.
     *
     * @param student fully-loaded Student (with skills populated)
     * @param job     target job
     */
    public void analyzeSkillGap(Student student, Job job) {
        Set<String> studentSkillsLower = toNormalizedSet(student.getSkills());

        List<String> present = new ArrayList<>();
        List<String> missing  = new ArrayList<>();

        if (job.getRequiredSkills() != null && !job.getRequiredSkills().isEmpty()) {
            for (String req : job.getRequiredSkills().split(",")) {
                String trimmed = req.trim();
                if (studentSkillsLower.contains(trimmed.toLowerCase())) {
                    present.add(trimmed);
                } else {
                    missing.add(trimmed);
                }
            }
        }

        String line = "=".repeat(65);
        System.out.println();
        System.out.println("  " + line);
        System.out.println("          SKILL GAP ANALYSIS");
        System.out.println("  " + line);
        System.out.printf("  Student : %s (ID: %d)%n", student.getName(), student.getStudentId());
        System.out.printf("  Job     : %s @ %s (ID: %d)%n", job.getTitle(), job.getCompanyName(), job.getJobId());
        System.out.println("  " + "-".repeat(65));

        System.out.println("  SKILLS YOU ALREADY HAVE:");
        if (present.isEmpty()) {
            System.out.println("    (none matching)");
        } else {
            for (String s : present) {
                System.out.println("    ✔  " + s);
            }
        }

        System.out.println();
        System.out.println("  SKILLS YOU NEED TO LEARN:");
        if (missing.isEmpty()) {
            System.out.println("    (none – you already have all required skills!)");
        } else {
            for (String s : missing) {
                System.out.println("    ✘  " + s);
                String resource = RESOURCES.getOrDefault(s.toLowerCase(),
                    "Search on Coursera / Udemy / YouTube for '" + s + "' tutorials");
                System.out.println("       Resources: " + resource);
            }
        }
        System.out.println("  " + line);
    }

    /**
     * Analyses the skill gaps across all jobs for which the student has
     * sufficient CGPA, and prints the top-3 most needed skills to learn.
     *
     * @param student fully-loaded Student (with skills populated)
     */
    public void analyzeOverallGap(Student student) {
        List<Job> allJobs = jobDAO.getAllJobs();
        Set<String> studentSkillsLower = toNormalizedSet(student.getSkills());

        // Count how many jobs need each missing skill
        Map<String, Integer> missingSkillCount = new HashMap<>();

        for (Job job : allJobs) {
            if (student.getCgpa() < job.getMinCgpa()) continue;
            if (job.getRequiredSkills() == null || job.getRequiredSkills().isEmpty()) continue;

            for (String req : job.getRequiredSkills().split(",")) {
                String trimmed = req.trim();
                if (!studentSkillsLower.contains(trimmed.toLowerCase())) {
                    missingSkillCount.merge(trimmed, 1, Integer::sum);
                }
            }
        }

        // Sort by frequency descending
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(missingSkillCount.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        String line = "=".repeat(65);
        System.out.println();
        System.out.println("  " + line);
        System.out.println("          OVERALL SKILL GAP ANALYSIS");
        System.out.println("  " + line);
        System.out.printf("  Student : %s (ID: %d)   CGPA: %.2f%n",
            student.getName(), student.getStudentId(), student.getCgpa());
        System.out.println("  " + "-".repeat(65));

        if (sorted.isEmpty()) {
            System.out.println("  Great! You have all skills needed for eligible jobs, or no eligible jobs found.");
        } else {
            System.out.println("  TOP SKILLS TO LEARN (ranked by relevance across eligible jobs):");
            System.out.println();
            int rank = 1;
            for (Map.Entry<String, Integer> entry : sorted) {
                if (rank > 3) break;
                String skill    = entry.getKey();
                int    jobCount = entry.getValue();
                String resource = RESOURCES.getOrDefault(skill.toLowerCase(),
                    "Search on Coursera / Udemy / YouTube for '" + skill + "' tutorials");

                System.out.printf("  %d. %-28s (needed in %d job%s)%n",
                    rank, skill, jobCount, jobCount == 1 ? "" : "s");
                System.out.println("     Resources: " + resource);
                System.out.println();
                rank++;
            }
        }
        System.out.println("  " + line);
    }

    // ================================================================
    // Helper
    // ================================================================

    /** Converts a list of skill strings to a lower-case, trimmed HashSet. */
    private Set<String> toNormalizedSet(List<String> skills) {
        Set<String> set = new HashSet<>();
        for (String s : skills) {
            set.add(s.trim().toLowerCase());
        }
        return set;
    }
}
