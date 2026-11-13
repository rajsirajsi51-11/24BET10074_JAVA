package com.taskflow.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record TaskRequest(@NotBlank @Size(max=150) String title, String description,
                          @NotBlank String priority, @NotBlank String status,
                          LocalDate dueDate, @NotNull Long projectId) {}
