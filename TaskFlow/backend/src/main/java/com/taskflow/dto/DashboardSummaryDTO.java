package com.taskflow.dto;
public record DashboardSummaryDTO(long totalProjects, long totalTasks, long pendingTasks, long inProgressTasks, long completedTasks, long overdueTasks, double completionPercentage) {}
