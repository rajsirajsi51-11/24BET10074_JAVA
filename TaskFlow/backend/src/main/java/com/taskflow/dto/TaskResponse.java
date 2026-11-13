package com.taskflow.dto;

import com.taskflow.entity.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(Long id, String title, String description, String priority, String status,
                           LocalDateTime createdAt, LocalDate dueDate, Long projectId, String projectName) {
    public static TaskResponse from(Task t){return new TaskResponse(t.getId(),t.getTitle(),t.getDescription(),t.getPriority(),t.getStatus(),t.getCreatedAt(),t.getDueDate(),t.getProject().getId(),t.getProject().getName());}
}
