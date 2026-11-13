package com.taskflow.controller;

import com.taskflow.entity.Project;
import com.taskflow.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/projects") @CrossOrigin(origins={"http://localhost:5173","http://127.0.0.1:5173"})
public class ProjectController {
    private final ProjectService service; public ProjectController(ProjectService service){this.service=service;}
    @GetMapping public List<Project> all(){return service.all();}
    @GetMapping("/{id}") public Project get(@PathVariable Long id){return service.get(id);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Project create(@Valid @RequestBody Project p){return service.save(p);}
    @PutMapping("/{id}") public Project update(@PathVariable Long id,@Valid @RequestBody Project p){return service.update(id,p);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}
