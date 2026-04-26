package com.Sahil.job_portal_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateApplicationRequest(
        @NotNull Long jobId,
        @NotBlank @Size(max = 2000) String coverLetter
) {
}
