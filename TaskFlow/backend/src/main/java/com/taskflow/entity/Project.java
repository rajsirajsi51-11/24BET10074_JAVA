package com.taskflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "projects")
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 150) private String name;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "start_date") private LocalDate startDate;
    private LocalDate deadline;
    @Column(nullable = false, length = 30) private String status = "Not Started";
    @JsonIgnore @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true) private List<Task> tasks = new ArrayList<>();
    public Project() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;}
    public LocalDate getDeadline(){return deadline;} public void setDeadline(LocalDate v){deadline=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public List<Task> getTasks(){return tasks;}
}
