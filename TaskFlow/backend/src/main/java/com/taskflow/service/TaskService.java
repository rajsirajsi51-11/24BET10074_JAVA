package com.taskflow.service;

import com.taskflow.dto.*;
import com.taskflow.entity.*;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service @Transactional
public class TaskService {
    private final TaskRepository tasks; private final ProjectRepository projects;
    public TaskService(TaskRepository tasks,ProjectRepository projects){this.tasks=tasks;this.projects=projects;}
    @Transactional(readOnly=true) public List<TaskResponse> all(){return tasks.findAllByOrderByCreatedAtDesc().stream().map(TaskResponse::from).toList();}
    @Transactional(readOnly=true) public TaskResponse get(Long id){return TaskResponse.from(require(id));}
    public TaskResponse create(TaskRequest r){Task t=new Task();apply(t,r);return TaskResponse.from(tasks.save(t));}
    public TaskResponse update(Long id,TaskRequest r){Task t=require(id);apply(t,r);return TaskResponse.from(tasks.save(t));}
    public void delete(Long id){tasks.delete(require(id));}
    public List<TaskResponse> byProject(Long id){if(!projects.existsById(id))throw new ResourceNotFoundException("Project "+id+" not found");return tasks.findByProjectIdOrderByDueDateAsc(id).stream().map(TaskResponse::from).toList();}
    public List<TaskResponse> byStatus(String s){status(s);return tasks.findByStatusIgnoreCaseOrderByDueDateAsc(s).stream().map(TaskResponse::from).toList();}
    public List<TaskResponse> byPriority(String p){priority(p);return tasks.findByPriorityIgnoreCaseOrderByDueDateAsc(p).stream().map(TaskResponse::from).toList();}
    public List<TaskResponse> overdue(){return tasks.findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate.now(),"Completed").stream().map(TaskResponse::from).toList();}
    public TaskResponse updateStatus(Long id,String s){status(s);Task t=require(id);t.setStatus(s);return TaskResponse.from(tasks.save(t));}
    private Task require(Long id){return tasks.findById(id).orElseThrow(()->new ResourceNotFoundException("Task "+id+" not found"));}
    private void apply(Task t,TaskRequest r){if(r.title().isBlank())throw new IllegalArgumentException("Task title is required");priority(r.priority());status(r.status());Project p=projects.findById(r.projectId()).orElseThrow(()->new ResourceNotFoundException("Project "+r.projectId()+" not found"));t.setTitle(r.title().trim());t.setDescription(r.description());t.setPriority(r.priority());t.setStatus(r.status());t.setDueDate(r.dueDate());t.setProject(p);}
    private void status(String s){if(!List.of("To Do","In Progress","Completed").contains(s))throw new IllegalArgumentException("Status must be To Do, In Progress, or Completed");}
    private void priority(String p){if(!List.of("Low","Medium","High").contains(p))throw new IllegalArgumentException("Priority must be Low, Medium, or High");}
}
