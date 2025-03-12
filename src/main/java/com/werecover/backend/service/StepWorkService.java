package com.werecover.backend.service;

import com.werecover.backend.model.StepWork;
import com.werecover.backend.model.StepWorkStatus;
import com.werecover.backend.model.User;
import com.werecover.backend.repository.StepWorkRepository;
import com.werecover.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StepWorkService {
    private final StepWorkRepository stepWorkRepository;
    private final UserRepository userRepository;

    public StepWorkService(StepWorkRepository stepWorkRepository, UserRepository userRepository) {
        this.stepWorkRepository = stepWorkRepository;
        this.userRepository = userRepository;
    }

    /**
     * Assigns step work, ensuring the sponsor is assigning it to their own sponsee.
     */
    public StepWork assignStepWork(Long sponsorId, Long sponseeId, int stepNumber, String instructions) {
        User sponsor = userRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        User sponsee = userRepository.findById(sponseeId)
                .orElseThrow(() -> new RuntimeException("Sponsee not found"));

        // 🔒 Ensure the sponsor is actually assigned to this sponsee
        if (!sponsee.getSponsor().equals(sponsor)) {
            throw new RuntimeException("Unauthorized: You can only assign step work to your own sponsees.");
        }

        StepWork stepWork = new StepWork();
        stepWork.setSponsor(sponsor);
        stepWork.setSponsee(sponsee);
        stepWork.setStepNumber(stepNumber);
        stepWork.setInstructions(instructions);
        stepWork.setStatus(StepWorkStatus.PENDING);

        return stepWorkRepository.save(stepWork);
    }

    /**
     * Retrieves step work for a specific sponsee, ensuring only their sponsor can access it.
     */
    public List<StepWork> getSponseeStepWork(Long sponseeId) {
        User authenticatedUser = getAuthenticatedUser();

        User sponsee = userRepository.findById(sponseeId)
                .orElseThrow(() -> new RuntimeException("Sponsee not found"));

        // 🔒 Ensure only the assigned sponsor can retrieve the sponsee's step work
        if (!sponsee.getSponsor().equals(authenticatedUser)) {
            throw new RuntimeException("Unauthorized: You are not the sponsor of this sponsee.");
        }

        return stepWorkRepository.findBySponseeId(sponseeId);
    }

    /**
     * Submits a response to a step work, ensuring only the assigned sponsee can submit it.
     */
    public StepWork submitStepWorkResponse(Long stepWorkId, String response) {
        User authenticatedUser = getAuthenticatedUser();

        StepWork stepWork = stepWorkRepository.findById(stepWorkId)
                .orElseThrow(() -> new RuntimeException("Step work not found"));

        // 🔒 Ensure only the correct sponsee can submit their own step work
        if (!stepWork.getSponsee().equals(authenticatedUser)) {
            throw new RuntimeException("Unauthorized: You can only submit your own step work.");
        }

        stepWork.setResponse(response);
        stepWork.setStatus(StepWorkStatus.COMPLETED);
        return stepWorkRepository.save(stepWork);
    }

    /**
     * Retrieves the currently authenticated user.
     */
    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        }
        throw new RuntimeException("Unauthorized: Unable to determine authenticated user.");
    }
}
