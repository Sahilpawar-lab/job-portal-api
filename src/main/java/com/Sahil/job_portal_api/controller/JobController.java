package com.Sahil.job_portal_api.controller;

import com.Sahil.job_portal_api.dto.CreateJobRequest;
import com.Sahil.job_portal_api.dto.JobResponse;
import com.Sahil.job_portal_api.dto.UpdateJobRequest;
import com.Sahil.job_portal_api.service.JobService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public Page<JobResponse> findAll(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return jobService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public JobResponse findById(@PathVariable Long id) {
        return jobService.findById(id);
    }

    @GetMapping("/search")
    public Page<JobResponse> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        return jobService.search(keyword, location, company, employmentType, minSalary, maxSalary, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER')")
    public ResponseEntity<JobResponse> create(@Valid @RequestBody CreateJobRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.create(request, principal));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER')")
    public JobResponse update(@PathVariable Long id, @Valid @RequestBody UpdateJobRequest request, Principal principal) {
        return jobService.update(id, request, principal);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        jobService.delete(id, principal);
        return ResponseEntity.noContent().build();
    }
}
