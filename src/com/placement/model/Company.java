package com.placement.model;

/**
 * POJO representing a company in the placement system.
 */
public class Company {

    private int    companyId;
    private String name;
    private String industry;
    private String location;
    private String website;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------

    public Company() {}

    public Company(int companyId, String name, String industry, String location, String website) {
        this.companyId = companyId;
        this.name      = name;
        this.industry  = industry;
        this.location  = location;
        this.website   = website;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------

    public int getCompanyId()                    { return companyId; }
    public void setCompanyId(int companyId)      { this.companyId = companyId; }

    public String getName()                      { return name; }
    public void setName(String name)             { this.name = name; }

    public String getIndustry()                  { return industry; }
    public void setIndustry(String industry)     { this.industry = industry; }

    public String getLocation()                  { return location; }
    public void setLocation(String location)     { this.location = location; }

    public String getWebsite()                   { return website; }
    public void setWebsite(String website)       { this.website = website; }

    // ----------------------------------------------------------------
    // toString
    // ----------------------------------------------------------------

    @Override
    public String toString() {
        return String.format(
            "Company{id=%d, name='%s', industry='%s', location='%s', website='%s'}",
            companyId, name, industry, location, website);
    }
}
