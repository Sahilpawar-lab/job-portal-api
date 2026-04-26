package com.Sahil.job_portal_api.service;

import com.Sahil.job_portal_api.dto.ApplicationResponse;
import com.Sahil.job_portal_api.dto.CreateApplicationRequest;
import com.Sahil.job_portal_api.dto.UpdateApplicationStatusRequest;
import com.Sahil.job_portal_api.entity.AppUser;
import com.Sahil.job_portal_api.entity.Job;
import com.Sahil.job_portal_api.entity.Job.JobStatus;
import com.Sahil.job_portal_api.entity.JobApplication;
import com.Sahil.job_portal_api.entity.Role;
import com.Sahil.job_portal_api.exception.BadRequestException;
import com.Sahil.job_portal_api.exception.ForbiddenException;
import com.Sahil.job_portal_api.exception.ResourceNotFoundException;
import com.Sahil.job_portal_api.repository.ApplicationRepository;
import com.Sahil.job_portal_api.repository.JobRepository;
import com.Sahil.job_portal_api.repository.UserRepository;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository, JobRepository jobRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> findAllForCurrentUser(Principal principal) {
        AppUser user = currentUser(principal);
        if (user.getRole() == Role.ADMIN) {
            return applicationRepository.findAll().stream().map(this::toResponse).toList();
        }
        if (user.getRole() == Role.EMPLOYER) {
            return applicationRepository.findByJobEmployerEmail(user.getEmail()).stream().map(this::toResponse).toList();
        }
        return applicationRepository.findByCandidateEmail(user.getEmail()).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ApplicationResponse findById(Long id, Principal principal) {
        JobApplication application = getApplication(id);
        requireVisible(application, principal);
        return toResponse(application);
    }

    public ApplicationResponse create(CreateApplicationRequest request, Principal principal) {
        AppUser candidate = currentUser(principal);
        if (candidate.getRole() != Role.CANDIDATE) {
            throw new ForbiddenException("Only candidates can apply for jobs");
        }
        Job job = jobRepository.findById(request.jobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (job.getStatus() != JobStatus.OPEN) {
            throw new BadRequestException("Cannot apply to a closed job");
        }
        if (applicationRepository.existsByCandidateIdAndJobId(candidate.getId(), job.getId())) {
            throw new BadRequestException("Candidate has already applied to this job");
        }
        JobApplication application = new JobApplication();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setCoverLetter(request.coverLetter());
        return toResponse(applicationRepository.save(application));
    }

    public ApplicationResponse updateStatus(Long id, UpdateApplicationStatusRequest request, Principal principal) {
        JobApplication application = getApplication(id);
        AppUser user = currentUser(principal);
        boolean ownsJob = application.getJob().getEmployer().getId().equals(user.getId());
        if (user.getRole() != Role.ADMIN && !ownsJob) {
            throw new ForbiddenException("Only the employer or an admin can update application status");
        }
        application.setStatus(request.status());
        return toResponse(application);
    }

    public void delete(Long id, Principal principal) {
        JobApplication application = getApplication(id);
        requireVisible(application, principal);
        applicationRepository.delete(application);
    }

    private JobApplication getApplication(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    private void requireVisible(JobApplication application, Principal principal) {
        AppUser user = currentUser(principal);
        boolean isCandidate = application.getCandidate().getId().equals(user.getId());
        boolean isEmployer = application.getJob().getEmployer().getId().equals(user.getId());
        if (user.getRole() != Role.ADMIN && !isCandidate && !isEmployer) {
            throw new ForbiddenException("You cannot access this application");
        }
    }

    private AppUser currentUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private ApplicationResponse toResponse(JobApplication application) {
        return new ApplicationResponse(
                application.getId(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getCandidate().getId(),
                application.getCandidate().getName(),
                application.getCoverLetter(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}
