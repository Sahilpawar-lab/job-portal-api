package com.Sahil.job_portal_api.dto;

import com.Sahil.job_portal_api.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateApplicationStatusRequest(@NotNull ApplicationStatus status) {
}
