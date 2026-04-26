package com.Sahil.job_portal_api.dto;

import com.Sahil.job_portal_api.entity.ApplicationStatus;
import java.time.Instant;

public record ApplicationResponse(
        Long id,
        Long jobId,
        String jobTitle,
        Long candidateId,
        String candidateName,
        String coverLetter,
        ApplicationStatus status,
        Instant appliedAt
) {
}
