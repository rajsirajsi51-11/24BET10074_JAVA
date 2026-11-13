package com.taskflow.service;

import com.taskflow.dto.*;
import com.taskflow.entity.Project;
import com.taskflow.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {
    private final ProjectRepository projects; private final TaskRepository tasks;
    public DashboardService(ProjectRepository projects,TaskRepository tasks){this.projects=projects;this.tasks=tasks;}
    public DashboardSummaryDTO summary(){long total=tasks.count(),done=tasks.countByStatusIgnoreCase("Completed");return new DashboardSummaryDTO(projects.count(),total,tasks.countByStatusIgnoreCase("To Do"),tasks.countByStatusIgnoreCase("In Progress"),done,tasks.countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate.now(),"Completed"),total==0?0:Math.round(done*1000.0/total)/10.0);}
    @Transactional(readOnly=true) public List<ProjectProgressDTO> progress(){return projects.findAll().stream().map(this::projectProgress).toList();}
    private ProjectProgressDTO projectProgress(Project p){long total=p.getTasks().size(),done=p.getTasks().stream().filter(t->"Completed".equalsIgnoreCase(t.getStatus())).count();return new ProjectProgressDTO(p.getId(),p.getName(),p.getStatus(),total,done,total==0?0:Math.round(done*1000.0/total)/10.0);}
}
