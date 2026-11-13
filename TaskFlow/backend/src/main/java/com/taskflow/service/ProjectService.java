package com.taskflow.service;

import com.taskflow.entity.Project;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projects;
    public ProjectService(ProjectRepository projects){this.projects=projects;}
    public List<Project> all(){return projects.findAll();}
    public Project get(Long id){return projects.findById(id).orElseThrow(()->new ResourceNotFoundException("Project "+id+" not found"));}
    public Project save(Project p){validate(p);return projects.save(p);}
    public Project update(Long id,Project input){Project p=get(id);p.setName(input.getName());p.setDescription(input.getDescription());p.setStartDate(input.getStartDate());p.setDeadline(input.getDeadline());p.setStatus(input.getStatus());return save(p);}
    public void delete(Long id){projects.delete(get(id));}
    private void validate(Project p){if(p.getName()==null||p.getName().isBlank())throw new IllegalArgumentException("Project name is required");if(p.getName().length()>150)throw new IllegalArgumentException("Project name must be 150 characters or fewer");if(!List.of("Not Started","In Progress","Completed").contains(p.getStatus()))throw new IllegalArgumentException("Status must be Not Started, In Progress, or Completed");if(p.getStartDate()!=null&&p.getDeadline()!=null&&p.getDeadline().isBefore(p.getStartDate()))throw new IllegalArgumentException("Deadline cannot be before start date");}
}
