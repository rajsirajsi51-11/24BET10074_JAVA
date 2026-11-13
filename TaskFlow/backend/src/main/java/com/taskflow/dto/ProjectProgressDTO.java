package com.taskflow.dto;
public record ProjectProgressDTO(Long projectId, String projectName, String status, long totalTasks, long completedTasks, double progress) {}
