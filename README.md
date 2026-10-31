# Placement Preparation & Job Recommendation System

A Java console application that helps students prepare for campus placements by managing student profiles, company listings, job postings, and providing smart job recommendations with skill-gap analysis.

---

## Tech Stack

| Layer      | Technology                     |
|------------|-------------------------------|
| Language   | Java 17+                      |
| Database   | MySQL 8.0+                    |
| JDBC       | mysql-connector-java-8.x.jar  |
| Build      | Manual `javac` / any IDE      |

---

## Project Structure

```
java-placement project/
├── src/
│   └── com/placement/
│       ├── Main.java                    ← Entry point & interactive menus
│       ├── config/
│       │   └── DBConnection.java        ← Singleton JDBC connection
│       ├── model/
│       │   ├── Student.java
│       │   ├── Company.java
│       │   ├── Job.java
│       │   └── JobApplication.java
│       ├── dao/
│       │   ├── StudentDAO.java
│       │   ├── CompanyDAO.java
│       │   ├── JobDAO.java
│       │   └── JobApplicationDAO.java
│       └── service/
│           ├── RecommendationService.java
│           ├── EligibilityService.java
│           └── SkillGapService.java
├── sql/
│   └── schema.sql                       ← DDL + sample data
├── lib/
│   └── (place mysql-connector-java.jar here)
├── .gitignore
└── README.md
```

---

## Setup Instructions

### 1. Prerequisites

- Java 17 or higher (`java -version`)
- MySQL 8.0 or higher (`mysql --version`)
- MySQL Connector/J JAR — download from:  
  https://dev.mysql.com/downloads/connector/j/

### 2. Database Setup

```bash
mysql -u root -p < sql/schema.sql
```

This creates the `placement_db` database and inserts sample data.

### 3. Add JDBC Driver

Download `mysql-connector-java-X.X.X.jar` and place it in the `lib/` folder.

### 4. Configure Database Password

Edit `src/com/placement/config/DBConnection.java` and set:
```java
private static final String PASSWORD = "your_mysql_password";
```

### 5. Compile

From the project root directory:

**Windows (PowerShell / CMD):**
```powershell
javac -cp "lib\mysql-connector-java-8.x.x.jar" -d out\classes (Get-ChildItem -Recurse src -Filter *.java | % { $_.FullName })
```

**Or simpler — using a file list:**
```powershell
# Create output directory
New-Item -ItemType Directory -Force out\classes

# Compile all Java sources
javac -cp "lib\mysql-connector-java-8.x.x.jar" -d out\classes `
  src\com\placement\config\DBConnection.java `
  src\com\placement\model\Student.java `
  src\com\placement\model\Company.java `
  src\com\placement\model\Job.java `
  src\com\placement\model\JobApplication.java `
  src\com\placement\dao\StudentDAO.java `
  src\com\placement\dao\CompanyDAO.java `
  src\com\placement\dao\JobDAO.java `
  src\com\placement\dao\JobApplicationDAO.java `
  src\com\placement\service\RecommendationService.java `
  src\com\placement\service\EligibilityService.java `
  src\com\placement\service\SkillGapService.java `
  src\com\placement\Main.java
```

**Linux / macOS:**
```bash
find src -name "*.java" > sources.txt
javac -cp "lib/mysql-connector-java-8.x.x.jar" -d out/classes @sources.txt
```

### 6. Run

**Windows:**
```powershell
java -cp "out\classes;lib\mysql-connector-java-8.x.x.jar" com.placement.Main
```

**Linux / macOS:**
```bash
java -cp "out/classes:lib/mysql-connector-java-8.x.x.jar" com.placement.Main
```

---

## Features

| #  | Feature                    | Description |
|----|---------------------------|-------------|
| 1  | **Student Management**     | Add, view, update, delete students; manage skills & interests; search by branch |
| 2  | **Company Management**     | Full CRUD for company records |
| 3  | **Job Management**         | Full CRUD for job postings; filter by company or job type |
| 4  | **Job Recommendations**    | Scored recommendations based on CGPA, skill match, and interest alignment |
| 5  | **Eligibility Checker**    | Detailed CGPA + skill eligibility report for any student–job pair |
| 6  | **Skill Gap Analysis**     | Per-job and overall gap analysis with curated learning resources |
| 7  | **Apply for Job**          | One-click application from within the app |
| 8  | **View My Applications**   | Application history with real-time status |

---

## Recommendation Algorithm

1. Filter: student CGPA ≥ job minimum CGPA  
2. Filter: student has ≥ 1 matching required skill  
3. Score each eligible job:
   - **+10** per matching skill  
   - **+5**  if job type appears in student interests  
   - **+15** if company industry appears in student interests  
4. Sort descending by score → return top 5

---

## Sample Data

The `schema.sql` seeds:

- **5 Companies**: Google, TCS, Infosys, Amazon, Wipro  
- **6 Jobs**: Software Engineer, ML Intern, Associate SE, Systems Engineer, Cloud Support Associate, Project Engineer  
- **3 Students**: Arjun (CS, CGPA 8.5), Priya (IT, CGPA 7.2), Rohit (ECE, CGPA 6.8)

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Add `mysql-connector-java.jar` to the `lib/` folder and include it in the classpath |
| `Access denied for user 'root'@'localhost'` | Update `PASSWORD` in `DBConnection.java` to match your MySQL root password |
| `Unknown database 'placement_db'` | Run `mysql -u root -p < sql/schema.sql` first |
| `Duplicate entry` on student email | Each student email must be unique |

---

## License

This project is intended for educational/placement preparation purposes.
