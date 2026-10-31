-- ============================================================
--  Placement Preparation & Job Recommendation System
--  Database Schema with Seed Data
-- ============================================================

CREATE DATABASE IF NOT EXISTS placement_db;
USE placement_db;

-- ----------------------------------------------------------------
-- Drop tables in reverse dependency order (safe re-run)
-- ----------------------------------------------------------------
DROP TABLE IF EXISTS job_applications;
DROP TABLE IF EXISTS interests;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS jobs;
DROP TABLE IF EXISTS companies;
DROP TABLE IF EXISTS students;

-- ----------------------------------------------------------------
-- students
-- ----------------------------------------------------------------
CREATE TABLE students (
    student_id      INT             NOT NULL AUTO_INCREMENT,
    name            VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    NOT NULL UNIQUE,
    cgpa            DECIMAL(3,2)    NOT NULL,
    branch          VARCHAR(100)    NOT NULL,
    year_of_passing INT             NOT NULL,
    phone           VARCHAR(15),
    PRIMARY KEY (student_id)
);

-- ----------------------------------------------------------------
-- skills
-- ----------------------------------------------------------------
CREATE TABLE skills (
    skill_id          INT          NOT NULL AUTO_INCREMENT,
    student_id        INT          NOT NULL,
    skill_name        VARCHAR(100) NOT NULL,
    proficiency_level ENUM('Beginner','Intermediate','Advanced') NOT NULL DEFAULT 'Beginner',
    PRIMARY KEY (skill_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------
-- interests
-- ----------------------------------------------------------------
CREATE TABLE interests (
    interest_id   INT          NOT NULL AUTO_INCREMENT,
    student_id    INT          NOT NULL,
    interest_area VARCHAR(100) NOT NULL,
    PRIMARY KEY (interest_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------
-- companies
-- ----------------------------------------------------------------
CREATE TABLE companies (
    company_id INT          NOT NULL AUTO_INCREMENT,
    name       VARCHAR(150) NOT NULL,
    industry   VARCHAR(100) NOT NULL,
    location   VARCHAR(150),
    website    VARCHAR(200),
    PRIMARY KEY (company_id)
);

-- ----------------------------------------------------------------
-- jobs
-- ----------------------------------------------------------------
CREATE TABLE jobs (
    job_id          INT             NOT NULL AUTO_INCREMENT,
    company_id      INT             NOT NULL,
    title           VARCHAR(150)    NOT NULL,
    description     TEXT,
    min_cgpa        DECIMAL(3,2)    NOT NULL DEFAULT 6.00,
    required_skills TEXT,
    salary_range    VARCHAR(100),
    job_type        ENUM('Full-Time','Internship','Part-Time') NOT NULL DEFAULT 'Full-Time',
    deadline        DATE,
    PRIMARY KEY (job_id),
    FOREIGN KEY (company_id) REFERENCES companies(company_id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------
-- job_applications
-- ----------------------------------------------------------------
CREATE TABLE job_applications (
    app_id       INT  NOT NULL AUTO_INCREMENT,
    student_id   INT  NOT NULL,
    job_id       INT  NOT NULL,
    applied_date DATE NOT NULL DEFAULT (CURDATE()),
    status       ENUM('Pending','Shortlisted','Rejected','Selected') NOT NULL DEFAULT 'Pending',
    PRIMARY KEY (app_id),
    UNIQUE KEY uq_application (student_id, job_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (job_id)     REFERENCES jobs(job_id)         ON DELETE CASCADE
);

-- ================================================================
--  SEED DATA
-- ================================================================

-- ----------------------------------------------------------------
-- Companies
-- ----------------------------------------------------------------
INSERT INTO companies (name, industry, location, website) VALUES
('Google',  'Technology',        'Bangalore, India',   'https://careers.google.com'),
('TCS',     'IT Services',       'Mumbai, India',      'https://www.tcs.com/careers'),
('Infosys', 'IT Consulting',     'Pune, India',        'https://www.infosys.com/careers'),
('Amazon',  'E-Commerce / Cloud','Hyderabad, India',   'https://www.amazon.jobs'),
('Wipro',   'IT Services',       'Chennai, India',     'https://careers.wipro.com');

-- ----------------------------------------------------------------
-- Jobs
-- ----------------------------------------------------------------
INSERT INTO jobs (company_id, title, description, min_cgpa, required_skills, salary_range, job_type, deadline) VALUES
(1, 'Software Engineer',
 'Design and develop scalable backend services and APIs.',
 7.50, 'Java,Python,Data Structures,Algorithms,System Design',
 '18-25 LPA', 'Full-Time', '2026-12-31'),

(1, 'Machine Learning Engineer Intern',
 'Work on ML pipelines and model deployment.',
 7.00, 'Python,Machine Learning,TensorFlow,Statistics',
 '60000/month', 'Internship', '2026-11-30'),

(2, 'Associate Software Engineer',
 'Develop enterprise Java applications and maintain legacy systems.',
 6.00, 'Java,SQL,Spring Boot,REST API',
 '4-7 LPA', 'Full-Time', '2026-12-15'),

(3, 'Systems Engineer',
 'Full-stack development and support for client applications.',
 6.50, 'Java,HTML,CSS,JavaScript,SQL',
 '3.5-6 LPA', 'Full-Time', '2026-12-20'),

(4, 'Cloud Support Associate',
 'Provide technical support for AWS cloud services.',
 7.00, 'AWS,Linux,Networking,Python',
 '8-12 LPA', 'Full-Time', '2026-11-25'),

(5, 'Project Engineer',
 'Develop web-based solutions for enterprise clients.',
 6.00, 'Java,Spring Boot,SQL,REST API,HTML',
 '3.5-5.5 LPA', 'Full-Time', '2027-01-15');

-- ----------------------------------------------------------------
-- Students
-- ----------------------------------------------------------------
INSERT INTO students (name, email, cgpa, branch, year_of_passing, phone) VALUES
('Arjun Mehta',  'arjun.mehta@example.com',  8.50, 'Computer Science Engineering', 2025, '9876543210'),
('Priya Sharma', 'priya.sharma@example.com', 7.20, 'Information Technology',       2025, '8765432109'),
('Rohit Kumar',  'rohit.kumar@example.com',  6.80, 'Electronics & Communication',  2026, '7654321098');

-- Skills for Arjun (student_id = 1)
INSERT INTO skills (student_id, skill_name, proficiency_level) VALUES
(1, 'Java',            'Advanced'),
(1, 'Python',          'Intermediate'),
(1, 'Data Structures', 'Advanced'),
(1, 'Algorithms',      'Advanced'),
(1, 'SQL',             'Intermediate'),
(1, 'Spring Boot',     'Intermediate'),
(1, 'REST API',        'Intermediate');

-- Interests for Arjun
INSERT INTO interests (student_id, interest_area) VALUES
(1, 'Software Development'),
(1, 'Backend Engineering'),
(1, 'Open Source');

-- Skills for Priya (student_id = 2)
INSERT INTO skills (student_id, skill_name, proficiency_level) VALUES
(2, 'Python',           'Advanced'),
(2, 'Machine Learning', 'Intermediate'),
(2, 'TensorFlow',       'Beginner'),
(2, 'Statistics',       'Intermediate'),
(2, 'SQL',              'Intermediate'),
(2, 'HTML',             'Beginner'),
(2, 'CSS',              'Beginner');

-- Interests for Priya
INSERT INTO interests (student_id, interest_area) VALUES
(2, 'Machine Learning'),
(2, 'Data Science'),
(2, 'Artificial Intelligence');

-- Skills for Rohit (student_id = 3)
INSERT INTO skills (student_id, skill_name, proficiency_level) VALUES
(3, 'Java',       'Intermediate'),
(3, 'HTML',       'Intermediate'),
(3, 'CSS',        'Intermediate'),
(3, 'JavaScript', 'Beginner'),
(3, 'SQL',        'Beginner');

-- Interests for Rohit
INSERT INTO interests (student_id, interest_area) VALUES
(3, 'Web Development'),
(3, 'IT Services'),
(3, 'Full Stack Development');
