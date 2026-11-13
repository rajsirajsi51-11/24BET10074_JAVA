package com.taskflow.controller;

import com.taskflow.dto.*;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/tasks") @CrossOrigin(origins={"http://localhost:5173","http://127.0.0.1:5173"})
public class TaskController {
    private final TaskService service; public TaskController(TaskService service){this.service=service;}
    @GetMapping public List<TaskResponse> all(){return service.all();}
    @GetMapping("/{id}") public TaskResponse get(@PathVariable Long id){return service.get(id);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public TaskResponse create(@Valid @RequestBody TaskRequest r){return service.create(r);}
    @PutMapping("/{id}") public TaskResponse update(@PathVariable Long id,@Valid @RequestBody TaskRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
    @GetMapping("/project/{projectId}") public List<TaskResponse> byProject(@PathVariable Long projectId){return service.byProject(projectId);}
    @GetMapping("/status/{status}") public List<TaskResponse> byStatus(@PathVariable String status){return service.byStatus(status);}
    @GetMapping("/priority/{priority}") public List<TaskResponse> byPriority(@PathVariable String priority){return service.byPriority(priority);}
    @GetMapping("/overdue") public List<TaskResponse> overdue(){return service.overdue();}
    @PutMapping("/{id}/status") public TaskResponse status(@PathVariable Long id,@RequestBody Map<String,String> body){return service.updateStatus(id,body.getOrDefault("status",""));}
}
