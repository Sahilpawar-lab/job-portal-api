package com.Sahil.job_portal_api.service;

import com.Sahil.job_portal_api.dto.RegisterRequest;
import com.Sahil.job_portal_api.dto.UpdateUserRequest;
import com.Sahil.job_portal_api.dto.UserResponse;
import com.Sahil.job_portal_api.entity.AppUser;
import com.Sahil.job_portal_api.exception.BadRequestException;
import com.Sahil.job_portal_api.exception.ResourceNotFoundException;
import com.Sahil.job_portal_api.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return toResponse(getUser(id));
    }

    public UserResponse create(RegisterRequest request) {
        String email = request.email().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered");
        }
        AppUser user = new AppUser();
        user.setName(request.name());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        return toResponse(userRepository.save(user));
    }

    public UserResponse update(Long id, UpdateUserRequest request) {
        AppUser user = getUser(id);
        String email = request.email().toLowerCase();
        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BadRequestException("Email is already registered");
                });
        user.setName(request.name());
        user.setEmail(email);
        user.setRole(request.role());
        return toResponse(user);
    }

    public void delete(Long id) {
        userRepository.delete(getUser(id));
    }

    public AppUser getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserResponse toResponse(AppUser user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
