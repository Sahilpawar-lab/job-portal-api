package com.Sahil.job_portal_api.dto;

import com.Sahil.job_portal_api.entity.Role;
import java.time.Instant;

public record UserResponse(Long id, String name, String email, Role role, Instant createdAt) {
}
