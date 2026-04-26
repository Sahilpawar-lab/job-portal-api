package com.Sahil.job_portal_api.dto;

import com.Sahil.job_portal_api.entity.Job.JobStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record JobResponse(
        Long id,
        String title,
        String description,
        String company,
        String location,
        String employmentType,
        BigDecimal minSalary,
        BigDecimal maxSalary,
        JobStatus status,
        Instant createdAt,
        Long employerId,
        String employerName
) {
}
