package com.Sahil.job_portal_api.dto;

import com.Sahil.job_portal_api.entity.Role;

public record AuthResponse(String token, Long userId, String name, String email, Role role) {
}
