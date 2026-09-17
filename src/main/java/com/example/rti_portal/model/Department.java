package com.example.rti_portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "Department name is required")
    @Column(nullable = false)
    private String name;

    private String jurisdiction;

    private String contactOfficer;

     @jakarta.validation.constraints.Min(value = 1, message = "SLA days must be at least 1")
    @Column(nullable = false)
    private Integer slaDays = 30;

    public Department() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getJurisdiction() { return jurisdiction; }
    public void setJurisdiction(String jurisdiction) { this.jurisdiction = jurisdiction; }

    public String getContactOfficer() { return contactOfficer; }
    public void setContactOfficer(String contactOfficer) { this.contactOfficer = contactOfficer; }

    public Integer getSlaDays() { return slaDays; }
    public void setSlaDays(Integer slaDays) { this.slaDays = slaDays; }
}
