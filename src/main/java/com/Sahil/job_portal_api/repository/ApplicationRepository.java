package com.Sahil.job_portal_api.repository;

import com.Sahil.job_portal_api.entity.JobApplication;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);

    List<JobApplication> findByCandidateEmail(String email);

    List<JobApplication> findByJobEmployerEmail(String email);
}
