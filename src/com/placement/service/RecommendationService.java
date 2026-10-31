package com.placement.service;

import com.placement.dao.JobDAO;
import com.placement.model.Job;
import com.placement.model.Student;

import java.util.*;

/**
 * Service that recommends the most suitable jobs for a given student
 * based on CGPA eligibility, skill matching, and interest alignment.
 */
public class RecommendationService {

    private final JobDAO jobDAO = new JobDAO();

    /**
     * Returns the top (up to 5) recommended jobs for a student, sorted by match score.
     *
     * <p>Scoring rules:
     * <ul>
     *   <li>+10 per student skill that matches a required job skill (case-insensitive)</li>
     *   <li>+5  if the job type appears in the student's interest list</li>
     *   <li>+15 if the company's industry appears in the student's interest list</li>
     * </ul>
     *
     * @param student a fully-loaded Student (with skills and interests)
     * @return ordered list of recommended jobs (best match first)
     */
    public List<Job> getRecommendedJobs(Student student) {
        List<Job> allJobs = jobDAO.getAllJobs();

        // Normalize student skills and interests to lowercase for comparison
        Set<String> studentSkillsLower = new HashSet<>();
        for (String s : student.getSkills()) {
            studentSkillsLower.add(s.trim().toLowerCase());
        }
        Set<String> studentInterestsLower = new HashSet<>();
        for (String i : student.getInterests()) {
            studentInterestsLower.add(i.trim().toLowerCase());
        }

        // Map: Job → score
        Map<Job, Integer> scoreMap = new LinkedHashMap<>();

        for (Job job : allJobs) {
            // Filter 1: CGPA eligibility
            if (student.getCgpa() < job.getMinCgpa()) {
                continue;
            }

            // Determine matching skills
            List<String> matchingSkills = new ArrayList<>();
            if (job.getRequiredSkills() != null && !job.getRequiredSkills().isEmpty()) {
                String[] required = job.getRequiredSkills().split(",");
                for (String req : required) {
                    if (studentSkillsLower.contains(req.trim().toLowerCase())) {
                        matchingSkills.add(req.trim());
                    }
                }
            }

            // Filter 2: must have at least one matching skill
            if (matchingSkills.isEmpty()) {
                continue;
            }

            // Score calculation
            int score = matchingSkills.size() * 10;

            // Bonus: job type appears in interests
            if (studentInterestsLower.contains(job.getJobType().trim().toLowerCase())) {
                score += 5;
            }

            // Bonus: industry/company name appears in interests (partial match)
            if (job.getCompanyName() != null) {
                for (String interest : studentInterestsLower) {
                    if (job.getCompanyName().toLowerCase().contains(interest) ||
                        interest.contains(job.getCompanyName().toLowerCase())) {
                        score += 15;
                        break;
                    }
                }
            }

            scoreMap.put(job, score);
        }

        // Sort by score descending
        List<Map.Entry<Job, Integer>> entries = new ArrayList<>(scoreMap.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());

        // Build result list and print explanation
        List<Job> recommendations = new ArrayList<>();
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("  │              JOB RECOMMENDATION RESULTS                         │");
        System.out.println("  └─────────────────────────────────────────────────────────────────┘");

        if (entries.isEmpty()) {
            System.out.println("  No matching jobs found for your profile.");
            return recommendations;
        }

        int rank = 1;
        for (Map.Entry<Job, Integer> entry : entries) {
            if (rank > 5) break;
            Job job = entry.getKey();
            int score = entry.getValue();

            recommendations.add(job);

            // Compute matching skills for display
            List<String> matchingSkills = new ArrayList<>();
            if (job.getRequiredSkills() != null) {
                for (String req : job.getRequiredSkills().split(",")) {
                    if (studentSkillsLower.contains(req.trim().toLowerCase())) {
                        matchingSkills.add(req.trim());
                    }
                }
            }

            System.out.printf("%n  Rank #%d  [Score: %d]%n", rank, score);
            System.out.printf("  %-20s : %s%n", "Job ID",          job.getJobId());
            System.out.printf("  %-20s : %s%n", "Title",           job.getTitle());
            System.out.printf("  %-20s : %s%n", "Company",         job.getCompanyName());
            System.out.printf("  %-20s : %s%n", "Type",            job.getJobType());
            System.out.printf("  %-20s : %.2f%n","Min CGPA",        job.getMinCgpa());
            System.out.printf("  %-20s : %s%n", "Salary",          job.getSalaryRange());
            System.out.printf("  %-20s : %s%n", "Deadline",        job.getDeadline());
            System.out.printf("  %-20s : %s%n", "Matching Skills", matchingSkills);
            System.out.println("  " + "-".repeat(65));
            rank++;
        }
        return recommendations;
    }
}
