package com.Sahil.job_portal_api.controller;

import com.Sahil.job_portal_api.dto.ApplicationResponse;
import com.Sahil.job_portal_api.dto.CreateApplicationRequest;
import com.Sahil.job_portal_api.dto.UpdateApplicationStatusRequest;
import com.Sahil.job_portal_api.service.ApplicationService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER','CANDIDATE')")
    public List<ApplicationResponse> findAllForCurrentUser(Principal principal) {
        return applicationService.findAllForCurrentUser(principal);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER','CANDIDATE')")
    public ApplicationResponse findById(@PathVariable Long id, Principal principal) {
        return applicationService.findById(id, principal);
    }

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody CreateApplicationRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.create(request, principal));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER')")
    public ApplicationResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            Principal principal
    ) {
        return applicationService.updateStatus(id, request, principal);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER','CANDIDATE')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        applicationService.delete(id, principal);
        return ResponseEntity.noContent().build();
    }
}
