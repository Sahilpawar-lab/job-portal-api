package com.Sahil.job_portal_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.Sahil.job_portal_api.dto.RegisterRequest;
import com.Sahil.job_portal_api.entity.AppUser;
import com.Sahil.job_portal_api.entity.Role;
import com.Sahil.job_portal_api.exception.BadRequestException;
import com.Sahil.job_portal_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createHashesPasswordAndReturnsSafeResponse() {
        RegisterRequest request = new RegisterRequest("Jane Candidate", "jane@example.com", "password123", Role.CANDIDATE);
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        var response = userService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("jane@example.com");
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void createRejectsDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("Jane", "jane@example.com", "password123", Role.CANDIDATE);
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request)).isInstanceOf(BadRequestException.class);
    }
}
