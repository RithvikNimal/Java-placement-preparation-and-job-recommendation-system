package com.placement.model;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO representing a student in the placement system.
 */
public class Student {

    private int          studentId;
    private String       name;
    private String       email;
    private double       cgpa;
    private String       branch;
    private int          yearOfPassing;
    private String       phone;
    private List<String> skills;
    private List<String> interests;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------

    public Student() {
        this.skills    = new ArrayList<>();
        this.interests = new ArrayList<>();
    }

    public Student(int studentId, String name, String email, double cgpa,
                   String branch, int yearOfPassing, String phone) {
        this.studentId     = studentId;
        this.name          = name;
        this.email         = email;
        this.cgpa          = cgpa;
        this.branch        = branch;
        this.yearOfPassing = yearOfPassing;
        this.phone         = phone;
        this.skills        = new ArrayList<>();
        this.interests     = new ArrayList<>();
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------

    public int getStudentId()                      { return studentId; }
    public void setStudentId(int studentId)        { this.studentId = studentId; }

    public String getName()                        { return name; }
    public void setName(String name)               { this.name = name; }

    public String getEmail()                       { return email; }
    public void setEmail(String email)             { this.email = email; }

    public double getCgpa()                        { return cgpa; }
    public void setCgpa(double cgpa)               { this.cgpa = cgpa; }

    public String getBranch()                      { return branch; }
    public void setBranch(String branch)           { this.branch = branch; }

    public int getYearOfPassing()                      { return yearOfPassing; }
    public void setYearOfPassing(int yearOfPassing)    { this.yearOfPassing = yearOfPassing; }

    public String getPhone()                       { return phone; }
    public void setPhone(String phone)             { this.phone = phone; }

    public List<String> getSkills()                        { return skills; }
    public void setSkills(List<String> skills)             { this.skills = skills; }
    public void addSkill(String skill)                     { this.skills.add(skill); }

    public List<String> getInterests()                     { return interests; }
    public void setInterests(List<String> interests)       { this.interests = interests; }
    public void addInterest(String interest)               { this.interests.add(interest); }

    // ----------------------------------------------------------------
    // toString
    // ----------------------------------------------------------------

    @Override
    public String toString() {
        return String.format(
            "Student{id=%d, name='%s', email='%s', cgpa=%.2f, branch='%s', yearOfPassing=%d, phone='%s', skills=%s, interests=%s}",
            studentId, name, email, cgpa, branch, yearOfPassing, phone, skills, interests);
    }
}
