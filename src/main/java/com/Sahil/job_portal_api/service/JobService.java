package com.Sahil.job_portal_api.service;

import com.Sahil.job_portal_api.dto.CreateJobRequest;
import com.Sahil.job_portal_api.dto.JobResponse;
import com.Sahil.job_portal_api.dto.UpdateJobRequest;
import com.Sahil.job_portal_api.entity.AppUser;
import com.Sahil.job_portal_api.entity.Job;
import com.Sahil.job_portal_api.entity.Role;
import com.Sahil.job_portal_api.exception.ForbiddenException;
import com.Sahil.job_portal_api.exception.ResourceNotFoundException;
import com.Sahil.job_portal_api.repository.JobRepository;
import com.Sahil.job_portal_api.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<JobResponse> findAll(Pageable pageable) {
        return jobRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public JobResponse findById(Long id) {
        return toResponse(getJob(id));
    }

    @Transactional(readOnly = true)
    public Page<JobResponse> search(String keyword, String location, String company, String employmentType,
                                    BigDecimal minSalary, BigDecimal maxSalary, Pageable pageable) {
        return jobRepository.findAll(jobSpecification(keyword, location, company, employmentType, minSalary, maxSalary), pageable)
                .map(this::toResponse);
    }

    public JobResponse create(CreateJobRequest request, Principal principal) {
        AppUser employer = currentUser(principal);
        if (employer.getRole() == Role.CANDIDATE) {
            throw new ForbiddenException("Candidates cannot post jobs");
        }
        Job job = new Job();
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setCompany(request.company());
        job.setLocation(request.location());
        job.setEmploymentType(request.employmentType());
        job.setMinSalary(request.minSalary());
        job.setMaxSalary(request.maxSalary());
        job.setEmployer(employer);
        return toResponse(jobRepository.save(job));
    }

    public JobResponse update(Long id, UpdateJobRequest request, Principal principal) {
        Job job = getJob(id);
        requireOwnerOrAdmin(job, principal);
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setCompany(request.company());
        job.setLocation(request.location());
        job.setEmploymentType(request.employmentType());
        job.setMinSalary(request.minSalary());
        job.setMaxSalary(request.maxSalary());
        job.setStatus(request.status());
        return toResponse(job);
    }

    public void delete(Long id, Principal principal) {
        Job job = getJob(id);
        requireOwnerOrAdmin(job, principal);
        jobRepository.delete(job);
    }

    public Job getJob(Long id) {
        return jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }

    private void requireOwnerOrAdmin(Job job, Principal principal) {
        AppUser user = currentUser(principal);
        boolean ownsJob = job.getEmployer().getId().equals(user.getId());
        if (user.getRole() != Role.ADMIN && !ownsJob) {
            throw new ForbiddenException("Only the job owner or an admin can modify this job");
        }
    }

    private AppUser currentUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private Specification<Job> jobSpecification(String keyword, String location, String company, String employmentType,
                                                BigDecimal minSalary, BigDecimal maxSalary) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("title")), like),
                        builder.like(builder.lower(root.get("description")), like)
                ));
            }
            if (location != null && !location.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }
            if (company != null && !company.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("company")), "%" + company.toLowerCase() + "%"));
            }
            if (employmentType != null && !employmentType.isBlank()) {
                predicates.add(builder.equal(builder.lower(root.get("employmentType")), employmentType.toLowerCase()));
            }
            if (minSalary != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("maxSalary"), minSalary));
            }
            if (maxSalary != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("minSalary"), maxSalary));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private JobResponse toResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getCompany(),
                job.getLocation(),
                job.getEmploymentType(),
                job.getMinSalary(),
                job.getMaxSalary(),
                job.getStatus(),
                job.getCreatedAt(),
                job.getEmployer().getId(),
                job.getEmployer().getName()
        );
    }
}
