package com.werecover.backend.repository;

import com.werecover.backend.model.StepWork;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StepWorkRepository extends JpaRepository<StepWork, Long> {
    List<StepWork> findBySponseeId(Long sponseeId);
    List<StepWork> findBySponsorId(Long sponsorId);
}
