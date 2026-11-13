package com.taskflow.controller;

import com.taskflow.dto.*;
import com.taskflow.service.DashboardService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/dashboard") @CrossOrigin(origins={"http://localhost:5173","http://127.0.0.1:5173"})
public class DashboardController {
    private final DashboardService service; public DashboardController(DashboardService service){this.service=service;}
    @GetMapping("/summary") public DashboardSummaryDTO summary(){return service.summary();}
    @GetMapping("/project-progress") public List<ProjectProgressDTO> progress(){return service.progress();}
}
