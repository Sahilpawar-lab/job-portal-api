package com.Sahil.job_portal_api.dto;

import com.Sahil.job_portal_api.entity.Job.JobStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateJobRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 4000) String description,
        @NotBlank @Size(max = 160) String company,
        @NotBlank @Size(max = 160) String location,
        @NotBlank @Size(max = 80) String employmentType,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal minSalary,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal maxSalary,
        @NotNull JobStatus status
) {
}
