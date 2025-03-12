package com.werecover.backend.service;

import com.werecover.backend.model.User;
import com.werecover.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class SponseeService {
    private final UserRepository userRepository;

    public SponseeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String assignSponsee(Long sponsorId, Long sponseeId) {
        Optional<User> sponsorOpt = userRepository.findById(sponsorId);
        Optional<User> sponseeOpt = userRepository.findById(sponseeId);

        if (sponsorOpt.isEmpty() || sponseeOpt.isEmpty()) {
            throw new RuntimeException("Sponsor or Sponsee not found.");
        }

        User sponsor = sponsorOpt.get();
        User sponsee = sponseeOpt.get();

        if (!sponsor.getRole().equals(com.werecover.backend.model.Role.ROLE_SPONSOR)) {
            throw new RuntimeException("User is not a Sponsor.");
        }

        if (!sponsee.getRole().equals(com.werecover.backend.model.Role.ROLE_SPONSEE)) {
            throw new RuntimeException("User is not a Sponsee.");
        }

        sponsee.setSponsor(sponsor);
        userRepository.save(sponsee);

        return "Sponsee assigned to sponsor successfully!";
    }
}
